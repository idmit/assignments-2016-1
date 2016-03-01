package ru.spbau.mit;

import java.io.*;

/**
 * Created by idmit on 01/03/2016.
 */
public class CopyClone {
    private static int MAX_SIZE = 1000;

    public static void main(String[] args) {
        try {
            copyFile(args[0], args[1]);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static void copyFile(String source, String destination) throws IOException {

        byte[] buffer = new byte[MAX_SIZE];

        FileInputStream input = new FileInputStream(source);
        FileOutputStream output = new FileOutputStream(destination);

        try (BufferedInputStream buffInput = new BufferedInputStream(input);
             BufferedOutputStream buffOutput = new BufferedOutputStream(output);) {

            int numBytes = 0;
            while ((numBytes = buffInput.read(buffer)) != -1) {
                buffOutput.write(buffer, 0, numBytes);
            }
        }
    }
}
