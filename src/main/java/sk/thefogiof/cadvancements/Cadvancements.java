package sk.thefogiof.cadvancements;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.commands.AdvancementCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.thefogiof.cadvancements.compat.BetterDisplayRegistry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Cadvancements implements ModInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger("CAdvancements");

    public static Path ADVANCEMENTS_DIR;

    @Override
    public void onInitialize() {
        ADVANCEMENTS_DIR = FabricLoader.getInstance().getConfigDir().resolve("cadvancments").resolve("advancements");
        try {
            Files.createDirectories(ADVANCEMENTS_DIR);
        } catch (IOException e) {
            LOGGER.error("Failed to create {} {}", ADVANCEMENTS_DIR, e);
        }
        LOGGER.info("Custom Advancements: {} | BA loaded = {}", ADVANCEMENTS_DIR, FabricLoader.getInstance().isModLoaded("betteradvancements"));

        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
            if (success) BetterDisplayRegistry.reset();
        });
    }

    public static Logger getLogger() {
        return LOGGER;
    }
}
