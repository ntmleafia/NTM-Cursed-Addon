package com.leafia.transformer;

import com.custom_hbm.contents.oilycoal.BlockCoalOil;
import com.hbm.items.tool.ItemToolAbility;
import com.hbm.lib.Library;
import com.hbm.main.AdvancementManager;
import com.leafia.contents.AddonBlocks.LegacyBlocks;
import com.leafia.contents.gear.advisor.AdvisorItem;
import com.leafia.dev.optimization.LeafiaParticlePacket.FiaSpark;
import com.leafia.dev.optimization.LeafiaParticlePacket.VanillaExt;
import com.leafia.init.AddonAdvancements;
import com.leafia.init.LeafiaSoundEvents;
import com.llib.exceptions.LeafiaDevFlaw;
import com.llib.group.LeafiaSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import java.lang.reflect.Field;

public class WorldServerLeafia {
	public static void onBreakBlockProgress(World world,BlockPos pos,EntityPlayer player) {
		if(player.getHeldItemMainhand().isEmpty())
			return;

		if(!(player.getHeldItemMainhand().getItem() instanceof ItemTool || player.getHeldItemMainhand().getItem() instanceof ItemToolAbility))
			return;

		ItemTool tool = (ItemTool)player.getHeldItemMainhand().getItem();

		if(!tool.getToolMaterialName().equals(ToolMaterial.WOOD.toString())) {
			world.playSound(null,pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5,LeafiaSoundEvents.sbPickaxeOre,SoundCategory.BLOCKS,0.4f+world.rand.nextFloat()*0.2f,1);
			if (world.rand.nextBoolean()) {
				RayTraceResult res = Library.rayTrace(player,20,0);
				FiaSpark spark = new FiaSpark();
				spark.color = 0xFFEE80;
				spark.count = world.rand.nextInt(3)+1;
				spark.thickness = 0.014f;
				//spark.speed = 0.15+world.rand.nextDouble()*0.2;

				spark.emit(res.hitVec,new Vec3d(res.sideHit.getDirectionVec()),world.provider.getDimension());
				if (world.getBlockState(pos).getBlock() instanceof BlockCoalOil) {
					int rand = world.rand.nextInt(4);
					for (int i = 0; i <= rand; i++)
						VanillaExt.Smoke().emit(res.hitVec.add(world.rand.nextDouble()*0.2-0.1,world.rand.nextDouble()*0.2-0.1,world.rand.nextDouble()*0.2-0.1),new Vec3d(0,0,0),world.provider.getDimension());
					if (rand == 3) {
						VanillaExt.Lava().emit(res.hitVec,new Vec3d(0,0,0),world.provider.getDimension());
						if (world.rand.nextInt(3) == 0) {
							world.playSound(null,pos,SoundEvents.ITEM_FIRECHARGE_USE,SoundCategory.BLOCKS,0.65F,0.9F+world.rand.nextFloat()*0.2F);
							world.setBlockState(pos,LegacyBlocks.ore_coal_oil_burning.getDefaultState());
						}
					}
				}
			}
//			TauSpark spark = new TauSpark();
//			spark.color = 0xFFEE80;
//			spark.life = 2;
//			spark.width = 0.03F;
//			spark.emit(res.hitVec,new Vec3d(res.sideHit.getDirectionVec()).scale(3F+world.rand.nextFloat()),world.provider.getDimension());
		}
	}
	public static void player_onBreakBlockProgress(WorldServer world,int breakerId,BlockPos pos,int progress) {
		EntityPlayer doxxed = null;
		for (EntityPlayer entity : world.playerEntities) {
			if (entity.getEntityId() == breakerId)
				doxxed = entity;
		}
		if (doxxed != null) {
			IBlockState state = world.getBlockState(pos);
			Block block = state.getBlock();
			if (block instanceof BlockOre && Math.floorMod(progress,2) == 0)
				onBreakBlockProgress(world,pos,doxxed);
				//((BlockCoalOil) block).onBreakBlockProgress(world,pos,doxxed);
		}
	}
	static Field droppedBy;
	//static Field health;
	static Field wasPickedUp;
	static {
		try {
			droppedBy = EntityItem.class.getDeclaredField("addon_droppedBy");
			//health = EntityItem.class.getDeclaredField("health");
			//health.setAccessible(true);
			wasPickedUp = EntityItem.class.getDeclaredField("addon_wasPickedUp");
		} catch (NoSuchFieldException e) {
			throw new LeafiaDevFlaw(e);
		}
	}
	public static void onItemDroppedByPlayer(EntityPlayer player,EntityItem item) {
		try {
			droppedBy.set(item,player);
		} catch (IllegalAccessException e) {
			throw new LeafiaDevFlaw(e);
		}
	}
	public static void onEntityRemoved(World world,Entity entity) {
		if (!world.isRemote && entity instanceof EntityItem item) {
			try {
				if (!wasPickedUp.getBoolean(item)) {
					Object obj = droppedBy.get(item);
					if (obj != null) {
						EntityPlayer player = (EntityPlayer)obj;
						if (item.getItem().getItem() instanceof AdvisorItem)
							AdvancementManager.grantAchievement(player,AddonAdvancements.loseadvisor);
					}
				}
			} catch (IllegalAccessException e) {
				throw new LeafiaDevFlaw(e);
			}
		}
	}
	/// fuck off
	public static void onItemDestroyed(EntityItem item) {
		/*
		try {
			if (health.getInt(item) <= 0) {
				Sys.alert("Destroyed","Item destroyed");
				LeafiaDebug.debugLog(item.world,"DESTROYED!!");
			}
		} catch (IllegalAccessException e) {
			throw new LeafiaDevFlaw(e);
		}*/
	}
	public static void fluid_onFilling(FluidStack stack,IFluidHandler inst) {
		World world = null;
		BlockPos pos = null;
		if (inst instanceof TileEntity) {
			world = ((TileEntity) inst).getWorld();
			pos = ((TileEntity) inst).getPos();
		} else if (inst instanceof IFluidHandlerItem) {
			ItemStack istack = ((IFluidHandlerItem) inst).getContainer();
			//istack. ah forget it
		} else if (inst != null) {
			for (Field field : inst.getClass().getFields()) {
				try {
					Object o = field.get(inst);
					if (o instanceof TileEntity) {
						world = ((TileEntity) o).getWorld();
						pos = ((TileEntity) o).getPos();
					}
				} catch (IllegalAccessException ignored) {}
			}
		}
		if (!fluid_checkTraits(stack,inst,world,pos))
			stack.amount = 0; // fuck you
	}
	public static boolean fluid_canContinue(FluidStack stack,TileEntity te) {
		if (true) return true; // ah fuck it
		if (te != null)
			return fluid_checkTraits(stack,te,te.getWorld(),te.getPos());
		else
			return fluid_checkTraits(stack,null,null,null);
	}
	public static LeafiaSet<BlockPos> violatedPositions = new LeafiaSet<>();
	static boolean fluid_checkTraits(FluidStack stack,Object inst,World world,BlockPos pos) {
		/*
		LeafiaFluid fluid = LeafiaFluid.cast(stack);
		if (fluid != null) {
			List<LeafiaFluidTrait> hazards = new ArrayList<>();
			for (String trait : fluid.getTraits().fiaTraits) {
				LeafiaFluidTrait tr = LeafiaFluidTrait.reg.get(trait);
				if (tr.needsSpecializedContainer())
					hazards.add(tr);
			}
			if (!hazards.isEmpty()) {
				if (pos == null || world == null) return false;
				else {
					List<String> attributes = new ArrayList<>();
					if (inst instanceof ISpecializedContainer)
						attributes.addAll(Arrays.asList(((ISpecializedContainer) inst).protections()));
					boolean changed = true;
					while (changed) {
						changed = false;
						for (String trait : fluid.getTraits().fiaTraits) {
							for (Pair<String,String> re : LeafiaFluidTrait.reg.get(trait).redirections) {
								if (attributes.contains(re.getA()) && !attributes.contains(re.getB())) {
									attributes.add(re.getB());
									changed = true;
								}
							}
						}
					}
					if (!violatedPositions.contains(pos)) {
						for (LeafiaFluidTrait hazard : hazards) {
							boolean prevented = false;
							for (String preventation : hazard.preventations) {
								if (attributes.contains(preventation)) {
									prevented = true;
									break;
								}
							}
							if (!prevented) {
								Runnable callback = hazard.onViolation(world,pos,stack,inst);
								if (callback != null) {
									violatedPositions.add(pos);
									LeafiaPassiveServer.queueFunction(callback);
								}
							}
						}
					}
				}
			}
		}*/
		return true;
	}
}
