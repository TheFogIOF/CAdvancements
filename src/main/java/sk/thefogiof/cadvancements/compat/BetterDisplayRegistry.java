package sk.thefogiof.cadvancements.compat;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.Identifier;
import sk.thefogiof.cadvancements.CustomAdvancements;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

public final class BetterDisplayRegistry {
    private static final Map<Identifier, JsonObject> DATA = new HashMap<>();
    private static boolean loaded = false;

    private BetterDisplayRegistry() {}

    public static synchronized void reset() {
        DATA.clear();
        loaded = false;
        CustomAdvancements.getLogger().info("[CADV] better_display cache reset");
    }

    public static synchronized JsonObject get(Identifier id) {
        if (!loaded) load();
        return DATA.get(id);
    }

    private static void load() {
        loaded = true;
        Path root = CustomAdvancements.ADVANCEMENTS_DIR;
        if (!Files.isDirectory(root)) return;

        try (Stream<Path> stream = Files.walk(root)) {
            stream.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".json"))
                    .forEach(BetterDisplayRegistry::readOne);
        } catch (Exception e) {
            CustomAdvancements.getLogger().error("Cant scan better_display {}", String.valueOf(e));
        }
    }

    private static void readOne(Path file) {
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
            if (!obj.has("better_display") || !obj.get("better_display").isJsonObject()) return;

            Path relativePath = CustomAdvancements.ADVANCEMENTS_DIR.relativize(file);
            if (relativePath.getNameCount() < 2) return;

            StringBuilder path = new StringBuilder();
            for (int i = 1; i < relativePath.getNameCount(); i++) {
                if (!path.isEmpty()) path.append('/');
                String seg = relativePath.getName(i).toString();
                if (i == relativePath.getNameCount() - 1) {
                    seg = seg.substring(0, seg.lastIndexOf('.'));
                }
                path.append(seg);
            }
            Identifier id = Identifier.fromNamespaceAndPath(relativePath.getName(0).toString(), path.toString());
            DATA.put(id, obj.getAsJsonObject("better_display"));
            //CustomAdvancements.getLogger().info("[CADV] better_display loaded for " + id);
        } catch (Exception e) {
            CustomAdvancements.getLogger().error("Read error {}: {}", file, e.getMessage());
        }
    }
}