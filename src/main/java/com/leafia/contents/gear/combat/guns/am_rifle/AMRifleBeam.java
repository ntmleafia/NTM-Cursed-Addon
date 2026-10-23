// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.gear.combat.guns.am_rifle;

import com.hbm.entity.projectile.EntityBulletBeamBase;
import com.hbm.items.weapon.sedna.BulletConfig;
import com.leafia.dev.math.FiaMatrix;
import com.leafia.dev.math.FiaMatrix.RotationOrder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AMRifleBeam extends EntityBulletBeamBase {
    public AMRifleBeam(EntityLivingBase entity, Entity lockTarget, BulletConfig config, float baseDamage,
                       double sideOffset, double heightOffset, double frontOffset) {
        super(entity, config, baseDamage);
        var yaw = entity.rotationYaw;
        var pitch = entity.rotationPitch;
        var eyeY = entity.posY + entity.getEyeHeight();
        if (lockTarget != null) {
            var aim = new FiaMatrix(new Vec3d(entity.posX, eyeY, entity.posZ),
                    new Vec3d(lockTarget.posX, lockTarget.posY + lockTarget.getEyeHeight(), lockTarget.posZ));
            var rotation = aim.getRotation(RotationOrder.YXZ);
            yaw = (float) -rotation.angleY + 180;
            pitch = (float) -rotation.angleX;
        }
        setLocationAndAngles(entity.posX, eyeY, entity.posZ, yaw, pitch);

        // Rotate the muzzle offset in pitch-then-yaw order without a temporary vector.
        var yawRadians = rotationYaw / 180F * (float) Math.PI;
        var pitchRadians = rotationPitch / 180F * (float) Math.PI;
        var pitchSin = Math.sin(-pitchRadians);
        var pitchCos = Math.cos(-pitchRadians);
        var yawSin = Math.sin(-yawRadians);
        var yawCos = Math.cos(-yawRadians);
        var forward = frontOffset * pitchCos - heightOffset * pitchSin;
        setPosition(posX + (sideOffset * yawCos + forward * yawSin),
                posY + (heightOffset * pitchCos + frontOffset * pitchSin),
                posZ + (-sideOffset * yawSin + forward * yawCos));

        var horizontal = MathHelper.cos(pitchRadians);
        headingX = -MathHelper.sin(yawRadians) * horizontal;
        headingZ = MathHelper.cos(yawRadians) * horizontal;
        headingY = -MathHelper.sin(pitchRadians);
        performHitscanExternal(250D);
    }

    public AMRifleBeam(World world) {
        super(world);
    }
}
