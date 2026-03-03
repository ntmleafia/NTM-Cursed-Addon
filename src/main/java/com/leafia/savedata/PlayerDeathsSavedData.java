package com.leafia.savedata;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class PlayerDeathsSavedData extends WorldSavedData {
	public static final String key = "leafiaPlayerDeaths";
	public final Map<String,Long> timestamps = new HashMap<>();
	public static PlayerDeathsSavedData forWorld(World world) {
		PlayerDeathsSavedData data = (PlayerDeathsSavedData)world.getPerWorldStorage().getOrLoadData(PlayerDeathsSavedData.class,key);
		if (data == null) {
			world.getPerWorldStorage().setData(key,new PlayerDeathsSavedData(key));
			data = (PlayerDeathsSavedData)world.getPerWorldStorage().getOrLoadData(PlayerDeathsSavedData.class,key);
		}
		return data;
	}

	public PlayerDeathsSavedData(String name) {
		super(name);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		for (String s : nbt.getKeySet())
			timestamps.put(s,nbt.getLong(s));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		for (Entry<String,Long> entry : timestamps.entrySet())
			compound.setLong(entry.getKey(),entry.getValue());
		return compound;
	}
}
