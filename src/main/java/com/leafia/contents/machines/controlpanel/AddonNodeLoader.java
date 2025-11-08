package com.leafia.contents.machines.controlpanel;

import com.hbm.inventory.control_panel.NodeSystem;
import com.hbm.inventory.control_panel.modular.INodeLoader;
import com.hbm.inventory.control_panel.nodes.Node;
import com.leafia.contents.machines.controlpanel.nodes.*;
import net.minecraft.nbt.NBTTagCompound;

public class AddonNodeLoader implements INodeLoader {
	@Override
	public Node nodeFromNBT(NBTTagCompound nbtTagCompound,NodeSystem nodeSystem) {
		Node node = null;
		switch(nbtTagCompound.getString("nodeType")) {
			case "sounder":
				node = new NodeSounder(0,0);
				break;
			case "addString":
				node = new NodeAddString(0,0);
				break;
			case "subString":
				node = new NodeSubString(0,0);
				break;
			case "sipfx":
				node = new NodeSIPfx(0,0);
				break;
			case "format":
				node = new NodeFormat(0,0);
				break;
		}
		return node;
	}
}
