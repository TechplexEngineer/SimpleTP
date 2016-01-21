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
    
    public static String PERM_BASE = "simpletp";
    
    public ArrayList<Warp> warps = new ArrayList();
	public int last_config_warps_amount = 0;
    public static Main inst;

    @Override
	public void onEnable() {
        inst = this;
        
        getCommand("warp").setExecutor(new WarpCMD());
        
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
}
