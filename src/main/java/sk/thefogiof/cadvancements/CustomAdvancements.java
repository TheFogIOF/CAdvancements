package sk.thefogiof.cadvancements;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.thefogiof.cadvancements.compat.BetterDisplayRegistry;
import sk.thefogiof.cadvancements.config.CustomAdvancementsConfig;

import java.nio.file.Path;

public class CustomAdvancements implements ModInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger("CAdvancements");

    public static Path ADVANCEMENTS_DIR;

    @Override
    public void onInitialize() {
        ADVANCEMENTS_DIR = CustomAdvancementsConfig.register();

        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
            if (success) BetterDisplayRegistry.reset();
        });
    }

    public static Logger getLogger() {
        return LOGGER;
    }
}