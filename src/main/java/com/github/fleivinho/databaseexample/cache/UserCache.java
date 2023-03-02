package com.github.fleivinho.databaseexample.cache;

import com.github.fleivinho.databaseexample.Main;
import com.github.fleivinho.databaseexample.database.UserDatabase;
import com.github.fleivinho.databaseexample.objects.FavoriteColor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class UserCache {

    @Getter
    private static HashMap<Player, UserCache> caches = new HashMap<>();

    @Getter
    private UUID uuid;

    @Getter
    private Player player;

    @Getter
    @Setter
    private FavoriteColor favoriteColor;

    public UserCache(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();
    }

    public void load() {
        UserDatabase userDatabase = Main.getUserDatabase();

        if (userDatabase == null) {
            player.kickPlayer("§cInfelizmente o sistema de cores favoritas não está disponível!");
            return;
        }

        if (userDatabase.createPlayer(uuid)) {
            favoriteColor = FavoriteColor.NONE;
            player.sendMessage("§f§lDATABASE » §aVocê foi registrado!");
        } else {
            Optional<FavoriteColor> databaseColor = userDatabase.getFavoriteColor(uuid);
            favoriteColor = databaseColor.orElse(FavoriteColor.NONE);
            player.sendMessage("§f§lDATABASE » §aSua cor favorita foi carregada: " + favoriteColor);
        }

        player.sendMessage("§eSua cor favorita é: §r" + favoriteColor.getMinecraftColor() + favoriteColor.getName());
        player.sendMessage("§eAltere sua cor favorita dizendo: §rminha cor favorita é <cor>");
        caches.put(player, this);
    }

    public void unload() {
        // Armazenando a informação no banco de dados somente quando sair
        Main.getUserDatabase().update(uuid, favoriteColor);

        caches.remove(player);
    }

}
