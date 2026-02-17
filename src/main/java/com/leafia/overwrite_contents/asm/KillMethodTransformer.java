package com.leafia.overwrite_contents.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.util.*;

//mlbv: this class assumes that all methods scheduled for removal are not obfuscated. Use ObfSafeName if you ever need to touch obfuscated methods.
public final class KillMethodTransformer implements IClassTransformer {
	private final Map<String, Set<MethodSig>> bySig = new HashMap<>();
	private final Map<String, Set<String>> byName = new HashMap<>();
	private boolean init = false;

	private static String toInternal(String s) {
		return s.indexOf('/') >= 0 ? s : s.replace('.', '/');
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (basicClass == null) return null;
		if (!init) {
			buildTargets();
			init = true;
		}

		String owner = toInternal(transformedName != null ? transformedName : name);
		if (!bySig.containsKey(owner) && !byName.containsKey(owner)) return basicClass;

		ClassNode cn = new ClassNode();
		ClassReader cr = new ClassReader(basicClass);
		cr.accept(cn, 0);
		Set<MethodSig> sigs = bySig.getOrDefault(owner, Collections.emptySet());
		Set<String> names = byName.getOrDefault(owner, Collections.emptySet());
		boolean changed = cn.methods.removeIf(m -> names.contains(m.name) || sigs.contains(MethodSig.of(m.name, m.desc)));
		if (!changed) return basicClass;
		ClassWriter cw = new ClassWriter(cr, 0);
		cn.accept(cw);
		return cw.toByteArray();
	}

	private void buildTargets() {
		//killSig("com.hbm.tileentity.machine.TileEntityCoreEmitter",
		//		MethodSig.of("setInput", "(Lli/cil/oc/api/machine/Context;Lli/cil/oc/api/machine/Arguments;)[Ljava/lang/Object;")
		//);
		killNames("com.hbm.tileentity.machine.TileEntityCoreEmitter",
				"getEnergyInfo","getCryogel","getInput","getInfo","isActive","setInput"
		);
		killNames("com.hbm.tileentity.machine.TileEntityCoreReceiver",
				"getEnergyInfo","getCryogel","getInfo"
		);
		killNames("com.hbm.tileentity.machine.TileEntityCoreStabilizer",
				"getEnergyInfo","getInput","getDurability","getInfo","setInput"
		);
		killNames("com.hbm.tileentity.machine.TileEntityReactorZirnox",
				"setActive","isActive"
		);
	}

	private void killSig(String owner, MethodSig... sigs) {
		String o = toInternal(owner);
		bySig.computeIfAbsent(o, k -> new HashSet<>()).addAll(Arrays.asList(sigs));
	}

	private void killNames(String owner, String... names) {
		String o = toInternal(owner);
		byName.computeIfAbsent(o, k -> new HashSet<>()).addAll(Arrays.asList(names));
	}

	public static final class MethodSig {
		public final String name, desc;

		private MethodSig(String name, String desc) {
			this.name = name;
			this.desc = desc;
		}

		public static MethodSig of(String n, String d) {
			return new MethodSig(n, d);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (!(obj instanceof MethodSig sig)) return false;
			return Objects.equals(name, sig.name) && Objects.equals(desc, sig.desc);
		}

		@Override
		public int hashCode() {
			int result = 1;
			result = 31 * result + name.hashCode();
			result = 31 * result + desc.hashCode();
			return result;
		}

		@Override
		public String toString() {
			return name + desc;
		}
	}
}
