// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.building.generic.mixed;

import com.google.common.collect.ImmutableMap;
import com.hbm.blocks.BlockBase;
import com.hbm.blocks.ModBlocks;
import com.hbm.items.IDynamicModels;
import com.hbm.main.MainRegistry;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.building.generic.mixed.TextureAtlasSpriteHalf.HalfDirection;
import com.leafia.dev.blocks.blockbase.DirectionalBlockStates;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.leafia.contents.AddonBlocks.GenericBlockResistance.CONCRETE;

public class BlockMixedConcrete extends BlockBase implements IDynamicModels {
    public static final PropertyDirection FACING = DirectionalBlockStates.FACING;
    private static final String[] MODEL_FACINGS = {"north", "south", "west", "east", "up", "down"};
    private static final String[] SPLIT_FACES = {"top", "bottom", "left", "right"};
    final String a;
    final String b;

    public BlockMixedConcrete(String name, String firstTexture, String secondTexture) {
        super(Material.ROCK, name);
        ModBlocks.ALL_BLOCKS.remove(this);
        AddonBlocks.ALL_BLOCKS.add(this);
        INSTANCES.add(this);
        setCreativeTab(MainRegistry.blockTab);
        setHardness(15.0F);
        setResistance(CONCRETE.v);
        a = firstTexture;
        b = secondTexture;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return DirectionalBlockStates.createContainer(this);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return DirectionalBlockStates.metadata(state);
    }

    @Override
    public IBlockState getStateFromMeta(int metadata) {
        return DirectionalBlockStates.fromMetadata(getDefaultState(), metadata);
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rotation) {
        return DirectionalBlockStates.rotate(state, rotation);
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirror) {
        return DirectionalBlockStates.mirror(state, mirror);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos position, IBlockState state,
                                EntityLivingBase placer, ItemStack stack) {
        world.setBlockState(position, state.withProperty(FACING,
                EnumFacing.getDirectionFromEntityLiving(position, placer)));
    }

    private ResourceLocation splitTexture(String face) {
        return new ResourceLocation("hbm", "blocks/" + getRegistryName() + "_" + face);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void bakeModel(ModelBakeEvent event) {
        try {
            var textures = ImmutableMap.<String, String>builder()
                    .put("north", new ResourceLocation("hbm", "blocks/" + a).toString())
                    .put("south", new ResourceLocation("hbm", "blocks/" + b).toString())
                    .put("up", splitTexture("bottom").toString())
                    .put("down", splitTexture("top").toString())
                    .put("west", splitTexture("right").toString())
                    .put("east", splitTexture("left").toString()).build();
            var model = ModelLoaderRegistry.getModel(new ResourceLocation("minecraft:block/cube")).retexture(textures);
            var rotations = new ModelRotation[]{ModelRotation.X0_Y0, ModelRotation.X0_Y180,
                    ModelRotation.X0_Y270, ModelRotation.X0_Y90, ModelRotation.X270_Y0, ModelRotation.X90_Y0};
            var inventory = model.bake(rotations[0], DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter());
            for (var index = 0; index < MODEL_FACINGS.length; index++) {
                var baked = index == 0 ? inventory
                        : model.bake(rotations[index], DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter());
                event.getModelRegistry().putObject(new ModelResourceLocation(getRegistryName(),
                        "facing=" + MODEL_FACINGS[index]), baked);
            }
            event.getModelRegistry().putObject(new ModelResourceLocation(getRegistryName(), "inventory"), inventory);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0,
                new ModelResourceLocation(getRegistryName(), "facing=north"));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerSprite(TextureMap atlas) {
        var splits = new HalfDirection[]{HalfDirection.A_DOWN_B_UP, HalfDirection.A_UP_B_DOWN,
                HalfDirection.A_RIGHT_B_LEFT, HalfDirection.A_LEFT_B_RIGHT};
        atlas.registerSprite(new ResourceLocation("hbm", "blocks/" + a));
        atlas.registerSprite(new ResourceLocation("hbm", "blocks/" + b));
        for (var index = 0; index < SPLIT_FACES.length; index++) {
            atlas.setTextureEntry(new TextureAtlasSpriteHalf(splitTexture(SPLIT_FACES[index]).toString(),
                    "blocks/" + a, "blocks/" + b, splits[index]));
        }
    }
}
