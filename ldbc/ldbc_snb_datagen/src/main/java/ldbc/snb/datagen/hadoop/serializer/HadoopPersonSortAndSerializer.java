/* 
 Copyright (c) 2013 LDBC
 Linked Data Benchmark Council (http://www.ldbcouncil.org)
 
 This file is part of ldbc_snb_datagen.
 
 ldbc_snb_datagen is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 
 ldbc_snb_datagen is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU General Public License
 along with ldbc_snb_datagen.  If not, see <http://www.gnu.org/licenses/>.
 
 Copyright (C) 2011 OpenLink Software <bdsmt@openlinksw.com>
 All Rights Reserved.
 
 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation;  only Version 2 of the License dated
 June 1991.
 
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.*/
package ldbc.snb.datagen.hadoop.serializer;

import ldbc.snb.datagen.DatagenParams;
import ldbc.snb.datagen.LdbcDatagen;
import ldbc.snb.datagen.dictionary.Dictionaries;
import ldbc.snb.datagen.entities.dynamic.person.Person;
import ldbc.snb.datagen.entities.dynamic.relations.Knows;
import ldbc.snb.datagen.hadoop.HadoopBlockMapper;
import ldbc.snb.datagen.hadoop.HadoopBlockPartitioner;
import ldbc.snb.datagen.hadoop.HadoopTuplePartitioner;
import ldbc.snb.datagen.hadoop.key.TupleKey;
import ldbc.snb.datagen.hadoop.key.blockkey.BlockKey;
import ldbc.snb.datagen.hadoop.key.blockkey.BlockKeyComparator;
import ldbc.snb.datagen.hadoop.key.blockkey.BlockKeyGroupComparator;
import ldbc.snb.datagen.hadoop.miscjob.HadoopFileRanker;
import ldbc.snb.datagen.serializer.DynamicPersonSerializer;
import ldbc.snb.datagen.serializer.UpdateEventSerializer;
import ldbc.snb.datagen.vocabulary.SN;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;

import java.io.IOException;

public class HadoopPersonSortAndSerializer {

    private Configuration conf;

    public static class HadoopDynamicPersonSerializerReducer extends Reducer<BlockKey, Person, LongWritable, Person> {

        private int reducerId;
        /**
         * The id of the reducer.
         **/
        private DynamicPersonSerializer dynamicPersonSerializer_;
        /**
         * The person serializer
         **/
        private UpdateEventSerializer updateSerializer_;

        @Override
        protected void setup(Context context) {
            Configuration conf = context.getConfiguration();
            reducerId = context.getTaskAttemptID().getTaskID().getId();
            LdbcDatagen.initializeContext(conf);
            try {
                dynamicPersonSerializer_ = (DynamicPersonSerializer) Class
                        .forName(conf.get("ldbc.snb.datagen.serializer.dynamicPersonSerializer")).newInstance();
                dynamicPersonSerializer_.initialize(conf, reducerId);
                if (DatagenParams.updateStreams) {
                    updateSerializer_ = new UpdateEventSerializer(conf, DatagenParams.hadoopDir + "/temp_updateStream_person_" + reducerId, reducerId, DatagenParams.numUpdatePartitions);
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
                throw new RuntimeException(e);
            }
        }

        @Override
        public void reduce(BlockKey key, Iterable<Person> valueSet, Context context)
                throws IOException {
            SN.machineId = key.block;
            for (Person p : valueSet) {
                if (p.creationDate() < Dictionaries.dates.getUpdateThreshold() || !DatagenParams.updateStreams) {
                    dynamicPersonSerializer_.export(p);
                } else {
                    updateSerializer_.export(p);
                    updateSerializer_.changePartition();
                }

                for (Knows k : p.knows()) {
                    if (k.creationDate() < Dictionaries.dates.getUpdateThreshold() || !DatagenParams.updateStreams) {
                        dynamicPersonSerializer_.export(p, k);
                    }
                }
            }

        }

        @Override
        protected void cleanup(Context context) {
            dynamicPersonSerializer_.close();
            if (DatagenParams.updateStreams) {
                try {
                    updateSerializer_.close();
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        }
    }


    public HadoopPersonSortAndSerializer(Configuration conf) {
        this.conf = new Configuration(conf);
    }

    public void run(String inputFileName) throws Exception {

        FileSystem fs = FileSystem.get(conf);

        String rankedFileName = conf.get("ldbc.snb.datagen.serializer.hadoopDir") + "/ranked";
        HadoopFileRanker hadoopFileRanker = new HadoopFileRanker(conf, TupleKey.class, Person.class, null);
        hadoopFileRanker.run(inputFileName, rankedFileName);

        int numThreads = Integer.parseInt(conf.get("ldbc.snb.datagen.generator.numThreads"));
        Job job = Job.getInstance(conf, "Person Serializer");
        job.setMapOutputKeyClass(BlockKey.class);
        job.setMapOutputValueClass(Person.class);
        job.setOutputKeyClass(LongWritable.class);
        job.setOutputValueClass(Person.class);
        job.setJarByClass(HadoopBlockMapper.class);
        job.setMapperClass(HadoopBlockMapper.class);
        job.setReducerClass(HadoopDynamicPersonSerializerReducer.class);
        job.setNumReduceTasks(numThreads);
        job.setInputFormatClass(SequenceFileInputFormat.class);
        job.setOutputFormatClass(SequenceFileOutputFormat.class);

        job.setPartitionerClass(HadoopTuplePartitioner.class);

        job.setSortComparatorClass(BlockKeyComparator.class);
        job.setGroupingComparatorClass(BlockKeyGroupComparator.class);
        job.setPartitionerClass(HadoopBlockPartitioner.class);

        FileInputFormat.setInputPaths(job, new Path(rankedFileName));
        FileOutputFormat.setOutputPath(job, new Path(conf.get("ldbc.snb.datagen.serializer.hadoopDir") + "/aux"));
        if (!job.waitForCompletion(true)) {
            throw new Exception();
        }


        try {
            fs.delete(new Path(rankedFileName), true);
            fs.delete(new Path(conf.get("ldbc.snb.datagen.serializer.hadoopDir") + "/aux"), true);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
