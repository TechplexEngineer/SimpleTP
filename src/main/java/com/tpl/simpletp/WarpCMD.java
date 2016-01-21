/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tpl.simpletp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

/**
 *
 * @author techplex
 */
public class WarpCMD implements CommandExecutor, TabCompleter {
    
    
    public boolean isPlayer(CommandSender s) {
		return (s instanceof Player);
	}

	public Player getPlayer(CommandSender s) {
		return (Player)s;
	}

	public boolean isWarp(String name) {
		int k = 0;
		while (k != Main.inst.warps.size()) {
			if (k >= Main.inst.warps.size()) break;
			if (Main.inst.warps.get((int)k).name.equalsIgnoreCase(name)) {
				return true;
			}
			++k;
		}
		return false;
	}

	public Warp getWarpByName(String name) {
		int k = 0;
		while (k != Main.inst.warps.size()) {
			if (k >= Main.inst.warps.size()) break;
			if (Main.inst.warps.get((int)k).name.equalsIgnoreCase(name)) {
				return Main.inst.warps.get(k);
			}
			++k;
		}
		return null;
	}
    
    private final String c = "list|create|all|op|users|to";
    /**
     * Executes the given command, returning its success
     *
     * @param sender Source of the command
     * @param cmd Command which was executed
     * @param label Alias of the command which was used
     * @param args Passed command arguments
     * @return true if a valid command, otherwise false
     */
    @Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED+"Usage");
            // print usage @todo
            return false;
        }
        
        List<String> cmds = Arrays.asList(c.split("\\|"));
        if (cmds.contains(args[0].toLowerCase())) {
            String subcmd = args[0];
            
            // /warp list
            if (subcmd.equalsIgnoreCase("list")) { //@todo perms  && sender.hasPermission(Main.inst.PERM_BASE+".list")
                if (Main.inst.warps.isEmpty()) {
                    sender.sendMessage(ChatColor.GREEN + "Sorry there are no warps.");
                } else {
                    sender.sendMessage(ChatColor.GREEN + "Here a list of all (" + Main.inst.warps.size() + ") available warps:");
                    Main.inst.warps.stream().forEach((w) -> {
                        sender.sendMessage(ChatColor.GREEN + w.name);
                    });
                }
                return true;
            }
            
            if (!isPlayer(sender)) {
                sender.sendMessage(ChatColor.RED + "Sorry you must be a player ingame to do that.");
                return false;
            }
            
            // /warp create <warpName>
            if (subcmd.equalsIgnoreCase("create")) {
                if (!isPlayer(sender)) {
                    sender.sendMessage(ChatColor.RED + "Sorry you must be a player ingame to create a warp");
                    return false;
                }
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Not enough arugments to command. \nProper use: warp create <warpName>");
                    return false;
                }
                String warpName = args[1];
                if (cmds.contains(warpName.toLowerCase())) {
                    sender.sendMessage(ChatColor.RED + "Sorry, you cannot create a warp named "+warpName+" because it would conflict with a command.");
                    return false;
                }
                Main.inst.warps.add(new Warp(warpName, "", getPlayer(sender).getLocation(), false));
                getPlayer(sender).sendMessage(ChatColor.GREEN + "Warp " + warpName + " added");
                return true;
                
            }
            
            // /warp all
            if (subcmd.equalsIgnoreCase("all")) {
                Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
                Player issuer = getPlayer(sender);
                onlinePlayers.stream().forEach((p) -> {
                    if (p != issuer) {
                        p.teleport(issuer.getLocation());
                    }
                });
                return true;
            }
            // /warp op
            if (subcmd.equalsIgnoreCase("op")) {
                Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
                Player issuer = getPlayer(sender);
                onlinePlayers.stream().forEach((p) -> {
                    if (p != issuer && p.isOp()) {
                        p.teleport(issuer.getLocation());
                    }
                });
                return true;
            }
            // /warp users
            if (subcmd.equalsIgnoreCase("users")) {
                Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
                Player issuer = getPlayer(sender);
                onlinePlayers.stream().forEach((p) -> {
                    if (p != issuer && !p.isOp()) {
                        p.teleport(issuer.getLocation());
                    }
                });
                return true;
            }
            
            // /warp to <otherPlayer>
            if (subcmd.equalsIgnoreCase("to")) {
                if (args[1].length() == 0) {
                    sender.sendMessage(ChatColor.RED + "Not enough arugments to command. \nProper use: warp tp <playerName>");
                    return false;
                }
                Player destPlayer = Bukkit.getServer().getPlayerExact(args[1]);
                if (destPlayer != null) {
                    Player issuer = getPlayer(sender);
                    issuer.teleport(destPlayer.getLocation());
                    return true;
                }
                sender.sendMessage(ChatColor.RED + "Unknown player \""+args[1]+"\"");
                return false;
            }
        } else if (isWarp(args[0])) {
            if (!isPlayer(sender)) {
                sender.sendMessage(ChatColor.RED + "Sorry you must be a player ingame to warp");
                return false;
            }
            Warp warp = getWarpByName(args[0]);
            getPlayer(sender).teleport(warp.location);

            if (!warp.message.equals("")) {
                getPlayer(sender).sendMessage(warp.message);
            }
            if (warp.sound) {
                getPlayer(sender).getWorld().playSound(getPlayer(sender).getLocation(), Sound.PORTAL_TRAVEL, 100.0f, 100.0f);
            }
            return true;
        
        } else {
            sender.sendMessage(ChatColor.RED+"Unknown action");
            // print usage @todo
            return false;
        }
        
        return false;
      
	}

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> pos = new ArrayList<>();
        
        List<String> cmds = Arrays.asList(c.split("\\|"));
        if (args.length < 1) {
            return cmds;
        }
        
        if (args.length == 1) {
            for (String subcmd : cmds) {
                if (subcmd.startsWith(args[0].toLowerCase())) {
                    pos.add(subcmd);
                }
            }
            return pos;
        }
        
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("to")) {
                Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
                Player issuer = getPlayer(sender);
                onlinePlayers.stream().forEach((p) -> {
                    if (p != issuer) {
                        pos.add(p.getName());
                    }
                });

                return pos;
            }
        }
        return pos;
  
    }
    
}
