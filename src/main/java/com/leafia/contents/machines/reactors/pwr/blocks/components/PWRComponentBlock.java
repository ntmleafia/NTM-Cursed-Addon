package com.leafia.contents.machines.reactors.pwr.blocks.components;

import com.leafia.contents.machines.reactors.pwr.PWRDiagnosis;
import com.leafia.dev.LeafiaDebug;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface PWRComponentBlock {
    default boolean shouldRenderOnGUI() {
        return false;
    }
	/// Only control rods and elements may return false. Basically if the TE is stacked or not
    boolean tileEntityShouldCreate(World world,BlockPos pos);
	/// Get this TileEntity as PWR component
    @Nullable
    default PWRComponentEntity getPWR(World world,BlockPos pos) {
        if (!tileEntityShouldCreate(world,pos)) return null;
        TileEntity entity = world.getTileEntity(pos);
        if (entity != null) {
            if (entity instanceof PWRComponentEntity) {
                return (PWRComponentEntity)entity;
            }
        }
        return null;
    };
	/// getPWR but as TileEntity (is this even necessary)
    @Nullable
    @Deprecated
    default TileEntity getEntity(World world,BlockPos pos) {
        if (!tileEntityShouldCreate(world,pos)) return null;
        TileEntity entity = world.getTileEntity(pos);
        if (entity != null) {
            if (entity instanceof PWRComponentEntity) {
                return entity;
            }
        }
        return null;
    };
	/// Start diagnosis (DOES NOT WORK ON CLIENT)
    default void beginDiagnosis(World world,BlockPos pos,BlockPos trigger) {
        if (world.isRemote) return;
        PWRDiagnosis.cleanup();
        if (PWRDiagnosis.preventScan.contains(pos)) {
            LeafiaDebug.debugLog(world,"Cancelled possible duplicate diagnosis");
            return;
        }
        PWRDiagnosis diagnosis = new PWRDiagnosis(world,trigger);
        diagnosis.addPosition(pos);
    }
}
