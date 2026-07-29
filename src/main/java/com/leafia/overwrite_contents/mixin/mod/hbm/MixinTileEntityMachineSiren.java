package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.custom_hbm.sound.LCEAudioWrapper;
import com.hbm.inventory.control_panel.ControlEventSystem;
import com.hbm.items.machine.ItemCassette.SoundType;
import com.hbm.items.machine.ItemCassette.TrackType;
import com.hbm.lib.InventoryHelper;
import com.hbm.packet.PacketDispatcher;
import com.hbm.packet.toclient.TESirenPacket;
import com.hbm.tileentity.machine.TileEntityMachineSiren;
import com.leafia.AddonBase;
import com.leafia.compat.ComputronicsCompat;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntitySiren;
import com.leafia.unsorted.TileEntityMachineSirenSounder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.Loader;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = TileEntityMachineSiren.class)
public abstract class MixinTileEntityMachineSiren extends TileEntity implements IMixinTileEntitySiren, LeafiaPacketReceiver {
	@Shadow(remap = false)
	public abstract TrackType getCurrentType();

	@Unique public LCEAudioWrapper leafia$audio = new LCEAudioWrapper();
	@Unique int leafia$playingType = 0;
	@Unique boolean leafia$playing = false;

	@Override
	public double affectionRange() {
		return 0;
	}
	@Override
	public String getPacketIdentifier() {
		return "SIREN";
	}
	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		switch(key) {
			case 0 -> {
				if (leafia$playingType != (int)value) {
					TrackType type = TrackType.byIndex((int)value);
					if (leafia$audio != null)
						leafia$audio.stopSound();
					if (type == TrackType.NULL)
						leafia$audio = null;
					else {
						int vol = type.getVolume();
						leafia$audio = AddonBase.proxy.getLoopedSound(
								type.getSoundLocation(),SoundCategory.BLOCKS,
								pos.getX()+0.5f,pos.getY()+0.5f,pos.getZ()+0.5f,
								type.getVolume(),1
						).setCustomAttenuation((intended,dist)->{
							double minDist = 6;
							double maxDist = minDist+vol*1.5;
							double maxVolume = Math.min(vol,1);
							double linear = MathHelper.clamp((dist-minDist)/(maxDist-minDist),0,1);
							return maxVolume*Math.pow(1-linear,1);
						});
					}
					leafia$playing = false;
					leafia$playingType = type.getId();
				}
			}
			case 1 -> {
				if (leafia$audio != null)
					leafia$audio.setLooped((boolean)value);
			}
			case 2 -> {
				if (leafia$audio != null) {
					if ((boolean)value) {
						if (!leafia$playing)
							leafia$audio.startSound();
						leafia$playing = true;
					} else {
						if (leafia$playing)
							leafia$audio.stopSound();
						leafia$playing = false;
					}
				}
			}
		}
	}
	@Override
	public void onReceivePacketServer(byte key,Object value,EntityPlayer plr) { }
	@Override
	public void onPlayerValidate(EntityPlayer plr) { }

	@Shadow(remap = false)
	public boolean ctrlActive;
	@Shadow(remap = false)
	public boolean lock;
	@Unique final private List<TileEntityMachineSirenSounder> leafia$sounders = new ArrayList<>();
	@Unique @Final @Mutable boolean leafia$computronics;
	@Unique public boolean leafia$speakerMode = false;
	@Override
	public boolean leafia$speakerMode() {
		return leafia$speakerMode;
	}
	@Override
	public List<TileEntityMachineSirenSounder> leafia$sounders() {
		return leafia$sounders;
	}

	protected MixinTileEntityMachineSiren() {
    }

    @Unique
    boolean leafia$checkSpeakerMode() {
        boolean spk = false;
		for (EnumFacing face : EnumFacing.VALUES) {
			if (ComputronicsCompat.isAudioReceiver(world,pos,face)) {
				spk = true;
				InventoryHelper.dropInventoryItems(world,pos,this);
				break;
			}
		}
		return spk;
	}

	/**
	 * @author ntmleafia
	 * @reason tapes support
	 */
	@Overwrite
	public void update() {
		if(!world.isRemote) {
			if (leafia$computronics)
				leafia$speakerMode = leafia$checkSpeakerMode();
			// speaker check end

			TrackType currentType = getCurrentType();
			int id = currentType.getId();

			if(currentType == TrackType.NULL) {
				PacketDispatcher.wrapper.sendToDimension(new TESirenPacket(pos.getX(), pos.getY(), pos.getZ(), id, false), world.provider.getDimension());
				return;
			}

			boolean active = ctrlActive || world.isBlockPowered(pos);
			if (leafia$speakerMode)
				active = false;

			if(currentType.getType() == SoundType.LOOP) {
				// screw that
				//PacketDispatcher.wrapper.sendToDimension(new TESirenPacket(pos.getX(), pos.getY(), pos.getZ(), id, active), world.provider.getDimension());
				LeafiaPacket._start(this)
						.__write(0,currentType.getId())
						.__write(1,true)
						.__write(2,active)
						.__sendToClients(8192);
			} else {

				if(!lock && active) {
					lock = true;
					// screw this
					//PacketDispatcher.wrapper.sendToDimension(new TESirenPacket(pos.getX(), pos.getY(), pos.getZ(), id, false), world.provider.getDimension());
					//PacketDispatcher.wrapper.sendToDimension(new TESirenPacket(pos.getX(), pos.getY(), pos.getZ(), id, true), world.provider.getDimension());
					LeafiaPacket._start(this)
							.__write(0,currentType.getId())
							.__write(1,false)
							.__write(2,true)
							.__sendToClients(8192);
				}

				if(lock && !active) {
					lock = false;
					LeafiaPacket._start(this)
							.__write(2,false)
							.__sendToClients(8192);
				}
			}
		}
	}

	@Inject(method = "isUseableByPlayer",at = @At(value = "HEAD"),require = 1,remap = false,cancellable = true)
	public void onIsUsableByPlayer(EntityPlayer player,CallbackInfoReturnable<Boolean> cir) {
		if (leafia$speakerMode) {
			cir.setReturnValue(false);
			cir.cancel();
		}
	}

	@Inject(method = "<init>",at = @At(value = "TAIL"),require = 1,remap = false)
	public void onInit(CallbackInfo ci) {
		leafia$computronics = Loader.isModLoaded("computronics");
		for (int i = 0; i < 4; i++)
			leafia$sounders.add(new TileEntityMachineSirenSounder((TileEntityMachineSiren)(IMixinTileEntitySiren)this,i));
	}

	@Override
	public void invalidate() {
		ControlEventSystem.get(world).removeControllable((TileEntityMachineSiren)(IMixinTileEntitySiren)this);
		for (TileEntityMachineSirenSounder sounder : leafia$sounders)
			sounder.invalidate();
		leafia$sounders.clear();
		if (leafia$audio != null) {
			leafia$audio.stopSound();
			leafia$audio = null;
		}
		super.invalidate();
	}

	@Override
	public void onChunkUnload() {
		if (leafia$audio != null) {
			leafia$audio.stopSound();
			leafia$audio = null;
		}
		super.onChunkUnload();
	}

	@Inject(method = "validate",at = @At(value = "TAIL"),require = 1)
	public void onValidate(CallbackInfo ci) {
		for (TileEntityMachineSirenSounder sounder : leafia$sounders)
			sounder.validate();
	}
}
