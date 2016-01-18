/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tpl.simpletp;

import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 *
 * @author techplex
 */
public class Warp implements ConfigurationSerializable {
	public Location location = null;
	public String message = "";
	public String name = "A warp";
	public boolean sound = false; //play the portal sound on warp
//    public UUID creator;

	public Warp(String name, String message, Location location, boolean sound/*, UUID creator*/) {
		this.name = name;
		this.message = message;
		this.location = location;
		this.sound = sound;
//        this.creator = creator;
	}

    @Override
    public Map<String, Object> serialize() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
