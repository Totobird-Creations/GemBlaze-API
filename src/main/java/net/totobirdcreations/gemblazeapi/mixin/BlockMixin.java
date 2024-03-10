package net.totobirdcreations.gemblazeapi.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallSignBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import net.totobirdcreations.gemblazeapi.api.State;
import net.totobirdcreations.gemblazeapi.api.hypercube.InstructionInfo;
import net.totobirdcreations.gemblazeapi.api.hypercube.InstructionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;


@Mixin(Block.class)
class BlockMixin {

    @Inject(method = "getPickStack", at = @At("HEAD"), cancellable = true)
    private void getPickStack(WorldView world, BlockPos blockPos, BlockState blockState, CallbackInfoReturnable<ItemStack> cir) {
        if (State.isOnDF() && State.isInDev()) {
            BlockPos   blockPos1   = blockPos;
            BlockState blockState1 = blockState;
            if (blockState.getBlock() instanceof WallSignBlock) {
                blockPos1   = blockPos1.add(1, 0, 0);
                blockState1 = world.getBlockState(blockPos1);
            } else if (blockState.isOf(Blocks.CHEST)) {
                blockPos1   = blockPos1.add(0, -1, 0);
                blockState1 = world.getBlockState(blockPos1);
            }
            if (Boolean.FALSE.equals(Objects.requireNonNull(State.getPlot()).getArea().isInBuildArea(blockPos1))) {
                for (InstructionType type : InstructionType.getEntries()) {
                    if (blockState1.isOf(type.getBlock())) {
                        InstructionInfo info = InstructionInfo.get(type);
                        if (info != null) {
                            cir.setReturnValue(info.getStack());
                            return;
                        }
                        break;
                    }
                }
            }
        }
    }

}
