package ru.spbau.mit;

import static org.junit.Assert.*;

import org.junit.Test;

public class PredicateTest {
    @Test
    public void testAnd() {
        final boolean[] sideEffect = {false};

        Predicate<Integer> isEven = new Predicate<Integer>() {
            @Override
            public Boolean apply(Integer x) {
                return x % 2 == 0;
            }
        };

        final int evenNumber = 16;

        Predicate<Object> isParticularEvenNumberWithSideEffects = new Predicate<Object>() {
            @Override
            public Boolean apply(Object x) {
                sideEffect[0] = true;
                Integer in = (Integer) x;
                return in.equals(evenNumber);
            }
        };

        Predicate<Integer> conjunction = isEven.and(isParticularEvenNumberWithSideEffects);

        final int oddNumber = 11;
        final int otherEvenNumber = 20;

        assertEquals(false, conjunction.apply(oddNumber));
        assertEquals(false, sideEffect[0]);

        sideEffect[0] = false;

        assertEquals(false, conjunction.apply(otherEvenNumber));
        assertEquals(true, sideEffect[0]);

        sideEffect[0] = false;

        assertEquals(true, conjunction.apply(evenNumber));
        assertEquals(true, sideEffect[0]);
    }

    @Test
    public void testOr() {
        final boolean[] sideEffect = {false};

        Predicate<Integer> isOdd = new Predicate<Integer>() {
            @Override
            public Boolean apply(Integer x) {
                return x % 2 == 1;
            }
        };

        final int evenNumber = 10;

        Predicate<Object> isParticularEvenNumberWithSideEffects = new Predicate<Object>() {
            @Override
            public Boolean apply(Object x) {
                sideEffect[0] = true;
                Integer in = (Integer) x;
                return in.equals(evenNumber);
            }
        };

        Predicate<Integer> disjunction = isOdd.or(isParticularEvenNumberWithSideEffects);

        final int oddNumber = 11;
        final int otherEvenNumber = 20;

        assertEquals(true, disjunction.apply(oddNumber));
        assertEquals(false, sideEffect[0]);

        sideEffect[0] = false;

        assertEquals(false, disjunction.apply(otherEvenNumber));
        assertEquals(true, sideEffect[0]);

        sideEffect[0] = false;

        assertEquals(true, disjunction.apply(evenNumber));
        assertEquals(true, sideEffect[0]);
    }

    @Test
    public void testNot() {
        Predicate<Integer> isEven = new Predicate<Integer>() {
            @Override
            public Boolean apply(Integer x) {
                return x % 2 == 0;
            }
        };

        Predicate<Integer> negatedEven = isEven.not();

        final int evenNumber = 10;
        assertEquals(false, negatedEven.apply(evenNumber));

        final int oddNumber = 5;
        assertEquals(true, negatedEven.apply(oddNumber));
    }

    @Test
    public void testAlwaysTrue() {
        final int[] differentNumbers = {-1, 0, 1};
        for (int x : differentNumbers) {
            assertTrue(Predicate.ALWAYS_TRUE.apply(x));
        }
    }

    @Test
    public void testAlwaysFalse() {
        final int[] differentNumbers = {-1, 0, 1};
        for (int x : differentNumbers) {
            assertFalse(Predicate.ALWAYS_FALSE.apply(x));
        }
    }
}
