package sk.thefogiof.cadv;

import sk.thefogiof.cadv.platform.Services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CommonMod {
    public static Path ADV_DIR;

    public static void init() {
        ADV_DIR = Services.PLATFORM.getConfigDir().resolve("c_advancements").resolve("advancements");
        try {
            Files.createDirectories(ADV_DIR);
        } catch (IOException e) {
            Constants.LOG.error("Failed to create {}", ADV_DIR, e);
        }
    }
}