package ru.spbau.mit;


import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

public class ForkJoinCheckSumComputer implements CheckSumComputer {

    @Override
    public String computeCheckSum(Path path) throws IOException, NoSuchAlgorithmException {
        return new ForkJoinPool().invoke(new CheckSumTask(path));
    }

    private class CheckSumTask extends RecursiveTask<String> {
        private Path path;

        CheckSumTask(Path path) {
            this.path = path;
        }

        @Override
        protected String compute() {
            byte[] bytes = new byte[0];

            if (Files.isDirectory(path)) {
                List<CheckSumTask> tasks = new ArrayList<>();
                List<Path> paths;

                try {
                    paths = Files.list(path).collect(Collectors.toList());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                for (Path p : paths) {
                    CheckSumTask task = new CheckSumTask(p);
                    task.fork();
                    tasks.add(task);
                }

                String partialString = path.toString();
                for (CheckSumTask task : tasks) {
                    partialString += task.join();
                }

                bytes = partialString.getBytes(Charset.forName("UTF-8"));
            } else {
                try {
                    bytes = Main.contentToByteArray(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                return Main.toHexString(md.digest(bytes));
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
