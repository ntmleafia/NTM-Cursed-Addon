package com.leafia.database;

import com.hbm.interfaces.IRadResistantBlock;
import com.hbm.packet.PacketDispatcher;
import com.leafia.dev.custompacket.LeafiaCustomPacket;
import com.leafia.dev.custompacket.LeafiaCustomPacketEncoder;
import com.leafia.dev.optimization.bitbyte.LeafiaBuf;
import com.leafia.unsorted.StructuralIntegrityHandler;
import com.leafia.unsorted.StructuralIntegrityHandler.SimulationData;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ImpactSeismic {
	public static float seismic = 0;
	public static void tryCollapseBlock(World world,BlockPos pos,IBlockState state,Block block) {
		boolean canCollapse = true;
		if (block instanceof IRadResistantBlock)
			if (((IRadResistantBlock)block).isRadResistant(world,pos))
				canCollapse = false;
		if (canCollapse) {
			if (world.rand.nextInt(100) == 0 && (world.isAirBlock(pos.down()) || !state.isOpaqueCube() || !state.isFullCube())) {
				SimulationData sim = StructuralIntegrityHandler.SERVER.handleBlock(world,pos,true,false,null);
				if (sim != null && sim.maxRatio >= 1) {
					if (!state.isFullBlock())
						world.destroyBlock(pos,true);
					else if (!state.isOpaqueCube())
						world.destroyBlock(pos,true);
					else {
						BlockPos newPos = pos;
						for (boolean stop = false; !stop; ) {
							BlockPos downPos = newPos.down();
							stop = true;
							if (!world.isBlockLoaded(downPos))
								break;
							if (!world.isValid(downPos))
								break;
							if (world.isAirBlock(downPos))
								stop = false;
							else {
								IBlockState dstate = world.getBlockState(downPos);
								if (!dstate.isOpaqueCube() || !dstate.isFullBlock()) {
									stop = false;
									world.destroyBlock(downPos,true);
								}
							}
							if (!stop) {
								newPos = downPos;
								for (int j2 = 0; j2 < 2; ++j2) {
									for (int k2 = 0; k2 < 2; ++k2) {
										for (int l2 = 0; l2 < 2; ++l2) {
											double d0 = ((double) j2+0.5D)/2.0D;
											double d1 = ((double) k2+0.5D)/2.0D;
											double d2 = ((double) l2+0.5D)/2.0D;
											NBTTagCompound data = new NBTTagCompound();
											data.setString("type","vanillaExt");
											data.setString("mode","blockdust");
											//data.setFloat("scale", 4);
											data.setInteger("block",Block.getIdFromBlock(block));
											data.setDouble("mX",d0-0.5);
											data.setDouble("mY",d1-0.75);
											data.setDouble("mZ",d2-0.5);
											//PacketDispatcher.wrapper.sendToAllAround(new AuxParticlePacketNT(data,downPos.getX()+d0,downPos.getY()+d1,downPos.getZ()+d2), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5, 250));
											//MainRegistry.proxy.effectNT(data); fuck off this causes a critical memory leak Bruh
											// whoops
										}
									}
								}
							}
						}
						if (newPos != pos) {
							world.setBlockState(newPos,state);
							world.setBlockToAir(pos);
							world.playSound(null,newPos,state.getBlock().getSoundType().getPlaceSound(),SoundCategory.BLOCKS,1,1);
							TomImpactCollapsePacket packet = new TomImpactCollapsePacket();
							packet.x = newPos.getX();
							packet.y0 = pos.getY();
							packet.y1 = newPos.getY();
							packet.z = newPos.getZ();
							LeafiaCustomPacket.__start(packet).__sendToAllAround(world.provider.getDimension(),pos,128);
						}
					}
				}
			}
		}
	}
	public static class TomImpactCollapsePacket implements LeafiaCustomPacketEncoder {
		public int x;
		public int y0;
		public int y1;
		public int z;
		@Override
		public void encode(LeafiaBuf buf) {
			buf.writeInt(x);
			buf.writeInt(y0);
			buf.writeInt(y1);
			buf.writeInt(z);
		}
		@Override
		public @Nullable Consumer<MessageContext> decode(LeafiaBuf buf) {
			int x = buf.readInt();
			int y0 = buf.readInt();
			int y1 = buf.readInt();
			int z = buf.readInt();
			return (ctx)->{
				Minecraft.getMinecraft().addScheduledTask(() -> {
					BlockPos pos = new BlockPos(x,y1,z);
					World world = Minecraft.getMinecraft().world;
					IBlockState state = world.getBlockState(pos);
					if (world.isAirBlock(pos)) {
						BlockPos pos0 = new BlockPos(x,y0,z);
						if (!world.isAirBlock(pos0))
							state = world.getBlockState(pos0);
					}
					for (int i = 1; i <= (y0-y1); i++) {
						for (int j2 = 0; j2 < 3; ++j2) {
							for (int k2 = 0; k2 < 3; ++k2) {
								for (int l2 = 0; l2 < 3; ++l2) {
									double d0 = ((double) j2 + 0.5D) / 3.0D;
									double d1 = ((double) k2 + 0.5D) / 3.0D;
									double d2 = ((double) l2 + 0.5D) / 3.0D;
									world.spawnParticle(EnumParticleTypes.BLOCK_DUST,pos.getX()+d0,pos.getY()+i+d1,pos.getZ()+d2,(d0-0.5)/i,d1-0.75,(d2-0.5)/i,Block.getStateId(state));
								}
							}
						}
					}
					//Minecraft.getMinecraft().effectRenderer.addBlockDestroyEffects(pos.up(i),state);
				});
			};
		}
	}
}
