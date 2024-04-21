package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
    
    /**
     * Expose a utility method to connect to a MySQL database
     */
    public final  class ConnectionProvider {
        
        public  Connection getMySQLConnection() {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (Exception e) {
                throw new IllegalStateException("Could not find correct JDBC Driver", e);
            }
            final String dbUri = "jdbc:mysql://localhost:3306/farmacia1";
            try {
                // Thanks to the JDBC DriverManager we can get a connection to the database
                return DriverManager.getConnection(dbUri,"root","Franco-01");
            } catch (final SQLException e) {
                throw new IllegalStateException("Could not establish a connection with db", e);
            }
        }
    }    
