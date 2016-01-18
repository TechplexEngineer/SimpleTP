/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tpl.simpletp;

import org.bukkit.Location;

/**
 *
 * @author techplex
 */
public class Warp {
	public Location location = null;
	public String message = "";
	public String name = "A warp";
	public int sound = 0;

	public Warp(String name, String message, Location location, int sound) {
		this.name = name;
		this.message = message;
		this.location = location;
		this.sound = sound;
	}
}
