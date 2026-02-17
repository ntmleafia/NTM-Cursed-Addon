package com.leafia.contents.machines.reactors.pwr.blocks.components.channel;

import com.hbm.blocks.ITooltipProvider;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.machines.reactors.pwr.blocks.components.PWRComponentBlock;
import com.leafia.dev.blocks.blockbase.AddonBlockBase;
import com.leafia.dev.machine.MachineTooltip;
import com.leafia.passive.LeafiaPassiveServer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class PWRChannelBlock extends AddonBlockBase implements ITooltipProvider, PWRComponentBlock {
    public PWRChannelBlock() {
        super(Material.IRON,"lwr_channel");
        this.setSoundType(AddonBlocks.PWR.soundTypePWRTube);
    }
    @Override
    public void addInformation(ItemStack stack,@Nullable World player,List<String> tooltip,ITooltipFlag advanced) {
        MachineTooltip.addMultiblock(tooltip);
        MachineTooltip.addModular(tooltip);
        MachineTooltip.addBoiler(tooltip);
        addStandardInfo(tooltip);
        super.addInformation(stack,player,tooltip,advanced);
    }

	@Override
	public void onBlockAdded(World worldIn,BlockPos pos,IBlockState state) {
		super.onBlockAdded(worldIn,pos,state);
		if (!worldIn.isRemote)
			LeafiaPassiveServer.queueFunction(()->beginDiagnosis(worldIn,pos,pos));
	}

	@Override
    public boolean shouldRenderOnGUI() {
        return true;
    }

    @Override
    public boolean tileEntityShouldCreate(World world,BlockPos pos) {
        return false;
    }
}
