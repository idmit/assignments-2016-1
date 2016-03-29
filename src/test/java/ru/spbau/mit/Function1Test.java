package ru.spbau.mit;

import static org.junit.Assert.*;

import org.junit.Test;

public class Function1Test {
    @Test
    public void testCompose() {
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
        final int answer = 100;

        assertEquals(composition.apply(arg).intValue(), answer);
    }
}
