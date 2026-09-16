package com.leafia.contents.machines.reactors.apr.blocks.outlet;

import com.hbm.api.energymk2.IEnergyProviderMK2;
import com.hbm.lib.ForgeDirection;
import com.hbm.tileentity.TileEntityLoadedBase;
import com.leafia.contents.machines.reactors.apr.blocks.core.APRCoreTE;
import net.minecraft.util.ITickable;

public class APRPowerOutletTE extends TileEntityLoadedBase implements IEnergyProviderMK2, ITickable {
	public long power = 0;
	@Override
	public long getPower() {
		return power;
	}
	@Override
	public void setPower(long l) {
		power = l;
	}
	@Override
	public long getMaxPower() {
		return power;
	}
	@Override
	public void update() {
		if (world.getTileEntity(pos.up(5)) instanceof APRCoreTE core)
			power = core.getPowerOutput();
		tryProvide(world,pos.down(),ForgeDirection.DOWN);
	}
}
