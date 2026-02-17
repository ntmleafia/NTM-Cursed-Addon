package com.leafia.contents.network.ff_duct;

import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.lib.ForgeDirection;
import com.hbm.uninos.UniNodespace;
import com.leafia.contents.network.ff_duct.uninos.FFNet;
import com.leafia.contents.network.ff_duct.uninos.FFNode;
import com.leafia.contents.network.ff_duct.uninos.IFFConductor;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public class FFDuctTE extends TileEntity implements ITickable, IFFConductor, LeafiaPacketReceiver {
	public FluidType type = Fluids.NONE;
	protected FFNode node;

	@Override
	public void update() {
		if (!world.isRemote) {
			if (type.getFF() != null) {
				if (this.node == null || this.node.expired) {
					if (this.shouldCreateNode()) {
						this.node = UniNodespace.getNode(world,pos,FFNet.PROVIDER);
						if (this.node == null || this.node.expired) {
							this.node = this.createNode();
							UniNodespace.createNode(world,node);
						}
					}
				}
			}
			/* ah forget it
			for (EnumFacing face : EnumFacing.values()) {
				TileEntity te = world.getTileEntity(pos.offset(face));
				if (te != null) {
					if (te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,face.getOpposite())) {
						IFluidHandler handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,face.getOpposite());

					}
				}
			}*/
		}
	}
	public boolean shouldCreateNode() {
		return true;
	}
	@Override
	public void invalidate() {
		super.invalidate();
		if (!world.isRemote) {
			if (node != null)
				UniNodespace.destroyNode(world,pos,FFNet.PROVIDER);
		}
	}

	public FluidType getType() {
		return this.type;
	}

	public void setType(FluidType type) {
		FluidType prev = this.type;
		this.type = type;
		this.markDirty();

		if (world instanceof WorldServer) {
			IBlockState state = world.getBlockState(pos);
			world.notifyBlockUpdate(pos, state, state, 3);
			world.markBlockRangeForRenderUpdate(pos, pos);

			sendTypeUpdatePacket();
		}

		UniNodespace.destroyNode(world, pos, prev.getNetworkProvider());

		if(this.node != null) {
			this.node = null;
		}
	}

	@Override
	public boolean canConnect(@Nullable FluidStack stack,ForgeDirection dir) {
		//if (stack == null)
			//throw new LeafiaDevFlaw("canConnect stack cannot be null");
		return dir != ForgeDirection.UNKNOWN && (stack == null || (type.getFF() == null ? type.getFF() == stack.getFluid() : type.getFF().equals(stack.getFluid())));
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		setType(Fluids.fromID(compound.getInteger("fluid")));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setInteger("fluid",type.getID());
		return super.writeToNBT(compound);
	}

	@Override
	public String getPacketIdentifier() {
		return "FF_DUCT";
	}

	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		if (key == 31) {
			type = Fluids.fromID((int)value);
			if (world != null)
				world.markBlockRangeForRenderUpdate(pos, pos);
		}
	}

	@Override
	public void onReceivePacketServer(byte key,Object value,EntityPlayer plr) { }

	@Override
	public void onPlayerValidate(EntityPlayer plr) {
		LeafiaPacket._start(this).__write(31,type.getID()).__sendToClient(plr);
	}

	public void sendTypeUpdatePacket() {
		LeafiaPacket._start(this).__write(31,type.getID()).__sendToAffectedClients();
	}
}
