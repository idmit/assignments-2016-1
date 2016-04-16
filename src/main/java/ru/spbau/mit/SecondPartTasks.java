package ru.spbau.mit;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.*;

public final class SecondPartTasks {
    private static final int NUMBER_OF_SHOTS = 5000;

    private SecondPartTasks() {
    }

    // Найти строки из переданных файлов, в которых встречается указанная подстрока.
    public static List<String> findQuotes(List<String> paths, CharSequence sequence) {
        Stream<String> allLines = paths.stream().flatMap(f -> {
            Stream<String> lines;
            try {
                lines = Files.lines(Paths.get(f));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
            return lines;
        });

        return allLines.filter(l -> l.contains(sequence)).collect(Collectors.toList());
    }

    // В квадрат с длиной стороны 1 вписана мишень.
    // Стрелок атакует мишень и каждый раз попадает в произвольную точку квадрата.
    // Надо промоделировать этот процесс с помощью класса java.util.Random и посчитать,
    // какова вероятность попасть в мишень.
    public static double piDividedBy4() {
        final double min = 0;
        final double max = 1;
        final double center = 0.5;
        final double squaredRad = 0.25;

        Supplier<Double> shotSupplier = () -> ThreadLocalRandom.current().nextDouble(min, max);

        Stream<Boolean> shotResults = Stream.generate(() -> {
            double x = shotSupplier.get();
            double y = shotSupplier.get();
            return (x - center) * (x - center) + (y - center) * (y - center) < squaredRad;
        });

        double numberOfHits = shotResults.limit(NUMBER_OF_SHOTS).filter(x -> x).count();

        return numberOfHits / NUMBER_OF_SHOTS;
    }

    // Дано отображение из имени автора в список с содержанием его произведений.
    // Надо вычислить, чья общая длина произведений наибольшая.
    public static String findPrinter(Map<String, List<String>> compositions) {
        // Use `get` on Option, because operation should fail if map is empty.
        return compositions.entrySet().stream()
                .max(Comparator.comparing(p -> p.getValue().stream()
                        .mapToInt(String::length).sum()
                )).get().getKey();
    }

    // Вы крупный поставщик продуктов. Каждая торговая сеть делает вам заказ в виде Map<Товар, Количество>.
    // Необходимо вычислить, какой товар и в каком количестве надо поставить.
    public static Map<String, Integer> calculateGlobalOrder(List<Map<String, Integer>> orders) {
        Stream<Map.Entry<String, Integer>> totalStream = orders.stream().flatMap(x -> x.entrySet().stream());

        return totalStream.collect(Collectors.groupingBy(Map.Entry::getKey,
                Collectors.collectingAndThen(Collectors.toList(), x -> x.stream().mapToInt(Map.Entry::getValue)
                        .sum())));
    }
}
