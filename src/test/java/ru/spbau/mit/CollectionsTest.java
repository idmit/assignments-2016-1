package ru.spbau.mit;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class CollectionsTest {
    @Test
    public void testMap() {
        final ArrayList<Integer> originalData = new ArrayList<>(Arrays.asList(2, 4, 5, 7));

        Function1<Integer, Integer> timesTwo = new Function1<Integer, Integer>() {
            @Override
            public Integer apply(Integer x) {
                return x + x;
            }
        };

        Iterable<Integer> transformedData = Collections.map(timesTwo, originalData);

        final ArrayList<Integer> correctlyTransformedData = new ArrayList<>(originalData);
        for (int i = 0; i < originalData.size(); i++) {
            correctlyTransformedData.set(i, originalData.get(i) + originalData.get(i));
        }

        assertEquals(transformedData, correctlyTransformedData);
    }

    @Test
    public void testFilter() {
        final ArrayList<Integer> originalData = new ArrayList<>(Arrays.asList(2, 5, 4, 7));

        Predicate<Integer> isEven = new Predicate<Integer>() {
            @Override
            public Boolean apply(Integer x) {
                return x % 2 == 0;
            }
        };

        Iterable<Integer> filteredData = Collections.filter(isEven, originalData);

        final ArrayList<Integer> correctlyFilteredData = new ArrayList<>(0);
        for (Integer x : originalData) {
            if (x % 2 == 0) {
                correctlyFilteredData.add(x);
            }
        }

        assertEquals(filteredData, correctlyFilteredData);
    }

    @Test
    public void testTakeWhile() {
        final ArrayList<Integer> originalData = new ArrayList<>(Arrays.asList(2, 0, 5, 4, 7));

        Predicate<Integer> isEven = new Predicate<Integer>() {
            @Override
            public Boolean apply(Integer x) {
                return x % 2 == 0;
            }
        };

        Iterable<Integer> shortenedData = Collections.takeWhile(isEven, originalData);

        final ArrayList<Integer> correctlyShortenedData = new ArrayList<>(0);
        for (Integer x : originalData) {
            if (x % 2 != 0) {
                break;
            }
            correctlyShortenedData.add(x);
        }

        assertEquals(shortenedData, correctlyShortenedData);
    }

    @Test
    public void testTakeUnless() {
        final ArrayList<Integer> originalData = new ArrayList<>(Arrays.asList(3, 4, 7, 9, 0));

        Predicate<Integer> isEven = new Predicate<Integer>() {
            @Override
            public Boolean apply(Integer x) {
                return x % 2 == 0;
            }
        };

        Iterable<Integer> shortenedData = Collections.takeUnless(isEven, originalData);

        final ArrayList<Integer> correctlyShortenedData = new ArrayList<>(0);
        for (Integer x : originalData) {
            if (x % 2 == 0) {
                break;
            }
            correctlyShortenedData.add(x);
        }

        assertEquals(shortenedData, correctlyShortenedData);
    }

    @Test
    public void testFoldl() {
        final ArrayList<Integer> originalData = new ArrayList<>(Arrays.asList(3, 4, 7, 9, 0));

        Function2<Object, Object, Integer> sum = new Function2<Object, Object, Integer>() {
            @Override
            public Integer apply(Object z, Object x) {
                return (Integer) z + (Integer) x;
            }
        };

        Number s = Collections.foldl(sum, 0, originalData);

        Integer answer = 0;
        for (Integer x : originalData) {
            answer += x;
        }

        assertEquals(s, answer);
    }

    @Test
    public void testFoldr() {
        final ArrayList<Integer> originalData = new ArrayList<>(Arrays.asList(3, 4, 7, 9, 3));

        Function2<Object, Object, Integer> sum = new Function2<Object, Object, Integer>() {
            @Override
            public Integer apply(Object x, Object z) {
                return (Integer) x + (Integer) z;
            }
        };

        Number s = Collections.foldr(sum, 0, originalData);

        Integer answer = 0;
        for (Integer x : originalData) {
            answer += x;
        }

        assertEquals(s, answer);
    }
}
