package com.leafia.contents.effects.folkvangr.visual;

import com.hbm.entity.effect.EntityCloudFleija;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;

public class EntityCloudFleijaBase {
	public static final DataParameter<Integer> MAXAGE = EntityDataManager.createKey(EntityCloudFleija.class,DataSerializers.VARINT);
	public static final DataParameter<Float> SCALE = EntityDataManager.createKey(EntityCloudFleija.class,DataSerializers.FLOAT);
	public static final DataParameter<Float> TICKRATE = EntityDataManager.createKey(EntityCloudFleija.class,DataSerializers.FLOAT);
	public static final DataParameter<Boolean> ANTISCHRAB = EntityDataManager.createKey(EntityCloudFleija.class,DataSerializers.BOOLEAN);
	public static final DataParameter<Boolean> FINISHED = EntityDataManager.createKey(EntityCloudFleija.class,DataSerializers.BOOLEAN);
}
