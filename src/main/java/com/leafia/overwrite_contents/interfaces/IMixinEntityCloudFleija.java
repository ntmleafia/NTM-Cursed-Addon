package com.leafia.overwrite_contents.interfaces;

import com.leafia.contents.effects.folkvangr.EntityNukeFolkvangr;

public interface IMixinEntityCloudFleija {
	boolean getIsAntischrab();
	void setIsAntischrab(boolean value);
	double getScale();
	void setScale(double value);
	EntityNukeFolkvangr getBound();
	void setBound(EntityNukeFolkvangr value);
}
