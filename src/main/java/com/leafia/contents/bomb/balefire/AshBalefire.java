package com.leafia.contents.bomb.balefire;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockMeta;
import com.hbm.handler.radiation.ChunkRadiationManager;
import com.hbm.items.ModItems;
import com.hbm.potion.HbmPotion;
import com.leafia.contents.AddonBlocks;
import com.leafia.shit.BlockFallingLeafia;
import com.leafia.shit.Bruh;
import com.leafia.unsorted.ParticleBalefire;
import com.leafia.unsorted.ParticleBalefireLava;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Random;

import static net.minecraft.block.BlockSnow.LAYERS;

public class AshBalefire extends BlockFallingLeafia {
    protected static final AxisAlignedBB[] SNOW_AABB = new AxisAlignedBB[] {new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.375D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.625D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.875D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)};
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        if (side == EnumFacing.UP)
        {
            return true;
        }
        else
        {
            IBlockState iblockstate = blockAccess.getBlockState(pos.offset(side));
            return iblockstate.getBlock() == this && ((Integer)iblockstate.getValue(LAYERS)).intValue() >= ((Integer)blockState.getValue(LAYERS)).intValue() ? false : super.shouldSideBeRendered(blockState, blockAccess, pos, side);
        }
    }
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(LAYERS, Integer.valueOf((meta & 7) + 1));
    }
    public int getMetaFromState(IBlockState state)
    {
        return ((Integer)state.getValue(LAYERS)).intValue() - 1;
    }
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {LAYERS});
    }
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return SNOW_AABB[((Integer)state.getValue(LAYERS)).intValue()];
    }
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
    {
        return ((Integer)worldIn.getBlockState(pos).getValue(LAYERS)).intValue() < 5;
    }
    public boolean isTopSolid(IBlockState state)
    {
        return ((Integer)state.getValue(LAYERS)).intValue() == 8;
    }
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        int i = ((Integer)blockState.getValue(LAYERS)).intValue() - 1;
        float f = 0.125F;
        AxisAlignedBB axisalignedbb = blockState.getBoundingBox(worldIn, pos);
        return new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.maxX, (double)((float)i * 0.125F), axisalignedbb.maxZ);
    }
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////

    public AshBalefire(Material mat,String s,SoundType type) {
        super(mat, s, type);
        this.setDefaultState(this.blockState.getBaseState().withProperty(LAYERS, Integer.valueOf(1)));
        this.setTickRandomly(true);
    }
    public int tickRate(World worldIn)
    {
        return 1;
    }
    @Override
    public boolean spaceBelow(World worldIn, BlockPos pos) {
        return !worldIn.getBlockState(pos.down()).isFullCube() && !worldIn.getBlockState(pos.down()).isTopSolid();
    }
    @Override
    public void blockLanded(World worldIn,BlockPos pos,IBlockState block,Bruh entity,Random rand) {
        if (!spaceBelow(worldIn,pos)) {
            if (worldIn.mayPlace(block.getBlock(), pos, true, EnumFacing.UP, entity)) {
                worldIn.setBlockState(pos, block);
                return;
            } else if (worldIn.getBlockState(pos).getBlock() == AddonBlocks.ash_balefire) {
                int layer = worldIn.getBlockState(pos).getValue(LAYERS)+block.getValue(LAYERS);
                worldIn.setBlockState(pos,block.withProperty(LAYERS,Math.min(layer,8)));
                if ((layer > 8) && worldIn.mayPlace(block.getBlock(), pos.up(), true, EnumFacing.UP, entity)) {
                    worldIn.setBlockState(pos.up(), block.withProperty(LAYERS, layer - 8));
                    return;
                } else
                    entity.entityDropItem(
                            new ItemStack(
                                    ModItems.powder_balefire,
                                    drops(layer-8,rand
                                    )
                            ), 1.0F);
                return;
            } else {
                entity.entityDropItem(
                        new ItemStack(
		                        ModItems.powder_balefire,
                                quantityDropped(block, 0, rand
                                )
                        ), 0.0F);
                return;
            }
        } else
            entity.entityDropItem(
                    new ItemStack(
		                    ModItems.powder_balefire,
                            quantityDropped(block,0,rand
                            )
                    ), 0.0F);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    private int drops(int layer,Random random) {
        return Math.floorDiv(layer,2)-random.nextInt(2);
    }
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return ModItems.powder_balefire;
    }

    @Override
    public int quantityDropped(IBlockState state, int fortune, Random random) {
        return drops(state.getValue(LAYERS),random);
    }
    @Override
    public void onEntityWalk(World world, BlockPos pos, Entity entity){
        entity.setFire(6);
        if (entity instanceof EntityLivingBase)
            ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(HbmPotion.radiation, 5 * 5, 4));
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        entityIn.setFire(6);
        if (entityIn instanceof EntityLivingBase)
            ((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(HbmPotion.radiation, 5 * 5, 4));
    }
    protected boolean isSurroundingBlockFlammable(World worldIn, BlockPos pos)
    {
        for (EnumFacing enumfacing : EnumFacing.values())
        {
            if (this.getCanBlockBurn(worldIn, pos.offset(enumfacing)))
            {
                return true;
            }
        }

        return false;
    }

    private boolean getCanBlockBurn(World worldIn, BlockPos pos)
    {
        return pos.getY() >= 0 && pos.getY() < 256 && !worldIn.isBlockLoaded(pos) ? false : worldIn.getBlockState(pos).getMaterial().getCanBurn();
    }
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (rand.nextInt(4000) == 0) {
            if (state.getValue(LAYERS) <= 2) {
                worldIn.setBlockState(pos,ModBlocks.balefire.getDefaultState());
                return;
            } else
                worldIn.setBlockState(pos,state.withProperty(LAYERS,state.getValue(LAYERS)-2));
        } else if (rand.nextInt(8) < state.getValue(LAYERS)) {
            BlockPos target = pos.add(
                    rand.nextInt(5)-2,
                    rand.nextInt(5)-2,
                    rand.nextInt(5)-2
            );
            if (worldIn.isAirBlock(target) || worldIn.getBlockState(target).getBlock() == AddonBlocks.ash_balefire) {
                if (isSurroundingBlockFlammable(worldIn,target)) {
                    worldIn.setBlockState(target,ModBlocks.balefire.getDefaultState());
                }
            } else {
				IBlockState state1 = worldIn.getBlockState(target);
                Block bock = state1.getBlock();
                if (bock == Blocks.COBBLESTONE || bock == Blocks.STONE || bock == AddonBlocks.baleonitite || bock == Blocks.MONSTER_EGG || state1.getMaterial().equals(Material.ROCK)) {
                    int rng = (rand.nextInt(3)+1)^2;
                    if (rng <= 1) worldIn.setBlockState(target,AddonBlocks.baleonitite.getDefaultState().withProperty(BlockMeta.META,2));
                    else if (rng <= 4) worldIn.setBlockState(target,AddonBlocks.baleonitite.getDefaultState().withProperty(BlockMeta.META,1));
                    else if (rng <= 9) worldIn.setBlockState(target,AddonBlocks.baleonitite.getDefaultState().withProperty(BlockMeta.META,0));
                }
            }
        }
		ChunkRadiationManager.proxy.incrementRad(worldIn,pos,80*0.01F,80);
        super.updateTick(worldIn,pos,state,rand);
        worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn) + rand.nextInt(5));
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("incomplete-switch")
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        super.randomDisplayTick(state, world, pos, rand);
        if (rand.nextDouble() < 0.2D)
        {
            world.playSound((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.5F, false);
        }
        for(EnumFacing dir : EnumFacing.VALUES) {

            if(dir == EnumFacing.DOWN)
                continue;
            for (int rep = 0; rep < 2; rep+=(rand.nextInt(2)+1)) {
                if (world.getBlockState(pos.offset(dir)).getMaterial() == Material.AIR) {
                    double offs = rand.nextDouble();
                    if (dir != EnumFacing.UP) {
                        if (offs * 8 > state.getValue(LAYERS))
                            continue;
                    }

                    double ix = pos.getX() + 0.5F + dir.getXOffset() + rand.nextDouble() - 0.5D;
                    double iy = pos.getY() + offs;
                    double iz = pos.getZ() + 0.5F + dir.getZOffset() + rand.nextDouble() - 0.5D;

                    if (dir.getXOffset() != 0)
                        ix = pos.getX() + 0.5F + dir.getXOffset() * 0.5 + rand.nextDouble() * 0.125 * dir.getXOffset();
                    if (dir.getYOffset() != 0)
                        iy = pos.getY() + state.getValue(LAYERS)/8D + rand.nextDouble() * 0.125;
                    if (dir.getZOffset() != 0)
                        iz = pos.getZ() + 0.5F + dir.getZOffset() * 0.5 + rand.nextDouble() * 0.125 * dir.getZOffset();

                    ParticleBalefire fx = new ParticleBalefire(world, ix, iy, iz);
                    Minecraft.getMinecraft().effectRenderer.addEffect(fx);
                    if (rand.nextInt(2) == 0) {
                        ParticleBalefireLava fx2 = new ParticleBalefireLava(world, ix, iy, iz);
                        Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
                    }
                    world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, ix, iy, iz, 0.0, 0.0, 0.0);
                    world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, ix, iy, iz, 0.0, 0.1, 0.0);
                }
            }
        }
    }
}
