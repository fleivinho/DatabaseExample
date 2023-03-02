package com.github.fleivinho.databaseexample.database;

import com.github.fleivinho.databaseexample.objects.DatabaseType;
import lombok.Getter;
import lombok.Setter;
import javax.sql.rowset.CachedRowSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Database {

    @Getter
    private DatabaseType databaseType;

    @Getter
    @Setter
    public Connection connection;

    @Getter
    private ExecutorService executor = Executors.newCachedThreadPool();

    public Database(DatabaseType databaseType) {
        this.databaseType = databaseType;
    }

    public static Database setup(DatabaseType databaseType) {
        if (databaseType == DatabaseType.MYSQL) {
            return new MySQLDatabase();
        }
        return new SQLiteDatabase();
    }

    public abstract void openConnection();

    public abstract void closeConnection();

    public abstract CachedRowSet query(String sql, Object... vars);

    public abstract PreparedStatement prepareStatement(String query, Object... vars) throws SQLException;

    public boolean isConnected() {
        try {
            if (getDatabaseType() == DatabaseType.MYSQL) {
                return connection != null && !connection.isClosed() && connection.isValid(5);
            }
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isClosed() {
        return !isConnected();
    }

    public void insertData(String tableName, String columns, String values) {
        String sql = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + values + ")";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTable(String tableName, String columnToUpdate, String newValue, String conditionColumn, String conditionValue) {
        String sql = "UPDATE " + tableName + " SET " + columnToUpdate + " = ? WHERE " + conditionColumn + " = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, newValue);
            statement.setString(2, conditionValue);
            int rowsUpdated = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ResultSet executeQuery(String table, String where, String whereTo) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM `" + table + "` WHERE `" + where + "`=?;");
        statement.setString(1, whereTo);
        return statement.executeQuery();
    }

    public boolean contains(String table, String where, String whereTo) {
        try (ResultSet set = executeQuery(table, where, whereTo)) {
            return set.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void createTable(String tableName, String columns) {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + columns + ")";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
