package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.tileentity.machine.TileEntityMachineSiren;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntitySiren;
import com.leafia.unsorted.TileEntityMachineSirenSounder;
import gnu.trove.set.hash.TIntHashSet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import pl.asie.computronics.api.audio.AudioPacket;
import pl.asie.computronics.api.audio.IAudioReceiver;
import pl.asie.computronics.audio.AudioUtils;

import javax.annotation.Nullable;

@Mixin(value = TileEntityMachineSiren.class)
public abstract class MixinTileEntityMachineSirenComputronics extends TileEntity implements IMixinTileEntitySiren, IAudioReceiver {
	@Unique private final TIntHashSet leafia$packetIds = new TIntHashSet();
	@Unique private long leafia$idTick = -1;

	@Override
	public World getSoundWorld() {
		return world;
	}

	@Override
	public Vec3d getSoundPos() {
		return new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
	}

	@Override
	public int getSoundDistance() {
		return 128;
	}

	@Override
	public void receivePacket(AudioPacket packet,@Nullable EnumFacing direction) {
		if(!hasWorld() || leafia$idTick == world.getTotalWorldTime()) {
			if(leafia$packetIds.contains(packet.id)) {
				return;
			}
		} else {
			leafia$idTick = world.getTotalWorldTime();
			leafia$packetIds.clear();
		}
		leafia$packetIds.add(packet.id);
		for (TileEntityMachineSirenSounder sounder : leafia$sounders())
			packet.addReceiver(sounder); // fuck it, I ain't coding a whole new packet handler just to make it louder
	}

	@Override
	public String getID() {
		return AudioUtils.positionId(getPos());
	}

	@Override
	public boolean connectsAudio(EnumFacing enumFacing) {
		return true;
	}
}
