package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.custom_hbm.sound.LCEAudioWrapper;
import com.hbm.api.fluid.IFluidStandardReceiver;
import com.hbm.inventory.control_panel.ControlEventSystem;
import com.hbm.inventory.control_panel.IControllable;
import com.hbm.inventory.control_panel.types.DataValue;
import com.hbm.inventory.control_panel.types.DataValueFloat;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.items.machine.IItemFluidIdentifier;
import com.hbm.tileentity.IGUIProvider;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.tileentity.machine.TileEntityCore;
import com.hbm.tileentity.machine.TileEntityCoreInjector;
import com.leafia.AddonBase;
import com.leafia.contents.machines.powercores.dfc.components.injector.CoreInjectorContainer;
import com.leafia.contents.machines.powercores.dfc.components.injector.CoreInjectorGUI;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.init.LeafiaSoundEvents;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityInjector;
import com.leafia.settings.AddonConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
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

import java.util.HashMap;
import java.util.Map;

@Mixin(value = TileEntityCoreInjector.class)
public abstract class MixinTileEntityCoreInjector extends TileEntityMachineBase implements ITickable, IFluidStandardReceiver, IMixinTileEntityInjector, IGUIProvider, IControllable {
	@Shadow(remap = false) public FluidTankNTM[] tanks;
	@Unique public TileEntityCore lastGetCore;
	@Unique public BlockPos targetPosition = new BlockPos(0,0,0);

	@Unique LCEAudioWrapper leafia$sound;
	@Unique boolean leafia$isPlaying = false;
	@Unique void leafia$playSound() {
		if (leafia$sound == null) {
			leafia$sound = AddonBase.proxy.getLoopedSoundStartStop(
					world,
					LeafiaSoundEvents.laser2loop,
					LeafiaSoundEvents.laser2start,
					LeafiaSoundEvents.laser2stop,
					SoundCategory.BLOCKS,
					pos.getX()+0.5f,pos.getY()+0.5f,pos.getZ()+0.5f,
					1,1
			).setCustomAttenuation((intended,distance)->Math.pow(Math.max(0,1-distance/50),6)/4);
		}
		if (!leafia$isPlaying)
			leafia$sound.startSound();
		leafia$isPlaying = true;
	}
	@Unique void leafia$stopSound() {
		if (leafia$sound == null) return;
		if (leafia$isPlaying)
			leafia$sound.stopSound();
		leafia$isPlaying = false;
	}
	@Override
	public void validate(){
		super.validate();
		ControlEventSystem.get(world).addControllable(this);
	}
	@Override
	public void invalidate(){
		super.invalidate();
		if (leafia$sound != null) {
			leafia$sound.stopSound();
			leafia$sound = null;
		}
		ControlEventSystem.get(world).removeControllable(this);
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		if (leafia$sound != null) {
			leafia$sound.stopSound();
			leafia$sound = null;
		}
	}

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
		TileEntityCore core = leafia$getCore(AddonConfig.dfcComponentRange);
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
		} else {
			if (core != null)
				leafia$playSound();
			else
				leafia$stopSound();
		}
	}

	@Override
	public TileEntityCore leafia$lastGetCore() {
		return lastGetCore;
	}

	@Override
	public void leafia$lastGetCore(TileEntityCore core) {
		this.lastGetCore = core;
	}

	@Override
	public BlockPos leafia$getTargetPosition() {
		return targetPosition;
	}

	@Override
	public void leafia$targetPosition(BlockPos pos) {
		this.targetPosition = pos;
	}

	@Inject(method = "readFromNBT",at = @At("HEAD"),require = 1)
	public void onReadFromNBT(NBTTagCompound compound,CallbackInfo ci) {
		leafia$readTargetPos(compound);
	}

	@Inject(method = "writeToNBT",at = @At("HEAD"),require = 1)
	public void onWriteToNBT(NBTTagCompound compound,CallbackInfoReturnable<NBTTagCompound> cir) {
		leafia$writeTargetPos(compound);
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

	/**
	 * @author ntmleafia
	 * @reason me when the lasers
	 */
	@Overwrite(remap = false)
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public Map<String,DataValue> getQueryData() {
		Map<String,DataValue> map = new HashMap<>();
		map.put("tankA",new DataValueFloat(tanks[0].getFill()));
		map.put("tankB",new DataValueFloat(tanks[0].getFill()));
		return map;
	}

	@Override
	public BlockPos getControlPos() {
		return getPos();
	}
	@Override
	public World getControlWorld() {
		return getWorld();
	}
}
