// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.controlpanel.instruments.types.lever;

import com.hbm.inventory.control_panel.*;
import com.hbm.inventory.control_panel.types.*;
import com.hbm.inventory.control_panel.controls.ControlType;
import com.hbm.inventory.control_panel.nodes.*;
import com.hbm.inventory.control_panel.nodes.NodeMath.Operation;
import com.hbm.render.loader.IModelCustom;
import com.leafia.contents.machines.controlpanel.CCPSyncPacketBase;
import com.leafia.contents.machines.controlpanel.instruments.AddonControlType;
import com.leafia.contents.machines.controlpanel.instruments.types.AddonInstrumentModels;
import com.leafia.dev.custompacket.LeafiaCustomPacket;
import com.leafia.dev.optimization.bitbyte.LeafiaBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class LeverBase extends com.hbm.inventory.control_panel.controls.SliderVertical {
	float curLevel = 0.5f;
	public LeverBase(String name,String registryName,ControlPanel panel) {
		super(name,registryName,panel);
        vars.remove("value");
		vars.put("position",new DataValueFloat(50f));
	}
	@Override
	public ControlType getControlType() {
		return AddonControlType.LEVER;
	}
	@Override
	public List<String> getOutEvents() {
		return Collections.singletonList("ctrl_press");
	}
	@Override
	@SideOnly(Side.CLIENT)
	public IModelCustom getModel() {
		return AddonInstrumentModels.lever;
	}
	static final float increment = 200/7f;
	@Override
	public void receiveEvent(ControlEvent evt) {
		if (evt.name.equals("tick")) {
			vars.put("position",new DataValueFloat(Math.max(Math.min(vars.get("position").getNumber(),100),0)));
			float tgtLevel = vars.get("position").getNumber();
			float delta = tgtLevel-curLevel;
			float difference = Math.abs(delta);
			if (difference > 0) {
				if (difference <= increment)
					curLevel = tgtLevel;
				else {
					float direction = Math.signum(delta);
					curLevel += direction*increment;
				}
			}
			BlockPos pos = panel.parent.getControlPos();
			CCPLeverSyncPacket packet = new CCPLeverSyncPacket(
					pos,
					panel.controls.indexOf(this),
					curLevel
			);
			LeafiaCustomPacket.__start(packet).__sendToAllAround(
					panel.parent.getControlWorld().provider.getDimension(),
					pos,256
			);
		}
		super.receiveEvent(evt);
	}
	public static class CCPLeverSyncPacket extends CCPSyncPacketBase {
		float level = 0;
		public CCPLeverSyncPacket() { }
		public CCPLeverSyncPacket(BlockPos pos,int idx,float level) {
			super(pos,idx);
			this.level = level;
		}
		@Override
		public void encode(LeafiaBuf buf) {
			super.encode(buf);
			buf.writeFloat(level);
		}
		@Override
		public @Nullable Consumer<MessageContext> decode(LeafiaBuf buf) {
			boolean success = load(buf);
			float level = buf.readFloat();
			return (ctx)->{
				if (!success) return;
				if (instrument instanceof LeverBase sw)
					sw.curLevel = level;
			};
		}
	}
	@Override
	public void populateDefaultNodes(List<ControlEvent> receiveEvents) {
        super.populateDefaultNodes(receiveEvents);
        var press = receiveNodeMap.get("ctrl_press");
        var read = (NodeGetVar) press.nodes.get(1);
        var direction = (NodeConditional) press.nodes.get(2);
        var addition = (NodeMath) press.nodes.get(3);
        var clamp = (NodeMath) press.nodes.get(4);
        var write = (NodeSetVar) press.nodes.get(5);
        read.setData("position", false);
        direction.inputs.get(1).setDefault(new DataValueFloat(-10));
        direction.inputs.get(2).setDefault(new DataValueFloat(10));
        write.setData("position", false);
        write.setPosition(350, 130);
        write.inputs.get(0).setData(addition, 0, true);
        press.nodes.remove(clamp);
	}
}
