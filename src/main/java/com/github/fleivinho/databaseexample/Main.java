package com.github.fleivinho.databaseexample;

import com.github.fleivinho.databaseexample.database.Database;
import com.github.fleivinho.databaseexample.database.UserDatabase;
import com.github.fleivinho.databaseexample.events.CacheListener;
import com.github.fleivinho.databaseexample.events.ColorListener;
import com.github.fleivinho.databaseexample.objects.DatabaseType;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Getter
    private static JavaPlugin plugin;

    @Getter
    private static Database database;

    @Getter
    private static UserDatabase userDatabase;

    @Override
    public void onEnable() {
        plugin = this;

        // Escolha qual tipo de database utilizar.
        database = Database.setup(DatabaseType.MYSQL);

        // Iniciando conexão com o banco de dados.
        database.openConnection();

        // Criando a instance para a database de usuários.
        userDatabase = new UserDatabase(database, this);

        // Registro padrão de eventos.
        Bukkit.getPluginManager().registerEvents(new CacheListener(), this);
        Bukkit.getPluginManager().registerEvents(new ColorListener(), this);
    }

    @Override
    public void onDisable() {
        // Fechando conexão com o banco de dados.
        database.closeConnection();
    }
}
