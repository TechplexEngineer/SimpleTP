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
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author techplex
 */
public class Main extends JavaPlugin {
    public ArrayList<Warp> warps = new ArrayList();
	public int last_config_warps_amount = 0;

    @Override
	public void onEnable() {
        
//        getCommand("warp").setExecutor(new WarpCMD());
        
		int index = 0;
		while (this.getConfig().get("warppoint_" + index + "_name") != null) {
			String n = (String)this.getConfig().get("warppoint_" + index + "_name");
			String m = (String)this.getConfig().get("warppoint_" + index + "_message");
			String l = (String)this.getConfig().get("warppoint_" + index + "_location");
            boolean s = false;
            boolean success = false;
            
            
            try {
                s = (boolean) this.getConfig().get("warppoint_" + index + "_sound");
                success = true;
            } catch (Exception ex) {}
            if (!success) {
                getLogger().info(ChatColor.GOLD + "Found an unconverted entry");
                try {
                    int a = (Integer)this.getConfig().get("warppoint_" + index + "_sound");
                    s = (a == 1);
                    success = true;
                } catch (Exception ex) {}
            }
            
            
            
			World w = this.getServer().getWorld(l.split("~")[5]);
			if (w == null) {
				w = Bukkit.getServer().createWorld(new WorldCreator(l.split("~")[5]));
			}
			double x = Double.parseDouble(l.split("~")[0]);
			double y = Double.parseDouble(l.split("~")[1]);
			double z = Double.parseDouble(l.split("~")[2]);
			float p = Float.parseFloat(l.split("~")[3]);
			float y2 = Float.parseFloat(l.split("~")[4]);
			this.warps.add(new Warp(n, m, new Location(w, x, y, z, p, y2), s));
			++index;
		}
		this.last_config_warps_amount = index - 1;
		if (this.last_config_warps_amount < 0) {
			this.last_config_warps_amount = 0;
		}
		if (this.warps == null) {
			this.warps = new ArrayList();
		}
	}

    @Override
	public void onDisable() {
		int k = 0;
		while (k != this.last_config_warps_amount + 1) {
			this.getConfig().set("warppoint_" + k + "_location", (Object)null);
			this.getConfig().set("warppoint_" + k + "_name", (Object)null);
			this.getConfig().set("warppoint_" + k + "_message", (Object)null);
			this.getConfig().set("warppoint_" + k + "_sound", (Object)null);
			++k;
		}
		k = 0;
		while (k != this.warps.size()) {
			this.getConfig().set("warppoint_" + k + "_location", (Object)(String.valueOf(this.warps.get((int)k).location.getX()) + "~" + this.warps.get((int)k).location.getY() + "~" + this.warps.get((int)k).location.getZ() + "~" + this.warps.get((int)k).location.getYaw() + "~" + this.warps.get((int)k).location.getPitch() + "~" + this.warps.get((int)k).location.getWorld().getName()));
			this.getConfig().set("warppoint_" + k + "_name", (Object)this.warps.get((int)k).name);
			this.getConfig().set("warppoint_" + k + "_message", (Object)this.warps.get((int)k).message);
			this.getConfig().set("warppoint_" + k + "_sound", (Object)this.warps.get((int)k).sound);
			++k;
		}
		this.saveConfig();
	}

	public boolean isPlayer(CommandSender s) {
		return (s instanceof Player);
	}

	public Player getPlayer(CommandSender s) {
		return (Player)s;
	}

	public boolean isWarp(String name) {
		int k = 0;
		while (k != this.warps.size()) {
			if (k >= this.warps.size()) break;
			if (this.warps.get((int)k).name.equalsIgnoreCase(name)) {
				return true;
			}
			++k;
		}
		return false;
	}

	public Warp getWarpByName(String name) {
		int k = 0;
		while (k != this.warps.size()) {
			if (k >= this.warps.size()) break;
			if (this.warps.get((int)k).name.equalsIgnoreCase(name)) {
				return this.warps.get(k);
			}
			++k;
		}
		return null;
	}

    @Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED+"Usage");
            // print usage @todo
            return false;
        }
        final String c = "list|create|all|op|users";
        List<String> cmds = Arrays.asList(c.split("\\|"));
        if (cmds.contains(args[0].toLowerCase())) {
            String subcmd = args[0];
            
            // /warp list
            if (subcmd.equalsIgnoreCase("list")) {
                if (warps.isEmpty()) {
                    sender.sendMessage(ChatColor.GREEN + "Sorry there are no warps.");
                } else {
                    sender.sendMessage(ChatColor.GREEN + "Here a list of all (" + warps.size() + ") available warps:");
                    warps.stream().forEach((w) -> {
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
                    sender.sendMessage(ChatColor.RED + "Not enough arugments to command. Proper use: warp create <warpName>");
                    return false;
                }
                String warpName = args[1];
                if (cmds.contains(warpName.toLowerCase())) {
                    sender.sendMessage(ChatColor.RED + "Sorry, you cannot create a warp named "+warpName+" because it would conflict with a command.");
                    return false;
                }
                this.warps.add(new Warp(warpName, "", getPlayer(sender).getLocation(), false));
                this.getPlayer(sender).sendMessage(ChatColor.GREEN + "Warp " + warpName + " added");
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

    private void elseif() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
