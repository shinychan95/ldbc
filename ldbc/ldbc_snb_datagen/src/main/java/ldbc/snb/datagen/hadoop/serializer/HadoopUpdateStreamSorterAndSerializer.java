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

import ldbc.snb.datagen.hadoop.generator.HadoopUpdateEventKeyPartitioner;
import ldbc.snb.datagen.hadoop.key.updatekey.UpdateEventKey;
import ldbc.snb.datagen.hadoop.key.updatekey.UpdateEventKeyGroupComparator;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public class HadoopUpdateStreamSorterAndSerializer {

    private Configuration conf;

    public static class HadoopUpdateStreamSorterAndSerializerReducer extends Reducer<UpdateEventKey, Text, UpdateEventKey, Text> {

        private boolean compressed = false;
        private Configuration conf;
        private String streamType;

        protected void setup(Context context) {
            conf = context.getConfiguration();
            streamType = conf.get("streamType");
            try {
                compressed = Boolean.parseBoolean(conf.get("ldbc.snb.datagen.serializer.compressed"));
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        @Override
        public void reduce(UpdateEventKey key, Iterable<Text> valueSet, Context context)
                throws IOException, InterruptedException {
            OutputStream out;
            try {
                FileSystem fs = FileSystem.get(conf);
                if (compressed) {
                    Path outFile = new Path(context.getConfiguration()
                                                   .get("ldbc.snb.datagen.serializer.socialNetworkDir") + "/updateStream_" + key.reducerId + "_" + key.partition + "_" + streamType + ".csv.gz");
                    out = new GZIPOutputStream(fs.create(outFile));
                } else {
                    Path outFile = new Path(context.getConfiguration()
                                                   .get("ldbc.snb.datagen.serializer.socialNetworkDir") + "/updateStream_" + key.reducerId + "_" + key.partition + "_" + streamType + ".csv");
                    out = fs.create(outFile);
                }
                for (Text t : valueSet) {
                    out.write(t.toString().getBytes("UTF8"));
                }
                out.close();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }


    public HadoopUpdateStreamSorterAndSerializer(Configuration conf) {
        this.conf = new Configuration(conf);
    }

    public void run(List<String> inputFileNames, String type) throws Exception {

        int numThreads = conf.getInt("ldbc.snb.datagen.generator.numThreads", 1);
        conf.set("streamType", type);

        Job job = Job.getInstance(conf, "Update Stream Serializer");
        job.setMapOutputKeyClass(UpdateEventKey.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(UpdateEventKey.class);
        job.setOutputValueClass(Text.class);
        job.setJarByClass(HadoopUpdateStreamSorterAndSerializerReducer.class);
        job.setReducerClass(HadoopUpdateStreamSorterAndSerializerReducer.class);
        job.setNumReduceTasks(numThreads);
        job.setInputFormatClass(SequenceFileInputFormat.class);
        job.setOutputFormatClass(SequenceFileOutputFormat.class);
        job.setPartitionerClass(HadoopUpdateEventKeyPartitioner.class);
        job.setGroupingComparatorClass(UpdateEventKeyGroupComparator.class);
        //job.setSortComparatorClass(UpdateEventKeySortComparator.class);

        for (String s : inputFileNames) {
            FileInputFormat.addInputPath(job, new Path(s));
        }
        FileOutputFormat.setOutputPath(job, new Path(conf.get("ldbc.snb.datagen.serializer.hadoopDir") + "/aux"));
        if (!job.waitForCompletion(true)) {
            throw new Exception();
        }


        try {
            FileSystem fs = FileSystem.get(conf);
            fs.delete(new Path(conf.get("ldbc.snb.datagen.serializer.hadoopDir") + "/aux"), true);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}

