package com.ldbc.driver.generator;

import com.ldbc.driver.util.Bucket.DiscreteBucket;
import com.ldbc.driver.util.Histogram;
import com.ldbc.driver.util.Tuple;
import com.ldbc.driver.util.Tuple2;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DiscreteListGeneratorConstantProbabilitiesVariableSizeTest extends GeneratorTest<List<String>, Integer> {

    @Override
    public Histogram<List<String>, Integer> getExpectedDistribution() {
        List<String> s = new ArrayList<String>();
        List<String> s1 = new ArrayList<String>(Arrays.asList(new String[]{"1"}));
        List<String> s2 = new ArrayList<String>(Arrays.asList(new String[]{"2"}));
        List<String> s3 = new ArrayList<String>(Arrays.asList(new String[]{"3"}));
        List<String> s12 = new ArrayList<String>(Arrays.asList(new String[]{"1", "2"}));
        List<String> s13 = new ArrayList<String>(Arrays.asList(new String[]{"1", "3"}));
        List<String> s23 = new ArrayList<String>(Arrays.asList(new String[]{"2", "3"}));

        List<String> s21 = new ArrayList<String>(Arrays.asList(new String[]{"2", "1"}));
        List<String> s31 = new ArrayList<String>(Arrays.asList(new String[]{"3", "1"}));
        List<String> s32 = new ArrayList<String>(Arrays.asList(new String[]{"3", "2"}));

        List<String> s123 = new ArrayList<String>(Arrays.asList(new String[]{"1", "2", "3"}));

        List<String> s132 = new ArrayList<String>(Arrays.asList(new String[]{"1", "3", "2"}));
        List<String> s213 = new ArrayList<String>(Arrays.asList(new String[]{"2", "1", "3"}));
        List<String> s231 = new ArrayList<String>(Arrays.asList(new String[]{"2", "3", "1"}));
        List<String> s312 = new ArrayList<String>(Arrays.asList(new String[]{"3", "1", "2"}));
        List<String> s321 = new ArrayList<String>(Arrays.asList(new String[]{"3", "2", "1"}));

        Histogram<List<String>, Integer> expectedDistribution = new Histogram<List<String>, Integer>(0);
        expectedDistribution.addBucket(DiscreteBucket.create(s), 2500);

        expectedDistribution.addBucket(DiscreteBucket.create(s1), 833);
        expectedDistribution.addBucket(DiscreteBucket.create(s2), 833);
        expectedDistribution.addBucket(DiscreteBucket.create(s3), 833);

        expectedDistribution.addBucket(DiscreteBucket.create(s12), 416);
        expectedDistribution.addBucket(DiscreteBucket.create(s13), 416);
        expectedDistribution.addBucket(DiscreteBucket.create(s21), 416);
        expectedDistribution.addBucket(DiscreteBucket.create(s23), 416);
        expectedDistribution.addBucket(DiscreteBucket.create(s31), 416);
        expectedDistribution.addBucket(DiscreteBucket.create(s32), 416);

        expectedDistribution.addBucket(DiscreteBucket.create(s123), 416);
        expectedDistribution.addBucket(DiscreteBucket.create(s132), 416);
        expectedDistribution.addBucket(DiscreteBucket.create(s213), 416);
        expectedDistribution.addBucket(DiscreteBucket.create(s231), 416);
        expectedDistribution.addBucket(DiscreteBucket.create(s312), 416);
        expectedDistribution.addBucket(DiscreteBucket.create(s321), 416);

        return expectedDistribution;
    }

    @Override
    public double getDistributionTolerance() {
        return 0.01;
    }

    @Override
    public Iterator<List<String>> getGeneratorImpl(GeneratorFactory generatorFactory) {
        Tuple2<Double, String> p1 = Tuple.tuple2(1.0, "1");
        Tuple2<Double, String> p2 = Tuple.tuple2(1.0, "2");
        Tuple2<Double, String> p3 = Tuple.tuple2(1.0, "3");
        ArrayList<Tuple2<Double, String>> items = new ArrayList<Tuple2<Double, String>>();
        items.add(p1);
        items.add(p2);
        items.add(p3);
        Iterator<Integer> amountToRetrieveGenerator = generatorFactory.uniform(0, 3);
        Iterator<List<String>> generator = generatorFactory.weightedDiscreteList(items,
                amountToRetrieveGenerator);
        return generator;
    }

    @Test(expected = GeneratorException.class)
    public void emptyConstructorTest() {
        // Given
        GeneratorFactory generatorFactory = new GeneratorFactory(new RandomDataGeneratorFactory());
        Iterator<Integer> amountToRetrieveGenerator = generatorFactory.uniform(0, 3);
        ArrayList<Tuple2<Double, String>> emptyItems = new ArrayList<Tuple2<Double, String>>();
        Iterator<List<String>> generator = generatorFactory.weightedDiscreteList(emptyItems,
                amountToRetrieveGenerator);

        // When
        generator.next();

        // Then
        assertEquals("Empty DiscreteGenerator should throw exception on next()", false, true);
    }
}
