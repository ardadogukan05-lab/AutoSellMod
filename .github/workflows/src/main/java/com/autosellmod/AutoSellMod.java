package com.autosellmod;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoSellMod implements ModInitializer {
    public static final String MOD_ID = "autosellmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("AutoSell Mod yuklendi! (Fabric 1.21.1)");
    }
}
