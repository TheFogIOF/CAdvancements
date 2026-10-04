package sk.thefogiof.cadvancements.mixin;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.server.ServerAdvancementManager;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sk.thefogiof.cadvancements.CustomAdvancements;
import sk.thefogiof.cadvancements.load.UserAdvancementLoader;

import java.util.*;

@Mixin(ServerAdvancementManager.class)
public class ServerAdvancementManagerMixin {
    @Final @Mutable @Shadow private Map<Identifier, AdvancementHolder> advancements;
    @Final @Mutable @Shadow private AdvancementTree tree;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cadv$inject(HolderLookup.Provider registries, CallbackInfo ci) {
        Map<Identifier, Advancement> userAdvancements = UserAdvancementLoader.loadAll(registries);
        if (userAdvancements.isEmpty()) return;

        Map<Identifier, AdvancementHolder> merged = new HashMap<>(this.advancements);
        for (Map.Entry<Identifier, Advancement> entry : userAdvancements.entrySet()) {
            merged.put(entry.getKey(), new AdvancementHolder(entry.getKey(), entry.getValue()));
        }
        this.advancements = merged;

        AdvancementTree newTree = new AdvancementTree();
        newTree.addAll(this.advancements.values());
        newTree.repositionNodes();
        this.tree = newTree;

        CustomAdvancements.getLogger().info("Added {} user advancements.", userAdvancements.size());
    }
}
