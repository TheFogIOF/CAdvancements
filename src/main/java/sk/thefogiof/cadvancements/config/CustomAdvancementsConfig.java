package sk.thefogiof.cadvancements.config;

import net.fabricmc.loader.api.FabricLoader;
import sk.thefogiof.cadvancements.CustomAdvancements;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CustomAdvancementsConfig {
    public static Path register() {
        Path ADVANCEMENTS_DIR = FabricLoader.getInstance().getConfigDir().resolve("cadvancments").resolve("advancements");
        try {
            Files.createDirectories(ADVANCEMENTS_DIR);
        } catch (IOException e) {
            CustomAdvancements.getLogger().error("Failed to create {} {}", ADVANCEMENTS_DIR, e);
        }
        CustomAdvancements.getLogger().info("Custom Advancements: {} | BA loaded = {}", ADVANCEMENTS_DIR, FabricLoader.getInstance().isModLoaded("betteradvancements"));
        return ADVANCEMENTS_DIR;
    }
}