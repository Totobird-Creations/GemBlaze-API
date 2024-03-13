package net.totobirdcreations.gemblaze.mixin;

import dev.dfonline.codeclient.location.Dev;
import dev.dfonline.codeclient.location.Location;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.totobirdcreations.gemblaze.Main;
import net.totobirdcreations.gemblaze.mixinternal.ClientConnectionMixin;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(BlockItem.class)
class BlockItemMixin {

    // CompactCoder
    @Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;", at = @At("RETURN"))
    private void place(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> cir) {
        @Nullable Location loc = Main.getLocation();
        if (loc instanceof Dev devLoc && cir.getReturnValue().isAccepted() && devLoc.isInDev(context.getBlockPos())) {
            BlockPos   pos    = context.getBlockPos();
            BlockPos   tPos   = pos.offset(context.getSide().getOpposite());
            BlockState tState = context.getWorld().getBlockState(tPos);
            if (tState.isOf(Blocks.STONE) || tState.isOf(Blocks.PISTON) || tState.isOf(Blocks.STICKY_PISTON)) {
                pos = tPos.offset(Direction.SOUTH);
            }
            ClientConnectionMixin.INSTANCE.putLastPlacedCodeBlock(pos);
        }
    }

}
