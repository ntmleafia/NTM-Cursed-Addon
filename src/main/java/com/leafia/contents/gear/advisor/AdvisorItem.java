package com.leafia.contents.gear.advisor;

import com.hbm.blocks.gas.BlockGasAsbestos;
import com.hbm.blocks.gas.BlockGasCoal;
import com.hbm.main.ClientProxy;
import com.hbm.main.MainRegistry;
import com.hbm.util.ArmorRegistry;
import com.hbm.util.ArmorRegistry.HazardClass;
import com.hbm.util.I18nUtil;
import com.leafia.dev.LeafiaDebug;
import com.leafia.dev.custompacket.LeafiaCustomPacketEncoder;
import com.leafia.dev.items.itembase.AddonItemBase;
import com.leafia.dev.math.FiaMatrix;
import com.leafia.dev.math.FiaMatrix.RotationOrder;
import com.leafia.dev.optimization.bitbyte.LeafiaBuf;
import com.leafia.init.LeafiaSoundEvents;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class AdvisorItem extends AddonItemBase {
	public static class AdvisorWarningPacket implements LeafiaCustomPacketEncoder {
		public int warningId;
		public AdvisorWarningPacket() { }
		public AdvisorWarningPacket(int warningId) {
			this.warningId = warningId;
		}
		@Override
		public void encode(LeafiaBuf buf) {
			buf.writeInt(warningId);
		}
		@Override
		public @Nullable Consumer<MessageContext> decode(LeafiaBuf buf) {
			int warningId = buf.readInt();
			return (ctx)->{
				if (warningId == 0) // PYROPHORIC
					Warns.pyro = true;
				else if (warningId == 1) // SKIN DAMAGE II
					Warns.skinDmg2 = true;
				else if (warningId == 2) // SKIN DAMAGE III
					Warns.skinDmg3 = true;
			};
		}
	}
	final static int len = 10000;
	public static void showMessage(ITextComponent msg,int millisec,int id) {
		id*=10;
		MainRegistry.proxy.displayTooltipLegacy(msg.getFormattedText(),millisec,1121+id);
	}
	public static void showMessage(String msg,int millisec,int id) {
		id*=10;
		for (String s : msg.split("\\$")) {
			MainRegistry.proxy.displayTooltipLegacy(TextFormatting.GOLD+s,millisec,1121+id);
			id++;
		}
	}
	@SideOnly(Side.CLIENT)
	public static void playWarning() {
		Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(LeafiaSoundEvents.advisor_warning,1));
	}
	public AdvisorItem(String s) {
		super(s);
	}
	public static class Warns {
		static int gas = 0;
		static boolean pyro = false;
		static int pyroCooldown = 0;
		static boolean skinDmg2 = false;
		static boolean skinDmg3 = false;
		static int lava = 0;
		public static void preTick() {
			gas = decrement(gas);
			pyro = false;
			skinDmg2 = false;
			skinDmg3 = false;
			lava = decrement(lava);
		}
		static int decrement(int v) { return Math.max(v-1,0); }
	}
	static final String msgRoot = "item.advisor.message.";
	static String msg(String key) {
		return I18nUtil.resolveKey(msgRoot+key);
	}
	static final int gasCooldown = 20*60;
	public void gasAlert(World world,BlockPos pos,EntityPlayer player) {
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block instanceof BlockGasCoal) {
			if (!ArmorRegistry.hasProtection(player,EntityEquipmentSlot.HEAD,HazardClass.PARTICLE_COARSE)) {
				if (Warns.gas <= 0)
					playWarning();
				showMessage(msg("coal"),len,0);
				Warns.gas = gasCooldown;
			}
		}
		if (block instanceof BlockGasAsbestos) {
			if (!ArmorRegistry.hasProtection(player,EntityEquipmentSlot.HEAD,HazardClass.PARTICLE_FINE)) {
				if (Warns.gas <= 0)
					playWarning();
				showMessage(msg("asbestos"),len,1);
				Warns.gas = gasCooldown;
			}
		}
	}
	@Override
	public void onUpdate(ItemStack stack,World world,Entity entity,int itemSlot,boolean isSelected) {
		if (world.isRemote && MainRegistry.proxy instanceof ClientProxy && entity instanceof EntityPlayer player) {
			//showMessage("Selected: "+isSelected,1000,0);
			BlockPos pos = new BlockPos(entity.posX,entity.posY,entity.posZ);
			{
				// BLOCK ALERT
				gasAlert(world,pos,player);
				gasAlert(world,pos.up(),player);
			}
			if (Warns.pyro) {
				if (Warns.pyroCooldown <= 0)
					playWarning();
				Warns.pyroCooldown = gasCooldown;
				showMessage(msg("pyro"),len,2);
			}
			if (Warns.skinDmg2) {
				playWarning();
				showMessage(msg("skindmg2"),len,3);
			}
			if (Warns.skinDmg3) {
				playWarning();
				showMessage(msg("skindmg3"),len,3);
			}
			{
				// BEHIND CHECK
				FiaMatrix facing = new FiaMatrix(new Vec3d(player.posX+0.5,player.posY+0.5,player.posZ+0.5)).rotateY(-player.rotationYawHead);
				Vec3d relativeVelocity = facing.toObjectSpace(facing.add(new Vec3d(player.motionX,0,player.motionZ))).position.normalize();
				// i must've been smoking my tail when I was coding this but the positive Z is forward for some reason lmao
				if (relativeVelocity.z < -0.707) {
					boolean lavaWarn = false;
					for (int i = 1; i <= 4; i++) {
						BlockPos p = new BlockPos(facing.translate(0,0,-i).position);
						if (!isAir(world,p)) break;
						for (int j = 0; j < 10; j++) {
							BlockPos p2 = p.down(j);
							if (world.getBlockState(p2).getMaterial().equals(Material.LAVA)) {
								lavaWarn = true;
								break;
							}
							if (!isAir(world,p2))
								break;
						}
						if (lavaWarn)
							break;
					}
					if (lavaWarn) {
						if (Warns.lava <= 0)
							playWarning();
						Warns.lava = 200;
						showMessage(msg("lava"),4000,4);
					}
				}
			}
		}
	}
	public boolean isAir(World world,BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		AxisAlignedBB bb = state.getBlock().getCollisionBoundingBox(state,world,pos);
		return bb == Block.NULL_AABB;
	}
	@Override
	public void addInformation(ItemStack stack,@Nullable World worldIn,List<String> tooltip,ITooltipFlag flagIn) {
		tooltip.add(TextFormatting.DARK_RED+"W.I.P.");
		super.addInformation(stack,worldIn,tooltip,flagIn);
	}
}
