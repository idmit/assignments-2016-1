package ru.spbau.mit;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class Main {
    static String toHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();

        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xFF & aByte);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }

    static byte[] contentToByteArray(Path path) throws IOException {
        InputStream is = new BufferedInputStream(Files.newInputStream(path));

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int readBytesCount;
        byte[] readBytes = new byte[1024];

        while ((readBytesCount = is.read(readBytes, 0, readBytes.length)) != -1) {
            buffer.write(readBytes, 0, readBytesCount);
        }

        return buffer.toByteArray();
    }

    public static void main(String[] args) {
        List<CheckSumComputer> checkSumComputers = new LinkedList<>();
        checkSumComputers.add(new SingleThreadCheckSumComputer());
        checkSumComputers.add(new ForkJoinCheckSumComputer());
    }
}

