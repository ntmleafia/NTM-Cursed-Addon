package com.leafia.init;

import com.hbm.inventory.material.NTMMaterial;

import static com.hbm.inventory.OreDictManager.*;
import static com.leafia.init.AddonOreDict.*;
import static com.hbm.inventory.material.MaterialShapes.*;
import static com.hbm.inventory.material.Mats.*;

public class AddonMats {
	public static final int _LF = 21121; // 32767 seems to be the limit
	public static final NTMMaterial MAT_XANAXIUM = makeSmeltable(
			_LF,XN,0xcff3ab,0x8375a7,0x97b7ac
	).setAutogen(CASTPLATE,WELDEDPLATE).m();
	public static final NTMMaterial MAT_CORIUM = makeSmeltable(
			_LF+1,CORIUM,0x272f35,0x161616,0x1d2123
	).n();
	public static final NTMMaterial MAT_TAINTIUM = makeSmeltable(
			_LF+2,TN,0x616dba,0x463d49,0x586294
	).setAutogen(CASTPLATE,WELDEDPLATE).m();
	public static final NTMMaterial MAT_NC279 = makeSmeltable(
			_LF+3,NC279,0xbfbfc7,0x514967,0x72758d
	).m();
	public static final NTMMaterial MAT_MYSTICITE = makeNonSmeltable(
			_LF+4,MYSTICITE,0xfdfdfd,0xbbbbbb,0xfdfdfd
	).setAutogen(LIGHTRECEIVER).m();
	public static final NTMMaterial MAT_FISSITE = makeSmeltable(
			_LF+5,FSALLOY,0xd9d4dc,0x615468,0xb5abbb
	).setAutogen(LIGHTRECEIVER).m();
	public static final NTMMaterial MAT_FISSIUM = makeSmeltable(
			_LF+6,FS,0xfbfbfc,0x777a90,0xd4d4dc
	).m();
	public static final NTMMaterial MAT_MANA = makeSmeltable(
			_LF+7,MANA,0xf58cff,0xa039a9,0xf58cff
	).n();
	public static final NTMMaterial MAT_TNALLOY = makeSmeltable(
			_LF+8,TNALLOY,0xbfc3d1,0x5d4b83,0xa6aec0
	).setAutogen(CASTPLATE,WELDEDPLATE).m();
	static {
		MAT_BSCCO.setAutogen(WIRE); // add wire
	}
}