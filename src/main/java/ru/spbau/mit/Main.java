package ru.spbau.mit;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

final class Main {
    private Main() {
    }

    static String toHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();

        for (byte aByte : bytes) {
            final int mask = 0xFF;
            String hex = Integer.toHexString(mask & aByte);
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
        final int bytesPerRead = 1024;
        byte[] readBytes = new byte[bytesPerRead];

        while ((readBytesCount = is.read(readBytes, 0, readBytes.length)) != -1) {
            buffer.write(readBytes, 0, readBytesCount);
        }

        return buffer.toByteArray();
    }
}

