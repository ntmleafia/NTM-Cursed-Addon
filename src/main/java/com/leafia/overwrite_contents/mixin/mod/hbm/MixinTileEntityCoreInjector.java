package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.api.fluid.IFluidStandardReceiver;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.items.machine.IItemFluidIdentifier;
import com.hbm.tileentity.IGUIProvider;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.tileentity.machine.TileEntityCore;
import com.hbm.tileentity.machine.TileEntityCoreInjector;
import com.leafia.contents.machines.powercores.dfc.components.injector.CoreInjectorContainer;
import com.leafia.contents.machines.powercores.dfc.components.injector.CoreInjectorGUI;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityInjector;
import com.leafia.settings.AddonConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TileEntityCoreInjector.class)
public abstract class MixinTileEntityCoreInjector extends TileEntityMachineBase implements ITickable, IFluidStandardReceiver, IMixinTileEntityInjector, IGUIProvider {
	@Shadow(remap = false) public FluidTankNTM[] tanks;
	@Unique public TileEntityCore lastGetCore;
	@Unique public BlockPos targetPosition = new BlockPos(0,0,0);

	public MixinTileEntityCoreInjector(int scount) {
		super(scount);
	}

	public FluidType getFluid(int slot) {
		ItemStack stack = inventory.getStackInSlot(slot);
		if (stack.getItem() instanceof IItemFluidIdentifier identifier)
			return identifier.getType(world,pos.getX(),pos.getY(),pos.getZ(),stack);
		return Fluids.NONE;
	}

	/**
	 * @author ntmleafia
	 * @reason asjdkjflkdjslkg
	 */
	@Override
	@Overwrite
	public void update() {
		TileEntityCore core = getCore(AddonConfig.dfcComponentRange);
		if (!world.isRemote) {
			this.subscribeToAllAround(this.tanks[0].getTankType(), this);
			this.subscribeToAllAround(this.tanks[1].getTankType(), this);

			if (!getFluid(0).equals(tanks[1].getTankType()))
				this.tanks[0].setType(0, 1, this.inventory);
			if (!getFluid(2).equals(tanks[0].getTankType()))
				this.tanks[1].setType(2, 3, this.inventory);
			LeafiaPacket._start(this).__write(31,targetPosition).__sendToAffectedClients();
			if (core != null) {
				for(int t = 0; t < 2; ++t) {
					if (core.tanks[t].getTankType() == this.tanks[t].getTankType()) {
						int f = Math.min(this.tanks[t].getFill(), core.tanks[t].getMaxFill() - core.tanks[t].getFill());
						this.tanks[t].setFill(this.tanks[t].getFill() - f);
						core.tanks[t].setFill(core.tanks[t].getFill() + f);
						core.markDirty();
					} else if (core.tanks[t].getFill() == 0) {
						core.tanks[t].setTankType(this.tanks[t].getTankType());
						int f = Math.min(this.tanks[t].getFill(), core.tanks[t].getMaxFill() - core.tanks[t].getFill());
						this.tanks[t].setFill(this.tanks[t].getFill() - f);
						core.tanks[t].setFill(core.tanks[t].getFill() + f);
						core.markDirty();
					}
				}
			}
			this.markDirty();
			this.networkPackNT(250);
		}
	}

	@Override
	public TileEntityCore lastGetCore() {
		return lastGetCore;
	}

	@Override
	public void lastGetCore(TileEntityCore core) {
		this.lastGetCore = core;
	}

	@Override
	public BlockPos getTargetPosition() {
		return targetPosition;
	}

	@Override
	public void targetPosition(BlockPos pos) {
		this.targetPosition = pos;
	}

	@Inject(method = "readFromNBT",at = @At("HEAD"),require = 1)
	public void onReadFromNBT(NBTTagCompound compound,CallbackInfo ci) {
		readTargetPos(compound);
	}

	@Inject(method = "writeToNBT",at = @At("HEAD"),require = 1)
	public void onWriteToNBT(NBTTagCompound compound,CallbackInfoReturnable<NBTTagCompound> cir) {
		writeTargetPos(compound);
	}

	@Override
	public String getPacketIdentifier() {
		return "dfc_injector";
	}

	/**
	 * @author ntmleafia
	 * @reason uses different gui
	 */
	@Override
	@Overwrite(remap = false)
	public Container provideContainer(int i,EntityPlayer entityPlayer,World world,int i1,int i2,int i3) {
		return new CoreInjectorContainer(entityPlayer.inventory,(TileEntityCoreInjector)(IMixinTileEntityInjector)this);
	}

	/**
	 * @author ntmleafia
	 * @reason uses different gui
	 */
	@Override
	@Overwrite(remap = false)
	@SideOnly(Side.CLIENT)
	public GuiScreen provideGUI(int i,EntityPlayer entityPlayer,World world,int i1,int i2,int i3) {
		return new CoreInjectorGUI(entityPlayer.inventory,(TileEntityCoreInjector)(IMixinTileEntityInjector)this);
	}
}
