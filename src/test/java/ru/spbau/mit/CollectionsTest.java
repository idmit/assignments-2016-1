package ru.spbau.mit;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class CollectionsTest {
    static final Predicate<Integer> IS_EVEN = new Predicate<Integer>() {
        @Override
        public Boolean apply(Integer x) {
            return x % 2 == 0;
        }
    };

    static final Function2<Object, Object, Integer> DIFF = new Function2<Object, Object, Integer>() {
        @Override
        public Integer apply(Object x, Object y) {
            return (Integer) x - (Integer) y;
        }
    };

    @Test
    public void testMap() {
        final List<Integer> originalData = Arrays.asList(2, 4, 5, 7);

        Function1<Integer, Integer> timesTwo = new Function1<Integer, Integer>() {
            @Override
            public Integer apply(Integer x) {
                return x + x;
            }
        };

        Iterable<Integer> transformedData = Collections.map(timesTwo, originalData);

        final List<Integer> correctlyTransformedData = Arrays.asList(4, 8, 10, 14);

        assertEquals(correctlyTransformedData, transformedData);
    }

    @Test
    public void testFilter() {
        final List<Integer> originalData = Arrays.asList(2, 5, 4, 7);

        Iterable<Integer> filteredData = Collections.filter(IS_EVEN, originalData);

        final List<Integer> correctlyFilteredData = Arrays.asList(2, 4);

        assertEquals(correctlyFilteredData, filteredData);
    }

    @Test
    public void testTakeWhile() {
        final List<Integer> originalData = Arrays.asList(2, 0, 5, 4, 7);

        Iterable<Integer> shortenedData = Collections.takeWhile(IS_EVEN, originalData);

        final List<Integer> correctlyShortenedData = Arrays.asList(2, 0);

        assertEquals(correctlyShortenedData, shortenedData);
    }

    @Test
    public void testTakeUnless() {
        final List<Integer> originalData = Arrays.asList(3, 4, 7, 9, 0);

        Iterable<Integer> shortenedData = Collections.takeUnless(IS_EVEN, originalData);

        final List<Integer> correctlyShortenedData = Arrays.asList(3);

        assertEquals(correctlyShortenedData, shortenedData);
    }

    @Test
    public void testFoldl() {
        final List<Integer> originalData = Arrays.asList(3, 4, 7, 9);

        Number s = Collections.foldl(DIFF, 0, originalData);

        final Integer answer = -23;

        assertEquals(answer, s);
    }

    @Test
    public void testFoldr() {
        final List<Integer> originalData = Arrays.asList(3, 4, 7, 9);

        Number s = Collections.foldr(DIFF, 0, originalData);

        final Integer answer = -3;

        assertEquals(answer, s);
    }

    @Test
    public void testFancyMap() {
        final List<Integer> originalData = Arrays.asList(2, 4, 5, 7);

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

        final List<Integer> correctlyTransformedData = Arrays.asList(4, 8, 10, 14);

        assertEquals(correctlyTransformedData, transformedNumData);
    }
}
