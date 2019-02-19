package Main;

import Model.DatabaseFabData;
import Model.DatabaseRawData;
import Model.Tool;
import Threads.FabDataInsertThread;
import Threads.RawDataInsertThread;
import Threads.ShutDownThread;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestingUnit extends JFrame {
    ArrayList<Tool> tools;

    private int TOOLS_NUMBER;
    private long pause;

    public static int totalRequestsNumber = 0;
    static long startTime = 0;

    static boolean userWants = true;

    private Utils utils = new Utils();

    private final static JTextArea outputArea = new JTextArea(10, 30);
    private String FILE_NAME = "data.txt";


    public static void main(String[] args) {
        final TestingUnit testingUnit = new TestingUnit();

        JButton STARTButton = new JButton("Start");
        JButton STOPButton = new JButton("Stop");


        final JTextField toolNumber = new JTextField("100", 6);
        final JTextField pauseSize = new JTextField("1000", 6);


        JFrame frame = new JFrame("Testing unit");
//        frame.setSize(400, 400);
        frame.setResizable(false);

        Container frameContentPane = frame.getContentPane();
        frameContentPane.setLayout(new BoxLayout(frameContentPane, BoxLayout.Y_AXIS));

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout());
        buttonsPanel.add(STARTButton);
        buttonsPanel.add(STOPButton);

        final JCheckBox checkBoxReuse = new JCheckBox("Reuse", true);
        buttonsPanel.add(checkBoxReuse);

        final JCheckBox checkBoxOnlyRaw = new JCheckBox("Only raw_data", false);
        buttonsPanel.add(checkBoxOnlyRaw);

        frameContentPane.add(buttonsPanel);

        // ==================================================================0

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

        JLabel robotNumberLabel = new JLabel("Tools number: ");
        inputPanel.add(robotNumberLabel);
        inputPanel.add(toolNumber);

        JLabel pauseSizeLabel = new JLabel("Pause size: ");
        inputPanel.add(pauseSizeLabel);
        inputPanel.add(pauseSize);
        frameContentPane.add(inputPanel);

        // ==================================================================

        JPanel logPanel = new JPanel();
        logPanel.setLayout(new FlowLayout());
        JLabel infoLabel = new JLabel("Info: ");
        logPanel.add(infoLabel);
        frameContentPane.add(logPanel);

        // ==================================================================


        outputArea.setEditable(false);
        frameContentPane.add(outputArea);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        STARTButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Number of tools: " + toolNumber.getText() + ", with pause size: " + pauseSize.getText());
                outputArea.append("Number of tools: " + toolNumber.getText() + ", with pause size: " + pauseSize.getText() + "\n");
                userWants = true;

                totalRequestsNumber = 0;
                new Thread() {
                    public void run() {
                        testingUnit.run(toolNumber.getText(), pauseSize.getText(), checkBoxReuse.isSelected(), checkBoxOnlyRaw.isSelected());
                    }
                }.start();
            }
        });

        STOPButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long endTime = System.currentTimeMillis();
                userWants = false;
                System.out.println("Requests: " + totalRequestsNumber);

                outputArea.append(" " + totalRequestsNumber + " requests made in " + (endTime - startTime) + " ms.");

                System.exit(0);
//                userWants = false;
            }
        });
    }

    private void run(String toolNumber, String pauseSize, boolean reuse, boolean onlyRaw) {

        try {
            this.pause = Long.parseLong(pauseSize);
        } catch (NumberFormatException e) {
            this.pause = 1000;
        }

        try {
            this.TOOLS_NUMBER = Integer.parseInt(toolNumber);
        } catch (Exception e) {
            this.TOOLS_NUMBER = 1000;
        }


        // Check if user wants to reuse the previous data.
        if (reuse) {
            readStoredInformation();
        } else {
            generateFakeData(toolNumber, pauseSize);
        }

        // Check if user wants to only simulate raw_data.
        if (onlyRaw) {
            System.out.println("Simulating only raw_data.");
            outputArea.append("Simulating only raw_data.\n");

            DatabaseRawData databaseRawData = new DatabaseRawData();
            for (Tool tool : tools) {
                utils.generateAndStoreTranslation(tool, 0, databaseRawData, false);
                utils.generateAndStoreTranslation(tool, 1, databaseRawData, false);
                utils.generateAndStoreTranslation(tool, 2, databaseRawData, false);
            }

            System.out.println("Done.");
            outputArea.append("Done.\n");

            System.exit(0);
        }

        startTime = System.currentTimeMillis();

        Runtime.getRuntime().addShutdownHook(new ShutDownThread(startTime, this));


        ExecutorService executor = Executors.newFixedThreadPool(2);

        System.out.println("Execution started.");
        outputArea.append("Execution started.\n");

        // Fab data threads.
        executor.submit(new Runnable() {
            public void run() {
                // Pool for the threads that will store data into fab_data.
                System.out.println("FabDataInsertThread started");

                while (userWants) {
                    // Uncomment to see when one raw is inserted.
//                    System.out.println("FabDataThread inserted a raw");
                    ExecutorService pool = Executors.newFixedThreadPool(4);
                    DatabaseFabData databaseFabData = new DatabaseFabData();

//                    System.out.println(pause);

                    Random random = new Random();
                    int index = random.nextInt((TOOLS_NUMBER - 1) + 1);
                    Tool currTool = tools.get(index);

                    Connection connection = databaseFabData.getConnection();

                    Runnable worker = new FabDataInsertThread(currTool, connection);
//                    worker.run();
                    pool.execute(worker); //calling execute method of ExecutorService

                    totalRequestsNumber++;

                    try {
                        Thread.sleep(pause);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // Raw data thread.
        Runnable rawDataThread = new RawDataInsertThread(tools);
        System.out.println("RawDataInsertThread started");

        executor.submit(rawDataThread);


    }

    private void readStoredInformation() {

        this.tools = read(FILE_NAME);
        if (this.tools != null)
            outputArea.append("Information read.\n");
    }

    private void generateFakeData(String toolNumber, String pauseSize) {
        Random random = new Random();

        // Create an array of fake Robots.
        tools = new ArrayList<Tool>(TOOLS_NUMBER);

        int RECIPES_NUMBER = 0;
        if (TOOLS_NUMBER / 10 <= 3) {
            RECIPES_NUMBER = random.nextInt(10);
        } else {
            RECIPES_NUMBER = TOOLS_NUMBER / 10;
        }

        int STEP_NUMBER = 0;
        if (RECIPES_NUMBER / 10 <= 5) {
            STEP_NUMBER = random.nextInt(10);
        } else {
            STEP_NUMBER = RECIPES_NUMBER / 10;
        }

        ArrayList<String> recipes = utils.createFakeData(RECIPES_NUMBER);
        ArrayList<String> steps = utils.createFakeData(STEP_NUMBER);


        for (int i = 0; i < TOOLS_NUMBER; i++) {
            tools.add(utils.createFakeRobot(recipes.get(Math.abs(random.nextInt(recipes.size()))), steps.get(Math.abs(random.nextInt(steps.size())))));
        }

        save();

        System.out.println("Array of fake robots has been created and stored.");
        outputArea.append("Array of fake robots has been created and stored.\n");
    }

    public int getTotalRequestsNumber() {
        return totalRequestsNumber;
    }

    public JTextArea getTextArea() {
        return outputArea;
    }

    public boolean save() {

        try {

            // Write objects to file.
            Gson gson = new Gson();
            String result = gson.toJson(tools);
            FileUtils.writeStringToFile(new File(FILE_NAME), result, Charset.defaultCharset());

        } catch (IOException e) {
            System.out.println("Error while storing: " + e);
            outputArea.append("Error while storing: " + e + "\n");
            return false;
        }
        return true;
    }

    public ArrayList<Tool> read(String fileName) {
        System.out.println("Reading from file: " + FILE_NAME);
        outputArea.append("Reading from file: " + FILE_NAME + "\n");
        ArrayList tools;

        try {

            String input = FileUtils.readFileToString(new File(fileName), Charset.defaultCharset());
            Gson gson = new Gson();
            tools = gson.fromJson(input, new TypeToken<ArrayList<Tool>>() {
            }.getType());

        } catch (Exception e) {
            System.out.println("Error while reading from file: " + e);
            outputArea.append("Error while reading from file: " + e + "\n");
            return null;
        }
        return tools;
    }
}
