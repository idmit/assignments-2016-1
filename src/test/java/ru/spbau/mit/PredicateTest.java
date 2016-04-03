package ru.spbau.mit;

import static org.junit.Assert.*;

import org.junit.Test;

public class PredicateTest {
    static final int SIXTEEN = 16;
    static final int ODD_INT = 11;
    static final int EVEN_INT = 22; // not 16

    static final Predicate<Integer> IS_EVEN = new Predicate<Integer>() {
        @Override
        public Boolean apply(Integer x) {
            return x % 2 == 0;
        }
    };

    static final Predicate<Integer> IS_ODD = new Predicate<Integer>() {
        @Override
        public Boolean apply(Integer x) {
            return x % 2 == 1;
        }
    };

    static final Predicate<Object> IS_SIXTEEN = new Predicate<Object>() {
        @Override
        public Boolean apply(Object x) {
            Integer in = (Integer) x;
            return in.equals(SIXTEEN);
        }
    };

    static final Predicate<Object> ALWAYS_THROWS = new Predicate<Object>() {
        @Override
        public Boolean apply(Object x) {
            throw new RuntimeException("Exception throwing predicate was applied.");
        }
    };

    @Test
    public void testAnd() {
        Predicate<Integer> conjunction = IS_EVEN.and(IS_SIXTEEN);

        assertEquals(false, conjunction.apply(ODD_INT));
        assertEquals(false, conjunction.apply(EVEN_INT));
        assertEquals(true, conjunction.apply(SIXTEEN));
    }

    @Test
    public void testAndShortCircuits() {
        Predicate<Integer> conjunction = IS_EVEN.and(ALWAYS_THROWS);

        assertEquals(false, conjunction.apply(ODD_INT));
    }

    @Test(expected = RuntimeException.class)
    public void testAndEvaluatesAll() {
        Predicate<Integer> conjunction = IS_EVEN.and(ALWAYS_THROWS);

        assertEquals(false, conjunction.apply(EVEN_INT));
    }

    @Test
    public void testOr() {
        Predicate<Integer> disjunction = IS_ODD.or(IS_SIXTEEN);

        assertEquals(true, disjunction.apply(ODD_INT));
        assertEquals(false, disjunction.apply(EVEN_INT));
        assertEquals(true, disjunction.apply(SIXTEEN));
    }

    @Test
    public void testOrShortCircuits() {
        Predicate<Integer> disjunction = IS_ODD.or(ALWAYS_THROWS);

        assertEquals(true, disjunction.apply(ODD_INT));
    }

    @Test(expected = RuntimeException.class)
    public void testOrEvaluatesAll() {
        Predicate<Integer> disjunction = IS_ODD.or(ALWAYS_THROWS);

        assertEquals(false, disjunction.apply(EVEN_INT));
    }

    @Test
    public void testNot() {
        Predicate<Integer> negatedEven = IS_EVEN.not();

        assertEquals(false, negatedEven.apply(EVEN_INT));
        assertEquals(true, negatedEven.apply(ODD_INT));
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
