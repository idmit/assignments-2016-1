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

        assertEquals(conjunction.apply(oddNumber), false);
        assertEquals(sideEffect[0], false);

        sideEffect[0] = false;

        assertEquals(conjunction.apply(otherEvenNumber), false);
        assertEquals(sideEffect[0], true);

        sideEffect[0] = false;

        assertEquals(conjunction.apply(evenNumber), true);
        assertEquals(sideEffect[0], true);
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

        assertEquals(disjunction.apply(oddNumber), true);
        assertEquals(sideEffect[0], false);

        sideEffect[0] = false;

        assertEquals(disjunction.apply(otherEvenNumber), false);
        assertEquals(sideEffect[0], true);

        sideEffect[0] = false;

        assertEquals(disjunction.apply(evenNumber), true);
        assertEquals(sideEffect[0], true);
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
        assertEquals(negatedEven.apply(evenNumber), false);

        final int oddNumber = 5;
        assertEquals(negatedEven.apply(oddNumber), true);
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
