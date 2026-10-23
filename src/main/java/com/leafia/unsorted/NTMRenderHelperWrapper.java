// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.unsorted;

import com.hbm.render.NTMRenderHelper;
import net.minecraft.client.renderer.Tessellator;

public class NTMRenderHelperWrapper {
	public static void addVertexWithUV(double x, double y, double z, double u, double v){
		NTMRenderHelper.addVertexWithUV((float)x,(float)y,(float)z,(float)u,(float)v);
	}
	public static void addVertex(double x, double y, double z){
		NTMRenderHelper.addVertex((float)x,(float)y,(float)z);
	}
	public static void addVertexWithUV(double x, double y, double z, double u, double v, Tessellator tes){
		NTMRenderHelper.addVertexWithUV((float)x,(float)y,(float)z,(float)u,(float)v,tes);
	}
	public static void addVertexColor(double x, double y, double z, int red, int green, int blue, int alpha){
		NTMRenderHelper.addVertexColor((float)x,(float)y,(float)z,red,green,blue,alpha);
	}
	public static void addVertexColor(double x, double y, double z, float red, float green, float blue, float alpha){
		NTMRenderHelper.addVertexColor((float)x,(float)y,(float)z,red,green,blue,alpha);
	}
	public static void addVertexColor(double x, double y, double z, float red, float green, float blue, float alpha, Tessellator tess){
		NTMRenderHelper.addVertexColor((float)x,(float)y,(float)z,red,green,blue,alpha,tess);
	}
}
