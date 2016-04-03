package ru.spbau.mit;

import static org.junit.Assert.*;

import org.junit.Test;

public class Function2Test {
    @Test
    public void testCompose() {
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
        String answer = "AXAX";

        assertEquals(answer, composition.apply(template, times));
    }

    @Test
    public void testBind1() {
        Function2<String, Integer, String> repeat = new Function2<String, Integer, String>() {
            @Override
            public String apply(String s, Integer x) {
                return new String(new char[x]).replace("\0", s);
            }
        };

        String template = "Ax";

        Function1<Integer, String> repeatTemplate = repeat.bind1(template);

        final int times = 2;
        String answer = "AxAx";

        assertEquals(answer, repeatTemplate.apply(times));
    }

    @Test
    public void testBind2() {
        Function2<String, Integer, String> repeat = new Function2<String, Integer, String>() {
            @Override
            public String apply(String s, Integer x) {
                return new String(new char[x]).replace("\0", s);
            }
        };

        final int times = 2;

        Function1<String, String> repeatTimes = repeat.bind2(times);

        String template = "Ax";
        String answer = "AxAx";

        assertEquals(answer, repeatTimes.apply(template));
    }

    @Test
    public void testCurry() {
        Function2<String, Integer, String> repeat = new Function2<String, Integer, String>() {
            @Override
            public String apply(String s, Integer x) {
                return new String(new char[x]).replace("\0", s);
            }
        };

        Function1<String, Function1<Integer, String>> curried = repeat.curry();

        String template = "Ax";
        final int times = 2;
        String answer = "AxAx";

        assertEquals(answer, curried.apply(template).apply(times));
    }

    @Test
    public void testFancyCurry() {
        Function2<String, Number, String> repeat = new Function2<String, Number, String>() {
            @Override
            public String apply(String s, Number x) {
                return new String(new char[(Integer) x]).replace("\0", s);
            }
        };

        // It's possible to narrow type of second argument from Number to Integer.
        // This feature can be useful and doesn't interfere with straightforward currying.
        Function1<String, Function1<Integer, String>> curried = repeat.curry();

        String template = "Ax";
        final int times = 2;
        String answer = "AxAx";

        assertEquals(answer, curried.apply(template).apply(times));
    }
}
