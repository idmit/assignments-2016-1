package ru.spbau.mit;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CollectionsTest {
    @Test
    public void testMap() {
        final List<Integer> originalData = new ArrayList<>(Arrays.asList(2, 4, 5, 7));

        Function1<Integer, Integer> timesTwo = new Function1<Integer, Integer>() {
            @Override
            public Integer apply(Integer x) {
                return x + x;
            }
        };

        Iterable<Integer> transformedData = Collections.map(timesTwo, originalData);

        final List<Integer> correctlyTransformedData = new ArrayList<>(Arrays.asList(4, 8, 10, 14));

        assertEquals(transformedData, correctlyTransformedData);
    }

    @Test
    public void testFilter() {
        final List<Integer> originalData = new ArrayList<>(Arrays.asList(2, 5, 4, 7));

        Predicate<Integer> isEven = new Predicate<Integer>() {
            @Override
            public Boolean apply(Integer x) {
                return x % 2 == 0;
            }
        };

        Iterable<Integer> filteredData = Collections.filter(isEven, originalData);

        final List<Integer> correctlyFilteredData = new ArrayList<>(Arrays.asList(2, 4));

        assertEquals(filteredData, correctlyFilteredData);
    }

    @Test
    public void testTakeWhile() {
        final List<Integer> originalData = new ArrayList<>(Arrays.asList(2, 0, 5, 4, 7));

        Predicate<Integer> isEven = new Predicate<Integer>() {
            @Override
            public Boolean apply(Integer x) {
                return x % 2 == 0;
            }
        };

        Iterable<Integer> shortenedData = Collections.takeWhile(isEven, originalData);

        final List<Integer> correctlyShortenedData = new ArrayList<>(Arrays.asList(2, 0));

        assertEquals(shortenedData, correctlyShortenedData);
    }

    @Test
    public void testTakeUnless() {
        final List<Integer> originalData = new ArrayList<>(Arrays.asList(3, 4, 7, 9, 0));

        Predicate<Integer> isEven = new Predicate<Integer>() {
            @Override
            public Boolean apply(Integer x) {
                return x % 2 == 0;
            }
        };

        Iterable<Integer> shortenedData = Collections.takeUnless(isEven, originalData);

        final List<Integer> correctlyShortenedData = new ArrayList<>(Arrays.asList(3));

        assertEquals(shortenedData, correctlyShortenedData);
    }

    @Test
    public void testFoldl() {
        final List<Integer> originalData = new ArrayList<>(Arrays.asList(3, 4, 7, 9));

        Function2<Object, Object, Integer> diff = new Function2<Object, Object, Integer>() {
            @Override
            public Integer apply(Object x, Object y) {
                return (Integer) x - (Integer) y;
            }
        };

        Number s = Collections.foldl(diff, 0, originalData);

        Integer answer = -23;

        assertEquals(s, answer);
    }

    @Test
    public void testFoldr() {
        final List<Integer> originalData = new ArrayList<>(Arrays.asList(3, 4, 7, 9));

        Function2<Object, Object, Integer> diff = new Function2<Object, Object, Integer>() {
            @Override
            public Integer apply(Object x, Object y) {
                return (Integer) x - (Integer) y;
            }
        };

        Number s = Collections.foldr(diff, 0, originalData);

        Integer answer = -3;

        assertEquals(s, answer);
    }

    @Test
    public void testFancyMap() {
        final List<Integer> originalData = new ArrayList<>(Arrays.asList(2, 4, 5, 7));

        Function1<Integer, Integer> timesTwo = new Function1<Integer, Integer>() {
            @Override
            public Integer apply(Integer x) {
                return x + x;
            }
        };

        // It's possible to widen return type from Integer to Number.
        // Such operation requires method to have explicit parameters.
        // This feature can be useful and doesn't interfere with straightforward map.
        Iterable<Number> transformedNumData = Collections.<Integer, Number>map(timesTwo, originalData);

        final List<Integer> correctlyTransformedData = new ArrayList<>(Arrays.asList(4, 8, 10, 14));

        assertEquals(transformedNumData, correctlyTransformedData);
    }
}
