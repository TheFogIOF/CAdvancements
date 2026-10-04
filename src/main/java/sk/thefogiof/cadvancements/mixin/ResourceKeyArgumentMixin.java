package sk.thefogiof.cadvancements.mixin;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mixin(ResourceKeyArgument.class)
public class ResourceKeyArgumentMixin {

    /** Имя поля можно проверить в декомпилированном ResourceKeyArgument. */
    @Shadow
    @Final
    private ResourceKey<? extends Registry<?>> registryKey;

    @Inject(method = "listSuggestions", at = @At("HEAD"), cancellable = true)
    private <S> void cadv$injectSuggestions(CommandContext<S> context,
                                            SuggestionsBuilder builder,
                                            CallbackInfoReturnable<CompletableFuture<Suggestions>> cir) {
        // Только для аргумента-достижения. Для структур/рецептов/шаблонов — пусть ванила работает как обычно.
        if (!Registries.ADVANCEMENT.equals(this.registryKey)) return;
        if (!(context.getSource() instanceof CommandSourceStack source)) return;

        // Собираем все ID (ванильные + наши — getAllAdvancements() возвращает уже объединённую карту).
        List<String> ids = new ArrayList<>();
        for (AdvancementHolder h : source.getServer().getAdvancements().getAllAdvancements()) {
            ids.add(h.id().toString());
        }

        // SharedSuggestionProvider сам отфильтрует ids по текущему вводу пользователя
        // и вернёт корректный CompletableFuture<Suggestions>. Это именно тот метод,
        // которым пользуется сама ванила для подсказок критериев.
        cir.setReturnValue(SharedSuggestionProvider.suggest(ids, builder));
    }
}