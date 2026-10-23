// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.interfaces;

import net.minecraft.entity.player.EntityPlayer;

public interface IMixinEntityItem {

    EntityPlayer leafia$getDroppedBy();

    void leafia$setDroppedBy(EntityPlayer player);

    boolean leafia$getWasPickedUp();

    void leafia$setWasPickedUp(boolean pickedUp);
}
