package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.interfaces.ILaserable;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.lib.ModDamageSource;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.tileentity.machine.TileEntityCore;
import com.hbm.tileentity.machine.TileEntityCoreEmitter;
import com.leafia.dev.LeafiaUtil;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCore;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCoreEmitter;
import com.llib.LeafiaLib;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import org.spongepowered.asm.mixin.*;

import java.util.List;

@Mixin(value = TileEntityCoreEmitter.class, remap = false)
public abstract class MixinTileEntityCoreEmitter extends TileEntityMachineBase implements ITickable, IMixinTileEntityCoreEmitter {
    public MixinTileEntityCoreEmitter(int scount) {
        super(scount);
    }
    @Final
    @Shadow
    public static long maxPower;
    @Final
    @Shadow
    public static int range = 50;
    @Shadow
    public long power;
    @Shadow
    public int watts;
    @Shadow
    public int beam;
    @Shadow
    public long joules;
    @Shadow
    public boolean isOn;
    @Shadow
    public FluidTankNTM tank;
    @Shadow
    public long prev;
    @Unique
    private BlockPos targetPosition = new BlockPos(0,0,0);
    @Unique
    private RayTraceResult lastRaycast;
    @Unique
    private boolean isActive;


    /**
     * @author mlbv
     * @reason seriously who cares?
     */
    @Override
    @Overwrite
    public void update() {
        if (!world.isRemote) {
            LeafiaPacket._start(this).__write(31,targetPosition).__sendToAffectedClients();

            //this.updateStandardConnections(world, pos);
            //this.updateSPKConnections(world, pos);

            watts = MathHelper.clamp(watts, 1, 100);
            long demand = maxPower * Math.min(watts, 100) / 2000;
            isActive = false;

            beam = 0;

            if (joules > 0 || prev > 0) {

                if (tank.getFluidAmount() >= 20) {
                    tank.drain(20, true);
                } else {
                    world.setBlockState(pos, Blocks.FLOWING_LAVA.getDefaultState());
                    return;
                }
            }

            if (isOn) {
                //i.e. 50,000,000 HE = 10,000 SPK
                //1 SPK = 5,000HE

                if (power >= demand) {
                    power -= demand;
                    long add = watts * 100;
                    if (add > Long.MAX_VALUE - joules)
                        joules = Long.MAX_VALUE;
                    else
                        joules += add;
                }
                prev = joules;

                if (joules > 0) {

                    long out = joules;

					/*
					EnumFacing dir = EnumFacing.getFront(this.getBlockMetadata());
					for(int i = 1; i <= range; i++) {

						beam = i;

						int x = pos.getX() + dir.getFrontOffsetX() * i;
						int y = pos.getY() + dir.getFrontOffsetY() * i;
						int z = pos.getZ() + dir.getFrontOffsetZ() * i;

						BlockPos pos1 = new BlockPos(x, y, z);


					}*/
                    isActive = true;
                    raycast(out);

                    joules = 0;
                }
            } else {
                joules = 0;
                prev = 0;
            }

            this.markDirty();

            LeafiaPacket packet = LeafiaPacket._start(this)
                                              .__write(0, isOn)
                                              .__write(1, watts)
                                              .__write(2, prev)
                                              .__write(3, isActive);
            //if (watts != prevWatts)
            //	packet.__write(1,watts);
            packet.__sendToAffectedClients();
			/*
			//PacketDispatcher.wrapper.sendToAllTracking(new AuxGaugePacket(pos, beam, 0), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 250));
			if(watts != prevWatts) PacketDispatcher.wrapper.sendToAllTracking(new AuxGaugePacket(pos, watts, 1), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 250));
			PacketDispatcher.wrapper.sendToAllTracking(new AuxLongPacket(pos, prev, 0), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 250));
			prevWatts = watts;
			*/
            //this.networkPack(data, 250);
        } else if (isOn) {
            lastRaycast = raycast(0);
        }
    }


    //mlbv: intentionally marked this final. overriding may need bridge methods when you need to call super.raycast due to compile-time visibility problems
    @Override
    public final RayTraceResult raycast(long out) {
        return LeafiaLib.leafiaRayTraceBlocksCustom(world, new Vec3d(pos).add(0.5, 0.5, 0.5), new Vec3d(pos).add(0.5, 0.5, 0.5).add(getDirection().scale(range)), (process, config, current) -> {
            if (!world.isRemote) {
                Vec3d centerVec = current.posIntended.add(new Vec3d(config.pivotAxisFace.getDirectionVec()).scale(0.5)
                                                                                                           .add(config.secondaryVector.scale(0.5)));
                List<Entity> list = world.getEntitiesWithinAABB(Entity.class, LeafiaUtil.createAABB(
                        centerVec.subtract(0.5, 0.5, 0.5), centerVec.add(0.5, 0.5, 0.5)
                ));
                for (Entity e : list) {
                    e.attackEntityFrom(ModDamageSource.amsCore, joules * 0.000001F);
                    e.setFire(10);
                }
            }
            if (current.posSnapped.equals(pos)) return process.CONTINUE();

            RayTraceResult miss = new RayTraceResult(RayTraceResult.Type.MISS, current.posIntended, config.pivotAxisFace, current.posSnapped);
            if (!current.block.canCollideCheck(current.state, true))
                return process.CONTINUE(miss);

            RayTraceResult result = current.state.collisionRayTrace(world, current.posSnapped, current.posIntended.subtract(config.unitDir.scale(2)), current.posIntended.add(config.unitDir.scale(2)));
            if (result == null)
                return process.CONTINUE(miss);

            Vec3d vec = result.hitVec;
            TileEntity te = world.getTileEntity(current.posSnapped);
            if (te instanceof ILaserable) {
                if (!world.isRemote) ((ILaserable) te).addEnergy(world, current.posSnapped, out * 100 * watts / 10000, config.pivotAxisFace);
                return process.RETURN(result);
            }

            if (te instanceof TileEntityCore) {
                //out = Math.max(0, ((TileEntityCore)te).burn(out));
                IMixinTileEntityCore mixin = (IMixinTileEntityCore) te;
                if (!world.isRemote) mixin.setDFCIncomingSpk(mixin.getDFCIncomingSpk() + out / 1000_000d);
                //continue;
                //break;
                result.hitVec = new Vec3d(te.getPos()); // align to the center
                mixin.getDFCComponentPositions().add(pos);
                return process.RETURN(result);
            }

            IBlockState state = current.state;

            if (current.block != Blocks.AIR) { //(!state.getRenderType().equals(EnumBlockRenderType.INVISIBLE)) {
                if (!world.isRemote) {
                    if (state.getMaterial().isLiquid()) {
                        world.playSound(null, vec.x, vec.y, vec.z, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        world.setBlockToAir(result.getBlockPos());
                        return process.RETURN(result);
                    }
                    @SuppressWarnings("deprecation")
                    float hardness = state.getBlock().getExplosionResistance(null);
                    if (hardness < 10000 && world.rand.nextDouble() / 20 < (out * 0.00000001F) / hardness) {
                        world.playSound(null, vec.x, vec.y, vec.z, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        world.getBlockState(result.getBlockPos()).getBlock().onBlockExploded(world, result.getBlockPos(), new Explosion(world, null, result.getBlockPos().getX(), result.getBlockPos().getY(), result.getBlockPos().getZ(), 5, false, false));
                        //world.destroyBlock(pos1, false);
                    }
                }
                return process.RETURN(result);
            } else
                return process.CONTINUE(result);
        });
    }

    // networking
    @Override
    public void onReceivePacketLocal(byte key, Object value) {
        IMixinTileEntityCoreEmitter.super.onReceivePacketLocal(key, value);
        switch (key) {
            case 0:
                isOn = (boolean) value;
                break;
            case 1:
                watts = (int) value;
                break;
            case 2:
                prev = (long) value;
                break;
            case 3:
                isActive = (boolean) value;
                break;
        }
    }




    @Override
    public BlockPos targetPosition() {
        return targetPosition;
    }

    @Override
    public void targetPosition(BlockPos pos) {
        targetPosition = pos;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public void isActive(boolean active) {
        isActive = active;
    }
}
