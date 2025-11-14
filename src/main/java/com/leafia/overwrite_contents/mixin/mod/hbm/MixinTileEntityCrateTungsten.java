package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.interfaces.ILaserable;
import com.hbm.lib.ForgeDirection;
import com.hbm.tileentity.IBufPacketReceiver;
import com.hbm.tileentity.IGUIProvider;
import com.hbm.tileentity.machine.TileEntityCrate;
import com.hbm.tileentity.machine.TileEntityCrateTungsten;
import com.leafia.contents.network.spk_cable.uninos.ISPKReceiver;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityCrateTungsten.class)
public abstract class MixinTileEntityCrateTungsten extends TileEntityCrate implements IBufPacketReceiver, ITickable, ILaserable, IGUIProvider, ISPKReceiver {

    public MixinTileEntityCrateTungsten(int scount, String name) {
        super(scount, name);
    }

    @Shadow(remap = false)
    public long joules;

    @Shadow
    public abstract void addEnergy(World world, BlockPos pos, long energy, EnumFacing dir);

    @Inject(method = "update()V", at = @At(value = "FIELD", target = "Lcom/hbm/tileentity/machine/TileEntityCrateTungsten;heatTimer:I", ordinal = 0, shift = At.Shift.BEFORE, remap = false))
    public void onUpdate(CallbackInfo ci) {
        for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            trySubscribeSPK(world, pos, dir);
        }
    }

    @Override
    public long transferSPK(long power, boolean simulate) {
        if (!simulate) {
            addEnergy(null, null, power, null);
            joules = power;
        }
        return 0L;
    }

    @Override
    public long getSPK() {
        return joules;
    }

    @Override
    public void setSPK(long power) {
        joules = power;
    }

    @Override
    public long getMaxSPK() {
        return Long.MAX_VALUE;
    }
}


