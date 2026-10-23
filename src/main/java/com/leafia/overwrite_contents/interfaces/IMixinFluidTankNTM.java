// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.interfaces;

import net.minecraft.nbt.NBTTagCompound;

public interface IMixinFluidTankNTM {

    NBTTagCompound leafia$getAddonNbt();

    void leafia$setAddonNbt(NBTTagCompound nbt);
}
