// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.blocks.ModBlocks;
import com.hbm.handler.radiation.ChunkRadiationManager;
import com.hbm.items.machine.ItemFELCrystal.EnumWavelengths;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.tileentity.machine.TileEntityCore;
import com.hbm.tileentity.machine.TileEntityFEL;
import com.hbm.util.ContaminationUtil;
import com.leafia.dev.optimization.LeafiaParticlePacket.DFCBlastParticle;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCore;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityFEL.class)
public abstract class MixinTileEntityFEL extends TileEntityMachineBase {
    @Shadow(remap = false)
    public EnumWavelengths mode;

    @Shadow(remap = false)
    @Final
    public static int powerReq;

    public MixinTileEntityFEL(int scount) {
        super(scount);
    }

    // CE has already updated the beam distance. A core absorbs the beam and ends the server tick with the usual sync.
    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getExplosionResistance(Lnet/minecraft/entity/Entity;)F"), cancellable = true, require = 1, allow = 1)
    private void leafia$absorbBeamAtCore(CallbackInfo ci, @Local IBlockState state,
                                       @Local(name = "x") double x, @Local(name = "y") double y, @Local(name = "z") double z) {
        if (state.getBlock() != ModBlocks.dfc_core) return;
		BlockPos p = new BlockPos(x,y,z);
		TileEntity te = world.getTileEntity(p);
		if (te instanceof TileEntityCore core) {
			IMixinTileEntityCore mixin = (IMixinTileEntityCore)core;
			double scale = 0.4;
			double power = (powerReq * ((mode.ordinal() == 0) ? 0 : Math.pow(4, mode.ordinal())))/5000;
			mixin.setDFCIncomingSpk(mixin.getDFCIncomingSpk() + power);
			switch(this.mode) {
				case IR:
					mixin.setDFCTemperature(mixin.getDFCTemperature() + 15*scale);
					break;
				case VISIBLE:
					mixin.setDFCTemperature(mixin.getDFCTemperature() + 7*scale);
					break;
				case UV:
					mixin.setDFCTemperature(mixin.getDFCTemperature() + 8.5*scale);
					break;
				case GAMMA: {
					mixin.setDFCTemperature(mixin.getDFCTemperature() + 32.5*scale);
					DFCBlastParticle blast = new DFCBlastParticle(0.4f,1f,0.2f,5);
					blast.emit(new Vec3d(p).add(0.5,0.5,0.5),new Vec3d(0,1,0),world.provider.getDimension(),200);
					ChunkRadiationManager.proxy.incrementRad(world,p,5,50);
				} break;
				case DRX: {
					mixin.setDFCTemperature(mixin.getDFCTemperature() + 75.5*scale);
					DFCBlastParticle blast = new DFCBlastParticle(1,0.2f,0.2f,10);
					blast.emit(new Vec3d(p).add(0.5,0.5,0.5),new Vec3d(0,1,0),world.provider.getDimension(),200);
					ContaminationUtil.radiate(world,x+0.5,y+0.5,z+0.5,64,0,15,0);
				} break;
			}
		}
        networkPackNT(250);
        ci.cancel();
    }
}
