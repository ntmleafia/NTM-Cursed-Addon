package com.leafia.contents.worldgen;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hbm.blocks.BlockDummyable;
import com.hbm.lib.ForgeDirection;
import com.hbm.main.MainRegistry;
import com.leafia.contents.building.generic.lined_asphalt.LinedAsphaltBlock;
import com.leafia.contents.building.generic.lined_asphalt.LinedAsphaltBlock.AsphaltLine;
import com.leafia.contents.gear.wands.ItemWandSaving.SavingProperty;
import com.leafia.dev.optimization.bitbyte.LeafiaBuf;
import com.llib.LeafiaLib;
import com.llib.exceptions.LeafiaDevFlaw;
import com.llib.group.LeafiaMap;
import com.llib.technical.FifthString;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import java.io.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class NTMStructBuffer {
    public static final MethodHandle fillSpaceHandle;

    static {
        try {
            Method reflected = BlockDummyable.class.getDeclaredMethod("fillSpace",World.class,int.class,int.class,int.class,ForgeDirection.class,int.class);
            reflected.setAccessible(true);
            fillSpaceHandle = MethodHandles.lookup().unreflect(reflected);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new LeafiaDevFlaw(e);
        }
    }

	public enum NTMStructVersion {
		V0_FIRST_TEST,
		V1_REPLACEMENT_MAP_ADDITION,
		V2_BETTER_TE_COMPRESSION,
		V3_METADATA_FIX,
		;
		public boolean isUpOrNewerThan(NTMStructVersion other) { return this.ordinal() >= other.ordinal(); }
		public boolean isNewerThan(NTMStructVersion other) { return this.ordinal() > other.ordinal(); }
		public boolean isOlderThan(NTMStructVersion other) { return this.ordinal() < other.ordinal(); }
		public boolean isUpOrOlderThan(NTMStructVersion other) { return this.ordinal() <= other.ordinal(); }
		public static final NTMStructVersion latest = V3_METADATA_FIX;
	}
	public enum NTMStructAngle {
		ORIGINAL(1,1,0,0),RIGHT(0,0,-1,1),BACK(-1,-1,0,0),LEFT(0,0,1,-1);
		final int xx; final int zz; final int xz; final int zx;
		NTMStructAngle(int xx,int zz,int xz,int zx) {
			this.xx = xx;
			this.zz = zz;
			this.xz = xz;
			this.zx = zx;
		}
		public NTMStructAngle getRight() { return NTMStructAngle.values()[Math.floorMod(this.ordinal()+1,4)]; }
		public NTMStructAngle getLeft() { return NTMStructAngle.values()[Math.floorMod(this.ordinal()-1,4)]; }
		public int getX(int x,int z) { return x*xx+z*xz; }
		public int getZ(int x,int z) { return z*zz+x*zx; }
	}
	public static class StructData {
		LeafiaBuf buf;
		String name;
		boolean terrarinAware = false;
		public StructData() {}
		public String getName() { return name; }
	}

	public static class StructLoader {
		private static final String STRUCT_ROOT = "assets/leafia/structs";
		public static final JsonParser parser = new JsonParser();
		public static final Map<String,StructData> structs = new HashMap<>();
		static void addStructMeta(String name,JsonObject object) {
			StructData data = structs.computeIfAbsent(name, _ -> new StructData());
			if (object.has("name"))
				data.name = object.get("name").getAsString();
			if (object.has("terrarinAware"))
				data.terrarinAware = object.get("terrarinAware").getAsBoolean();
            MainRegistry.logger.info("[NTMSTRUCT META] Loaded structure meta {}", name);
		}

		public static void init() {
			structs.clear();
			loadBuiltinStructs(resolveStructMod());
		}

		private static ModContainer resolveStructMod() {
			ModContainer mod = Loader.instance().getIndexedModList().get("leafia");
			if (mod != null)
				return mod;
			throw new LeafiaDevFlaw("Unable to resolve mod container for leafia structures");
		}

		private static void loadBuiltinStructs(ModContainer mod) {
			boolean success = CraftingHelper.findFiles(mod, STRUCT_ROOT, null, (root, file) -> {
				if (!Files.isRegularFile(file))
					return true;

				String relative = root.relativize(file).toString().replace('\\', '/');
				try {
					if (relative.endsWith(".ntmstruct")) {
						loadStruct(relative, file);
					} else if (relative.endsWith(".json")) {
						loadStructMeta(relative, file);
					}
				} catch (Exception ex) {
                    MainRegistry.logger.error("[NTMSTRUCT] Error loading {}", relative, ex);
					return false;
				}
				return true;
			}, false, true);
			if (!success)
				throw new LeafiaDevFlaw("Failed to load structures from " + STRUCT_ROOT);
		}

		private static void loadStruct(String relative, Path file) throws IOException {
			byte[] bytes = Files.readAllBytes(file);
			LeafiaBuf buf = new LeafiaBuf(null);
			buf.bytes = bytes;
			buf.writerIndex = bytes.length * 8;
			String key = relative.substring(0,relative.length()-10);
			StructData data = structs.computeIfAbsent(key, _ -> new StructData());
			data.buf = buf;
			if (data.name == null)
				data.name = relative;
            MainRegistry.logger.info("[NTMSTRUCT] Loaded structure {}", relative);
		}

		private static void loadStructMeta(String relative, Path file) throws IOException {
			try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
				JsonObject obj = (JsonObject) parser.parse(reader);
				addStructMeta(relative.substring(0,relative.length()-5),obj);
			}
		}
	}
	public final LeafiaBuf buf;
	public final int bitNeedle;
	public final Block[] paletteBlock;
	public final NBTTagCompound[] paletteTEs;
	public final Vec3i size;
	public final Vec3i offset;
	public final EnumFacing originalFace;
	public final NTMStructVersion version;
	public NTMStructAngle rotation = NTMStructAngle.ORIGINAL;
	public static NTMStructBuffer fromFiles(String path) {
		LeafiaBuf buffer = new LeafiaBuf(null);
		try {
			buffer.bytes = Files.readAllBytes(Paths.get(path));
			buffer.writerIndex = buffer.bytes.length*8;
		} catch (IOException exception) {
			LeafiaDevFlaw flaw = new LeafiaDevFlaw("Exception while tryina read "+path+" as .ntmstruct");
			flaw.setStackTrace(exception.getStackTrace());
			throw flaw;
		}
		return new NTMStructBuffer(buffer);
	}
	public static NTMStructBuffer fromMetadata(StructData data) {
		LeafiaBuf buffer = new LeafiaBuf(null);
		buffer.bytes = data.buf.bytes;
		buffer.writerIndex = data.buf.writerIndex;
		NTMStructBuffer struct = new NTMStructBuffer(buffer);
		struct.terrarinAware = data.terrarinAware;
		return struct;
	}
	public NTMStructBuffer(LeafiaBuf buf) {
		super();
		//System.out.println("Data size: "+buf.readableBits());
		version = NTMStructVersion.values()[buf.readUnsignedByte()];
		offset = buf.readVec3i();
		size = buf.readVec3i();
		originalFace = EnumFacing.byHorizontalIndex(buf.readByte());
		paletteBlock = new Block[buf.readUnsignedShort()];
		for (int i = 0; i < paletteBlock.length; i++) {
			boolean hasNext = true;
			while (hasNext) {
				FifthString fifth = buf.readFifthString();
				if (paletteBlock[i] == null) {
					String rid = LeafiaLib.stringSwap(fifth.toString(),' ',':');
					paletteBlock[i] = Block.getBlockFromName(rid);
					if (paletteBlock[i] == null && rid.startsWith("hbm:")) {
						rid = "leafia:"+rid.substring("hbm:".length());
						paletteBlock[i] = Block.getBlockFromName(rid);
					}
				}
				hasNext = buf.extract(1) > 0;
			}
		}
		if (version.isOlderThan(NTMStructVersion.V2_BETTER_TE_COMPRESSION)) {
			paletteTEs = new NBTTagCompound[buf.readUnsignedShort()];
			for (int i = 0; i < paletteTEs.length; i++) {
				byte[] bytes = new byte[buf.readInt()];
				buf.readBytes(bytes);
				ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
				try {
					paletteTEs[i] = CompressedStreamTools.readCompressed(stream);
				} catch (IOException ignored) {}
			}
		} else {
			byte[] bytes = new byte[buf.readInt()];
			if (bytes.length > 0) {
				buf.readBytes(bytes);
				ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
				try {
					NBTTagCompound attr = CompressedStreamTools.readCompressed(stream);
					NBTTagList entities = attr.getTagList("te",10);
					paletteTEs = new NBTTagCompound[entities.tagCount()];
					for (int i = 0; i < paletteTEs.length; i++)
						paletteTEs[i] = entities.getCompoundTagAt(i);
				} catch (IOException fatal) {
					LeafiaDevFlaw flaw = new LeafiaDevFlaw("Error trying to decode NBTStruct V2+ attributes");
					flaw.setStackTrace(fatal.getStackTrace());
					throw flaw;
				}
			} else {
				paletteTEs = new NBTTagCompound[0];
			}
		}
		this.buf = buf;
		bitNeedle = buf.readerIndex;
	}
	public NTMStructBuffer rotateToFace(EnumFacing face) {
		rotation = NTMStructAngle.values()[Math.floorMod(face.getHorizontalIndex()-originalFace.getHorizontalIndex(),4)];
		return this;
	}
	public boolean terrarinAware = false; // name idea: movblock
	public void build(World world,BlockPos origin) {
		buf.readerIndex = bitNeedle;
		BlockPos start = origin.add(rotation.getX(offset.getX(),offset.getZ()),offset.getY(),rotation.getZ(offset.getX(),offset.getZ()));
		int sx = size.getX()+1;
		int sy = size.getY()+1;
		int sz = size.getZ()+1;
		Map<BlockPos,NBTTagCompound> tebuffer = new LeafiaMap<>();
		SavingProperty property = null;
		int repeats = 0;
		for (int i = 0; i < sx*sy*sz; i++) {
			int x = Math.floorMod(i,sx);
			int y = Math.floorMod(i/sx,sy);
			int z = Math.floorMod(i/sx/sy,sz);
			BlockPos pos = start.add(rotation.getX(x,z),y,rotation.getZ(x,z));
			if (terrarinAware)
				pos = world.getHeight(pos).up(offset.getY()+y);
			if (repeats <= 0) {
				property = new SavingProperty();
				int value = buf.readUnsignedShort();
				int modifier = value>>>13&0b11;
				repeats = value&(1<<13)-1;
				if (modifier == 0b01)
					property.ignore = true;
				else {
					if (modifier == 0b10) property.replaceAirOnly = true;
					Block block = paletteBlock[buf.readUnsignedShort()];
					int meta = -1;
					if ((value>>>15&1) > 0)
						meta = version.isUpOrNewerThan(NTMStructVersion.V3_METADATA_FIX) ? buf.extract(4) : buf.readInt();
					if (block == null) {
						block = Blocks.AIR;
						property.state = block.getDefaultState();
					} else {
						if (meta != -1)
							property.state = block.getStateFromMeta(meta);
						else
							property.state = block.getDefaultState();
					}
					if (modifier == 0b11)
						property.entity = buf.readUnsignedShort();
					for (IProperty<?> key : property.state.getPropertyKeys()) {
						if (key instanceof PropertyDirection) {
							PropertyDirection cast = (PropertyDirection)key;
							EnumFacing facing = property.state.getValue(cast);
							if (facing.getYOffset() == 0 && rotation != NTMStructAngle.ORIGINAL) {
								switch(rotation) {
									case RIGHT: property.state = property.state.withProperty(cast,facing.rotateY()); break;
									case BACK: property.state = property.state.withProperty(cast,facing.getOpposite()); break;
									case LEFT: property.state = property.state.withProperty(cast,facing.getOpposite().rotateY()); break;
								}
							}
						}
					}
				}
			}
			if (!property.ignore) {
				Block block = property.state.getBlock();
				if (block instanceof LinedAsphaltBlock) {
					AsphaltLine line = new AsphaltLine(block);
					line = switch(rotation) {
						case RIGHT -> line.rotate();
						case BACK -> line.rotate().rotate();
						case LEFT -> line.rotate().rotate().rotate();
						default -> line;
					};
					Block newBlock = AsphaltLine.getBlock(line.toString());
					if (newBlock != null)
						world.setBlockState(pos,newBlock.getDefaultState(),0b00010);
				} else if (block instanceof BlockDummyable dummyable) {
					int meta = property.state.getValue(BlockDummyable.META);
					if (meta >= 12) {
						EnumFacing dir = ForgeDirection.getOrientation(meta-BlockDummyable.offset).toEnumFacing();
						dir = switch(rotation) {
							case RIGHT -> dir.rotateY();
							case BACK -> dir.getOpposite();
							case LEFT -> dir.getOpposite().rotateY();
							default -> dir;
						};
						world.setBlockState(pos,dummyable.getStateFromMeta(ForgeDirection.getOrientation(dir).ordinal()+BlockDummyable.offset),0b00010);
						try {
							// thanks movblock
							BlockPos pos1 = pos.offset(dir,-dummyable.getOffset());
							fillSpaceHandle.invokeExact((BlockDummyable) dummyable,(World) world,pos1.getX(),pos1.getY(),pos1.getZ(),ForgeDirection.getOrientation(dir),dummyable.getOffset());
						} catch (Throwable e) {
							throw new LeafiaDevFlaw(e);
						}
					}
				} else
					world.setBlockState(pos,property.state,0b00010);
				if (property.entity != null)
					tebuffer.put(pos,paletteTEs[property.entity]);
			}
			repeats--;
		}
		for (Entry<BlockPos,NBTTagCompound> entry : tebuffer.entrySet()) {
			TileEntity te = world.getTileEntity(entry.getKey());
			if (te != null) {
				BlockPos pos = te.getPos();
				te.deserializeNBT(entry.getValue());
				te.setPos(pos);
			} //else
				//throw new LeafiaDevFlaw("Tile entity could not be created");
		}
	}
}
