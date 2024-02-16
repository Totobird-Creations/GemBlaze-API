package net.totobirdcreations.gemblazeapi.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.totobirdcreations.gemblazeapi.mod.render.ItemRenderer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(net.minecraft.client.render.item.ItemRenderer.class)
class ItemRendererMixin {

    @SuppressWarnings("unused")
    @WrapOperation(method = "getModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemModels;getModel(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/client/render/model/BakedModel;"))
    private BakedModel renderItemCaptureRenderMode(ItemModels instance, ItemStack stack, Operation<BakedModel> original) {
        try {
            @Nullable Identifier id = ItemRenderer.INSTANCE.getModel(stack);
            if (id != null) {
                @Nullable BakedModel model = instance.getModelManager().getModel(id);
                if (model != null) {
                    return model;
                } else {
                    return instance.getModelManager().getMissingModel();
                }
            }
        } catch (Exception ignored) {}
        return original.call(instance, stack);
    }

}
