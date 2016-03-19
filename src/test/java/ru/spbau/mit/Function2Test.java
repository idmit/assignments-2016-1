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
        String answer = (new String(new char[times]).replace("\0", template)).toUpperCase();

        assertEquals(composition.apply(template, times), answer);
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
        String answer = new String(new char[times]).replace("\0", template);

        assertEquals(repeatTemplate.apply(times), answer);
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
        String answer = new String(new char[times]).replace("\0", template);

        assertEquals(repeatTimes.apply(template), answer);
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
        String answer = new String(new char[times]).replace("\0", template);

        assertEquals(curried.apply(template).apply(times), answer);
    }
}
