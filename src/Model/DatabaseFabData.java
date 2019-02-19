package Model;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseFabData {
    // Singleton for DB client
    // Creates a single connection pool internally

    public static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    public static final String URL = "jdbc:mysql://localhost/fab_data";
    public static final String USERNAME = "sa18";
    public static final String PASSWORD = "software_architectures_18";

    private GenericObjectPool connectionPool = null;
    private DataSource dataSource = null;

    public DataSource setUp() throws Exception {
        // Load JDBC Driver class.
        Class.forName(DatabaseFabData.DRIVER).newInstance();

        // Creates an instance of GenericObjectPool that holds our
        // pool of connections object.
        connectionPool = new GenericObjectPool();
        connectionPool.setMaxActive(10);

        // Creates a connection factory object which will be use by
        // the pool to create the connection object. We passes the
        // JDBC url info, username and password.
        ConnectionFactory cf = new DriverManagerConnectionFactory(
                DatabaseFabData.URL,
                DatabaseFabData.USERNAME,
                DatabaseFabData.PASSWORD);

        // Creates a PoolableConnectionFactory that will wraps the
        // connection object created by the ConnectionFactory to add
        // object pooling functionality.
        PoolableConnectionFactory pcf =
                new PoolableConnectionFactory(cf, connectionPool,
                        null, null, false, true);
        return new PoolingDataSource(connectionPool);
    }

    public GenericObjectPool getConnectionPool() {
        return connectionPool;
    }

    public Connection getConnection() {
        if (this.dataSource == null) {
            try {
                this.dataSource = this.setUp();
                return this.dataSource.getConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                return dataSource.getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}