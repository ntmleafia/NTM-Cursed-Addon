package com.leafia.overwrite_contents.interfaces;

import com.leafia.contents.effects.folkvangr.EntityNukeFolkvangr;

public interface IMixinEntityCloudFleija {
	boolean getIsAntischrab();
	IMixinEntityCloudFleija setAntischrab();
	double getScale();
	void setScale(double value);
	EntityNukeFolkvangr getBound();
	void setBound(EntityNukeFolkvangr value);
	float getTickrate();
}
