package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.api.fluidmk2.FluidNode;
import com.hbm.api.fluidmk2.IFluidStandardTransceiverMK2;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.lib.DirPos;
import com.hbm.lib.ForgeDirection;
import com.hbm.lib.Library;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.tileentity.machine.TileEntityBarrel;
import com.hbm.uninos.UniNodespace;
import com.leafia.contents.network.pipe_amat.uninos.AmatNet;
import com.leafia.contents.network.pipe_amat.uninos.AmatNode;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.HashSet;

@Mixin(value = TileEntityBarrel.class)
public abstract class MixinTileEntityBarrel extends TileEntityMachineBase implements IFluidStandardTransceiverMK2 {
	@Shadow(remap=false) public FluidTankNTM tankNew;
	@Shadow(remap=false) public short mode = 0;

	public MixinTileEntityBarrel(int scount) {
		super(scount);
	}
	/**
	 * @author ntmleafia
	 * @reason only accept top and bottom
	 */
	@Overwrite(remap = false)
	public boolean hasCapability(Capability<?> capability,@Nullable EnumFacing facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			if (facing != null) {
				if (!facing.equals(EnumFacing.UP) && !facing.equals(EnumFacing.DOWN))
					return false;
			}
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	/**
	 * @author ntmleafia
	 * @reason only accept top and bottom
	 */
	@Overwrite(remap = false)
	protected DirPos[] getConPos() {
		return new DirPos[]{
				//new DirPos(pos.getX() + 1, pos.getY(), pos.getZ(), Library.POS_X),
				//new DirPos(pos.getX() - 1, pos.getY(), pos.getZ(), Library.NEG_X),
				new DirPos(pos.getX(), pos.getY() + 1, pos.getZ(), Library.POS_Y),
				new DirPos(pos.getX(), pos.getY() - 1, pos.getZ(), Library.NEG_Y)
				//new DirPos(pos.getX(), pos.getY(), pos.getZ() + 1, Library.POS_Z),
				//new DirPos(pos.getX(), pos.getY(), pos.getZ() - 1, Library.NEG_Z)
		};
	}

	@Override
	public boolean canConnect(FluidType type,ForgeDirection dir) {
		if (dir != ForgeDirection.UP && dir != ForgeDirection.DOWN)
			return false;
		return IFluidStandardTransceiverMK2.super.canConnect(type,dir);
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

	@Inject(method = "update",at = @At(value = "INVOKE", target = "Lcom/hbm/inventory/fluid/tank/FluidTankNTM;unloadTank(IILnet/minecraftforge/items/IItemHandler;)Z",shift = Shift.AFTER,remap=false),require = 1)
	public void onOnUpdate(CallbackInfo ci) {
		if (mode == 1) {
			if (amat == null || amat.expired || tankNew.getTankType() != lastTypeAmat) {
				amat = UniNodespace.getNode(world,pos,AmatNet.getProvider(tankNew.getTankType()));
				if (amat == null || amat.expired || tankNew.getTankType() != lastTypeAmat) {
					amat = createAmatNode(tankNew.getTankType());
					UniNodespace.createNode(world,amat);
					lastTypeAmat = tankNew.getTankType();
				}
			}
			if (amat != null && amat.hasValidNet()) {
				amat.net.addProvider(this);
				amat.net.addReceiver(this);
			}
		} else {
			if (amat != null) {
				UniNodespace.destroyNode(world,pos,AmatNet.getProvider(tankNew.getTankType()));
				amat = null;
			}
			for(DirPos pos : this.getConPos()) {
				AmatNode dirNode = (AmatNode)UniNodespace.getNode(world, pos.getPos(),AmatNet.getProvider(tankNew.getTankType()));

				if(mode == 2) {
					tryProvide(tankNew, world, pos.getPos(), pos.getDir());
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
