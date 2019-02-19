package Threads;

import Main.Utils;
import Model.DatabaseRawData;
import Model.Tool;

import java.util.ArrayList;
import java.util.Random;

public class RawDataInsertThread implements Runnable {
    private int MAX_DELAY = 10000; // 10 sec.

    // Each char is 1 byte in SQL, so 10MB = 10 millions of chars.
    // 8MB instead of 10MB because 8MB is the max default value allowed (without changing max_allowed_packet).
//    private int SIZE = 8000000;
    public static int SIZE = 2000000;

    private ArrayList<Tool> tools;
    private DatabaseRawData databaseRawData;
    private Utils utils;

    public RawDataInsertThread(ArrayList<Tool> tools) {
        this.tools = tools;
        this.databaseRawData = new DatabaseRawData();
        this.utils = new Utils();
    }

    @Override
    public void run() {

        while (true) {
            Random random = new Random();

            int index = random.nextInt((tools.size() - 1) + 1);
            Tool currTool = tools.get(index);

            int whichField = random.nextInt(3);
            utils.generateAndStoreTranslation(currTool, whichField, databaseRawData, true);

            try {
                Thread.sleep(random.nextInt(MAX_DELAY - 1) + 1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
