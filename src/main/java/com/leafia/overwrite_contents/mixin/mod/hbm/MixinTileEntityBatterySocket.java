package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.inventory.control_panel.ControlEvent;
import com.hbm.inventory.control_panel.ControlEventSystem;
import com.hbm.inventory.control_panel.IControllable;
import com.hbm.inventory.control_panel.types.DataValue;
import com.hbm.inventory.control_panel.types.DataValueFloat;
import com.hbm.tileentity.machine.storage.TileEntityBatteryBase;
import com.hbm.tileentity.machine.storage.TileEntityBatterySocket;
import com.leafia.contents.AddonItems;
import com.leafia.init.LeafiaSoundEvents;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityBatterySocket;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCore;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Deprecated // suck my butt
@Mixin(value = TileEntityBatterySocket.class)
public abstract class MixinTileEntityBatterySocket extends TileEntityBatteryBase implements IControllable /*IMixinTileEntityBatterySocket*/ {
	@Override
	public List<String> getInEvents() {
		return Collections.singletonList("setIOMode");
	}
	@Override
	public void receiveEvent(BlockPos from,ControlEvent e) {
		if (e.name.equals("setIOMode")) {
			redLow = (short)MathHelper.clamp(e.vars.get("mode").getNumber(),0,3);
			getRelevantMode(false);
		}
	}

	@Shadow(remap = false) public abstract long getPower();
	@Shadow(remap = false) public abstract long getMaxPower();

	public MixinTileEntityBatterySocket(int slotCount) {
		super(slotCount);
	}
	@Override
	public Map<String,DataValue> getQueryData() {
		Map<String,DataValue> map = new HashMap<>();
		map.put("charge",new DataValueFloat(getPower()));
		map.put("maxCharge",new DataValueFloat(getMaxPower()));
		map.put("ioMode",new DataValueFloat(getRelevantMode(true)));
		return map;
	}
	@Override
	public BlockPos getControlPos() {
		return getPos();
	}
	@Override
	public World getControlWorld() {
		return getWorld();
	}
	@Override
	public void validate() {
		super.validate();
		ControlEventSystem.get(world).addControllable(this);
	}
	@Override
	public void invalidate() {
		ControlEventSystem.get(world).removeControllable(this);
		super.invalidate();
	}
	/*
	@Inject(method = "discharge",at = @At(value = "HEAD"),remap = false,require = 1)
	private void leafia$onDischarge(CallbackInfo ci) {
		for (int i = 0; i < 4; i++)
			IMixinTileEntityCore.shockParticleEditionTrademark(world,new Vec3d(pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5));
		world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, LeafiaSoundEvents.mus_sfx_a_lithit, SoundCategory.BLOCKS, 6.66f, 1 + (float) world.rand.nextGaussian() * 0.1f);
	}*/

/*@Unique public int renderPackLeafia = -1;
	@Override
	public int renderPackLeafia() {
		return renderPackLeafia;
	}
	@Inject(method = "serialize",at = @At(value = "TAIL"),require = 1,remap = false)
	public void onSerialize(ByteBuf buf,CallbackInfo ci) {
		int renderPackLeafia = -1;
		if (!inventory.getStackInSlot(0).isEmpty() && inventory.getStackInSlot(0).getItem() == AddonItems.addon_battery_pack) {
			renderPackLeafia = inventory.getStackInSlot(0).getItemDamage();
		}
		buf.writeByte(renderPackLeafia);
	}
	@Inject(method = "deserialize",at = @At(value = "TAIL"),require = 1,remap = false)
	public void onDeserialize(ByteBuf buf,CallbackInfo ci) {
		renderPackLeafia = buf.readByte();
	}*/
}
