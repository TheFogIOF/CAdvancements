package sk.thefogiof.cadvancements.load;

import sk.thefogiof.cadvancements.CustomAdvancements;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public final class UserAdvancementLoader {

    private static final Set<String> EXTENSIONS = Set.of(".json", ".cadv");

    public static Map<Identifier, Advancement> loadAll(HolderLookup.Provider registries) {
        Map<Identifier, Advancement> result = new HashMap<>();

        Path root = CustomAdvancements.ADVANCEMENTS_DIR;
        if (!Files.isDirectory(root)) return result;

        try (Stream<Path> stream = Files.walk(root)) {
            stream.filter(Files::isRegularFile)
                    .filter(UserAdvancementLoader::isSupported)
                    .forEach(file -> {
                        try {
                            loadSingle(file, registries, result);
                        } catch (Exception e) {
                            CustomAdvancements.getLogger().error("Load error {}: {}", file, e.getMessage());
                        }
                    });
        } catch (IOException e) {
            CustomAdvancements.getLogger().error("Cant scan folder {}", String.valueOf(e));
        }
        return result;
    }

    private static boolean isSupported(Path path) {
        String name = path.getFileName().toString().toLowerCase(Locale.ROOT);
        return EXTENSIONS.stream().anyMatch(name::endsWith);
    }

    private static void loadSingle(Path file, HolderLookup.Provider registries, Map<Identifier, Advancement> target) throws IOException {
        Path relativePath = CustomAdvancements.ADVANCEMENTS_DIR.relativize(file);
        if (relativePath.getNameCount() < 2) {
            CustomAdvancements.getLogger().warn("File in root without namespace: {}", relativePath);
            return;
        }

        String namespace = relativePath.getName(0).toString();
        if (!Identifier.isValidNamespace(namespace)) {
            CustomAdvancements.getLogger().warn("Invalid namespace '{}': {}", namespace, relativePath);
            return;
        }

        StringBuilder path = new StringBuilder();
        for (int i = 1; i < relativePath.getNameCount(); i++) {
            if (!path.isEmpty()) path.append('/');
            String seg = relativePath.getName(i).toString();
            if (i == relativePath.getNameCount() - 1) {
                seg = stripExt(seg);
            }
            path.append(seg);
        }

        Identifier id = Identifier.fromNamespaceAndPath(namespace, path.toString());
        String raw = Files.readString(file, StandardCharsets.UTF_8);

        JsonElement parsed;
        try { parsed = JsonParser.parseString(raw); }
        catch (Exception e) {
            CustomAdvancements.getLogger().error("Invalid JSON in {}: {}", file, e.getMessage());
            return;
        }
        if (!parsed.isJsonObject()) {
            CustomAdvancements.getLogger().warn("{} - not JSON-Object", file);
            return;
        }

        JsonObject obj = parsed.getAsJsonObject();

        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, registries);
        Advancement advancement = Advancement.CODEC
                .parse(ops, obj)
                .getOrThrow(msg -> new IOException("CODEC error: " + msg));

        target.put(id, advancement);
        //CustomAdvancements.getLogger().info("Loaded: " + id);
    }

    private static String stripExt(String s) {
        int dot = s.lastIndexOf('.');
        return dot > 0 ? s.substring(0, dot) : s;
    }
}