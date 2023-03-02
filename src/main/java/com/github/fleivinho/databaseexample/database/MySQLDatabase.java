package com.github.fleivinho.databaseexample.database;

import com.github.fleivinho.databaseexample.objects.DatabaseType;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MySQLDatabase extends Database {

    public MySQLDatabase() {
        super(DatabaseType.MYSQL);
    }

    @Override
    public void openConnection() {
        if (connection != null) {
            return;
        }
        // Login do banco de dados (Coloquei o meu)
        try {
            String host = "localhost";
            String url = "jdbc:mysql://" + host + ":" + 3306 + "/" + "leaf" + "?autoReconnect=true&useSSL=false&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8";
            connection = DriverManager.getConnection(url, "root", "");
            Bukkit.getConsoleSender().sendMessage("§a[DatabaseExample] Conexão com MySQL efetuada!");
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage("§c[DatabaseExample] Conexão com MySQL mal sucedida: " + e);
            e.printStackTrace();
        }
    }

    @Override
    public void closeConnection() {
        if (connection == null) {
            return;
        }
        try {
            connection.close();
            connection = null;
            getExecutor().shutdown();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public @Nullable CachedRowSet query(String sql, Object... vars) {
        CachedRowSet rowSet = null;
        try {
            Future<CachedRowSet> future = getExecutor().submit(() -> {
                try (PreparedStatement ps = prepareStatement(sql, vars);
                     ResultSet rs = ps.executeQuery()) {
                    CachedRowSet crs = RowSetProvider.newFactory().createCachedRowSet();
                    crs.populate(rs);
                    if (crs.next()) {
                        return crs;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            });
            rowSet = future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return rowSet;
    }

    @Override
    public PreparedStatement prepareStatement(String query, Object... vars) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(query);
        for (int i = 0; i < vars.length; i++) {
            ps.setObject(i + 1, vars[i]);
        }
        return ps;
    }


}
