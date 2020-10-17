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

package ldbc.snb.datagen.generator.distribution;

import ldbc.snb.datagen.DatagenParams;
import ldbc.snb.datagen.generator.tools.Bucket;
import org.apache.hadoop.conf.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * This class generates Facebook-like social degree distribution
 * <p/>
 * A. Preparation
 * For a specific social network size (total number of users)
 * 1) compute the mean value of social degree
 * 2) compute the range of each bucket (100 buckets) using the data from facebookBucket100.dat
 * B. Generate social degree for each user
 * 1) Determine the bucket (Take a random number from 0-99)
 * 2) Randomly select a social degree in the range of that bucket
 */

public class FacebookDegreeDistribution extends BucketedDistribution {
    private int mean_ = 0;
    private static final int FB_MEAN_ = 190;
    private List<Bucket> buckets_;

    @Override
    public List<Bucket> getBuckets(Configuration conf) {
        mean_ = (int) mean(DatagenParams.numPersons);
        buckets_ = new ArrayList<>();
        loadFBBuckets();
        rebuildBucketRange();
        return buckets_;
    }

    public void loadFBBuckets() {
        try {
            BufferedReader fbDataReader = new BufferedReader(
                    new InputStreamReader(getClass().getResourceAsStream(DatagenParams.fbSocialDegreeFile), "UTF-8"));
            String line;
            while ((line = fbDataReader.readLine()) != null) {
                String data[] = line.split(" ");
                buckets_.add(new Bucket(Float.parseFloat(data[0]), Float.parseFloat(data[1])));
            }
            fbDataReader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void rebuildBucketRange() {
        double newMin;
        double newMax;
        for (int i = 0; i < buckets_.size(); i++) {
            newMin = buckets_.get(i).min() * mean_ / FB_MEAN_;
            newMax = buckets_.get(i).max() * mean_ / FB_MEAN_;
            if (newMax < newMin) newMax = newMin;
            buckets_.get(i).min(newMin);
            buckets_.get(i).max(newMax);
        }
    }

    @Override
    public double mean(long numPersons) {
        return Math.round(Math.pow(numPersons, (0.512 - 0.028 * Math.log10(numPersons))));
    }
}
