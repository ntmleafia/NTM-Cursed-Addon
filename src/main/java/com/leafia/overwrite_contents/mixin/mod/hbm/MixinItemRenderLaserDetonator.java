// SPDX-FileCopyrightText: 2025-2026 Leafia
// SPDX-FileCopyrightText: 2025-2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.render.item.ItemRendererDetonatorLaser;
import com.hbm.render.item.TEISRBase;
import com.leafia.dev.items.LeafiaGripOffsetHelper;
import com.leafia.eventbuses.LeafiaClientListener.HandlerClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemRendererDetonatorLaser.class)
public abstract class MixinItemRenderLaserDetonator extends TEISRBase {
	@Unique
	private static final LeafiaGripOffsetHelper offsets = new LeafiaGripOffsetHelper()
			.get(TransformType.GUI)
			.setScale(0.25).setPosition(-2.25,1.45,-1.25).setRotation(-40,0,0).getHelper()

			.get(TransformType.FIXED)
			.copySettings(TransformType.GUI).getHelper()

			.get(TransformType.FIRST_PERSON_RIGHT_HAND)
			.setScale(0.25).setPosition(-1.30,1.05,-3.25).setRotation(14,91,5).getHelper()
			.get(TransformType.FIRST_PERSON_LEFT_HAND) // Whoops
			.setScale(0.25).setPosition(1.75,6.25,-12.75).setRotation(-5,-5,-10).getHelper()
			.get(TransformType.THIRD_PERSON_RIGHT_HAND)
			.setScale(0.25).setPosition(-2.10,1.60,-1.35).setRotation(7,92,-2).getHelper()
			.get(TransformType.THIRD_PERSON_LEFT_HAND)
			.setScale(1).setPosition(1.25,0,0).setRotation(0,-9,0).getHelper()

			.get(TransformType.GROUND)
			.setScale(0.25).setPosition(-2,0.25,-1.75).setRotation(0,0,0).getHelper();

	@Unique
	private final LeafiaGripOffsetHelper.LeafiaGripOffset ADS = new LeafiaGripOffsetHelper.LeafiaGripOffset(offsets)
			.setScale(1.01).setPosition(-3.35,1,-0.9).setRotation(-14,1,-5);


    @Redirect(method = "renderByItem", at = @At(value = "FIELD",
            target = "Lcom/hbm/render/item/ItemRendererDetonatorLaser;type:Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;",
            opcode = Opcodes.GETFIELD, ordinal = 0, remap = false), require = 1)
    private TransformType leafia$applyViewOffsets(ItemRendererDetonatorLaser renderer) {
        var actual = type;
        boolean aimed = (actual == TransformType.FIRST_PERSON_RIGHT_HAND || actual == TransformType.FIRST_PERSON_LEFT_HAND) &&
                Math.signum(HandlerClient.getViewADS(Minecraft.getMinecraft().player)) == (actual == TransformType.FIRST_PERSON_RIGHT_HAND ? 1 : -1);
        offsets.apply(aimed ? TransformType.FIRST_PERSON_RIGHT_HAND : actual);
        if (aimed) offsets.applyCustomOffset(ADS);
        // NONE selects the dependency renderer's branch without a second camera transform.
        return TransformType.NONE;
    }
}
