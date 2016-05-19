package ru.spbau.mit;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class CheckSumTest {
    @Test
    public void testCorrectness() throws IOException, NoSuchAlgorithmException {
        List<CheckSumComputer> checkSumComputers = new LinkedList<>();
        checkSumComputers.add(new SingleThreadCheckSumComputer());
        checkSumComputers.add(new ForkJoinCheckSumComputer());
        checkSumComputers.add(new ExecutorServiceCheckSumComputer());

        URL url = this.getClass().getResource("/testDir");
        File file = new File(url.getFile());

        Path path = file.toPath();

        List<String> results = new LinkedList<>();
        for (CheckSumComputer csc : checkSumComputers) {
            results.add(csc.computeCheckSum(path));
        }

        assertEquals(results.stream().distinct().count(), 1);
    }

    @Test
    public void naiveBenchmark() throws IOException, NoSuchAlgorithmException {
        List<CheckSumComputer> checkSumComputers = new LinkedList<>();
        checkSumComputers.add(new SingleThreadCheckSumComputer());
        checkSumComputers.add(new ForkJoinCheckSumComputer());
        checkSumComputers.add(new ExecutorServiceCheckSumComputer());

        URL url = this.getClass().getResource("/testDir");
        File file = new File(url.getFile());

        Path path = file.toPath();

        for (CheckSumComputer csc : checkSumComputers) {
            long start = System.nanoTime();
            csc.computeCheckSum(path);
            long end = System.nanoTime();
            long nanoTime = end - start;
            System.out.println(csc.getClass().getSimpleName() + " --- " + (nanoTime / 1000000) + "ms.");
            System.out.println("----------");
        }
    }
}
