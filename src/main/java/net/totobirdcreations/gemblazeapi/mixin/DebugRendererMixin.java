package net.totobirdcreations.gemblazeapi.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.shape.VoxelShape;
import net.totobirdcreations.gemblazeapi.Main;
import net.totobirdcreations.gemblazeapi.api.DiamondFireMode;
import net.totobirdcreations.gemblazeapi.api.DiamondFirePlot;
import net.totobirdcreations.gemblazeapi.api.State;
import net.totobirdcreations.gemblazeapi.api.hypercube.InstructionType;
import net.totobirdcreations.gemblazeapi.mod.render.SearchRenderer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(DebugRenderer.class)
class DebugRendererMixin {

    @Inject(method = "render", at = @At("RETURN"))
    private void render(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        @Nullable String      term  = SearchRenderer.INSTANCE.getTerm();
        @Nullable ClientWorld world = Main.INSTANCE.getCLIENT().world;
        if (term != null && world != null) {
            if (State.isOnDF()) {
                DiamondFirePlot plot = State.getPlot();
                if (plot != null && plot.getMode() == DiamondFireMode.DEV) {
                    SearchRenderer.INSTANCE.getChunks((chunks -> {
                        VertexConsumer consumer = vertexConsumers.getBuffer(SearchRenderer.INSTANCE.layer());
                        for (ChunkPos chunkPos : chunks) {
                            for (BlockEntity entity : world.getChunk(chunkPos.x, chunkPos.z).getBlockEntities().values()) {
                                if (entity instanceof SignBlockEntity signEntity) {

                                    BlockPos pos = entity.getPos();
                                    if (Boolean.TRUE.equals(plot.getArea().isInDevArea(pos))) {
                                        BlockState instructionState = world.getBlockState(pos.add(1, 0, 0));
                                        if ((      instructionState.isOf(InstructionType.FUNCTION.getBlock())
                                                || instructionState.isOf(InstructionType.PROCESS.getBlock())
                                                || instructionState.isOf(InstructionType.CALL_FUNCTION.getBlock())
                                                || instructionState.isOf(InstructionType.CALL_PROCESS.getBlock())
                                        ) && signEntity.getText(true).getMessage(1, false).getString().contains(term)) {
                                            VoxelShape shape = world.getBlockState(pos).getOutlineShape(world, pos).offset(pos.getX(), pos.getY(), pos.getZ());
                                            WorldRenderer.drawShapeOutline(matrices, consumer, shape, -cameraX, -cameraY, -cameraZ, 1.0f, 1.0f, 1.0f, 1.0f, true);
                                        }
                                    }

                                }
                            }
                        }
                        return null;
                    }));
                }
            }
        }
    }

}
