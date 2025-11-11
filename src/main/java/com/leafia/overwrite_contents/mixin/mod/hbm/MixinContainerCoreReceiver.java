package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.forgefluid.FFUtils;
import com.hbm.inventory.container.ContainerCoreReceiver;
import com.hbm.packet.PacketDispatcher;
import com.hbm.tileentity.machine.TileEntityCoreReceiver;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCoreReceiver;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fluids.FluidTank;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ContainerCoreReceiver.class)
public abstract class MixinContainerCoreReceiver extends Container {
	@Shadow(remap = false) private TileEntityCoreReceiver te;
	@Unique private EntityPlayerMP player;

	@Inject(method = "<init>",at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Container;<init>()V",shift = Shift.AFTER))
	public void onInit(EntityPlayer player,TileEntityCoreReceiver te,CallbackInfo ci) {
		this.te = te;
		if(player instanceof EntityPlayerMP)
			this.player = (EntityPlayerMP) player;
	}

	@Override
	public void addListener(IContainerListener listener) {
		super.addListener(listener);
		//PacketDispatcher.sendTo(new AuxLongPacket(te.getPos(), te.syncJoules, 0), player);
		((IMixinTileEntityCoreReceiver)te).sendToPlayer(player);
	}
	@Override
	public void detectAndSendChanges() {
		((IMixinTileEntityCoreReceiver)te).sendToPlayer(player);
		super.detectAndSendChanges();
	}
}
