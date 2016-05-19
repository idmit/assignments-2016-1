package ru.spbau.mit;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class ExecutorServiceCheckSumComputer implements CheckSumComputer {
    private ExecutorService threadPool = Executors.newCachedThreadPool();

    @Override
    public String computeCheckSum(Path path) throws IOException, NoSuchAlgorithmException {
        byte[] bytes;

        if (Files.isDirectory(path)) {
            List<Future<String>> futures = new ArrayList<>();

            List<Path> paths = Files.list(path).collect(Collectors.toList());

            for (Path p : paths) {
                futures.add(threadPool.submit(() -> {
                    String checkSum = null;
                    try {
                        checkSum = computeCheckSum(p);
                    } catch (IOException | NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    return checkSum;
                }));
            }

            String partialString = path.toString();

            for (Future<String> future : futures) {
                try {
                    partialString += future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            bytes = partialString.getBytes(Charset.forName("UTF-8"));
        } else {
            bytes = Main.contentToByteArray(path);
        }

        MessageDigest md = MessageDigest.getInstance("MD5");
        return Main.toHexString(md.digest(bytes));
    }
}
