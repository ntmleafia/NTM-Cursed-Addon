package com.leafia.contents.building.catwalk.platform;

import com.hbm.blocks.network.SimpleUnlistedProperty;
import com.hbm.items.IDynamicModels;
import com.leafia.dev.blocks.blockbase.AddonBlockBase;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public abstract class CatwalkPlatformBase extends AddonBlockBase implements IDynamicModels {
	public static final PropertyBool POS_X = PropertyBool.create("support_pos_x");
	public static final PropertyBool POS_Z = PropertyBool.create("support_pos_z");
	public static final PropertyBool NEG_X = PropertyBool.create("support_neg_x");
	public static final PropertyBool NEG_Z = PropertyBool.create("support_neg_z");
	public static final int MASK_SUPPORT_POS_X = 0b1;
	public static final int MASK_SUPPORT_POS_Z = 0b10;
	public static final int MASK_SUPPORT_NEG_X = 0b100;
	public static final int MASK_SUPPORT_NEG_Z = 0b1000;

	public static final int MASK_PX = 0b1_0000;
	public static final int MASK_NX = 0b10_0000;
	public static final int MASK_PZ = 0b100_0000;
	public static final int MASK_NZ = 0b1000_0000;
	public static final int MASK_PX_PZ = 0b1_00000000;
	public static final int MASK_PX_NZ = 0b10_00000000;
	public static final int MASK_NX_PZ = 0b100_00000000;
	public static final int MASK_NX_NZ = 0b1000_00000000;
	public static final IUnlistedProperty<Integer> RENDER_MASK = new SimpleUnlistedProperty<>("render_mask",Integer.class);

	public void initializeState() {
		setDefaultState(this.getBlockState().getBaseState()
				.withProperty(POS_X,false)
				.withProperty(POS_Z,false)
				.withProperty(NEG_X,false)
				.withProperty(NEG_Z,false)
		);
	}
	SoundType sound = SoundType.STONE;
	final String reg;

	public CatwalkPlatformBase(Material m,String s) {
		super(m,s);
		reg = s;
		initializeState();
		IDynamicModels.INSTANCES.add(this);
	}

	public CatwalkPlatformBase(Material m,SoundType sound,String s) {
		super(m,sound,s);
		reg = s;
		initializeState();
		IDynamicModels.INSTANCES.add(this);
	}

	public abstract CatwalkPlatformBase copy(CatwalkPlatformBase base,String suffix);

	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this,new IProperty[]{POS_X,POS_Z,NEG_X,NEG_Z},new IUnlistedProperty[]{RENDER_MASK});
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState()
				.withProperty(POS_X,(meta&MASK_SUPPORT_POS_X)!=0)
				.withProperty(POS_Z,(meta&MASK_SUPPORT_POS_Z)!=0)
				.withProperty(NEG_X,(meta&MASK_SUPPORT_NEG_X)!=0)
				.withProperty(NEG_Z,(meta&MASK_SUPPORT_NEG_Z)!=0);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int meta = 0;
		if (state.getValue(POS_X)) meta |= MASK_SUPPORT_POS_X;
		if (state.getValue(POS_Z)) meta |= MASK_SUPPORT_POS_Z;
		if (state.getValue(NEG_X)) meta |= MASK_SUPPORT_NEG_X;
		if (state.getValue(NEG_Z)) meta |= MASK_SUPPORT_NEG_Z;
		return meta;
	}

	@Override
	public void bakeModel(ModelBakeEvent modelBakeEvent) {

	}

	@Override
	public void registerModel() {

	}

	@Override
	public void registerSprite(TextureMap textureMap) {

	}
}
