package ru.spbau.mit;

import static org.junit.Assert.*;

import org.junit.Test;

public class FunctionalTest {

    @Test
    public void testFunction1Compose() {
        Function1<Integer, Integer> timesTwo = new Function1<Integer, Integer>() {
            @Override
            public Integer apply(Integer x) {
                return x + x;
            }
        };

        Function1<Object, Integer> square = new Function1<Object, Integer>() {
            @Override
            public Integer apply(Object x) {
                Integer in = (Integer) x;
                return in * in;
            }
        };

        Function1<Integer, Integer> composition = timesTwo.compose(square);

        final int arg = 5;
        final int answer = (arg + arg) * (arg + arg);

        assertEquals(composition.apply(arg).intValue(), answer);
    }

    @Test
    public void testFunction2Compose() {
        Function2<String, Integer, String> repeatString = new Function2<String, Integer, String>() {
            @Override
            public String apply(String s, Integer x) {
                return new String(new char[x]).replace("\0", s);
            }
        };

        Function1<Object, String> objToUpper = new Function1<Object, String>() {
            @Override
            public String apply(Object x) {
                String s = (String) x;
                return s.toUpperCase();
            }
        };

        Function2<String, Integer, String> composition = repeatString.compose(objToUpper);

        String template = "Ax";
        final int times = 2;
        String answer = (new String(new char[times]).replace("\0", template)).toUpperCase();

        assertEquals(composition.apply(template, times), answer);
    }

    @Test
    public void testFunction2Bind1() {
        Function2<String, Integer, String> repeat = new Function2<String, Integer, String>() {
            @Override
            public String apply(String s, Integer x) {
                return new String(new char[x]).replace("\0", s);
            }
        };

        String template = "Ax";

        Function1<Integer, String> repeatTemplate = repeat.bind1(template);

        final int times = 2;
        String answer = new String(new char[times]).replace("\0", template);

        assertEquals(repeatTemplate.apply(times), answer);
    }

    @Test
    public void testFunction2Bind2() {
        Function2<String, Integer, String> repeat = new Function2<String, Integer, String>() {
            @Override
            public String apply(String s, Integer x) {
                return new String(new char[x]).replace("\0", s);
            }
        };

        final int times = 2;

        Function1<String, String> repeatTimes = repeat.bind2(times);

        String template = "Ax";
        String answer = new String(new char[times]).replace("\0", template);

        assertEquals(repeatTimes.apply(template), answer);
    }

    @Test
    public void testFunction2Curry() {
        Function2<String, Integer, String> repeat = new Function2<String, Integer, String>() {
            @Override
            public String apply(String s, Integer x) {
                return new String(new char[x]).replace("\0", s);
            }
        };

        Function1<String, Function1<Integer, String>> curried = repeat.curry();

        String template = "Ax";
        final int times = 2;
        String answer = new String(new char[times]).replace("\0", template);

        assertEquals(curried.apply(template).apply(times), answer);
    }

    @Test
    public void testPredicateAnd() {
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

        assertEquals(conjunction.apply(oddNumber), oddNumber == evenNumber);
        assertEquals(sideEffect[0], oddNumber % 2 == 0);

        sideEffect[0] = false;

        assertEquals(conjunction.apply(otherEvenNumber), otherEvenNumber == evenNumber);
        assertEquals(sideEffect[0], otherEvenNumber % 2 == 0);

        sideEffect[0] = false;

        assertEquals(conjunction.apply(evenNumber), evenNumber == evenNumber);
        assertEquals(sideEffect[0], evenNumber % 2 == 0);
    }

    @Test
    public void testPredicateOr() {
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
        assertEquals(sideEffect[0], oddNumber % 2 == 0);

        sideEffect[0] = false;

        assertEquals(disjunction.apply(otherEvenNumber), false);
        assertEquals(sideEffect[0], otherEvenNumber % 2 == 0);

        sideEffect[0] = false;

        assertEquals(disjunction.apply(evenNumber), true);
        assertEquals(sideEffect[0], evenNumber % 2 == 0);
    }

    @Test
    public void testPredicateNot() {
        Predicate<Integer> isEven = new Predicate<Integer>() {
            @Override
            public Boolean apply(Integer x) {
                return x % 2 == 0;
            }
        };

        Predicate<Integer> negatedEven = isEven.not();

        final int evenNumber = 10;
        assertEquals(negatedEven.apply(evenNumber), evenNumber % 2 == 1);

        final int oddNumber = 5;
        assertEquals(negatedEven.apply(oddNumber), oddNumber % 2 == 1);
    }

    @Test
    public void testPredicateAlwaysTrue() {
        final int[] differentNumbers = {-1, 0, 1};
        for (int x : differentNumbers) {
            assertTrue(Predicate.ALWAYS_TRUE.apply(x));
        }
    }

    @Test
    public void testPredicateAlwaysFalse() {
        final int[] differentNumbers = {-1, 0, 1};
        for (int x : differentNumbers) {
            assertFalse(Predicate.ALWAYS_FALSE.apply(x));
        }
    }
}
