/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tpl.simpletp;

import java.util.ArrayList;
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
        
        if (args.length >= 1) {
            String warpName = args[0];
            if (!isWarp(warpName)) {
                sender.sendMessage(ChatColor.RED+"Sorry " + warpName + " is not a valid warp");
                return false;
            }
            // /warp <warpName>
            if (args.length == 1) {
                if (!isPlayer(sender)) {
                    sender.sendMessage(ChatColor.RED+"Sorry you must be a player ingame to warp");
                    return false;
                }
                Warp warp = getWarpByName(warpName);
                getPlayer(sender).teleport(warp.location);
                
                if (!warp.message.equals("")) {
                    getPlayer(sender).sendMessage(warp.message);
                }
                if (warp.sound) {
                    getPlayer(sender).getWorld().playSound(getPlayer(sender).getLocation(), Sound.PORTAL_TRAVEL, 100.0f, 100.0f);
                }
                return true;
            }
            // /warp <warpName> <playerName>
            if (args.length == 3) {
                String playerName = args[1];
                Warp warp = getWarpByName(warpName);
                if (getPlayer(sender).isOp()) {
                    sender.sendMessage(ChatColor.RED+"sorry you must be an op to warp someone else");
                    return false;
                }
                if (getServer().getPlayer(playerName) == null) {
                    sender.sendMessage(ChatColor.RED+"sorry that player does not exist");
                    return false;
                }
                
                getServer().getPlayer(playerName).teleport(warp.location);
                if (!warp.message.equals("")) {
                    getServer().getPlayer(playerName).sendMessage(warp.message);
                }
                if (warp.sound) {
                    getPlayer(sender).getWorld().playSound(getPlayer(sender).getLocation(), Sound.PORTAL_TRAVEL, 100.0f, 100.0f);
                }
                return true;
            }

            return true;
        }
        
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                if (warps.isEmpty()) {
                    sender.sendMessage(ChatColor.GREEN + "Sorry there are no warps.");
                } else {
                    sender.sendMessage(ChatColor.GREEN + "Here a list of all ("+warps.size()+") available warps:");
                    warps.stream().forEach((w) -> {
                        sender.sendMessage(ChatColor.GREEN + w.name);
                    });
                }
                return true;
            }
        }
        if (args.length == 2) {
            
            if (!isPlayer(sender)) {
                sender.sendMessage(ChatColor.RED+"Sorry you must be a player ingame to create a warp");
                return false;
            }
            if (args[0].equalsIgnoreCase("create")) {
                String warpName = args[1];
                this.warps.add(new Warp(warpName, "", getPlayer(sender).getLocation(), false));
                this.getPlayer(sender).sendMessage(ChatColor.GREEN + "Warp " + warpName + " added");
                return true;
            }
        }
        return false;
        
        /*
		if (this.isPlayer(sender) && command.getName().equalsIgnoreCase("warp") && args.length > 0) {
			if (args.length == 1 && this.isWarp(args[0])) {
				this.getPlayer(sender).teleport(this.getWarpByName((String)args[0]).location);
				if (!this.getWarpByName((String)args[0]).message.equals("")) {
					this.getPlayer(sender).sendMessage(this.getWarpByName((String)args[0]).message);
				}
				if (this.getWarpByName((String)args[0]).sound) {
					this.getPlayer(sender).getWorld().playSound(this.getPlayer(sender).getLocation(), Sound.PORTAL_TRAVEL, 100.0f, 100.0f);
				}
				return true;
			}
			if (args.length == 2 && this.getPlayer(sender).isOp() && this.getServer().getPlayer(args[1]) != null && this.isWarp(args[0])) {
				this.getServer().getPlayer(args[1]).teleport(this.getWarpByName((String)args[0]).location);
				if (!this.getWarpByName((String)args[0]).message.equals("")) {
					this.getServer().getPlayer(args[1]).sendMessage(this.getWarpByName((String)args[0]).message);
				}
				if (this.getWarpByName((String)args[0]).sound) {
					this.getPlayer(sender).getWorld().playSound(this.getPlayer(sender).getLocation(), Sound.PORTAL_TRAVEL, 100.0f, 100.0f);
				}
				return true;
			}
		}
		if (this.isPlayer(sender) && command.getName().equalsIgnoreCase("listwarps") && args.length == 0) {
			this.getPlayer(sender).sendMessage("\u00a72Here a list of all available warps:");
			int k = 0;
			while (k != this.warps.size()) {
				this.getPlayer(sender).sendMessage("\u00a72" + this.warps.get((int)k).name);
				++k;
			}
			return true;
		}
		if (this.isPlayer(sender) && command.getName().equalsIgnoreCase("createwarp") && args.length == 1 && !this.isWarp(args[0]) && this.getPlayer(sender).isOp()) {
			this.warps.add(new Warp(args[0], "", this.getPlayer(sender).getLocation(), false));
			this.getPlayer(sender).sendMessage("\u00a72Warp " + args[0] + " added");
			return true;
		}
		if (this.isPlayer(sender) && command.getName().equalsIgnoreCase("deletewarp") && args.length == 1 && this.isWarp(args[0]) && this.getPlayer(sender).isOp()) {
			this.warps.remove(this.getWarpByName(args[0]));
			this.getPlayer(sender).sendMessage("\u00a72Warp " + args[0] + " deleted");
			return true;
		}
		if (this.isPlayer(sender) && command.getName().equalsIgnoreCase("warpmessage") && args.length > 1 && this.isWarp(args[0]) && this.getPlayer(sender).isOp()) {
			String m = "";
			int k = 1;
			while (k != args.length) {
				m = String.valueOf(m) + args[k] + " ";
				++k;
			}
			this.getWarpByName((String)args[0]).message = m;
			this.getPlayer(sender).sendMessage("\u00a72Warp " + args[0] + "'s message set to " + m);
			return true;
		}
		if (this.isPlayer(sender) && command.getName().equalsIgnoreCase("deletewarpmessage") && args.length == 1 && this.isWarp(args[0]) && this.getPlayer(sender).isOp()) {
			this.getWarpByName((String)args[0]).message = "";
			this.getPlayer(sender).sendMessage("\u00a72Warp " + args[0] + "'s message set to nothing");
			return true;
		}
		if (this.isPlayer(sender) && command.getName().equalsIgnoreCase("warpsound") && args.length > 1 && this.isWarp(args[0]) && this.getPlayer(sender).isOp()) {
			boolean s = false;
			if (args[1].equalsIgnoreCase("true")) {
				s = true;
				this.getPlayer(sender).sendMessage("\u00a72Sound of warp " + args[0] + " enabled!");
			}
			if (args[1].equalsIgnoreCase("false")) {
				s = false;
				this.getPlayer(sender).sendMessage("\u00a72Sound of warp " + args[0] + " disabled!");
			}
			this.getWarpByName((String)args[0]).sound = s;
			return true;
		}
		if (this.isPlayer(sender) && command.getName().equalsIgnoreCase("tpall") && args.length == 0 && this.getPlayer(sender).isOp()) {
            Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
            onlinePlayers.stream().forEach((p) -> {
                if (p != this.getPlayer(sender)) {
					p.teleport(this.getPlayer(sender).getLocation());
				}
            });
			return true;
		}
		if (this.isPlayer(sender) && command.getName().equalsIgnoreCase("tpallusers") && args.length == 0 && this.getPlayer(sender).isOp()) {
			Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
            onlinePlayers.stream().forEach((p) -> {
                if (p != this.getPlayer(sender) && !p.isOp()) {
					p.teleport(this.getPlayer(sender).getLocation());
				}
            });
			return true;
		}
		if (this.isPlayer(sender) && command.getName().equalsIgnoreCase("tpalladmins") && args.length == 0 && this.getPlayer(sender).isOp()) {
			Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
            onlinePlayers.stream().forEach((p) -> {
                if (p != this.getPlayer(sender) && p.isOp()) {
					p.teleport(this.getPlayer(sender).getLocation());
				}
            });
			return true;
		}
		if (this.isPlayer(sender) && command.getName().equalsIgnoreCase("sethome")) {
			if (this.getConfig().get("home_" + this.getPlayer(sender).getName() + "_world") == null) {
				this.getConfig().set("home_" + this.getPlayer(sender).getName() + "_world", (Object)((World)this.getServer().getWorlds().get(0)).getName());
				this.saveConfig();
			}
			this.getConfig().set("home_" + this.getPlayer(sender).getName() + "_x", (Object)this.getPlayer(sender).getLocation().getBlockX());
			this.getConfig().set("home_" + this.getPlayer(sender).getName() + "_y", (Object)this.getPlayer(sender).getLocation().getBlockY());
			this.getConfig().set("home_" + this.getPlayer(sender).getName() + "_z", (Object)this.getPlayer(sender).getLocation().getBlockZ());
			this.getConfig().set("home_" + this.getPlayer(sender).getName() + "_world", (Object)this.getPlayer(sender).getWorld().getName());
			this.saveConfig();
			this.getPlayer(sender).sendMessage("\u00a72Your home has been set");
			return true;
		}
		if (this.isPlayer(sender) && command.getName().equalsIgnoreCase("home")) {
			if (this.getConfig().get("home_" + this.getPlayer(sender).getName() + "_world") == null) {
				this.getConfig().set("home_" + this.getPlayer(sender).getName() + "_world", (Object)((World)this.getServer().getWorlds().get(0)).getName());
				this.saveConfig();
			}
			if (this.getConfig().get("home_" + this.getPlayer(sender).getName() + "_x") == null) {
				this.getPlayer(sender).sendMessage("\u00a72You don't have a home");
			} else {
				World w = this.getServer().getWorld((String)this.getConfig().get("home_" + this.getPlayer(sender).getName() + "_world"));
				if (w == null) {
					w = Bukkit.getServer().createWorld(new WorldCreator((String)this.getConfig().get("home_" + this.getPlayer(sender).getName() + "_world")));
				}
				if (w != null) {
					this.getPlayer(sender).teleport(new Location(w, (double)((Integer)this.getConfig().get("home_" + this.getPlayer(sender).getName() + "_x")).intValue(), (double)((Integer)this.getConfig().get("home_" + this.getPlayer(sender).getName() + "_y")).intValue(), (double)((Integer)this.getConfig().get("home_" + this.getPlayer(sender).getName() + "_z")).intValue()));
					this.getPlayer(sender).sendMessage("\u00a72Teleporting...");
				} else {
					this.getPlayer(sender).sendMessage("\u00a72The world, which you want to teleport in, was not found!");
				}
			}
			return true;
		}
		if (this.isPlayer(sender) && command.getName().equalsIgnoreCase("deletehome")) {
			if (this.getConfig().get("home_" + this.getPlayer(sender).getName() + "_world") == null) {
				this.getConfig().set("home_" + this.getPlayer(sender).getName() + "_world", (Object)((World)this.getServer().getWorlds().get(0)).getName());
				this.saveConfig();
			}
			if (this.getConfig().get("home_" + this.getPlayer(sender).getName() + "_x") == null) {
				this.getPlayer(sender).sendMessage("\u00a72You don't have a home that you could delete");
			} else {
				this.getConfig().set("home_" + this.getPlayer(sender).getName() + "_x", (Object)null);
				this.getConfig().set("home_" + this.getPlayer(sender).getName() + "_y", (Object)null);
				this.getConfig().set("home_" + this.getPlayer(sender).getName() + "_z", (Object)null);
				this.getConfig().set("home_" + this.getPlayer(sender).getName() + "_world", (Object)null);
				this.saveConfig();
				this.getPlayer(sender).sendMessage("\u00a72Your home has been deleted!");
			}
			return true;
		}
		return false;
    */
	}
}
