package sk.thefogiof.cadvancements.load;

import sk.thefogiof.cadvancements.Cadvancements;
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

        Path root = Cadvancements.ADVANCEMENTS_DIR;
        if (!Files.isDirectory(root)) return result;

        try (Stream<Path> stream = Files.walk(root)) {
            stream.filter(Files::isRegularFile)
                    .filter(UserAdvancementLoader::isSupported)
                    .forEach(file -> {
                        try {
                            loadSingle(file, registries, result);
                        } catch (Exception e) {
                            Cadvancements.getLogger().error("Load error {}: {}", file, e.getMessage());
                        }
                    });
        } catch (IOException e) {
            Cadvancements.getLogger().error("Cant scan folder {}", String.valueOf(e));
        }
        return result;
    }

    private static boolean isSupported(Path p) {
        String n = p.getFileName().toString().toLowerCase(Locale.ROOT);
        return EXTENSIONS.stream().anyMatch(n::endsWith);
    }

    private static void loadSingle(Path file, HolderLookup.Provider registries, Map<Identifier, Advancement> target) throws IOException {
        Path rel = Cadvancements.ADVANCEMENTS_DIR.relativize(file);
        if (rel.getNameCount() < 2) {
            Cadvancements.getLogger().warn("File in root without namespace: " + rel);
            return;
        }

        String ns = rel.getName(0).toString();
        if (!Identifier.isValidNamespace(ns)) {
            Cadvancements.getLogger().warn("Invalid namespace '" + ns + "': " + rel);
            return;
        }

        StringBuilder path = new StringBuilder();
        for (int i = 1; i < rel.getNameCount(); i++) {
            if (path.length() > 0) path.append('/');
            String seg = rel.getName(i).toString();
            if (i == rel.getNameCount() - 1) {
                seg = stripExt(seg);
            }
            path.append(seg);
        }

        Identifier id = Identifier.fromNamespaceAndPath(ns, path.toString());
        String raw = Files.readString(file, StandardCharsets.UTF_8);

        JsonElement parsed;
        try { parsed = JsonParser.parseString(raw); }
        catch (Exception e) {
            Cadvancements.getLogger().error("Invalid JSON in {}: {}", file, e.getMessage());
            return;
        }
        if (!parsed.isJsonObject()) {
            Cadvancements.getLogger().warn("{} - not JSON-Object", file);
            return;
        }

        JsonObject obj = parsed.getAsJsonObject();

        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, registries);
        Advancement advancement = Advancement.CODEC
                .parse(ops, obj)
                .getOrThrow(msg -> new IOException("CODEC error: " + msg));

        target.put(id, advancement);
        //Cadvancements.getLogger().info("Loaded: " + id);
    }

    private static String stripExt(String s) {
        int dot = s.lastIndexOf('.');
        return dot > 0 ? s.substring(0, dot) : s;
    }
}