package com.leafia.contents.control.upgrades;

import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemMachineUpgrade;
import com.hbm.util.I18nUtil;
import com.leafia.contents.AddonItems;
import com.leafia.contents.gear.advisor.AdvisorItem;
import com.leafia.dev.custompacket.LeafiaCustomPacketEncoder;
import com.leafia.dev.optimization.bitbyte.LeafiaBuf;
import com.llib.technical.FifthString;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class AddonUpgradeItem extends ItemMachineUpgrade {
	public AddonUpgradeItem(String s) {
		super(s);
		ModItems.ALL_ITEMS.remove(this);
		AddonItems.ALL_ITEMS.add(this);
	}
	public AddonUpgradeItem(String s,UpgradeType type) {
		super(s,type);
		ModItems.ALL_ITEMS.remove(this);
		AddonItems.ALL_ITEMS.add(this);
	}
	public AddonUpgradeItem(String s,UpgradeType type,int tier) {
		super(s,type,tier);
		ModItems.ALL_ITEMS.remove(this);
		AddonItems.ALL_ITEMS.add(this);
	}
	public static class ControlUpgradeFreqPacket implements LeafiaCustomPacketEncoder {
		public String value;
		public ControlUpgradeFreqPacket() { }
		public ControlUpgradeFreqPacket(String value) {
			this.value = value;
		}
		@Override
		public void encode(LeafiaBuf buf) {
			buf.writeFifthString(new FifthString(value));
		}
		@Override
		public @Nullable Consumer<MessageContext> decode(LeafiaBuf buf) {
			String value = buf.readFifthString().toString();
			return (ctx)->{
				EntityPlayer player = ctx.getServerHandler().player;
				ItemStack stack = null;
				if (player.getHeldItemMainhand().getItem() == AddonItems.upgrade_control)
					stack = player.getHeldItemMainhand();
				else if (player.getHeldItemMainhand().getItem() == AddonItems.upgrade_control)
					stack = player.getHeldItemMainhand();
				if (stack != null) {
					NBTTagCompound nbt = new NBTTagCompound();
					nbt.setString("freq",value);
					stack.setTagCompound(nbt);
				}
				player.inventoryContainer.detectAndSendChanges();
			};
		}
	}
	@SideOnly(Side.CLIENT)
	public void openGUI(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null) tag = new NBTTagCompound();
		Minecraft.getMinecraft().displayGuiScreen(new UpgradeControlGUI(tag.getString("freq")));
	}
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world,EntityPlayer player,EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (hand != EnumHand.MAIN_HAND) return new ActionResult<>(EnumActionResult.FAIL,stack);
		if (this == AddonItems.upgrade_control) {
			if (!world.isRemote)
				openGUI(stack);
			return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		}
		return super.onItemRightClick(world,player,hand);
	}
	@Override
	public void addInformation(@NotNull ItemStack stack,World worldIn,@NotNull List<String> list,@NotNull ITooltipFlag flagIn) {
		if (this == AddonItems.upgrade_control) {
			NBTTagCompound tag = stack.getTagCompound();
			if (tag == null) tag = new NBTTagCompound();
			list.add(I18nUtil.resolveKey("item.upgrade_control.freq",tag.getString("freq")));
			list.add("");
			list.addAll(Arrays.asList(I18nUtil.resolveKey("item.upgrade_control.desc").split("\\$")));
		}
	}
}
