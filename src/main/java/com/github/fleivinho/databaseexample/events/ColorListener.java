package com.github.fleivinho.databaseexample.events;

import com.github.fleivinho.databaseexample.cache.UserCache;
import com.github.fleivinho.databaseexample.objects.FavoriteColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Arrays;

public class ColorListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();

        // Código gambiarra, apenas para diversão ;)
        String key = "minha cor favorita é ";
        if (event.getMessage().toLowerCase().startsWith(key)) {
            String colorName = event.getMessage().split(key)[1];
            FavoriteColor favoriteColor = Arrays.stream(FavoriteColor.values()).filter(color -> color.getName().equalsIgnoreCase(colorName) || color.name().equalsIgnoreCase(colorName)).findFirst().orElse(null);
            if (favoriteColor == null) {
                player.sendMessage("§cCor '" + colorName + "' não encontrada!");
                return;
            }
            player.sendMessage("§eEba! Sua cor favorita é " + favoriteColor.getMinecraftColor() + favoriteColor.getName() + "§e!");
            UserCache.getCaches().get(player).setFavoriteColor(favoriteColor);
            event.setCancelled(true);
        }
    }
}
