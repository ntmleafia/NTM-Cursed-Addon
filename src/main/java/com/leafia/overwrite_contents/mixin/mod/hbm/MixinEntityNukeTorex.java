package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.entity.effect.EntityNukeTorex;
import com.hbm.interfaces.IConstantRenderer;
import com.hbm.packet.PacketDispatcher;
import com.leafia.overwrite_contents.interfaces.IMixinEntityNukeTorex;
import com.leafia.overwrite_contents.packets.TorexPacket;
import com.llib.exceptions.LeafiaDevFlaw;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Mixin(value = EntityNukeTorex.class)
public abstract class MixinEntityNukeTorex extends Entity implements IConstantRenderer, IMixinEntityNukeTorex {
    @Unique
    private static Method writeNBT;
    static {
        writeNBT = ObfuscationReflectionHelper.findMethod(
                EntityNukeTorex.class,
                "func_70014_b", // writeEntityToNBT
                null,
                NBTTagCompound.class
        );
        writeNBT.setAccessible(true);
    }
    @Unique
    private static Entity bindMe = null;
    @Unique
    private double initPosX;
    @Unique
    private double initPosY;
    @Unique
    private double initPosZ;
    @Unique
    private boolean valid = false;
    @Unique
    private Entity boundEntity;
    @Unique
    private boolean calculationFinished;

    public MixinEntityNukeTorex(World worldIn) {
        super(worldIn);
    }

    @Override
    public double getInitPosX() {
        return initPosX;
    }

    @Override
    public void setInitPosX(double value) {
        initPosX = value;
    }

    @Override
    public double getInitPosY() {
        return initPosY;
    }

    @Override
    public void setInitPosY(double value) {
        initPosY = value;
    }

    @Override
    public double getInitPosZ() {
        return initPosZ;
    }

    @Override
    public void setInitPosZ(double value) {
        initPosZ = value;
    }

    @Override
    public boolean getValid() {
        return valid;
    }

    @Override
    public void setValid(boolean value) {
        valid = value;
    }

    @Override
    public Entity getBoundEntity(){
        return boundEntity;
    }

    @Override
    public void setBoundEntity(Entity entity){
        this.boundEntity = entity;
    }

    @Override
    public boolean getCalculationFinished(){
        return calculationFinished;
    }

    @Override
    public void setCalculationFinished(boolean value) {
        calculationFinished = value;
    }

    @Redirect(method = "statFac", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
    private static boolean onTorexStatFac(World instance, Entity entity){
        spawnTorex(instance, (EntityNukeTorex) entity);
        return true;
    }

    @Redirect(method = "statFacBale", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
    private static boolean onTorexStatFacBale(World instance, Entity entity){
        spawnTorex(instance, (EntityNukeTorex) entity);
        return true;
    }

    public static void spawnTorex(World world, EntityNukeTorex torex) {
        IMixinEntityNukeTorex mixin = (IMixinEntityNukeTorex) torex;
        if (mixin.getBoundEntity() == null) {
            if (bindMe != null) {
                if (bindMe.isEntityAlive() && bindMe.ticksExisted < 10) {
                    if ((bindMe.dimension == torex.dimension) && (Math.sqrt(bindMe.getPosition().distanceSq(torex.getPosition())) < 1.5)) {
                        ((IMixinEntityNukeTorex) torex).setBoundEntity(bindMe);
                        bindMe = null;
                    }
                } else bindMe = null;
            }
        }
        mixin.setInitPosX(torex.posX);
        mixin.setInitPosY(torex.posY);
        mixin.setInitPosZ(torex.posZ);
        mixin.setValid(true);
        world.weatherEffects.add(torex);
        TorexPacket packet = new TorexPacket();
        packet.entityId = torex.getEntityId();
        packet.uuid = torex.getUniqueID();
        packet.x = mixin.getInitPosX();
        packet.y = mixin.getInitPosY();
        packet.z = mixin.getInitPosZ();
        mixin.setCalculationFinished(mixin.getBoundEntity() == null);
        packet.doWait = mixin.getBoundEntity() != null;
        NBTTagCompound nbt = new NBTTagCompound();
        try {
            writeNBT.invoke(torex, nbt);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new LeafiaDevFlaw(e);
        }
        packet.nbt = nbt;
        double amp = torex.getScale()*100;
        PacketDispatcher.wrapper.sendToAllAround(packet,new NetworkRegistry.TargetPoint(
                        torex.dimension,
                        packet.x,
                        packet.y,
                        packet.z,
                        200+amp+Math.pow(amp,0.8)*8
                )
        );
    }
}
