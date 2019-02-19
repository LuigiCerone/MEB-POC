package Threads;


import Main.TestingUnit;

import javax.swing.*;

// Thread used for display statistics when the Virtual Machine exit() is called.
public class ShutDownThread extends Thread {
    long startTime;
    TestingUnit testingUnit;

    public ShutDownThread(long startTime, TestingUnit testingUnit) {
        this.testingUnit = testingUnit;
        this.startTime = startTime;
    }

    @Override
    public void run() {
        JTextArea output = this.testingUnit.getTextArea();
        long endTime = System.currentTimeMillis();
        int totalRequestsNumber = this.testingUnit.getTotalRequestsNumber();
        System.out.println("I've run for : " + (endTime - startTime) + "ms and I've done: " + totalRequestsNumber + " requests.");
        output.append("I've run for : " + (endTime - startTime) + "ms and I've done: " + totalRequestsNumber + " requests.");
    }
}