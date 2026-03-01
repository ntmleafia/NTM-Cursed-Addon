package com.leafia.init;

import com.hbm.inventory.material.NTMMaterial;

import static com.hbm.inventory.OreDictManager.*;
import static com.leafia.init.AddonOreDict.*;
import static com.hbm.inventory.material.MaterialShapes.*;
import static com.hbm.inventory.material.Mats.*;

public class AddonMats {
	public static final int _LF = 21121; // 32767 seems to be the limit
	public static final NTMMaterial MAT_XANAXIUM = makeSmeltable(_LF,XN,0xcff3ab,0x8375a7,0x97b7ac).setAutogen(CASTPLATE,WELDEDPLATE).m();
}