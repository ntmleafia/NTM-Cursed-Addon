package com.leafia.contents.machines.elevators.floors;

import com.hbm.api.block.IToolable.ToolType;
import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.network.FluidDuctPaintable.FluidDuctPaintableModel;
import com.hbm.blocks.network.SimpleUnlistedProperty;
import com.hbm.items.IDynamicModels;
import com.hbm.items.tool.ItemTooling;
import com.hbm.lib.ForgeDirection;
import com.leafia.contents.machines.elevators.floors.model.EvFloorBakedModel;
import com.leafia.dev.LeafiaDebug;
import com.leafia.dev.blocks.blockbase.AddonBlockDummyable;
import com.leafia.dev.math.FiaBB;
import com.leafia.dev.math.FiaMatrix;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;

public class EvFloor extends AddonBlockDummyable implements IDynamicModels {
	public static final PropertyDirection FACE = PropertyDirection.create("face");
	public static final IUnlistedProperty<IBlockState> PAINT = new SimpleUnlistedProperty<>("paint",IBlockState.class);

	public EvFloor(Material materialIn,String s) {
		super(materialIn,s);
		IDynamicModels.INSTANCES.add(this);
	}

	@Override
	protected @NotNull BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this,new IProperty[]{META,FACE},new IUnlistedProperty[]{PAINT});
	}

	@Override
	public IBlockState getActualState(IBlockState state,IBlockAccess worldIn,BlockPos pos) {
		BlockPos core = findCore(worldIn,pos);
		if (core != null && worldIn.getBlockState(core).getBlock() instanceof EvFloor) {
			EnumFacing face = EnumFacing.byIndex(worldIn.getBlockState(core).getValue(META)-10);
			return state.withProperty(FACE,face);
		}
		return super.getActualState(state,worldIn,pos);
	}

	@Override
	public IBlockState getExtendedState(IBlockState state,IBlockAccess world,BlockPos pos) {
		if (!(state instanceof IExtendedBlockState extState)) {
			return state;
		}
		BlockPos core = findCore(world,pos);
		if (core != null) {
			TileEntity te = world.getTileEntity(core);
			if (te instanceof EvFloorTE floor) {
				if (floor.paintBlock != null) {
					IBlockState disguiseState = floor.paintBlock.getStateFromMeta(floor.paintMeta);
					return extState.withProperty(PAINT,disguiseState);
				}
			}
		}
		return super.getExtendedState(state,world,pos);
	}

	@Override
	public boolean onBlockActivated(World world,BlockPos pos,IBlockState state,EntityPlayer player,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ) {
		int[] core = this.findCore(world,pos.getX(),pos.getY(),pos.getZ());
		if (core == null) return false;
		ItemStack stack = player.getHeldItem(hand);
		Item item = stack.getItem();
		if (item instanceof ItemTooling tool) {
			if (tool.getType().equals(ToolType.SCREWDRIVER)) {
				if (!world.isRemote) {
					TileEntity te = world.getTileEntity(new BlockPos(core[0],core[1],core[2]));
					if (te instanceof EvFloorTE)
						((EvFloorTE)te).openGui(player);
				}
				return true;
			}
		} else if (item instanceof ItemBlock ib) {
			IBlockState paint = ib.getBlock().getStateFromMeta(stack.getMetadata());
			TileEntity te = world.getTileEntity(new BlockPos(core[0],core[1],core[2]));
			if (te instanceof EvFloorTE floor) {
				if (floor.paintBlock == null) {
					floor.paintBlock = paint.getBlock();
					floor.paintMeta = stack.getMetadata();
					floor.syncPaint();
					floor.markDirty();
					world.markChunkDirty(pos,floor);
					world.notifyBlockUpdate(pos,state,state,3);
					return true;
				}
			}
		} else {
			IBlockState coreState = world.getBlockState(new BlockPos(core[0],core[1],core[2]));
			FiaMatrix rot = getMatrix(getMetaFromState(coreState));
			FiaMatrix mat = new FiaMatrix(new Vec3d(core[0]+0.5,core[1],core[2]+0.5)).rotateAlong(rot);
			FiaMatrix clicked = new FiaMatrix(new Vec3d(pos.getX()+hitX,pos.getY()+hitY,pos.getZ()+hitZ));
			FiaMatrix rel = mat.toObjectSpace(clicked);
			//LeafiaDebug.debugMat(world,mat,1,0x00FF00,"mat");
			//LeafiaDebug.debugMat(world,clicked,1,0x00FF00,"clicked");
			//LeafiaDebug.debugLog(world,rel.position);
			double x = rel.position.x;
			double y = rel.position.y;
			float staticX = 0.125f*5;
			float staticY = 0.125f*8;
			if (x >= staticX && x <= staticX+1/16d && y >= staticY && y <= staticY+1/16d) {
				if (!world.isRemote) {

				}
				return true;
			}
		}
		return false;
	}

	@Override
	public int[] getDimensions(){
		return new int[]{2, 0, 0, 0, 1, 1};
	}
	@Override
	public int getOffset() {
		return 0;
	}
	public FiaMatrix getMatrix(int meta) {
		FiaMatrix mat = new FiaMatrix();
		switch(meta - 10) {
			case 2:
				mat = mat.rotateY(180); break;
			case 3:
				mat = mat.rotateY(0); break;
			case 4:
				mat = mat.rotateY(270); break;
			case 5:
				mat = mat.rotateY(90); break;
		}
		return mat;
	}
	public FiaMatrix getMatrix(IBlockAccess source,BlockPos pos) {
		int[] shit = findCore(source,pos.getX(),pos.getY(),pos.getZ());
		if (shit == null) return new FiaMatrix();
		IBlockState state = source.getBlockState(new BlockPos(shit[0],shit[1],shit[2]));
		return getMatrix(getMetaFromState(state));
	}
	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn,int meta) {
		if (meta >= 12)
			return new EvFloorTE();
		return null;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state,IBlockAccess source,BlockPos pos) {
		FiaBB bb = new FiaBB(new FiaMatrix(new Vec3d(0.5,0,0.5)).rotateAlong(getMatrix(source,pos)).translate(0,0,0.5).rotateY(180),-0.5,0,0.5,1,2/16d);
		return bb.toAABB();
	}

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState,IBlockAccess worldIn,BlockPos pos) {
		if (blockState.getValue(META) >= extra) {
			int[] shit = findCore(worldIn,pos.getX(),pos.getY(),pos.getZ());
			if (shit != null) {
				TileEntity te = worldIn.getTileEntity(new BlockPos(shit[0],shit[1],shit[2]));
				if (te instanceof EvFloorTE) {
					if (((EvFloorTE) te).open.cur > 0) return NULL_AABB;
				}
			}
		}
		return super.getCollisionBoundingBox(blockState,worldIn,pos);
	}

	@Override
	protected void fillSpace(World world,int x,int y,int z,ForgeDirection dir,int o) {
		super.fillSpace(world,x,y,z,dir,o);
		makeExtra(world,x,y+1,z);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		if (state.getValue(META) < extra)
			return EnumBlockRenderType.MODEL;
		return super.getRenderType(state);
	}

	public boolean shouldSideBeRendered(@NotNull IBlockState blockState,@NotNull IBlockAccess blockAccess,@NotNull BlockPos pos,@NotNull EnumFacing side) {
		return blockState.getValue(FACE).equals(side);
	}

	@Override
	public void bakeModel(ModelBakeEvent event) {
		EvFloorBakedModel model = new EvFloorBakedModel();
		ModelResourceLocation normal = new ModelResourceLocation(Objects.requireNonNull(getRegistryName()), "normal");

		event.getModelRegistry().putObject(normal, model);
	}
}
