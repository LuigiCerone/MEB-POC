package Threads;

import Model.Event;
import Model.Tool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class FabDataInsertThread implements Runnable {
    public final int CATEGORIES_NUM = 10;

    Tool tool;
    Connection connection;

    public FabDataInsertThread(Tool tool, Connection connection) {
        this.tool = tool;
        this.connection = connection;
    }

    @Override
    public void run() {
//            long start = System.currentTimeMillis();
        try {

            Random random = new Random();
            Model.Event event = new Model.Event(this.tool, "category" + random.nextInt(CATEGORIES_NUM), this.tool.isOnHold(),
                    System.currentTimeMillis());

            boolean oldValue = this.tool.isOnHold();
            event.setHoldFlag(!oldValue);

            // Now we insert the event into the fab_data database.
            store(event);

        } catch (Exception e) {
            e.printStackTrace();
        }

//            long end = System.currentTimeMillis();
//            System.out.println("One single request took : " + (end - start) + "ms.");

    }

    private boolean store(Event event) {
        PreparedStatement stmt = null;

        String query = "INSERT INTO event (id, equip, recipe, step, holdtype, holdflag, datetime) " +
                "VALUE (null, ?,?,?,?,?,?);";
        boolean done = false;
        try {


            stmt = this.connection.prepareStatement(query);
            stmt.setString(1, event.getTool().getEquipOID());
            stmt.setString(2, event.getTool().getRecipeOID());
            stmt.setString(3, event.getTool().getStepOID());
            stmt.setString(4, event.getHoldType());
            stmt.setBoolean(5, event.isHoldFlag());
            stmt.setLong(6, event.getDateTime());

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
            if (this.connection != null) {
                try {
                    this.connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return done;
    }
}