package ru.spbau.mit;

/**
 * Created by idmit on 26/04/16.
 */
public class Barrier {

    private int parties;

    public Barrier(int parties) {
        this.parties = parties;
    }

    synchronized public void await() {
        if (parties > 1) {
            parties -= 1;
            while (parties > 0) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else if (parties == 1) {
            synchronized (this) {
                parties -= 1;
                this.notifyAll();
            }
        }
    }
}
