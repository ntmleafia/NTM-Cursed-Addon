// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.reactors.rbmk.columns.realersim;

import com.hbm.handler.neutron.NeutronNodeWorld;
import com.hbm.handler.neutron.RBMKNeutronHandler;
import com.hbm.handler.neutron.RBMKNeutronHandler.RBMKNeutronNode;
import com.hbm.handler.neutron.RealerSimNeutronStream;
import com.hbm.tileentity.machine.rbmk.RBMKDials;
import com.hbm.tileentity.machine.rbmk.RBMKDials.RBMKKeys;
import com.hbm.tileentity.machine.rbmk.TileEntityRBMKRodReaSim;
import com.hbm.util.MutableVec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import static com.hbm.handler.neutron.RBMKNeutronHandler.makeNode;

public class RBMKRealerSimTE extends TileEntityRBMKRodReaSim {
	@Override
	public String getName() {
		return "tile.rbmk_rod_realersim.name";
	}

	private static int shittyWorkaroundParseInt(String s, int def) {
		try {
			return Integer.parseInt(s);
		} catch (Exception ex) {
		}
		return def;
	}
	static int getReaSimCount(World world) {
		return MathHelper.clamp(shittyWorkaroundParseInt(world.getGameRules().getString(RBMKKeys.KEY_REASIM_COUNT.keyString),
				(int) RBMKKeys.KEY_REASIM_COUNT.defValue), 1, 24);
	}

	@Override
	protected void spreadFlux(double flux, double ratio) {
		if (flux == 0) {
			// simple way to remove the node from the cache when no flux is going into it!
			NeutronNodeWorld.removeNode(world,pos);
			return;
		}
		NeutronNodeWorld.StreamWorld streamWorld = NeutronNodeWorld.getOrAddWorld(world);
		RBMKNeutronNode node = (RBMKNeutronNode)streamWorld.getNode(pos);

		if (node == null) {
			node = makeNode(streamWorld,this);
			streamWorld.addNode(node);
		}

		int count = getReaSimCount(world);

		for (int i = 0; i < count; i++) {
			MutableVec3d neutronVector = new MutableVec3d(1,0,0);

			neutronVector.rotateYawSelf((float)(Math.PI*2D*world.rand.nextDouble()));

			new RealerSimNeutronStream(makeNode(streamWorld,this),neutronVector,flux*0.75,ratio);
			// Create new neutron streams
		}
	}
}
