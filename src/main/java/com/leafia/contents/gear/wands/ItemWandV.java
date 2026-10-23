// SPDX-FileCopyrightText: 2025-2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.gear.wands;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.machine.rbmk.RBMKBase;
import com.hbm.lib.Library;
import com.hbm.main.MainRegistry;
import com.hbm.main.ResourceManager;
import com.hbm.particle.bullet_hit.ParticleDecalFlow;
import com.hbm.render.util.BakedModelUtil;
import com.hbm.render.util.BakedModelUtil.DecalType;
import com.hbm.util.I18nUtil;
import com.leafia.contents.machines.reactors.pwr.PWRDiagnosis;
import com.leafia.contents.worldgen.lib.SellacityRoadChunk;
import com.leafia.dev.LeafiaDebug;
import com.leafia.dev.LeafiaDebug.Tracker;
import com.leafia.dev.items.itembase.AddonItemBaked;
import com.leafia.dev.optimization.LeafiaParticlePacket.JumpingRBMKParticle;
import com.leafia.unsorted.StructuralIntegrityHandler;
import com.llib.group.LeafiaMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

public class ItemWandV extends AddonItemBaked {
	public static boolean remote = false;
	public ItemWandV(String s,String texture) {
		super(s,texture);
	}

	public enum DebuggerMode {
		DEFAULT_TRACKER,
		PWR_SET_CORE,
		PWR_PRINT_CORE,
		PRINT_ROAD_NOISE,
		JUMPING_RBMK_TEST,
		STRUCTURAL_INTEGRITY_TEST,
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		Block b = world.getBlockState(pos).getBlock();
		ItemStack stack = player.getHeldItem(hand);
		if (stack.getItem() instanceof ItemWandV wandV) {
			switch(getMode(stack)) {
				case STRUCTURAL_INTEGRITY_TEST -> {
					if (!world.isRemote) {
						StructuralIntegrityHandler.handleBlock(world,pos);
					}
				}
				case JUMPING_RBMK_TEST -> {
					if (!world.isRemote) {
						if (world.getBlockState(pos).getBlock() instanceof RBMKBase rbmk) {
							BlockPos core = rbmk.findCore(world,pos);
							if (core != null)
								new JumpingRBMKParticle(core).emitServer(world);
						}
					}
				}
				case PRINT_ROAD_NOISE -> {
					if (!world.isRemote) {
						SellacityRoadChunk snakeNoise = new SellacityRoadChunk(new Random(0));
						MutableBlockPos mbp = new MutableBlockPos();
						for (int i = 0; i < snakeNoise.data.length; i++) {
							int x = snakeNoise.x(i);
							int y = snakeNoise.y(i);
							mbp.setPos(x,200,y);
							int data = snakeNoise.data[i];
							EnumDyeColor color = EnumDyeColor.BLACK;
							if (data == 1)
								color = EnumDyeColor.WHITE;
							world.setBlockState(
									mbp,
									Blocks.WOOL.getDefaultState().withProperty(
											BlockColored.COLOR,color
									),2
							);
						}
						player.sendMessage(new TextComponentString("Generated sellacity road noise test at 0, 200, 0!"));
					}
				}
				case PWR_PRINT_CORE -> {
					if (!world.isRemote) {
						TileEntity te = world.getTileEntity(pos);
						if (te != null) {
							NBTTagCompound lol = te.writeToNBT(new NBTTagCompound());
							if (lol.hasKey("corePosX"))
								LeafiaDebug.debugPos(world,new BlockPos(lol.getInteger("corePosX"),lol.getInteger("corePosY"),lol.getInteger("corePosZ")),3,0x00FFFF,"Core");
						}
					}
				}
				case PWR_SET_CORE -> {
					if (!world.isRemote) {
						PWRDiagnosis.cleanup();
						PWRDiagnosis diagnosis = new PWRDiagnosis(world,pos);
						diagnosis.forcedCorePos = pos;
						diagnosis.addPosition(pos);
						player.sendMessage(new TextComponentString("PWR core set!"));
					}
				}
				case DEFAULT_TRACKER -> {
					if(!world.isRemote) {
						if(player.isSneaking()){
							RayTraceResult pos1 = Library.rayTrace(player, 500, 1);
							if(pos1 != null && pos1.typeOfHit == RayTraceResult.Type.BLOCK) {
								Tracker.selected = pos1.getBlockPos();
								LeafiaDebug.flagDebug();
								LeafiaMap<BlockPos,String> subjects = Tracker.getSubjects();
								if (!subjects.containsKey(pos1.getBlockPos())) {
									subjects.put(pos1.getBlockPos(),Character.toString((char)(97+ subjects.size())));
									Tracker.notifySubjectMapChanges(remote);
									LeafiaDebug.debugLog(world,"Added "+(remote ? "remote" : "server")+" watch \""+ subjects.get(pos1.getBlockPos())+"\"");
								} else {
									LeafiaDebug.debugLog(world,"Selected "+(remote ? "remote" : "server")+" watch \""+ subjects.get(pos1.getBlockPos())+"\"");
								}
								Tracker.notifySelectionChange();
							}
						} else {
							pos = player.getPosition().down();
							IBlockState state = world.getBlockState(pos);
							Block block = state.getBlock();
							Material mat = state.getMaterial();
							LeafiaDebug.debugPos(world,pos,15,0x40FF00,
									"CLASS: "+block.getClass().getSimpleName(),
									"REGISTRY: "+block.getRegistryName(),
									TextFormatting.YELLOW+"-----------",
									TextFormatting.GREEN+"BLK: !isPassable: "+pfx(!block.isPassable(world,pos)),
									"BLK: isCollidable: "+pfx(block.isCollidable()),
									"BLK: isNormalCube: "+pfx(block.isNormalCube(state,world,pos)),
									"TEST: "+pfx(ModBlocks.fallout.canPlaceBlockAt(world,pos)),
									TextFormatting.YELLOW+"-----------",
									"STAT: isFullBlock: "+pfx(state.isFullBlock()),
									"STAT: isFullCube: "+pfx(state.isFullCube()),
									"STAT: isNormalCube: "+pfx(state.isOpaqueCube()),
									"STAT: isBlockNormalCube: "+pfx(state.isBlockNormalCube()),
									"STAT: isOpaqueCube: "+pfx(state.isOpaqueCube()),
									//TextFormatting.GREEN+"BLK: !isTranslucent: "+pfx(!state.isTranslucent()),
									"STAT: renderType: "+state.getRenderType().name(),
									TextFormatting.YELLOW+"-----------",
									"MAT: Class: "+mat.getClass().getSimpleName(),
									"MAT: isOpaque: "+pfx(mat.isOpaque()),
									"MAT: isSolid: "+pfx(mat.isSolid()),
									TextFormatting.YELLOW+"-----------",
									"META: "+block.getMetaFromState(state)
							);
						}
					} else {
						clickClient(world, player, pos, hitX, hitY, hitZ);
					}
					MainRegistry.time = System.currentTimeMillis();
				}
			}
		}
		
		/*int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		Random rand = world.rand;
		world.setBlockState(new BlockPos(x, y, z), ModBlocks.safe.getDefaultState().withProperty(BlockStorageCrate.FACING, EnumFacing.byIndex(rand.nextInt(4) + 2)), 2);
		WeightedRandomChestContentFrom1710.generateChestContents(rand, HbmChestContents.getLoot(10),
				(TileEntitySafe) world.getTileEntity(new BlockPos(x, y, z)), rand.nextInt(4) + 3);
		((TileEntitySafe) world.getTileEntity(new BlockPos(x, y, z))).setPins(rand.nextInt(999) + 1);
		((TileEntitySafe) world.getTileEntity(new BlockPos(x, y, z))).setMod(1);
		((TileEntitySafe) world.getTileEntity(new BlockPos(x, y, z))).lock();*/

		
		return EnumActionResult.SUCCESS;
	}
	public String pfx(boolean shit) {
		return shit ? TextFormatting.DARK_GREEN+"true" : TextFormatting.DARK_RED+"false";
	}
	
	@SideOnly(Side.CLIENT)
	public void clickClient(World world, EntityPlayer player, BlockPos pos, float hitX, float hitY, float hitZ){
        var direction = player.getLookVec();
        float x = pos.getX() + hitX;
        float y = pos.getY() + hitY;
        float z = pos.getZ() + hitZ;
        // Preserve the random roll of the formerly discarded preview mesh.
        world.rand.nextFloat();
        int variant = world.rand.nextInt(9);
        var mesh = BakedModelUtil.generateDecalMesh(world, direction, 1, x, y, z,
                DecalType.FLOW, ResourceManager.blood_particles, variant, 4);
        var inset = direction.scale(0.001F);
        var particle = new ParticleDecalFlow(world, mesh, 120, x - inset.x, y - inset.y, z - inset.z);
        Minecraft.getMinecraft().effectRenderer.addEffect(particle.shader(ResourceManager.blood_dissolve));
	}
	
	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
		if(target.world.isRemote){
			//DisintegrationParticleHandler.spawnGluonDisintegrateParticles(target);
		} else {
		}
		return super.itemInteractionForEntity(stack, playerIn, target, hand);
	}

	public DebuggerMode getMode(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) nbt = new NBTTagCompound();
		int mode = nbt.getInteger("mode");
		return DebuggerMode.values()[mode];
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (stack.getItem() instanceof ItemWandV wandV) {
			if(player.isSneaking() && getMode(stack).equals(DebuggerMode.DEFAULT_TRACKER)) {
				if(world.isRemote)
					player.sendMessage(new TextComponentString(MainRegistry.x + " " + MainRegistry.y + " " + MainRegistry.z));
				else if (Tracker.selected != null) {
					LeafiaDebug.flagDebug();
					Tracker.selected = null;
					LeafiaDebug.debugLog(world,"Unselected watch");
					Tracker.notifySelectionChange();
				}
			} else {
				if (!world.isRemote) {
					NBTTagCompound nbt = stack.getTagCompound();
					if (nbt == null) nbt = new NBTTagCompound();
					int mode = nbt.getInteger("mode")+1;
					if (mode >= DebuggerMode.values().length)
						mode = 0;
					nbt.setInteger("mode",mode);
					player.sendMessage(new TextComponentString("Set mode "+DebuggerMode.values()[mode].name()));
					stack.setTagCompound(nbt);
					player.inventoryContainer.detectAndSendChanges();
				}
			}
		}
		
		return ActionResult.newResult(EnumActionResult.PASS, player.getHeldItem(hand));
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(I18nUtil.resolveKey("desc.debugwand"));
		tooltip.add("Hold on offhand to show NTM:LCE debugging info");
	}
}
