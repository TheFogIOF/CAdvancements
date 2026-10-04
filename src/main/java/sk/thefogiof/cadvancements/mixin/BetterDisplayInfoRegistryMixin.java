package sk.thefogiof.cadvancements.mixin;

import betteradvancements.common.advancements.BetterDisplayInfo;
import betteradvancements.common.advancements.BetterDisplayInfoRegistry;
import com.google.gson.JsonObject;
import net.minecraft.advancements.AdvancementHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sk.thefogiof.cadvancements.compat.BetterDisplayRegistry;

@Mixin(BetterDisplayInfoRegistry.class)
public abstract class BetterDisplayInfoRegistryMixin {

    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    private void cadv$inject(AdvancementHolder advancementHolder, CallbackInfoReturnable<BetterDisplayInfo> cir) {
        JsonObject raw = BetterDisplayRegistry.get(advancementHolder.id());
        if (raw == null) return;
        cir.setReturnValue(new BetterDisplayInfo(advancementHolder.id(), raw));
    }
}