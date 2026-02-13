package com.oldviking.beacon_overhaul_revived;

import com.oldviking.beacon_overhaul_revived.effect.BeaconEffects;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeaconOverhaulRevived implements ModInitializer {
	public static final String MOD_ID = "beacon-overhaul-revived";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
        BeaconEffects.registerBeaconEffects();
	}
}