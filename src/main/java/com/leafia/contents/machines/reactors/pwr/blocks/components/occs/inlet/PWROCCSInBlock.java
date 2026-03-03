package com.leafia.contents.machines.reactors.pwr.blocks.components.occs.inlet;

import com.hbm.blocks.ILookOverlay;
import com.hbm.blocks.ITooltipProvider;
import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockRadResistant;
import com.hbm.main.MainRegistry;
import com.hbm.util.I18nUtil;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.machines.reactors.pwr.PWRData;
import com.leafia.contents.machines.reactors.pwr.blocks.components.PWRComponentBlock;
import com.leafia.contents.machines.reactors.pwr.blocks.components.control.PWRControlBlock;
import com.leafia.contents.machines.reactors.pwr.blocks.components.control.PWRControlTE;
import com.leafia.dev.machine.MachineTooltip;
import com.leafia.passive.LeafiaPassiveServer;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PWROCCSInBlock extends BlockRadResistant implements ITooltipProvider, ITileEntityProvider, PWRComponentBlock, ILookOverlay {
    public PWROCCSInBlock() {
        super(Material.IRON,"lwr_occs_in");
        this.setCreativeTab(MainRegistry.machineTab);
        ModBlocks.ALL_BLOCKS.remove(this);
        AddonBlocks.ALL_BLOCKS.add(this);
    }
    @Override
    public void addInformation(ItemStack stack,@Nullable World player,List<String> tooltip,ITooltipFlag advanced) {
        MachineTooltip.addMultiblock(tooltip);
        MachineTooltip.addModular(tooltip);
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
    public boolean tileEntityShouldCreate(World world,BlockPos pos) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn,int meta) {
        return new PWROCCSInTE();
    }

	@SideOnly(Side.CLIENT)
	@Override
	public void printHook(RenderGameOverlayEvent.Pre event,World world,BlockPos pos) {
		TileEntity entity = world.getTileEntity(pos);
		if (!(entity instanceof PWROCCSInTE te))
			return;
		List<String> texts = new ArrayList<>();

		PWRData data = te.getLinkedCoreDiagnosis();
		if (data != null)
			texts.add(TextFormatting.GREEN+"-> "+TextFormatting.RESET+data.tanks[5].getTankType().getLocalizedName()+": "+data.tanks[5].getFill()+"/"+data.tanks[5].getMaxFill()+"mB");

		ILookOverlay.printGeneric(event,I18nUtil.resolveKey(getTranslationKey()+".name"),0xFF55FF,0x3F153F,texts);
	}
}
