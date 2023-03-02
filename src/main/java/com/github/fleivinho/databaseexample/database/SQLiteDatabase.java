package com.github.fleivinho.databaseexample.database;

import com.github.fleivinho.databaseexample.Main;
import com.github.fleivinho.databaseexample.objects.DatabaseType;
import lombok.Getter;
import org.bukkit.Bukkit;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.io.File;
import java.io.IOException;
import java.sql.*;

public class SQLiteDatabase extends Database {

    @Getter
    private File file;

    public SQLiteDatabase() {
        super(DatabaseType.SQLITE);
        this.file = new File("plugins/" + Main.getPlugin().getName() + "/database.db");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void openConnection() {
        if (connection != null) {
            return;
        }
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + file);
            Bukkit.getConsoleSender().sendMessage("§a[DatabaseExample] Conexão com SQLite efetuada!");
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("§c[DatabaseExample] Conexão com SQLite mal sucedida: " + e);
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
            getExecutor().shutdown();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CachedRowSet query(String sql, Object... vars) {
        CachedRowSet rowSet = null;
        try {
            if (tableExists()) {
                try (Connection connection = getConnection();
                     PreparedStatement ps = connection.prepareStatement(sql)) {
                    for (int i = 0; i < vars.length; i++) {
                        ps.setObject(i + 1, vars[i]);
                    }

                    try (ResultSet rs = ps.executeQuery()) {
                        CachedRowSet crs = RowSetProvider.newFactory().createCachedRowSet();
                        crs.populate(rs);
                        if (crs.next()) {
                            rowSet = crs;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowSet;
    }

    @Override
    public PreparedStatement prepareStatement(String query, Object... vars) {
        try {
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(query);
            for (int i = 0; i < vars.length; i++) {
                ps.setObject(i + 1, vars[i]);
            }
            return ps;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    private boolean tableExists() throws SQLException {
        DatabaseMetaData meta = getConnection().getMetaData();
        try (ResultSet rs = meta.getTables(null, null, "favoritecolours", new String[] { "TABLE" })) {
            return rs.next();
        }
    }

}
