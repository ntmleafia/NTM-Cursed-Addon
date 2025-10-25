package com.leafia.overwrite_contents.interfaces;

import com.hbm.entity.effect.EntityCloudFleija;
import com.leafia.contents.effects.folkvangr.EntityNukeFolkvangr;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;

public interface IMixinEntityCloudFleija {
    DataParameter<Float> SCALE = EntityDataManager.createKey(EntityCloudFleija.class, DataSerializers.FLOAT);
    DataParameter<Float> TICKRATE = EntityDataManager.createKey(EntityCloudFleija.class, DataSerializers.FLOAT);
    DataParameter<Boolean> ANTISCHRAB = EntityDataManager.createKey(EntityCloudFleija.class, DataSerializers.BOOLEAN);
    DataParameter<Boolean> FINISHED = EntityDataManager.createKey(EntityCloudFleija.class, DataSerializers.BOOLEAN);
	boolean getIsAntischrab();
	IMixinEntityCloudFleija setAntischrab();
	float getScale();
	void setScale(float value);
	EntityNukeFolkvangr getBound();
	void setBound(EntityNukeFolkvangr value);
	float getTickrate();
    boolean isFinished();
}
