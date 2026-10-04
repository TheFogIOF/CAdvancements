package sk.thefogiof.cadvancements.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;

public class CustomAdvancementsMixinPlugin implements IMixinConfigPlugin {
    private static final String BA_MOD_ID = "betteradvancements";
    private boolean baLoaded;

    @Override
    public void onLoad(String mixinPackage) {
        this.baLoaded = FabricLoader.getInstance().isModLoaded(BA_MOD_ID);
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.endsWith("BetterDisplayInfoRegistryMixin")) return baLoaded;
        return true;
    }
}
