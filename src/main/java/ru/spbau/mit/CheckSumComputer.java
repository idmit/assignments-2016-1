package ru.spbau.mit;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

interface CheckSumComputer {
    String computeCheckSum(Path path) throws IOException, NoSuchAlgorithmException;
}
