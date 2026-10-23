// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-FileCopyrightText: 2025-2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.api.fluidmk2.FluidNetMK2;
import com.hbm.api.fluidmk2.FluidNode;
import com.hbm.api.fluidmk2.IFluidProviderMK2;
import com.hbm.api.fluidmk2.IFluidReceiverMK2;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.trait.FluidTraitSimple.FT_Amat;
import com.hbm.uninos.NodeNet;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = FluidNetMK2.class, remap = false)
public abstract class MixinFluidNetMK2 extends NodeNet<IFluidReceiverMK2, IFluidProviderMK2, FluidNode, FluidNetMK2> {
    @Shadow protected FluidType type;

    @Unique
    public boolean checkExplode(IFluidProviderMK2 provider) {
        if (!type.hasTrait(FT_Amat.class) || !(provider instanceof TileEntity tile)) return false;
        var receivers = receiverEntries.object2LongEntrySet().fastIterator();
        while (receivers.hasNext()) {
            if (receivers.next().getKey() == provider) continue;
            tile.getWorld().newExplosion(null, tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), 5, true, true);
            return true;
        }
        return false;
    }

    @Redirect(method = "setupFluidProviders", at = @At(value = "INVOKE",
            target = "Lcom/hbm/api/fluidmk2/FluidNetMK2;isBadLink(Ljava/lang/Object;)Z"), require = 1)
    private boolean leafia$rejectUnsafeProvider(Object provider) {
        return NodeNet.isBadLink(provider) || checkExplode((IFluidProviderMK2) provider);
    }
}
