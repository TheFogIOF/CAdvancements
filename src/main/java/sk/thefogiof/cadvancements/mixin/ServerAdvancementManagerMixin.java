package sk.thefogiof.cadvancements.mixin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.ServerAdvancementManager;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sk.thefogiof.cadvancements.Cadvancements;
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
        for (Map.Entry<Identifier, Advancement> e : userAdvancements.entrySet()) {
            merged.put(e.getKey(), new AdvancementHolder(e.getKey(), e.getValue()));
        }
        this.advancements = merged;
/*
        for (Map.Entry<Identifier, Advancement> e : userAdvancements.entrySet()) {
            this.advancements.put(e.getKey(), new AdvancementHolder(e.getKey(), e.getValue()));
        }
*/

        AdvancementTree newTree = new AdvancementTree();
        newTree.addAll(this.advancements.values());
        newTree.repositionNodes();
        this.tree = newTree;

        Cadvancements.getLogger().info("Added {} user advancements.", userAdvancements.size());
    }
}
