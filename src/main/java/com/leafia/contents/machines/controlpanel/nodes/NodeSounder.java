package com.leafia.contents.machines.controlpanel.nodes;

import com.hbm.inventory.control_panel.*;
import com.hbm.inventory.control_panel.DataValue.DataType;
import com.hbm.inventory.control_panel.modular.StockNodesRegister;
import com.hbm.inventory.control_panel.nodes.NodeOutput;
import com.hbm.lib.HBMSoundHandler;
import com.hbm.tileentity.machine.TileEntityControlPanel;
import com.leafia.init.LeafiaSoundEvents;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class NodeSounder extends NodeOutput {
	NodeSnd snd = NodeSnd.BUTTON_ACCEPT;
	TileEntityControlPanel panel;
	enum NodeSnd {
		BUTTON_ACCEPT(HBMSoundHandler.buttonYes),
		BUTTON_DENY(HBMSoundHandler.buttonNo),
		CLICK_ACCEPT(SoundEvents.UI_BUTTON_CLICK),
		CLICK_INVALID(LeafiaSoundEvents.UI_BUTTON_INVALID),
		ARMED(HBMSoundHandler.fstbmbStart),
		TIMER(HBMSoundHandler.fstbmbPing),
		BLEEP(HBMSoundHandler.techBleep),
		POP(HBMSoundHandler.techBoop),
		SHUTDOWN(HBMSoundHandler.shutdown),
		;
		final SoundEvent evt;
		NodeSnd(SoundEvent evt) {
			this.evt = evt;
		}
		static NodeSnd getByName(String s) {
			for (NodeSnd value : NodeSnd.values()) {
				if (value.name().equals(s)) return value;
			}
			return null;
		}
	}
	public NodeSounder(float x,float y) {
		super(x,y);
		NodeDropdown sounds = new NodeDropdown(this,otherElements.size(),s->{
			NodeSnd snd = NodeSnd.getByName(s);
			if (snd != null)
				this.snd = snd;
			return null;
		},()->snd.name());
		String[] list = new String[NodeSnd.values().length];
		for (int i = 0; i < list.length; i++)
			list[i] = NodeSnd.values()[i].name();
		Arrays.sort(list);
		sounds.list.addItems(list);
		otherElements.add(sounds);
		this.inputs.add(new NodeConnection("Volume",this,inputs.size(),true,DataType.NUMBER,new DataValueFloat(0.5f)));
		this.inputs.add(new NodeConnection("Pitch",this,inputs.size(),true,DataType.NUMBER,new DataValueFloat(1)));
		recalcSize();
	}

	@Override
	public void readFromNBT(NBTTagCompound tag,NodeSystem sys) {
		NodeSnd snd = NodeSnd.getByName(tag.getString("sound"));
		if (snd != null)
			this.snd = snd;
		super.readFromNBT(tag,sys);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag,NodeSystem sys) {
		tag.setString("nodeType","sounder");
		tag.setString("sound",snd.name());
		return super.writeToNBT(tag,sys);
	}

	@Override
	public DataValue evaluate(int idx) {
		return null;
	}

	@Override
	public boolean doOutput(IControllable from,Map<String,NodeSystem> sendNodeMap,List<BlockPos> positions) {
		World world = from.getControlWorld();

		SoundEvent evt = snd.evt;
		float volume = inputs.get(0).evaluate().getNumber();
		float pitch = inputs.get(1).evaluate().getNumber();

		volume = MathHelper.clamp(volume,0,10);
		pitch = MathHelper.clamp(pitch,0.5f,2);
		//LeafiaDebug.debugLog(world,"PlaySound "+volume+": "+pitch); yipeeee
		world.playSound(null,from.getControlPos().getX()+0.5,from.getControlPos().getY()+0.5,from.getControlPos().getZ()+0.5,evt,SoundCategory.BLOCKS,volume,pitch);
		return false;
	}

	@Override
	public float[] getColor() {
		return StockNodesRegister.colorOutput;
	}

	@Override
	public String getDisplayName() {
		return "Play Sound";
	}
}
