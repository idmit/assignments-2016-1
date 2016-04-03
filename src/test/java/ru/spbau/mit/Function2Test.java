package ru.spbau.mit;

import static org.junit.Assert.*;

import org.junit.Test;

public class Function2Test {
    static final Function2<String, Integer, String> REPEAT_STRING = new Function2<String, Integer, String>() {
        @Override
        public String apply(String s, Integer x) {
            return new String(new char[x]).replace("\0", s);
        }
    };

    @Test
    public void testCompose() {
        Function1<Object, String> objToUpper = new Function1<Object, String>() {
            @Override
            public String apply(Object x) {
                String s = (String) x;
                return s.toUpperCase();
            }
        };

        Function2<String, Integer, String> composition = REPEAT_STRING.compose(objToUpper);

        String template = "Ax";
        final int times = 2;
        String answer = "AXAX";

        assertEquals(answer, composition.apply(template, times));
    }

    @Test
    public void testBind1() {
        String template = "Ax";

        Function1<Integer, String> repeatTemplate = REPEAT_STRING.bind1(template);

        final int times = 2;
        String answer = "AxAx";

        assertEquals(answer, repeatTemplate.apply(times));
    }

    @Test
    public void testBind2() {
        final int times = 2;

        Function1<String, String> repeatTimes = REPEAT_STRING.bind2(times);

        String template = "Ax";
        String answer = "AxAx";

        assertEquals(answer, repeatTimes.apply(template));
    }

    @Test
    public void testCurry() {
        Function1<String, Function1<Integer, String>> curried = REPEAT_STRING.curry();

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
