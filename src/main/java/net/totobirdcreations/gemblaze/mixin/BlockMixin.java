package net.totobirdcreations.gemblaze.mixin;

import dev.dfonline.codeclient.location.Dev;
import dev.dfonline.codeclient.location.Location;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;
import net.totobirdcreations.gemblaze.Main;
import net.totobirdcreations.gemblaze.util.item.InstructionBlock;
import net.totobirdcreations.gemblaze.util.item.InstructionBlocks;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Block.class)
class BlockMixin {

    @Inject(method = "getPickStack", at = @At("HEAD"), cancellable = true)
    private void getPickStack(WorldView world, BlockPos pos, BlockState state, CallbackInfoReturnable<ItemStack> cir) {
        @Nullable Location loc = Main.getLocation();
        if (loc instanceof Dev devLoc && devLoc.isInDev(pos)) {
            if (state.isOf(Blocks.CHEST)) {
                pos = pos.offset(Direction.DOWN);
            } else if (state.isOf(Blocks.OAK_WALL_SIGN)) {
                pos = pos.offset(Direction.EAST);
            }
            state = world.getBlockState(pos);
            InstructionBlock type = InstructionBlocks.INSTANCE.getInstructionBlocks$gemblaze().get(state.getBlock());
            if (type != null) {
                cir.setReturnValue(type.getStack());
            } else {
                cir.setReturnValue(ItemStack.EMPTY);
            }
        }
    }

}
