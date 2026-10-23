// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.controlpanel;

import com.hbm.inventory.control_panel.Control;
import com.hbm.tileentity.machine.TileEntityControlPanel;
import com.leafia.dev.custompacket.LeafiaCustomPacketEncoder;
import com.leafia.dev.optimization.bitbyte.LeafiaBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public abstract class CCPSyncPacketBase implements LeafiaCustomPacketEncoder {
	public BlockPos pos;
	public int idx;
	public CCPSyncPacketBase() { }
	public CCPSyncPacketBase(BlockPos pos,int idx) {
		this.pos = pos;
		this.idx = idx;
	}
	@Override
	public void encode(LeafiaBuf buf) {
		buf.writeVec3i(pos);
		buf.writeShort(idx);
	}
	public Control instrument;
	public World world;
	@SideOnly(Side.CLIENT)
	protected boolean load(LeafiaBuf buf) {
		pos = buf.readPos();
		idx = buf.readShort();
		world = Minecraft.getMinecraft().world;
		if (world.getTileEntity(pos) instanceof TileEntityControlPanel ccp) {
			if (idx >= 0 && idx < ccp.panel.controls.size()) {
				instrument = ccp.panel.controls.get(idx);
				return true;
			}
		}
		return false;
	}
}
