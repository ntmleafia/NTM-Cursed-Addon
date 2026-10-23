// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2025-2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.building.doors.special.reactor_door;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockConcreteColoredExt.EnumConcreteType;
import com.hbm.blocks.generic.BlockDoorGeneric;
import com.hbm.handler.radiation.RadiationSystemNT;
import com.hbm.handler.threading.PacketThreading;
import com.hbm.inventory.control_panel.ControlEvent;
import com.hbm.inventory.control_panel.ControlEventSystem;
import com.hbm.inventory.control_panel.types.DataValueFloat;
import com.hbm.lib.ForgeDirection;
import com.hbm.tileentity.TileEntityDoorGeneric;
import com.leafia.CommandLeaf;
import com.leafia.contents.AddonBlocks;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import com.leafia.init.LeafiaSoundEvents;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.jetbrains.annotations.NotNull;

public class ReactorDoorTE extends TileEntityDoorGeneric implements LeafiaPacketReceiver {
	boolean syncNeeded = false;
	@Override
	public void update() {
		if (syncNeeded) {
			syncNeeded = false;
			generateSyncPacket().__sendToAffectedClients();
		}
		if (getDoorType() == null && this.getBlockType() instanceof BlockDoorGeneric)
			setDoorType(((BlockDoorGeneric) this.getBlockType()).type);
		var update = getDoorType().onDoorUpdate();
		if (update != null)
			update.accept(this);
		if (state == DoorState.OPENING) {
			if (openTicks == 0)
				world.playSound(null,pos,LeafiaSoundEvents.reactor_door_handle,SoundCategory.BLOCKS,1,1);
			if (openTicks == 43) {
				PacketThreading.createSendToAllTrackingThreadedPacket(
						new CommandLeaf.ShakecamPacket(
								new String[]{
										"type=smooth",
										"intensity=0.15",
										"duration=0.5",
										"speed=2",
										"blurDulling=50",
										"bloomDulling=50",
										"range=12",
										"curve=0.5"
								}).setPos(pos),
						new NetworkRegistry.TargetPoint(
								world.provider.getDimension(),pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5,10)
				);
				world.playSound(null,pos,LeafiaSoundEvents.reactor_door_open,SoundCategory.BLOCKS,3,1);
			}
			openTicks++;
			if (openTicks >= getDoorType().timeToOpen()) {
				openTicks = getDoorType().timeToOpen();
			}
		} else if (state == DoorState.CLOSING) {
			if (openTicks == getDoorType().timeToOpen()-23)
				world.playSound(null,pos,LeafiaSoundEvents.reactor_door_close,SoundCategory.BLOCKS,3,1);
			if (openTicks == getDoorType().timeToOpen()-44-2) { // why is it off sync
				PacketThreading.createSendToAllTrackingThreadedPacket(
						new CommandLeaf.ShakecamPacket(
								new String[]{
										"type=smooth",
										"intensity=0.35",
										"duration=0.75",
										"speed=8",
										"blurDulling=50",
										"bloomDulling=50",
										"range=12",
										"curve=0.5"
								}).setPos(pos),
						new NetworkRegistry.TargetPoint(
								world.provider.getDimension(),pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5,10)
				);
			}
			if (openTicks == getDoorType().timeToOpen()-43-23)
				world.playSound(null,pos,LeafiaSoundEvents.reactor_door_handle,SoundCategory.BLOCKS,3,1);
			openTicks--;
			if (openTicks <= 0) {
				openTicks = 0;
			}
		}

		if (world.isRemote) return;
		if (state == DoorState.OPENING || state == DoorState.CLOSING) {
			boolean opening = state == DoorState.OPENING;
			updateDoorCells(opening);
			if (openTicks == (opening ? getDoorType().timeToOpen() : 0)) {
				state = opening ? DoorState.OPEN : DoorState.CLOSED;
				broadcastControlEvt();
				if (!opening) RadiationSystemNT.markSectionsForRebuild(world, getOccupiedSections());
			}
		}
		networkPackNT(100);
	}

	private void updateDoorCells(boolean opening) {
		var direction = ForgeDirection.getOrientation(getBlockMetadata() - BlockDummyable.offset);
		var rotation = direction.getBlockRotation();
		if (direction.offsetX != 0) rotation = rotation.add(Rotation.CLOCKWISE_180);
		var ranges = getDoorType().getDoorOpenRanges();
		var block = (BlockDummyable) getBlockType();
		for (int row = 0; row < ranges.length; row++) {
			var range = ranges[row];
			int end = opening ? Math.abs(range[3]) : -1;
			int stride = opening ? 1 : -1;
			float phase = getDoorType().getDoorRangeOpenTime(openTicks, row);
			for (int slice = opening ? 0 : Math.abs(range[3]) - 1; slice != end; slice += stride) {
				float threshold = (float) slice / Math.abs(range[3] - 1);
				if (opening ? threshold > phase : threshold < phase) break;
				int displacement = range[3] < 0 ? -slice : slice;
				for (int span = 0; span < range[4]; span++) {
					int x = range[0];
					int y = range[1];
					int z = range[2];
					switch (range[5]) {
						case 0 -> { y += span; z += displacement; }
						case 1 -> { x += span; y += displacement; }
						case 2 -> { x += displacement; y += span; }
						default -> throw new ArrayIndexOutOfBoundsException(range[5]);
					}
					int rotatedX = switch (rotation) {
						case NONE -> x;
						case CLOCKWISE_90 -> -z;
						case CLOCKWISE_180 -> -x;
						case COUNTERCLOCKWISE_90 -> z;
					};
					int rotatedZ = switch (rotation) {
						case NONE -> z;
						case CLOCKWISE_90 -> x;
						case CLOCKWISE_180 -> -z;
						case COUNTERCLOCKWISE_90 -> -x;
					};
					int worldX = pos.getX() + rotatedX;
					int worldY = pos.getY() + y;
					int worldZ = pos.getZ() + rotatedZ;
					if ((rotatedX | y | rotatedZ) == 0) shouldUseBB = opening;
					else if (opening) block.makeExtra(world, worldX, worldY, worldZ);
					else block.removeExtra(world, worldX, worldY, worldZ);
				}
			}
		}
	}

	private void broadcastControlEvt() {
		ControlEventSystem.get(world).broadcastToSubscribed(this, ControlEvent.newEvent("door_open_state").setVar("state",
				new DataValueFloat(state.ordinal())));
	}
	public Block skinA = AddonBlocks.brick_concrete_dark;
	public int metaA = 0;
	public Block skinB = ModBlocks.concrete_colored_ext;
	public int metaB = EnumConcreteType.MACHINE.ordinal();
	public LeafiaPacket generateSyncPacket() {
		return LeafiaPacket._start(this)
				.__write(0,skinA.getRegistryName().toString())
				.__write(1,metaA)
				.__write(2,skinB.getRegistryName().toString())
				.__write(3,metaB);
	}
	@Override
	public double affectionRange() {
		return 512;
	}
	@Override
	public String getPacketIdentifier() {
		return "DOOR_REAC";
	}
	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		if (key == 0 || key == 2) {
			Block block = Block.getBlockFromName((String)value);
			if (block != null) {
				if (key == 0) skinA = block;
				else    skinB = block;
			}
		} else if (key == 1) metaA = (int)value;
		else if (key == 3) metaB = (int)value;
	}
	@Override
	public void onReceivePacketServer(byte key,Object value,EntityPlayer plr) { }
	@Override
	public void onPlayerValidate(EntityPlayer plr) {
		generateSyncPacket().__sendToClient(plr);
	}
	@Override
	public @NotNull NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setString("skinA",skinA.getRegistryName().toString());
		tag.setInteger("metaA",metaA);
		tag.setString("skinB",skinB.getRegistryName().toString());
		tag.setInteger("metaB",metaB);
		return super.writeToNBT(tag);
	}
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		Block skinA = Block.getBlockFromName(tag.getString("skinA"));
		if (skinA != null)
			this.skinA = skinA;
		metaA = tag.getInteger("metaA");
		Block skinB = Block.getBlockFromName(tag.getString("skinB"));
		if (skinB != null)
			this.skinB = skinB;
		metaB = tag.getInteger("metaB");
		syncNeeded = true;
	}
}
