package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.inventory.control_panel.ControlPanel;
import com.hbm.tileentity.IPersistentNBT;
import com.hbm.tileentity.machine.TileEntityControlPanel;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityControlPanel;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TileEntityControlPanel.class)
public abstract class MixinTileEntityControlPanel extends TileEntity implements IMixinTileEntityControlPanel, LeafiaPacketReceiver, IPersistentNBT {
	@Shadow
	public abstract void readFromNBT(NBTTagCompound compound);

	@Shadow(remap = false)
	public ControlPanel panel;
	@Unique Block skin = null;
	@Override
	public Block getSkin() {
		return skin;
	}
	@Override
	public void setSkin(Block b) {
		skin = b;
	}

	@Override
	public String getPacketIdentifier() {
		return "CTRL_PNL";
	}

	@Override
	public void sendSkinPackets() {
		writeSkin(LeafiaPacket._start(this)).__sendToAffectedClients();
	}

	@Override
	public double affectionRange() {
		return 256;
	}

	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		if (key == 0) {
			if (value == null) skin = null;
			else skin = Block.getBlockFromName((String)value);
		}
	}

	@Override
	public void onReceivePacketServer(byte key,Object value,EntityPlayer plr) { }
	public LeafiaPacket writeSkin(LeafiaPacket packet) {
		if (skin != null)
			packet.__write(0,skin.getRegistryName().toString());
		else
			packet.__write(0,null);
		return packet;
	}

	@Override
	public void onPlayerValidate(EntityPlayer plr) {
		writeSkin(LeafiaPacket._start(this)).__sendToClient(plr);
	}

	@Inject(method = "readFromNBT",at = @At(value = "HEAD"),require = 1)
	public void onReadFromNBT(NBTTagCompound compound,CallbackInfo ci) {
		skin = null;
		if (compound.hasKey("skin"))
			skin = Block.getBlockFromName(compound.getString("skin"));
	}

	@Inject(method = "writeToNBT",at = @At(value = "HEAD"),require = 1)
	public void onWriteToNBT(NBTTagCompound compound,CallbackInfoReturnable<NBTTagCompound> cir) {
		if (skin != null)
			compound.setString("skin",skin.getRegistryName().toString());
	}

	@Unique
	private boolean destroyedByCreativePlayer = false;

	@Override
	public void setDestroyedByCreativePlayer() {
		destroyedByCreativePlayer = true;
	}

	@Override
	public boolean isDestroyedByCreativePlayer() {
		return destroyedByCreativePlayer;
	}

	@Override
	public void writeNBT(NBTTagCompound nbtTagCompound) {
		if (skin != null)
			nbtTagCompound.setString("skin",skin.getRegistryName().toString());
		nbtTagCompound.setTag("panel",panel.writeToNBT(new NBTTagCompound()));
	}

	@Override
	public void readNBT(NBTTagCompound nbtTagCompound) {
		skin = null;
		if (nbtTagCompound.hasKey("skin"))
			skin = Block.getBlockFromName(nbtTagCompound.getString("skin"));
		panel.readFromNBT(nbtTagCompound.getCompoundTag("panel"));
	}
}
