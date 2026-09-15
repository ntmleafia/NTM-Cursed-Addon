package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.tileentity.machine.rbmk.RBMKColumn;
import com.hbm.tileentity.machine.rbmk.TileEntityRBMKBase;
import com.hbm.tileentity.machine.rbmk.TileEntityRBMKControl;
import com.hbm.tileentity.machine.rbmk.TileEntityRBMKControlAuto;
import com.leafia.contents.machines.reactors.rbmk.columns.control_auto.container.RBMKControlAutoGUI;
import com.leafia.overwrite_contents.interfaces.IMixinRBMKColumn$ControlColumn;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityRBMKControlAuto;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayDeque;
import java.util.Queue;

@Mixin(value = TileEntityRBMKControlAuto.class)
public abstract class MixinTileEntityRBMKControlAuto extends TileEntityRBMKControl implements IMixinTileEntityRBMKControlAuto {
	@Unique boolean leafia$scrammed = false;
	@Override
	public boolean leafia$getScrammed() {
		return leafia$scrammed;
	}
	@Override
	public void leafia$setScrammed(boolean scrammed) {
		leafia$scrammed = scrammed;
	}
	/**
	 * @author ntmleafia
	 * @reason AZ-5 compatibility
	 */
	@Overwrite(remap = false)
	@SideOnly(Side.CLIENT)
	public GuiScreen provideGUI(int ID,EntityPlayer player,World world,int x,int y,int z) {
		return new RBMKControlAutoGUI(player.inventory, (TileEntityRBMKControlAuto)(IMixinTileEntityRBMKControlAuto)this);
	}
	//Family and Friends
	// iterative BFS version to prevent stack overflow
	// leafia: STOP FUCKING MAKING METHODS PRIVATE RAAAAAAAHHHRR
	void getFF(ReferenceOpenHashSet<TileEntityRBMKBase> columns,int x,int y,int z) {

		Queue<BlockPos> queue = new ArrayDeque<>();
		queue.add(new BlockPos(x, y, z));

		// Safety limit to prevent server freeze on world-edited mega structures
		int safetyLimit = 50000;

		while(!queue.isEmpty() && safetyLimit > 0) {
			safetyLimit--;
			BlockPos current = queue.poll();

			// prevent loading unloaded chunks during meltdown
			if (!world.isBlockLoaded(current)) continue;

			TileEntity te = world.getTileEntity(current);

			if(te instanceof TileEntityRBMKBase rbmk) {

				if(!columns.contains(rbmk)) {
					columns.add(rbmk);

					// Add neighbors to queue
					queue.add(current.add(1, 0, 0));
					queue.add(current.add(-1, 0, 0));
					queue.add(current.add(0, 0, 1));
					queue.add(current.add(0, 0, -1));
				}
			}
		}
	}
	@Inject(method = "receiveControl",at = @At(value = "HEAD"),remap = false,require = 1,cancellable = true)
	void onReceiveControl(EntityPlayerMP player,NBTTagCompound data,CallbackInfo ci) {
		if (data.hasKey("acknowledge")) {
			// acknowledge set to false means only this rod
			leafia$scrammed = false;
			if (data.getBoolean("acknowledge")) { // if it's true, then do it for all rods
				ReferenceOpenHashSet<TileEntityRBMKBase> columns = new ReferenceOpenHashSet<>();
				getFF(columns,pos.getX(),pos.getY(),pos.getZ());
				for (TileEntityRBMKBase column : columns) {
					if (column instanceof IMixinTileEntityRBMKControlAuto mixin) {
						mixin.leafia$setScrammed(false);
						column.markDirty();
					}
				}
			}
			ci.cancel();
		}
	}
	@Inject(method = "writeToNBT",at = @At(value = "HEAD"),require = 1)
	void leafia$onWriteToNBT(NBTTagCompound nbt,CallbackInfoReturnable<NBTTagCompound> cir) {
		nbt.setBoolean("leafia_scrammed",leafia$scrammed);
	}
	@Inject(method = "readFromNBT",at = @At(value = "HEAD"),require = 1)
	void leafia$onReadFromNBT(NBTTagCompound nbt,CallbackInfo ci) {
		leafia$scrammed = nbt.getBoolean("leafia_scrammed");
	}
	@Inject(method = "serialize",at = @At(value = "TAIL"),require = 1,remap = false)
	void leafia$onSerialize(ByteBuf buf,CallbackInfo ci) {
		buf.writeBoolean(leafia$scrammed);
	}
	@Inject(method = "deserialize",at = @At(value = "TAIL"),require = 1,remap = false)
	void leafia$onDeserialize(ByteBuf buf,CallbackInfo ci) {
		leafia$scrammed = buf.readBoolean();
	}
	@Override
	public RBMKColumn getConsoleData() {
		RBMKColumn col = super.getConsoleData();
		((IMixinRBMKColumn$ControlColumn)col).leafia$setExclamation(leafia$scrammed);
		return col;
	}
	@Inject(method = "update",at = @At(value = "INVOKE", target = "Lcom/hbm/tileentity/machine/rbmk/TileEntityRBMKControl;update()V"),require = 1)
	void leafia$onUpdate(CallbackInfo ci) {
		if (leafia$scrammed)
			targetLevel = 0;
	}
}
