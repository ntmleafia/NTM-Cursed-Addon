// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-FileCopyrightText: 2025 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.interfaces;

import net.minecraft.entity.Entity;

public interface IMixinEntityNukeTorex {
	double getInitPosX();
	double getInitPosY();
	double getInitPosZ();
	void setInitPosX(double value);
	void setInitPosY(double value);
	void setInitPosZ(double value);
	boolean getValid();
	void setValid(boolean value);
    Entity getBoundEntity();
    void setBoundEntity(Entity entity);
    boolean getCalculationFinished();
    void setCalculationFinished(boolean value);
}
