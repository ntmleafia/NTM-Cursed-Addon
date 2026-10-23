// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.debug.blackhole_test;

import com.leafia.transformer.LeafiaGls;
import com.leafia.unsorted.BlackholeRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class DebugBHRender extends TileEntitySpecialRenderer<DebugBHTE> {
	@Override
	public void render(DebugBHTE te,double x,double y,double z,float partialTicks,int destroyStage,float alpha) {
		LeafiaGls.pushMatrix();
		LeafiaGls.translate(x+0.5,y+0.5,z+0.5);
		BlackholeRenderer.draw(true);
		LeafiaGls.popMatrix();
	}
}
