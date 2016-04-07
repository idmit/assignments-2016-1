package ru.spbau.mit;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SecondPartTasksTest {

    @Test
    public void testFindQuotes() {
        final String prefix = "src/test/resources/";
        final List<String> paths = Arrays.asList(prefix + "poem1.txt", prefix + "poem2.txt", prefix + "poem3.txt");

        final List<String> canExpected = Arrays.asList(
                "Hoping one day I can smile,",
                "you can always make me smile. ",
                "When you can't find the words",
                "When you cannot see",
                "When you cannot hear",
                "I can't promise you the world",
                "I can't promise you the sky",
                "I can't promise you that we will never fight",
                "I can't promise you that I will never cry",
                "But I can promise you that I will always be true to you"
        );

        assertEquals(canExpected, SecondPartTasks.findQuotes(paths, "can"));

        final List<String> forExpected = Arrays.asList(
                "Having nobody to comfort me,",
                "Before long, I had many friends;",
                "And behind all the comfort were the fears.",
                "I felt like I had known you forever, ",
                "Will it ever really be forever? ",
                "the feelings I feel for you ",
                "I promise to always care for you"
        );

        assertEquals(forExpected, SecondPartTasks.findQuotes(paths, "for"));
    }

    @Test
    public void testPiDividedBy4() {
        assertEquals(Math.PI / 4, SecondPartTasks.piDividedBy4(), 0.01);
    }

    @Test
    public void testFindPrinter() {
        Map<String, List<String>> map = new HashMap<>();
        final List<String> x = Arrays.asList("XXX", "YYY", "ZZZ");
        final List<String> y = Arrays.asList("XXX", "YYY", "ZZZ", "A");
        final List<String> z = Arrays.asList("X", "Y", "Z");

        map.put("x", x);
        map.put("y", y);
        map.put("z", z);

        assertEquals("y", SecondPartTasks.findPrinter(map));

        final List<String> a = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K");

        map.put("a", a);

        assertEquals("a", SecondPartTasks.findPrinter(map));
    }

    @Test
    public void testCalculateGlobalOrder() {
        Map<String, Integer> order1 = new HashMap<>();
        Map<String, Integer> order2 = new HashMap<>();
        Map<String, Integer> order3 = new HashMap<>();

        order1.put("orange", 3);
        order1.put("bread", 5);
        order1.put("milk", 7);

        order2.put("meat", 8);
        order2.put("bread", 12);
        order2.put("chicken", 10);
        order2.put("pepper", 6);

        order3.put("salt", 20);
        order3.put("pepper", 19);
        order3.put("orange", 9);

        List<Map<String, Integer>> orders = Arrays.asList(order1, order2, order3);

        Map<String, Integer> globalOrder = SecondPartTasks.calculateGlobalOrder(orders);

        assertEquals((Integer) 12, globalOrder.get("orange"));
        assertEquals((Integer) 17, globalOrder.get("bread"));
        assertEquals((Integer) 7, globalOrder.get("milk"));
        assertEquals((Integer) 8, globalOrder.get("meat"));
        assertEquals((Integer) 10, globalOrder.get("chicken"));
        assertEquals((Integer) 25, globalOrder.get("pepper"));
        assertEquals((Integer) 20, globalOrder.get("salt"));
    }
}
