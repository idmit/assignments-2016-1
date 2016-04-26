package ru.spbau.mit;

import java.io.*;

/**
 * Created by idmit on 01/03/2016.
 */
public class Main {
    public static void main(String[] args) {
        final int parties = 3;
        final Barrier b = new Barrier(parties);

        Thread x = new Thread(() -> {
            b.await();
            System.out.println("1 finished.");
        });

        Thread y = new Thread(() -> {
            b.await();
            System.out.println("2 finished.");
        });

        Thread z = new Thread(() -> {
            b.await();
            System.out.println("3 finished.");
        });

        x.start();
        y.start();

        try {
            Thread.currentThread().sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        z.start();
    }
}

