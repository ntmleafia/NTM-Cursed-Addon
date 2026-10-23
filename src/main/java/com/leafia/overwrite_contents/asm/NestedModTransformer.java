// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public final class NestedModTransformer implements IClassTransformer {
    private static final String DISCOVERER = "net.minecraftforge.fml.common.discovery.ModDiscoverer";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (!DISCOVERER.equals(transformedName)) return basicClass;

        var reader = new ClassReader(basicClass);
        var node = new ClassNode();
        reader.accept(node, 0);
        var returns = 0;
        for (var method : node.methods) {
            if (!method.name.equals("identifyMods") || !method.desc.equals("()Ljava/util/List;")) continue;
            for (var instruction : method.instructions.toArray()) {
                if (instruction.getOpcode() != Opcodes.ARETURN) continue;
                var hook = new InsnList();
                hook.add(new VarInsnNode(Opcodes.ALOAD, 0));
                hook.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, DISCOVERER.replace('.', '/'),
                        "getASMTable", "()Lnet/minecraftforge/fml/common/discovery/ASMDataTable;", false));
                hook.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                        "com/leafia/overwrite_contents/asm/NestedModLoader", "discover",
                        "(Ljava/util/List;Lnet/minecraftforge/fml/common/discovery/ASMDataTable;)Ljava/util/List;", false));
                method.instructions.insertBefore(instruction, hook);
                returns++;
            }
        }
        if (returns == 0) throw new IllegalStateException("Forge ModDiscoverer.identifyMods hook is missing");
        var writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);
        node.accept(writer);
        return writer.toByteArray();
    }
}
