package com.axeelheaven.hskywars.scoregit;

import com.axeelheaven.hskywars.HSkyWars;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Arrays;
import java.util.HashMap;

public class ScoreboardManager implements Listener {

    private final HSkyWars plugin;
    private final HashMap<Player, ScoreboardUtil> scoreboards;
    final String title = "AxeelHeaven";
    private final ChatColor primary = ChatColor.GOLD, second = ChatColor.YELLOW;
    private final HashMap<Player, Long> timeInServer;

    public ScoreboardManager(final HSkyWars plugin) {
        this.plugin = plugin;
        this.timeInServer = new HashMap<>();
        this.scoreboards = new HashMap<>();
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, () -> scoreboards.forEach((player, scoreboard) -> {
            scoreboard.setName(getTitle());
            scoreboard.lines(Arrays.asList(
                    "",
                    "Name: " + ChatColor.GREEN + player.getName(),
                    PlaceholderAPI.setPlaceholders(player, "Rank: %hpermissions_prefix%"),
                    "",
                    "Online: " + ChatColor.GREEN + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers(),
                    "Playing: " + ChatColor.GREEN + getTimePlayer(player),
                    "",
                    ChatColor.YELLOW + " mc.minecraft.net "));
        }), 5L, 5L);
    }

    private String getTimePlayer(final Player player) {
        final int time = (int) (Math.max(System.currentTimeMillis() - this.timeInServer.get(player), 0) / 1000);
        if (time >= 86400) {
            return (time / 86400) + " day" + ((time / 86400) > 1 ? "s." : ".");
        } else if (time >= 3600) {
            return (time / 3600) + " hour" + ((time / 3600) > 1 ? "s." : ".");
        } else if (time >= 60) {
            return (time / 60) + " minute" + ((time / 60) > 1 ? "s." : ".");
        } else {
            return time + " seconds.";
        }
    }

    int currentFrame = 0;
    private String getTitle() {
        String frame = title;
        if (this.currentFrame >= this.title.length()) this.currentFrame = 0;
        frame = this.primary + this.title.substring(0, this.currentFrame) + this.second + this.title.charAt(this.currentFrame) + this.primary + this.title.substring(this.currentFrame + 1);
        this.currentFrame++;
        return frame;
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        this.scoreboards.put(player, new ScoreboardUtil(player).build());
        this.timeInServer.put(player, System.currentTimeMillis());
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        this.scoreboards.remove(player);
        this.timeInServer.remove(player);
    }

}
