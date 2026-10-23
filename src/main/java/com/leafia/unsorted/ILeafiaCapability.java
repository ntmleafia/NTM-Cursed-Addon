// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.unsorted;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import org.jetbrains.annotations.Nullable;

public class ILeafiaCapability {
	public interface ILeafiaData {
		boolean hasReceivedAdvisor();
		void setReceivedAdvisor(boolean v);
		default void serialize(ByteBuf buf) {
			buf.writeBoolean(hasReceivedAdvisor());
		}
		default void deserialize(ByteBuf buf) {
			if (buf.readableBytes() > 0) {
				setReceivedAdvisor(buf.readBoolean());
			}
		}
	}
	public static class LeafiaData implements ILeafiaData {
		public boolean hasReceivedAdvisor = false;
		@Override public boolean hasReceivedAdvisor() { return hasReceivedAdvisor; }
		@Override public void setReceivedAdvisor(boolean v) { hasReceivedAdvisor = v; }
	}
	public static class LeafiaDataStorage implements IStorage<ILeafiaData> {
		@Override
		public @Nullable NBTBase writeNBT(Capability<ILeafiaData> capability,ILeafiaData instance,EnumFacing side) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean("advisor",false); // fuck off
			return tag;
		}
		@Override
		public void readNBT(Capability<ILeafiaData> capability,ILeafiaData instance,EnumFacing side,NBTBase nbt) {

		}
	}
}
