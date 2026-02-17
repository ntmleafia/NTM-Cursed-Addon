package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.api.fluidmk2.IFluidStandardTransceiverMK2;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.lib.DirPos;
import com.hbm.lib.ForgeDirection;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.tileentity.machine.TileEntityMachineFluidTank;
import com.hbm.uninos.UniNodespace;
import com.leafia.contents.network.pipe_amat.uninos.AmatNet;
import com.leafia.contents.network.pipe_amat.uninos.AmatNode;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;

@Mixin(value = TileEntityMachineFluidTank.class)
public abstract class MixinTileEntityMachineFluidTank extends TileEntityMachineBase implements IFluidStandardTransceiverMK2 {
	@Shadow(remap = false) protected abstract DirPos[] getConPos();
	@Shadow(remap=false) public FluidTankNTM tank;
	@Shadow(remap=false) public short mode = 0;

	public MixinTileEntityMachineFluidTank(int scount) {
		super(scount);
	}

	@Unique protected FluidType lastTypeAmat;
	@Unique protected AmatNode amat;
	@Unique protected AmatNode createAmatNode(FluidType type) {
		DirPos[] conPos = getConPos();

		HashSet<BlockPos> posSet = new HashSet<>();
		posSet.add(pos);
		for(DirPos pos : conPos) {
			ForgeDirection dir = pos.getDir();
			posSet.add(new BlockPos(pos.getPos().getX() - dir.offsetX, pos.getPos().getY() - dir.offsetY, pos.getPos().getZ() - dir.offsetZ));
		}

		return new AmatNode(AmatNet.getProvider(type), posSet.toArray(new BlockPos[posSet.size()])).setConnections(conPos);
	}

	@Inject(method = "update",at = @At(value = "FIELD", target = "Lcom/hbm/tileentity/machine/TileEntityMachineFluidTank;mode:S",ordinal = 0,remap=false),require = 1)
	public void onOnUpdate(CallbackInfo ci) {
		if (mode == 1) {
			if (amat == null || amat.expired || tank.getTankType() != lastTypeAmat) {
				amat = UniNodespace.getNode(world,pos,AmatNet.getProvider(tank.getTankType()));
				if (amat == null || amat.expired || tank.getTankType() != lastTypeAmat) {
					amat = createAmatNode(tank.getTankType());
					UniNodespace.createNode(world,amat);
					lastTypeAmat = tank.getTankType();
				}
			}
			if (amat != null && amat.hasValidNet()) {
				amat.net.addProvider(this);
				amat.net.addReceiver(this);
			}
		} else {
			if (amat != null) {
				UniNodespace.destroyNode(world,pos,AmatNet.getProvider(tank.getTankType()));
				amat = null;
			}
			for(DirPos pos : this.getConPos()) {
				AmatNode dirNode = (AmatNode) UniNodespace.getNode(world, pos.getPos(),AmatNet.getProvider(tank.getTankType()));

				if(mode == 2) {
					tryProvide(tank, world, pos.getPos(), pos.getDir());
				} else {
					if(dirNode != null && dirNode.hasValidNet()) dirNode.net.removeProvider(this);
				}

				if(mode == 0) {
					if(dirNode != null && dirNode.hasValidNet()) dirNode.net.addReceiver(this);
				} else {
					if(dirNode != null && dirNode.hasValidNet()) dirNode.net.removeReceiver(this);
				}
			}
		}
	}
}
