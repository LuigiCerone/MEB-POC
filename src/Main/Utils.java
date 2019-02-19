package Main;

import Model.DatabaseRawData;
import Model.Tool;
import Threads.RawDataInsertThread;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

public class Utils {
    Random random = new Random();

    public ArrayList<String> createFakeData(int COUNT) {
        int minBound = COUNT - 10;
        if (minBound < 0) minBound = 0;
        int length = random.nextInt(((COUNT + 10) - minBound) + minBound);
        ArrayList<String> array = new ArrayList<>(length);

        for (int i = 0; i < length; i++) {
            array.add(getRandomHexString(6));
        }
        return array;
    }

    // Create a new tool with random data.
    public Tool createFakeRobot(String recipeOID, String stepOID) {
        String toolOID = getRandomHexString(6);

        return new Tool(toolOID, recipeOID, stepOID);
    }

    public String getRandomHexString(int numchars) {
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        while (sb.length() < numchars) {
            sb.append(Integer.toHexString(r.nextInt()));
        }

        return sb.toString().substring(0, numchars).toUpperCase();
    }

    public void generateAndStoreTranslation(Tool currTool, int whichField, DatabaseRawData databaseRawData, boolean addRandom) {
        Random random = new Random();

        String oid = null;
        String nameTranslation = null;
        if (whichField == 0) {
            // This means we need to insert in raw_data a equip OID translation.
            oid = currTool.getEquipOID();
            nameTranslation = "equipName" + currTool.getEquipOID();
        } else if (whichField == 1) {
            // This means we need to insert in raw_data a recipe OID translation.
            oid = currTool.getRecipeOID();
            nameTranslation = "recipeName" + currTool.getRecipeOID();
        } else if (whichField == 2) {
            // This means we need to insert in raw_data a step OID translation.
            oid = currTool.getStepOID();
            nameTranslation = "stepName" + currTool.getStepOID();
        }

        if (addRandom) {
            nameTranslation += "_" + random.nextInt(50);
        }

        Connection connection = databaseRawData.getConnection();

        // Add analytics.
        storeRawData(oid, nameTranslation, whichField, connection);
    }

    private boolean storeRawData(String oid, String nameTranslation, int type, Connection connection) {
        PreparedStatement stmt = null;

        String query = "INSERT INTO analytics (id, oid, nameTranslation, type, fakeData) " +
                "VALUE (null, ?,?,?,?);";

        char[] fakeData = new char[RawDataInsertThread.SIZE];
        String fakeString = new String(fakeData);

        boolean done = false;
        try {


            stmt = connection.prepareStatement(query);
            stmt.setString(1, oid);
            stmt.setString(2, nameTranslation);
            stmt.setInt(3, type);
            stmt.setString(4, fakeString);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                done = true;
            } else done = false;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return done;
    }
}
