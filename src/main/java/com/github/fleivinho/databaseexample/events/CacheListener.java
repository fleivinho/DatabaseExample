package com.github.fleivinho.databaseexample.events;

import com.github.fleivinho.databaseexample.cache.UserCache;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CacheListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UserCache userCache = new UserCache(event.getPlayer());
        userCache.load();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (UserCache.getCaches().containsKey(event.getPlayer())) {
            UserCache.getCaches().get(event.getPlayer()).unload();
        }
    }
}
