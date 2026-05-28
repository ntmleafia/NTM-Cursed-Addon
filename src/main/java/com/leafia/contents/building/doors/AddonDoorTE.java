package com.leafia.contents.building.doors;

import com.hbm.handler.threading.PacketThreading;
import com.hbm.tileentity.TileEntityDoorGeneric;
import com.leafia.CommandLeaf;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class AddonDoorTE extends TileEntityDoorGeneric {
	public String shakeIntensityOpen() {
		if (doorType instanceof AddonDoorDecl decl)
			return decl.shakeIntensityOpen();
		return null;
	}
	public String shakeIntensityClose() {
		if (doorType instanceof AddonDoorDecl decl)
			return decl.shakeIntensityClose();
		return null;
	}
	public String shakeRange() {
		if (doorType instanceof AddonDoorDecl decl)
			return decl.shakeRange();
		return "12";
	}
	@Override
	public void update() {
		DoorState prevState = getState();
		super.update();
		if (getState() != prevState) {
			if (getState() == DoorState.OPEN && shakeIntensityOpen() != null) {
				PacketThreading.createSendToAllTrackingThreadedPacket(
						new CommandLeaf.ShakecamPacket(
								new String[]{
										"type=smooth",
										"intensity=0.15",
										"duration="+shakeIntensityOpen(),
										"speed=2",
										"blurDulling=50",
										"bloomDulling=50",
										"range="+shakeRange(),
										"curve=0.5"
								}).setPos(pos),
						new NetworkRegistry.TargetPoint(
								world.provider.getDimension(),pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5,10)
				);
			}
			if (getState() == DoorState.CLOSED && shakeIntensityClose() != null) {
				PacketThreading.createSendToAllTrackingThreadedPacket(
						new CommandLeaf.ShakecamPacket(
								new String[]{
										"type=smooth",
										"intensity="+shakeIntensityClose(),
										"duration=0.75",
										"speed=8",
										"blurDulling=50",
										"bloomDulling=50",
										"range="+shakeRange(),
										"curve=0.5"
								}).setPos(pos),
						new NetworkRegistry.TargetPoint(
								world.provider.getDimension(),pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5,10)
				);
			}
		}
	}
}
