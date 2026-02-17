package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.config.GeneralConfig;
import com.hbm.handler.threading.PacketThreading;
import com.hbm.interfaces.IBomb;
import com.hbm.interfaces.IHoldableWeapon;
import com.hbm.items.ModItems;
import com.hbm.items.tool.ItemLaserDetonator;
import com.hbm.lib.HBMSoundHandler;
import com.hbm.main.MainRegistry;
import com.hbm.packet.PacketDispatcher;
import com.hbm.render.amlfrom1710.Vec3;
import com.hbm.util.I18nUtil;
import com.leafia.contents.gear.IADSWeapon;
import com.leafia.dev.LeafiaUtil;
import com.leafia.overwrite_contents.packets.LaserDetonatorPacket;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;

@Mixin(value = ItemLaserDetonator.class)
public abstract class MixinItemLaserDetonator extends Item implements IHoldableWeapon, IADSWeapon {

	public MixinItemLaserDetonator(String s) {
		this.setRegistryName(s);
		this.setTranslationKey(s);
		this.setCreativeTab(MainRegistry.controlTab);

		ModItems.ALL_ITEMS.add(this);
	}

	/**
	 * @author ntmleafia
	 * @reason item.detonator_laser.desc2
	 */
	@Overwrite
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack,World worldIn,List<String> list,ITooltipFlag flagIn) {
		list.add(I18nUtil.resolveKey("item.detonator_laser.desc"));
		list.add(I18nUtil.resolveKey("item.detonator_laser.desc2"));
	}

	@Override
	public float getADS() {
		return 0.3f;
	}

	/**
	 * @author ntmleafia
	 * @reason ability to detonate from 512 blocks away
	 */
	@Overwrite
	public ActionResult<ItemStack> onItemRightClick(World world,EntityPlayer player,EnumHand hand) {
		boolean rem = world.isRemote;
		int distance = 128; // vanilla raytrace (which it used to use) has force max range of 200 and even less depending on angle
		if (player.isSneaking()) {
			if (player.getHeldItemMainhand().isEmpty() != player.getHeldItemOffhand().isEmpty())
				distance = 512;
		}
		EnumHandSide side = player.getPrimaryHand();
		if (hand == EnumHand.OFF_HAND)
			side = side.opposite();
		Vec3d startPos = player.getPositionVector().add(0,player.getEyeHeight(),0);
		Vec3d vecLook = player.getLook(1);
		// thanks https://www.geogebra.org/m/psMTGDgc
		Vec3d vecRight = Vec3d.fromPitchYaw(0,player.rotationYawHead).crossProduct(new Vec3d(0,1,0));
		Vec3d vecUp = vecLook.crossProduct(vecRight.scale(-1));
		Vec3d rayStart = startPos.add(vecLook.scale(0.5)).add(vecRight.scale((distance < 200) ? (side == EnumHandSide.RIGHT) ? 0.1 : -0.1 : 0)).add(vecUp.scale(-0.16));
		RayTraceResult ray = LeafiaUtil.leafiaRayTraceBlocks(
				world,
				rayStart,
				startPos.add(vecLook.scale(distance)),
				false,false,true
		); //Library.rayTrace(player, distance, 1);
		if (ray != null) {
			BlockPos pos = ray.getBlockPos();
			Vec3 vec = Vec3.createVectorHelper(pos.getX() + 0.5 - rayStart.x,pos.getY() + 0.5 - rayStart.y,pos.getZ() + 0.5 - rayStart.z);
			PacketThreading.createSendToAllTrackingThreadedPacket(
					new LaserDetonatorPacket().set(new Vec3(rayStart),vec),
					new NetworkRegistry.TargetPoint(player.dimension,pos.getX(),pos.getY(),pos.getZ(),distance * 2)
			);
			if (world.getBlockState(pos).getBlock() instanceof IBomb) {
				if (!rem)
					((IBomb) world.getBlockState(pos).getBlock()).explode(world,pos,player);

				if (GeneralConfig.enableExtendedLogging)
					MainRegistry.logger.log(Level.INFO,"[DET] Tried to detonate block at " + pos.getX() + " / " + pos.getY() + " / " + pos.getZ() + " by " + player.getDisplayName() + "!");

				if (rem)
					player.sendMessage(new TextComponentString("§2[" + I18nUtil.resolveKey("chat.detonated") + "]" + "§r"));
				else
					world.playSound(null,player.posX,player.posY,player.posZ,HBMSoundHandler.techBleep,SoundCategory.AMBIENT,1.0F,1.0F);

			} else {
				if (rem)
					player.sendMessage(new TextComponentString("§c" + I18nUtil.resolveKey("chat.posbadrror") + "§r"));
				else
					world.playSound(null,player.posX,player.posY,player.posZ,HBMSoundHandler.techBleep,SoundCategory.AMBIENT,1.0F,1.0F);
			}
		} else {
			if (rem)
				player.sendMessage(new TextComponentString("§c" + I18nUtil.resolveKey("chat.postoofarerror") + "§r"));
			else
				world.playSound(null,player.posX,player.posY,player.posZ,HBMSoundHandler.techBleep,SoundCategory.AMBIENT,1.0F,1.0F);
		}
		return super.onItemRightClick(world, player, hand);
	}
}
