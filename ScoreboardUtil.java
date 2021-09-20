package com.axeelheaven.hskywars.scoregit;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardUtil {

    private final Scoreboard scoreboard;
    private final Objective objective;
    private final Player player;

    public ScoreboardUtil(final Player player) {
        this.player = player;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = this.scoreboard.registerNewObjective(player.getName(), "dummy");
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public ScoreboardUtil build() {
        this.player.setScoreboard(this.scoreboard);
        return this;
    }

    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    public void setName(String substring) {
        if (substring.length() > 32) {
            substring = substring.substring(0, 32);
        }
        this.objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', substring));
    }

    public void lines(Integer n, String substring) {
        org.bukkit.scoreboard.Team team = this.scoreboard.getTeam("TEAM_" + n);
        if (substring.length() > 32) {
            substring = substring.substring(0, 32);
        }
        String[] splitStringLine = this.splitStringLine(substring);
        if (team == null) {
            final Team registerNewTeam = this.scoreboard.registerNewTeam("TEAM_" + n);
            registerNewTeam.addEntry(this.getEntry(n));
            this.setPrefix(registerNewTeam, splitStringLine[0]);
            this.setSuffix(registerNewTeam, splitStringLine[1]);
            this.objective.getScore(this.getEntry(n)).setScore(n);
        } else {
            this.setPrefix(team, splitStringLine[0]);
            this.setSuffix(team, splitStringLine[1]);
        }
    }

    private String[] splitStringLine(final String text) {
        final StringBuilder prefix = new StringBuilder(text.substring(0, Math.min(text.length(), 16)));
        final StringBuilder suffix = new StringBuilder((text.length() > 16)? text.substring(16) : "");
        if (prefix.toString().length() > 1 && prefix.charAt(prefix.length() - 1) == '§') {
            prefix.deleteCharAt(prefix.length() - 1);
            suffix.insert(0, '§');
        }
        StringBuilder lastColor = new StringBuilder();
        if (prefix.length() >= 1 && prefix.toString().charAt(0) != '§') {
            lastColor = new StringBuilder("§f");
        }
        for (int i = 0; i < prefix.toString().length(); ++i) {
            if (prefix.toString().charAt(i) == '§' && i < prefix.toString().length() - 1) {
                lastColor.append("§").append(prefix.toString().charAt(i + 1));
            }
        }
        return new String[] { prefix.toString(), lastColor + suffix.toString() };
    }

    private void setPrefix(org.bukkit.scoreboard.Team team, String prefix) {
        if (prefix.length() > 16) {
            team.setPrefix(prefix.substring(0, 16));
        } else {
            team.setPrefix(prefix);
        }
    }

    private void setSuffix(org.bukkit.scoreboard.Team team, String suffix) {
        if (suffix.length() > 16) {
            team.setSuffix(suffix.substring(0, 16));
        } else {
            team.setSuffix(suffix);
        }
    }

    private void reset(final Integer value){
        org.bukkit.scoreboard.Team team = this.getScoreboard().getTeam("TEAM_" + value);
        if(team != null){
            this.getScoreboard().getTeam("TEAM_" + value).unregister();
            this.getScoreboard().resetScores(this.getEntry(value));
        }
    }

    public void lines(final List<String> list) {
        while (list.size() > 15) {
            list.remove(list.size() - 1);
        }
        int slots = list.size();
        for (int i = slots + 1; i <= 15; i++) {
            this.reset(i);
        }
        for (final String line : list) {
            this.lines(slots, line);
            slots--;
        }
    }
    
    private String getEntry(Integer value) {
        return ChatColor.values()[value].toString();
    }
}
