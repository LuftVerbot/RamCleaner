package com.luft.ramcleaner;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class RamCleaner extends JavaPlugin implements Listener {
    private int TARGET_MEMORY = this.getConfig().getInt("size"); // target memory in MB
    private int CLEAN_INTERVAL = this.getConfig().getInt("frequency"); // interval in seconds

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        this.saveDefaultConfig();
        if(this.getConfig().getBoolean("auto-cleaner")) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    runCleaner();
                }
            }.runTaskTimerAsynchronously((Plugin) this, 0L, 20L * CLEAN_INTERVAL);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(this.getConfig().getBoolean("onjoin")) {
            runCleaner();
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if(this.getConfig().getBoolean("onleave")) {
            runCleaner();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("ramclean") && args.length == 1 && args[0].equalsIgnoreCase("clean")) {
            if (sender.hasPermission("ramclean.clean")) {
                System.gc();
                getLogger().info("[RAM Cleaner] Running RAM Cleaner");
                sender.sendMessage("RAM cleaned!");
            } else {
                sender.sendMessage("You do not have permission to use this command.");
            }
            return true;
        }
        if (command.getName().equalsIgnoreCase("ramclean") && args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (sender.hasPermission("ramclean.reload")) {
                reload();
                sender.sendMessage("Config reloaded!");
            } else {
                sender.sendMessage("You do not have permission to use this command.");
            }
            return true;
        }
        if(command.getName().equalsIgnoreCase("ramclean") && args.length == 1 && args[0].equalsIgnoreCase("help")){
            sender.sendMessage("RAM Cleaner Help");
            sender.sendMessage("/ramclean clean - Cleans the RAM");
            sender.sendMessage("/ramclean reload - Reloads the config file");
            return true;
        }
        return false;
    }

    private void reload() {
        this.reloadConfig();
        TARGET_MEMORY = this.getConfig().getInt("size");
        CLEAN_INTERVAL = this.getConfig().getInt("frequency");

        Bukkit.getScheduler().cancelTasks(this);
        if(this.getConfig().getBoolean("auto-cleaner")) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    runCleaner();
                }
            }.runTaskTimerAsynchronously((Plugin) this, 0L, 20L * CLEAN_INTERVAL);
        }
    }

    private void runCleaner() {
        if(this.getConfig().getInt("type") == 1){
                if (RamCleaner.this.getConfig().getBoolean("debug")) {
                    getLogger().info("[RAM Cleaner] Running RAM Cleaner");
                }
                System.gc();
        }
        if(this.getConfig().getInt("type") == 2) {
            long totalMemory = Runtime.getRuntime().totalMemory() / (1024 * 1024);
            if (totalMemory > TARGET_MEMORY) {
                if (RamCleaner.this.getConfig().getBoolean("debug")) {
                    getLogger().info("[RAM Cleaner] Running RAM Cleaner");
                }
                System.gc();
            }
        }
    }
}
