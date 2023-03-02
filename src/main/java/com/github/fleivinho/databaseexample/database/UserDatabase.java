package com.github.fleivinho.databaseexample.database;

import com.github.fleivinho.databaseexample.objects.FavoriteColor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class UserDatabase {

    @Getter
    private JavaPlugin plugin;

    @Getter
    private Database database;

    public UserDatabase(Database database, JavaPlugin plugin) {
        this.database = database;
        this.plugin = plugin;
        setup();
    }

    public void setup() {
        database.createTable("favoritecolors",
                "uuid TEXT,"
                        + "color TEXT");

    }
    public boolean createPlayer(UUID uuid) {
        if (getDatabase().contains("favoritecolors", "uuid", uuid.toString())) {
            return false;
        }
        getDatabase().insertData("favoritecolors", "uuid, color", "'" + uuid.toString() + "', '" + FavoriteColor.NONE.name() + "'");
        return true;
    }

    public void update(UUID uuid, FavoriteColor favoriteColor) {
        getDatabase().updateTable("favoritecolors", "color", favoriteColor.name(), "uuid", uuid.toString());
    }

    public Optional<FavoriteColor> getFavoriteColor(UUID uuid) {
        CachedRowSet rs = getDatabase().query("SELECT * FROM favoritecolors");
        if (rs != null) {
            try {
                rs.beforeFirst();
                if (rs.next()) {
                    FavoriteColor favoriteColor = FavoriteColor.valueOf(rs.getString("color"));
                    Bukkit.getConsoleSender().sendMessage("§eFound from " + uuid + " -> " + rs.getString("color") + " : " + favoriteColor.name());
                    return Optional.of(favoriteColor);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return Optional.empty();
    }


}
