package com.leafia.contents.cannery;

import com.hbm.inventory.RecipesCommon;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.AddonBlocks.LFTR;
import com.leafia.contents.AddonBlocks.PWR;
import com.leafia.contents.AddonBlocks.TestBlocks;
import com.leafia.contents.cannery.cannery.CanneryLFTR;
import com.leafia.contents.cannery.cannery.CanneryPWR;
import com.leafia.contents.cannery.cannery.CanneryTest;

import static com.hbm.wiaj.cannery.Jars.canneries;

public class AddonJars {
	public static void initJars() {
		canneries.put(new ComparableStack(TestBlocks.ffsource),new CanneryTest());
		CanneryPWR pwr = new CanneryPWR();
		canneries.put(new ComparableStack(PWR.element),pwr);
		canneries.put(new ComparableStack(PWR.element_old),pwr);
		canneries.put(new ComparableStack(PWR.element_old_blank),pwr);
		canneries.put(new ComparableStack(PWR.channel),pwr);
		canneries.put(new ComparableStack(PWR.control),pwr);
		canneries.put(new ComparableStack(PWR.reactor_control),pwr);
		CanneryLFTR lftr = new CanneryLFTR();
		canneries.put(new ComparableStack(LFTR.element),lftr);
		canneries.put(new ComparableStack(LFTR.control),lftr);
		canneries.put(new ComparableStack(LFTR.plug),lftr);
		canneries.put(new ComparableStack(LFTR.arbitrary),lftr);
	}
}
