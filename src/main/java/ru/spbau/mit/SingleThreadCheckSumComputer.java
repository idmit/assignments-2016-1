package ru.spbau.mit;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

public class SingleThreadCheckSumComputer implements CheckSumComputer {

    @Override
    public String computeCheckSum(Path path) throws IOException, NoSuchAlgorithmException {
        return computeCheckSumRecursively(path);
    }

    private String computeCheckSumRecursively(Path path) throws IOException,
            NoSuchAlgorithmException {
        byte[] bytes;

        if (Files.isDirectory(path)) {

            String partialString = path.toString();

            List<Path> paths = Files.list(path).collect(Collectors.toList());
            for (Path p : paths) {
                partialString += computeCheckSumRecursively(p);
            }

            bytes = partialString.getBytes(Charset.forName("UTF-8"));
        } else {
            bytes = Main.contentToByteArray(path);
        }

        MessageDigest md = MessageDigest.getInstance("MD5");
        return Main.toHexString(md.digest(bytes));
    }
}
