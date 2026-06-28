package com.leafia.contents.building.catwalk.railing;

import com.hbm.blocks.ICustomBlockHighlight;
import com.hbm.blocks.network.SimpleUnlistedProperty;
import com.hbm.items.IDynamicModels;
import com.hbm.render.loader.HFRWavefrontObject;
import com.leafia.dev.LeafiaDebug;
import com.leafia.dev.LeafiaUtil;
import com.leafia.dev.blocks.ICustomItemBlockProvider;
import com.leafia.dev.blocks.blockbase.AddonBlockBase;
import com.leafia.dev.math.FiaBB;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.*;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class CatwalkRailingBase extends AddonBlockBase implements IDynamicModels, ICustomItemBlockProvider, ICustomBlockHighlight {
	public String basePath = "_integrated/decoration/catwalks/railings/";
	public String spritePath = "";
	public String modelPath = "";

	public static final PropertyBool POS_X = PropertyBool.create("pos_x");
	public static final PropertyBool POS_Z = PropertyBool.create("pos_z");
	public static final PropertyBool NEG_X = PropertyBool.create("neg_x");
	public static final PropertyBool NEG_Z = PropertyBool.create("neg_z");

	public static final int MASK_POS_X = 0b1;
	public static final int MASK_POS_Z = 0b10;
	public static final int MASK_NEG_X = 0b100;
	public static final int MASK_NEG_Z = 0b1000;
	public static final int MASK_S_DIR_SHIFT = 4;
	public static final int MASK_SLOPED = 0b100_0000;
	public static final int MASK_POS_X_NZ = 0b1_00000000;
	public static final int MASK_POS_X_PZ = 0b10_00000000;
	public static final int MASK_POS_Z_NX = 0b100_00000000;
	public static final int MASK_POS_Z_PX = 0b1000_00000000;
	public static final int MASK_NEG_X_NZ = 0b10000_00000000;
	public static final int MASK_NEG_X_PZ = 0b100000_00000000;
	public static final int MASK_NEG_Z_NX = 0b1000000_00000000;
	public static final int MASK_NEG_Z_PX = 0b10000000_00000000;
	private static final int NO_RENDER_MASK = Integer.MIN_VALUE;

	@Override
	public ItemBlock provideItem() {
		return new CatwalkRailingItem(this);
	}
	public static class CatwalkRailingItem extends ItemBlock {
		public CatwalkRailingItem(Block block) {
			super(block);
		}
		public EnumActionResult onItemUse(EntityPlayer player,World world,BlockPos pos,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ) {
			IBlockState state = world.getBlockState(pos);
			if (state.getBlock() instanceof CatwalkRailingBase base && hitX > 0 && hitX < 1 && hitZ > 0 && hitZ < 1 && facing.getAxis().isHorizontal()) {
				ItemStack stack = player.getHeldItem(hand);
				if (!stack.isEmpty() && player.capabilities.allowEdit && stack.canEditBlocks()) {
					Axis axis = facing.getAxis();
					if (axis == Axis.X) {
						hitX = 1-hitX;
						if (Math.abs(hitZ-0.5f) > 0.15f)
							hitX = 0.5f;
					} else if (axis == Axis.Z) {
						hitZ = 1-hitZ;
						if (Math.abs(hitX-0.5f) > 0.15f)
							hitZ = 0.5f;
					}
					if (base.tryAddRailing(world,pos,hitX,hitZ)) {
						stack.shrink(1);
						SoundType soundtype = state.getBlock().getSoundType(state,world,pos,player);
						world.playSound(player,pos,soundtype.getPlaceSound(),SoundCategory.BLOCKS,(soundtype.getVolume()+1.0F)/2.0F,soundtype.getPitch()*0.8F);
						return EnumActionResult.SUCCESS;
					}
				}
			} else
				return super.onItemUse(player,world,pos,hand,facing,hitX,hitY,hitZ);
			return EnumActionResult.FAIL;
		}
	}

	public boolean tryAddRailing(World world,BlockPos pos,float hitX,float hitZ) {
		IBlockState state = world.getBlockState(pos);
		hitX -= 0.5f;
		hitZ -= 0.5f;
		if (state.getBlock() instanceof CatwalkRailingBase) {
			if (Math.abs(hitX) >= Math.abs(hitZ)) {
				if (hitX >= 0) {
					if (!state.getValue(POS_X)) {
						world.setBlockState(pos,state.withProperty(POS_X,true));
						return true;
					}
				} else {
					if (!state.getValue(NEG_X)) {
						world.setBlockState(pos,state.withProperty(NEG_X,true));
						return true;
					}
				}
			} else {
				if (hitZ >= 0) {
					if (!state.getValue(POS_Z)) {
						world.setBlockState(pos,state.withProperty(POS_Z,true));
						return true;
					}
				} else {
					if (!state.getValue(NEG_Z)) {
						world.setBlockState(pos,state.withProperty(NEG_Z,true));
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public void addCollisionBoxToList(IBlockState state,World worldIn,BlockPos pos,AxisAlignedBB entityBox,List<AxisAlignedBB> collidingBoxes,@Nullable Entity entityIn,boolean isActualState) {
		if (state.getBlock().getMetaFromState(state) == 0)
			super.addCollisionBoxToList(state,worldIn,pos,entityBox,collidingBoxes,entityIn,isActualState);
		for (EnumFacing face : EnumFacing.HORIZONTALS) {
			boolean enabled = switch(face) {
				case NORTH -> state.getValue(NEG_Z);
				case SOUTH -> state.getValue(POS_Z);
				case WEST -> state.getValue(NEG_X);
				case EAST -> state.getValue(POS_X);
				default -> false;
			};
			if (enabled) {
				AxisAlignedBB aabb = LeafiaUtil.createAABBLayer(face,0.5/16,0,1).offset(pos);
				if (aabb.intersects(entityBox))
					collidingBoxes.add(aabb);
			}
		}
	}

	@Deprecated
	@Nullable
	public RayTraceResult collisionRayTrace(IBlockState state,World world,BlockPos pos,Vec3d start,Vec3d end) {
		RayTraceResult finalResult = null;
		double distance = 50;
		for (EnumFacing face : EnumFacing.HORIZONTALS) {
			boolean enabled = switch(face) {
				case NORTH -> state.getValue(NEG_Z);
				case SOUTH -> state.getValue(POS_Z);
				case WEST -> state.getValue(NEG_X);
				case EAST -> state.getValue(POS_X);
				default -> false;
			};
			if (enabled) {
				AxisAlignedBB aabb = LeafiaUtil.createAABBLayer(face,0.5/16,0,1).offset(pos);
				RayTraceResult result = aabb.calculateIntercept(start,end);
				if (result != null && result.typeOfHit != Type.MISS) {
					// find the closest BB hit
					double d = result.hitVec.distanceTo(start);
					if (d < distance) {
						distance = d;
						finalResult = result;
					}
				}
			}
		}
		if (finalResult != null)
			finalResult = new RayTraceResult(finalResult.hitVec,finalResult.sideHit,pos);
		return finalResult;
	}

	@Override
	public boolean shouldDrawHighlight(World world,BlockPos blockPos) {
		IBlockState state = world.getBlockState(blockPos);
		if (!(state.getBlock() instanceof CatwalkRailingBase))
			return false;
		if (getMetaFromState(state) == 0)
			return false;
		return true;
	}

	@Override
	public void drawHighlight(DrawBlockHighlightEvent event, World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() != this) return;

		final float exp = 0.002F;
		double dx = event.getPlayer().lastTickPosX + (event.getPlayer().posX - event.getPlayer().lastTickPosX) * event.getPartialTicks();
		double dy = event.getPlayer().lastTickPosY + (event.getPlayer().posY - event.getPlayer().lastTickPosY) * event.getPartialTicks();
		double dz = event.getPlayer().lastTickPosZ + (event.getPlayer().posZ - event.getPlayer().lastTickPosZ) * event.getPartialTicks();

		List<AxisAlignedBB> boxes = new ArrayList<>();
		for (EnumFacing face : EnumFacing.HORIZONTALS) {
			boolean enabled = switch(face) {
				case NORTH -> state.getValue(NEG_Z);
				case SOUTH -> state.getValue(POS_Z);
				case WEST -> state.getValue(NEG_X);
				case EAST -> state.getValue(POS_X);
				default -> false;
			};
			if (enabled)
				boxes.add(LeafiaUtil.createAABBLayer(face,0.5/16,0,1));
		}

		ICustomBlockHighlight.setup();
		for (AxisAlignedBB local : boxes) {
			AxisAlignedBB bb = local.expand(exp, exp, exp).offset(pos).offset(-dx, -dy, -dz);
			RenderGlobal.drawSelectionBoundingBox(bb, 0, 0, 0, 1.0F);
		}
		ICustomBlockHighlight.cleanup();
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn,BlockPos pos,EnumFacing facing,float hitX,float hitY,float hitZ,int meta,EntityLivingBase placer) {
		return super.getStateForPlacement(worldIn,pos,facing,hitX,hitY,hitZ,meta,placer);
	}

	public static final IUnlistedProperty<Integer> RENDER_MASK = new SimpleUnlistedProperty<>("render_mask",Integer.class);
	/// client-only; guarded by its own monitor — getExtendedState runs on chunk batcher threads
	private static final Long2ObjectOpenHashMap<Long2IntOpenHashMap> renderMaskCache = new Long2ObjectOpenHashMap<>();
	/// bumped on every invalidation so in-flight computations from stale ChunkCache snapshots don't repopulate the cache
	private static int renderMaskGeneration;

	public static void onClientWorldLoad(World world) {
		clearRenderMaskCache();
		world.addEventListener(new RenderMaskInvalidator());
	}

	public static void clearRenderMaskCache() {
		synchronized (renderMaskCache) {
			renderMaskCache.clear();
			renderMaskGeneration++;
		}
	}

	public static void invalidateRenderMaskCacheChunk(int chunkX,int chunkZ) {
		synchronized (renderMaskCache) {
			for (int x = -1; x <= 1; x++) {
				for (int z = -1; z <= 1; z++)
					renderMaskCache.remove(ChunkPos.asLong(chunkX + x,chunkZ + z));
			}
			renderMaskGeneration++;
		}
	}

	private static void invalidateRenderMaskAround(BlockPos pos) {
		synchronized (renderMaskCache) {
			for (int x = -1; x <= 1; x++) {
				for (int z = -1; z <= 1; z++) {
					long chunkKey = ChunkPos.asLong((pos.getX() + x) >> 4,(pos.getZ() + z) >> 4);
					Long2IntOpenHashMap chunkCache = renderMaskCache.get(chunkKey);
					if (chunkCache == null)
						continue;
					chunkCache.remove(pos.add(x,0,z).toLong());
					if (chunkCache.isEmpty())
						renderMaskCache.remove(chunkKey);
				}
			}
			renderMaskGeneration++;
		}
	}

	private static final class RenderMaskInvalidator implements IWorldEventListener {
		@Override
		public void notifyBlockUpdate(World world,BlockPos pos,IBlockState oldState,IBlockState newState,int flags) {
			// only railings feed getDiagonalMask; other block changes can't affect cached masks
			if (oldState.getBlock() instanceof CatwalkRailingBase || newState.getBlock() instanceof CatwalkRailingBase)
				invalidateRenderMaskAround(pos);
		}
		@Override public void notifyLightSet(BlockPos pos) {}
		@Override public void markBlockRangeForRenderUpdate(int x1,int y1,int z1,int x2,int y2,int z2) {}
		@Override public void playSoundToAllNearExcept(EntityPlayer player,SoundEvent sound,SoundCategory category,double x,double y,double z,float volume,float pitch) {}
		@Override public void playRecord(SoundEvent sound,BlockPos pos) {}
		@Override public void spawnParticle(int particleID,boolean ignoreRange,double x,double y,double z,double xSpeed,double ySpeed,double zSpeed,int... parameters) {}
		@Override public void spawnParticle(int id,boolean ignoreRange,boolean minimiseParticleLevel,double x,double y,double z,double xSpeed,double ySpeed,double zSpeed,int... parameters) {}
		@Override public void onEntityAdded(Entity entity) {}
		@Override public void onEntityRemoved(Entity entity) {}
		@Override public void broadcastSound(int soundID,BlockPos pos,int data) {}
		@Override public void playEvent(EntityPlayer player,int type,BlockPos pos,int data) {}
		@Override public void sendBlockBreakProgress(int breakerId,BlockPos pos,int progress) {}
	}

	public void initializeState() {
		setDefaultState(this.getBlockState().getBaseState()
				.withProperty(POS_X,false)
				.withProperty(POS_Z,false)
				.withProperty(NEG_X,false)
				.withProperty(NEG_Z,false)
		);
	}

	public CatwalkRailingBase(Material m,String s) {
		super(m,s);
		initializeState();
		IDynamicModels.INSTANCES.add(this);
	}

	public CatwalkRailingBase(Material m,SoundType sound,String s) {
		super(m,sound,s);
		initializeState();
		IDynamicModels.INSTANCES.add(this);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this,new IProperty[]{POS_X,POS_Z,NEG_X,NEG_Z},new IUnlistedProperty[]{RENDER_MASK});
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState()
				.withProperty(POS_X,(meta&MASK_POS_X)!=0)
				.withProperty(POS_Z,(meta&MASK_POS_Z)!=0)
				.withProperty(NEG_X,(meta&MASK_NEG_X)!=0)
				.withProperty(NEG_Z,(meta&MASK_NEG_Z)!=0);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int meta = 0;
		if (state.getValue(POS_X)) meta |= MASK_POS_X;
		if (state.getValue(POS_Z)) meta |= MASK_POS_Z;
		if (state.getValue(NEG_X)) meta |= MASK_NEG_X;
		if (state.getValue(NEG_Z)) meta |= MASK_NEG_Z;
		return meta;
	}

	private static int getDiagonalMask(IBlockAccess world,BlockPos pos) {
		int mask = 0;
		if (hasArm(world,pos.add( 0,0,-1),POS_X) || hasArm(world,pos.add( 1,0,-1),NEG_X)) mask |= MASK_POS_X_NZ;
		if (hasArm(world,pos.add( 0,0, 1),POS_X) || hasArm(world,pos.add( 1,0, 1),NEG_X)) mask |= MASK_POS_X_PZ;
		if (hasArm(world,pos.add(-1,0, 0),POS_Z) || hasArm(world,pos.add(-1,0, 1),NEG_Z)) mask |= MASK_POS_Z_NX;
		if (hasArm(world,pos.add( 1,0, 0),POS_Z) || hasArm(world,pos.add( 1,0, 1),NEG_Z)) mask |= MASK_POS_Z_PX;
		if (hasArm(world,pos.add( 0,0,-1),NEG_X) || hasArm(world,pos.add(-1,0,-1),POS_X)) mask |= MASK_NEG_X_NZ;
		if (hasArm(world,pos.add( 0,0, 1),NEG_X) || hasArm(world,pos.add(-1,0, 1),POS_X)) mask |= MASK_NEG_X_PZ;
		if (hasArm(world,pos.add(-1,0, 0),NEG_Z) || hasArm(world,pos.add(-1,0,-1),POS_Z)) mask |= MASK_NEG_Z_NX;
		if (hasArm(world,pos.add( 1,0, 0),NEG_Z) || hasArm(world,pos.add( 1,0,-1),POS_Z)) mask |= MASK_NEG_Z_PX;
		return mask;
	}

	@Override
	public IBlockState getExtendedState(IBlockState state,IBlockAccess world,BlockPos pos) {
		if (!(state instanceof IExtendedBlockState extState))
			return state;
		long chunkKey = ChunkPos.asLong(pos.getX() >> 4,pos.getZ() >> 4);
		long posKey = pos.toLong();
		int mask;
		int generation;
		synchronized (renderMaskCache) {
			Long2IntOpenHashMap chunkCache = renderMaskCache.get(chunkKey);
			mask = chunkCache == null ? NO_RENDER_MASK : chunkCache.get(posKey);
			generation = renderMaskGeneration;
		}
		if (mask == NO_RENDER_MASK) {
			mask = getMetaFromState(state) | getDiagonalMask(world,pos);
			synchronized (renderMaskCache) {
				if (generation == renderMaskGeneration) {
					Long2IntOpenHashMap chunkCache = renderMaskCache.get(chunkKey);
					if (chunkCache == null) {
						chunkCache = new Long2IntOpenHashMap();
						chunkCache.defaultReturnValue(NO_RENDER_MASK);
						renderMaskCache.put(chunkKey,chunkCache);
					}
					chunkCache.put(posKey,mask);
				}
			}
		}
		return extState.withProperty(RENDER_MASK,mask);
	}

	private static boolean hasArm(IBlockAccess world,BlockPos pos,PropertyBool arm) {
		IBlockState s = world.getBlockState(pos);
		if (!(s.getBlock() instanceof CatwalkRailingBase)) return false;
		return s.getValue(arm);
	}

	@Override
	public IBlockState getStateForPlacement(World world,BlockPos pos,EnumFacing facing,float hitX,float hitY,float hitZ,int meta,EntityLivingBase placer,EnumHand hand) {
		return getDefaultState()
				.withProperty(POS_X,true)
				.withProperty(POS_Z,true)
				.withProperty(NEG_X,true)
				.withProperty(NEG_Z,true);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public StateMapperBase getStateMapper(ResourceLocation loc) {
		return new StateMapperBase() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return new ModelResourceLocation(loc,"normal");
			}
		};
	}

	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite registeredSprite;

	@SideOnly(Side.CLIENT)
	@Override
	public void registerSprite(TextureMap map) {
		registeredSprite = map.registerSprite(new ResourceLocation("leafia",basePath+spritePath));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel() {
		Item item = Item.getItemFromBlock(this);
		ModelResourceLocation inv = new ModelResourceLocation(this.getRegistryName(),"inventory");
		ModelLoader.setCustomModelResourceLocation(item,0,inv);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void bakeModel(ModelBakeEvent evt) {
		HFRWavefrontObject wavefront = null;
		try {
			wavefront = new HFRWavefrontObject(new ResourceLocation("leafia","textures/"+basePath+modelPath+".obj"));
		} catch (Exception e) {
			System.out.println("Model baking error: "+e.getMessage());
			e.printStackTrace();
		}
		TextureAtlasSprite missing = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
		IBakedModel blockMdl;
		IBakedModel itemMdl;
		if (wavefront != null) {
			blockMdl = CatwalkRailingBakedModel.forBlock(this,wavefront,registeredSprite);
			itemMdl = CatwalkRailingBakedModel.forItem(this,wavefront,registeredSprite,0.75f,0,0,0,(float)Math.PI);
		} else {
			blockMdl = CatwalkRailingBakedModel.empty(this,missing);
			itemMdl = CatwalkRailingBakedModel.empty(this,missing);
		}
		ModelResourceLocation blockMrl = new ModelResourceLocation(getRegistryName(), "normal");
		evt.getModelRegistry().putObject(blockMrl,blockMdl);
		ModelResourceLocation itemMrl = new ModelResourceLocation(getRegistryName(), "inventory");
		evt.getModelRegistry().putObject(itemMrl,itemMdl);
	}

	@Override public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn,IBlockState state,BlockPos pos,EnumFacing face) { return BlockFaceShape.UNDEFINED; }
	@Override public boolean isFullCube(IBlockState state){ return false; }
	@Override public boolean isOpaqueCube(IBlockState state){ return false; }
}
