// SPDX-FileCopyrightText: 2025-2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.controlpanel;

import com.hbm.inventory.control_panel.NodeSystem;
import com.hbm.inventory.control_panel.modular.INodeLoader;
import com.hbm.inventory.control_panel.nodes.Node;
import com.leafia.contents.machines.controlpanel.nodes.*;
import com.leafia.contents.machines.controlpanel.nodes.NodeBulkQuery;
import com.leafia.contents.machines.controlpanel.nodes.bool.NodePulse;
import com.leafia.contents.machines.controlpanel.nodes.bool.NodeSRLatch;
import com.leafia.contents.machines.controlpanel.nodes.pack.NodeIC10;
import com.leafia.contents.machines.controlpanel.nodes.ror.NodeRoRReceiver;
import com.leafia.contents.machines.controlpanel.nodes.ror.NodeRoRSender;
import com.leafia.contents.machines.controlpanel.nodes.string.NodeAddString;
import com.leafia.contents.machines.controlpanel.nodes.string.NodeFormat;
import com.leafia.contents.machines.controlpanel.nodes.string.NodeSIPfx;
import com.leafia.contents.machines.controlpanel.nodes.string.NodeSubString;
import com.leafia.contents.machines.controlpanel.nodes.utility.*;
import net.minecraft.nbt.NBTTagCompound;

public class AddonNodeLoader implements INodeLoader {
	@Override
	public Node nodeFromNBT(NBTTagCompound nbtTagCompound,NodeSystem nodeSystem) {
		Node node = switch(nbtTagCompound.getString("nodeType")) {
			case "sounder" -> new NodeSounder(0,0);
			case "addString" -> new NodeAddString(0,0);
			case "subString" -> new NodeSubString(0,0);
			case "sipfx" -> new NodeSIPfx(0,0);
			case "format" -> new NodeFormat(0,0);
			case "bulkQuery" -> new NodeBulkQuery(0,0,nodeSystem.parent);
			case "leafia_clock" -> new NodeClock(0,0);
			case "leafia_pulse" -> new NodePulse(0,0);
			case "leafia_sr_latch" -> new NodeSRLatch(0,0);
			case "leafia_summarizer" -> new NodeSummarizer(0,0);
			case "leafia_cache" -> new NodeCache(0,0);
			case "leafia_graph" -> new NodeGraphAdd(0,0,nodeSystem.parent);
			case "leafia_graph_bounds" -> new NodeGraphBounds(0,0,nodeSystem.parent);
			case "leafia_parse_float" -> new NodeParseFloat(0,0);
			case "leafia_ror_receiver" -> new NodeRoRReceiver(0,0,nodeSystem.parent.panel.parent.getControlWorld());
			case "leafia_ror_sender" -> new NodeRoRSender(0,0,nodeSystem.parent.panel.parent.getControlWorld());
			case "leafia_rng" -> new NodeRNG(0,0,nodeSystem.parent.panel.parent.getControlWorld());
			case "leafia_rng_d" -> new NodeRNGDecimal(0,0,nodeSystem.parent.panel.parent.getControlWorld());
			case "leafia_ic10" -> new NodeIC10(0,0,nodeSystem.parent);
			default -> null;
		};
		return node;
	}
}
