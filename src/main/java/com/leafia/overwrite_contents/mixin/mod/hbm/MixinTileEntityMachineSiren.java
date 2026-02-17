package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.inventory.control_panel.ControlEventSystem;
import com.hbm.items.machine.ItemCassette.SoundType;
import com.hbm.items.machine.ItemCassette.TrackType;
import com.hbm.lib.InventoryHelper;
import com.hbm.packet.PacketDispatcher;
import com.hbm.packet.toclient.TESirenPacket;
import com.hbm.tileentity.machine.TileEntityMachineSiren;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntitySiren;
import com.leafia.unsorted.TileEntityMachineSirenSounder;
import gnu.trove.set.hash.TIntHashSet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pl.asie.computronics.api.audio.AudioPacket;
import pl.asie.computronics.api.audio.IAudioReceiver;
import pl.asie.computronics.audio.AudioUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static pl.asie.computronics.reference.Capabilities.AUDIO_RECEIVER_CAPABILITY;

@Mixin(value = TileEntityMachineSiren.class)
@Optional.InterfaceList({
		@Optional.Interface(iface = "pl.asie.computronics.api.audio.IAudioReceiver", modid = "computronics")
})
public abstract class MixinTileEntityMachineSiren extends TileEntity implements IMixinTileEntitySiren, IAudioReceiver {
	@Shadow(remap = false)
	public abstract TrackType getCurrentType();

	@Shadow(remap = false)
	public boolean ctrlActive;
	@Shadow(remap = false)
	public boolean lock;
	@Unique final private List<TileEntityMachineSirenSounder> sounders = new ArrayList<>();
	@Unique @Final @Mutable boolean computronics;
	@Unique public boolean speakerMode = false;
	@Override
	public boolean speakerMode() {
		return speakerMode;
	}

	protected MixinTileEntityMachineSiren() {
    }

    @Unique
    @Optional.Method(modid="computronics")
    boolean checkSpeakerMode() {
        boolean spk = false;
		for (EnumFacing face : EnumFacing.VALUES) {
			TileEntity ate = world.getTileEntity(pos.offset(face));
			if (computronics && ate != null && ate.hasCapability(AUDIO_RECEIVER_CAPABILITY,face.getOpposite())) {
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
			if (computronics)
				speakerMode = checkSpeakerMode();
			// speaker check end

			int id = Arrays.asList(TrackType.VALUES).indexOf(getCurrentType());

			if(getCurrentType() == TrackType.NULL) {
				PacketDispatcher.wrapper.sendToDimension(new TESirenPacket(pos.getX(), pos.getY(), pos.getZ(), id, false), world.provider.getDimension());
				return;
			}

			boolean active = ctrlActive || world.isBlockPowered(pos);
			if (speakerMode)
				active = false;

			if(getCurrentType().getType() == SoundType.LOOP) {

				PacketDispatcher.wrapper.sendToDimension(new TESirenPacket(pos.getX(), pos.getY(), pos.getZ(), id, active), world.provider.getDimension());
			} else {

				if(!lock && active) {
					lock = true;
					PacketDispatcher.wrapper.sendToDimension(new TESirenPacket(pos.getX(), pos.getY(), pos.getZ(), id, false), world.provider.getDimension());
					PacketDispatcher.wrapper.sendToDimension(new TESirenPacket(pos.getX(), pos.getY(), pos.getZ(), id, true), world.provider.getDimension());
				}

				if(lock && !active) {
					lock = false;
				}
			}
		}
	}

	@Inject(method = "isUseableByPlayer",at = @At(value = "HEAD"),require = 1,remap = false,cancellable = true)
	public void onIsUsableByPlayer(EntityPlayer player,CallbackInfoReturnable<Boolean> cir) {
		if (speakerMode) {
			cir.setReturnValue(false);
			cir.cancel();
		}
	}

	@Inject(method = "<init>",at = @At(value = "TAIL"),require = 1,remap = false)
	public void onInit(CallbackInfo ci) {
		computronics = Loader.isModLoaded("computronics");
		for (int i = 0; i < 4; i++)
			sounders.add(new TileEntityMachineSirenSounder((TileEntityMachineSiren)(IMixinTileEntitySiren)this,i));
	}

	@Override
	public void invalidate() {
		ControlEventSystem.get(world).removeControllable((TileEntityMachineSiren)(IMixinTileEntitySiren)this);
		for (TileEntityMachineSirenSounder sounder : sounders)
			sounder.invalidate();
		sounders.clear();
		super.invalidate();
	}

	@Inject(method = "validate",at = @At(value = "TAIL"),require = 1)
	public void onValidate(CallbackInfo ci) {
		for (TileEntityMachineSirenSounder sounder : sounders)
			sounder.validate();
	}

	// TAPES //
	@Override
	@Optional.Method(modid="computronics")
	public World getSoundWorld() {
		return world;
	}

	@Override
	@Optional.Method(modid="computronics")
	public Vec3d getSoundPos() {
		return new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
	}

	@Override
	@Optional.Method(modid="computronics")
	public int getSoundDistance() {
		return 128;
	}
	@Unique private final TIntHashSet packetIds = new TIntHashSet();
	@Unique private long idTick = -1;
	@Override
	@Optional.Method(modid="computronics")
	public void receivePacket(AudioPacket packet,@Nullable EnumFacing direction) {
		if(!hasWorld() || idTick == world.getTotalWorldTime()) {
			if(packetIds.contains(packet.id)) {
				return;
			}
		} else {
			idTick = world.getTotalWorldTime();
			packetIds.clear();
		}
		packetIds.add(packet.id);
		for (TileEntityMachineSirenSounder sounder : sounders)
			packet.addReceiver(sounder); // fuck it, I ain't coding a whole new packet handler just to make it louder
	}

	@Override
	@Optional.Method(modid="computronics")
	public String getID() {
		return AudioUtils.positionId(getPos());
	}

	@Override
	@Optional.Method(modid="computronics")
	public boolean connectsAudio(EnumFacing enumFacing) {
		return true;
	}
}
