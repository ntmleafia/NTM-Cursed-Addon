package com.leafia.contents.machines.misc.wind_turbines.medium;

import com.hbm.lib.ForgeDirection;
import com.leafia.contents.machines.misc.wind_turbines.WindTurbineTEBase;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.passive.Wind;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;

public class WindTurbineMediumTE extends WindTurbineTEBase {
	AxisAlignedBB aabb;
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		if (aabb == null) {
			aabb = new AxisAlignedBB(
					pos.getX()-1,
					pos.getY()-1,
					pos.getZ()-1,
					pos.getX()+2,
					pos.getY()+8,
					pos.getX()+2
			);
		}
		return aabb;
	}
	@Override
	public void update() {
		if (world.isRemote)
			addBladeAngle((float)Math.pow(generated*20/5000f,0.5)/20*360*2);
		else {
			generated = 0;
			updateObstructed(5,500);
			if (!obstructed)
				generated = (long)(Math.min(Wind.power*Wind.dimensionWindMultiplier.getOrDefault(world.provider.getDimension(),0d)*0.5/40,1)*5000/20);
			double multiplier = Math.pow(lube.getFill()/(double)lube.getMaxFill(),0.25)*0.95+0.05;
			generated = (long)(generated*multiplier*3);
			consumeLube();
			tryProvide(world,pos.down(),ForgeDirection.DOWN);
			LeafiaPacket._start(this)
					.__write(29,lube.getFill())
					.__write(30,generated)
					.__write(31,obstructed)
					.__sendToAffectedClients();
		}
	}
	@Override
	public String getPacketIdentifier() {
		return "WINDTURB_MEDIUM";
	}
	@Override
	public int lubeConsumptionPeriod() {
		return 60;
	}
	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		super.onReceivePacketLocal(key,value);
	}
}
