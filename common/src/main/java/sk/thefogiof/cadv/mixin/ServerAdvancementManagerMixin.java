package sk.thefogiof.cadv.mixin;

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
import sk.thefogiof.cadv.Constants;
import sk.thefogiof.cadv.compat.BetterDisplayRegistry;
import sk.thefogiof.cadv.UserAdvancementLoader;

import java.util.*;

@Mixin(ServerAdvancementManager.class)
public abstract class ServerAdvancementManagerMixin {
    @Final @Mutable @Shadow private Map<Identifier, AdvancementHolder> advancements;
    @Final @Mutable @Shadow private AdvancementTree tree;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cadv$inject(HolderLookup.Provider registries, CallbackInfo ci) {
        BetterDisplayRegistry.reset();

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

        Constants.LOG.info("Added {} user advancements.", userAdvancements.size());
    }
}