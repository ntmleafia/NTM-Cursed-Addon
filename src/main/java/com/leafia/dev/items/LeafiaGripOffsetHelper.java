// SPDX-FileCopyrightText: 2025-2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.dev.items;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;
import org.lwjgl.input.Keyboard;

import java.util.EnumMap;
import java.util.Map;

/**
 * Per-view model transforms, with optional keyboard editing.
 * Left-hand views first apply their corresponding right-hand transform; NONE is
 * applied last to every view. Debug edits are temporary and must be copied into
 * the renderer's initialization code to persist.
 */
public class LeafiaGripOffsetHelper {
    private static final TransformType[] TRANSFORMS = TransformType.values();
    private static final int[] KEYS = {Keyboard.KEY_UP, Keyboard.KEY_LEFT, Keyboard.KEY_DOWN, Keyboard.KEY_RIGHT,
            Keyboard.KEY_I, Keyboard.KEY_J, Keyboard.KEY_K, Keyboard.KEY_L, Keyboard.KEY_SPACE,
            Keyboard.KEY_RSHIFT, Keyboard.KEY_RCONTROL, Keyboard.KEY_ADD, Keyboard.KEY_SUBTRACT};
    private static final int[] EDIT_ORDER = {0, 2, 1, 3, 4, 6, 5, 7};
    static boolean blockInput;
    public boolean debug;
    public Map<TransformType, LeafiaGripOffset> offsetMap;
    protected int debugIndex;

    public static LeafiaGripOffsetHelper fixGrip = new LeafiaGripOffsetHelper()
            .get(TransformType.GUI).setScale(1).setRotation(180, 0, 90).getHelper()
            .get(TransformType.FIRST_PERSON_RIGHT_HAND).setScale(1).setRotation(180, 0, 90).setPosition(0.5, 0.5, 0.5).getHelper()
            .get(TransformType.FIRST_PERSON_LEFT_HAND).setPosition(0, 0, 1.125).getHelper()
            .get(TransformType.THIRD_PERSON_RIGHT_HAND).setScale(1).getHelper();
    static final boolean fixNeeded = Loader.isModLoaded("vintagefix");

    public static void fixGrip(TransformType type) {
        if (fixNeeded) fixGrip.apply(type);
    }

    public static class LeafiaGripOffset {
        public double scale = 1;
        public Vec3d rotation = Vec3d.ZERO;
        public Vec3d position = Vec3d.ZERO;
        protected LeafiaGripOffsetHelper helper;

        public LeafiaGripOffset(LeafiaGripOffsetHelper helper) {
            this.helper = helper;
        }

        public LeafiaGripOffset setPosition(double x, double y, double z) {
            position = new Vec3d(x, y, z);
            return this;
        }

        public LeafiaGripOffset setRotation(double x, double y, double z) {
            rotation = new Vec3d(x, y, z);
            return this;
        }

        public LeafiaGripOffset setScale(double scale) {
            this.scale = scale;
            return this;
        }

        public LeafiaGripOffset copySettings(TransformType type) {
            var source = helper.get(type);
            scale = source.scale;
            position = source.position;
            rotation = source.rotation;
            return this;
        }

        public LeafiaGripOffsetHelper getHelper() {
            return helper;
        }
    }

    public LeafiaGripOffsetHelper() {
        offsetMap = new EnumMap<>(TransformType.class);
        for (var type : TRANSFORMS) {
            var offset = new LeafiaGripOffset(this);
            offset.scale = switch (type) {
                case NONE, FIRST_PERSON_LEFT_HAND, THIRD_PERSON_LEFT_HAND, FIXED -> 1;
                default -> 0.25;
            };
            offsetMap.put(type, offset);
        }
    }

    public LeafiaGripOffset get(TransformType transform) {
        return offsetMap.get(transform);
    }

    public void applyCustomOffset(LeafiaGripOffset offset) {
        GlStateManager.scale(offset.scale, offset.scale, offset.scale);
        GlStateManager.translate(-offset.position.x, offset.position.y, offset.position.z);
        GlStateManager.rotate((float) -offset.rotation.y, 0, 1, 0);
        GlStateManager.rotate((float) offset.rotation.x, 1, 0, 0);
        GlStateManager.rotate((float) -offset.rotation.z, 0, 0, 1);
    }

    protected void render(TransformType type) {
        applyCustomOffset(get(type));
    }

    public void apply(TransformType type) {
        GlStateManager.rotate(-90F, 0, 1, 0);
        var inherited = switch (type) {
            case FIRST_PERSON_LEFT_HAND -> TransformType.FIRST_PERSON_RIGHT_HAND;
            case THIRD_PERSON_LEFT_HAND -> TransformType.THIRD_PERSON_RIGHT_HAND;
            default -> null;
        };
        if (inherited != null) render(inherited);
        render(type);
        render(TransformType.NONE);
        if (debug) tickDebug();
    }

    protected void tickDebug() {
        var keys = 0;
        for (var index = 0; index < KEYS.length; index++) {
            if (Keyboard.isKeyDown(KEYS[index])) keys |= 1 << index;
        }
        if (keys == 0) blockInput = false;
        if (blockInput) return;
        blockInput = keys != 0;
        debugIndex = Math.floorMod(debugIndex + ((keys >>> 9) & 1) - ((keys >>> 10) & 1), TRANSFORMS.length);
        var view = TRANSFORMS[debugIndex];
        var offset = get(view);
        if ((keys & ((1 << 8) | (1 << 9) | (1 << 10))) != 0) printOffset(view, offset);
        var fine = Keyboard.isKeyDown(Keyboard.KEY_TAB);
        var distance = fine ? 0.05 : 0.25;
        var angle = fine ? 1D : 5D;
        var scaleStep = fine ? 0.01 : 0.05;
        if ((keys & (1 << 11)) != 0) offset.scale += scaleStep;
        if ((keys & (1 << 12)) != 0) offset.scale -= scaleStep;
        var control = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL);
        for (var key : EDIT_ORDER) {
            if ((keys & (1 << key)) == 0) continue;
            var rotation = key >= 4;
            var axis = switch (key) {
                case 1, 3, 4, 6 -> 0;
                default -> control ? 2 : 1;
            };
            var sign = switch (key) {
                case 1, 2, 6 -> -1;
                case 5 -> control ? 1 : -1;
                case 7 -> control ? -1 : 1;
                default -> 1;
            };
            var delta = (rotation ? angle : distance) * sign;
            var value = rotation ? offset.rotation : offset.position;
            value = value.add(axis == 0 ? delta : 0, axis == 1 ? delta : 0, axis == 2 ? delta : 0);
            if (rotation) offset.rotation = value;
            else offset.position = value;
        }
    }

    private static void printOffset(TransformType type, LeafiaGripOffset offset) {
        var client = Minecraft.getMinecraft();
        var player = client.player;
        client.ingameGUI.getChatGUI().clearChatMessages(false);
        player.sendMessage(new TextComponentString("-- Current Grip --")
                .setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE)));
        player.sendMessage(new TextComponentString(String.format(" Scale: %01.2f", offset.scale)));
        player.sendMessage(new TextComponentString(String.format(" Position: %01.2f, %01.2f, %01.2f",
                offset.position.x, offset.position.y, offset.position.z)));
        player.sendMessage(new TextComponentString(String.format(" Rotation: %01.0f, %01.0f, %01.0f",
                offset.rotation.x, offset.rotation.y, offset.rotation.z)));
        player.sendMessage(new TextComponentString(" Type: " + (type == TransformType.NONE ? "CUSTOM" : type.name()))
                .setStyle(new Style().setColor(TextFormatting.GRAY)));
    }
}
