package net.totobirdcreations.gemblaze.render

import dev.dfonline.codeclient.location.Dev
import net.minecraft.block.entity.SignBlockEntity
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider.Immediate
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.render.WorldRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.ChunkPos
import net.totobirdcreations.gemblaze.Main
import net.totobirdcreations.gemblaze.util.item.InstructionBlock
import org.lwjgl.opengl.GL11
import java.util.*


internal object SearchRenderer {

    var type : InstructionBlock? = null;
    var term : String?           = null;


    private val chunks : MutableSet<ChunkPos> = mutableSetOf();
    fun getChunks(block : (MutableSet<ChunkPos>) -> Unit) {
        synchronized(this.chunks){-> block(this.chunks)};
    }


    fun render(stack : MatrixStack, provider : Immediate, cameraX : Double, cameraY : Double, cameraZ : Double) {
        try {
            val loc   = Main.location;
            val world = Main.CLIENT.world;
            val type  = this.type;
            val term  = this.term;
            if (world != null && loc is Dev && (type != null || term != null)) {
                val consumer = provider.getBuffer(Layer.LAYER);
                this.getChunks { chunks -> for (chunkPos in chunks) { for (entity in world.getChunk(chunkPos.x, chunkPos.z).blockEntities.values) {
                    if (entity is SignBlockEntity) {
                        val pos = entity.pos;
                        if (loc.isInDev(pos)) {

                            if (type != null) {
                                if (entity.frontText.getMessage(0, false).string != type.name_sign) {
                                    continue;
                                }
                            }
                            if (term != null) {
                                if (! entity.frontText.getMessage(1, false).string.lowercase().contains(term)) {
                                    continue;
                                }
                            }

                            val shape = entity.cachedState.getOutlineShape(world, pos).offset(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble());
                            WorldRenderer.drawShapeOutline(stack, consumer, shape, -cameraX, -cameraY, -cameraZ,
                                Main.CONFIG.developmentSearchColour.red   .toFloat() / 255.0f,
                                Main.CONFIG.developmentSearchColour.green .toFloat() / 255.0f,
                                Main.CONFIG.developmentSearchColour.blue  .toFloat() / 255.0f,
                                Main.CONFIG.developmentSearchColour.alpha .toFloat() / 255.0f,
                            true);

                        }
                    }
                } } };
            }
        } catch (_ : Exception) {}
    }


    private class Layer(
        name: String,
        vertexFormat: VertexFormat,
        drawMode: VertexFormat.DrawMode,
        expectedBufferSize: Int,
        hasCrumbling: Boolean,
        translucent: Boolean,
        startAction: Runnable,
        endAction: Runnable
    ) : RenderLayer(
        name,
        vertexFormat,
        drawMode,
        expectedBufferSize,
        hasCrumbling,
        translucent,
        startAction, endAction
    ) {companion object {
        @Suppress("INACCESSIBLE_TYPE")
        val LAYER : RenderLayer = of(
            "search_overlay",
            VertexFormats.LINES,
            VertexFormat.DrawMode.LINES,
            1536,
            MultiPhaseParameters.builder()
                .program(LINES_PROGRAM)
                .lineWidth(LineWidth(OptionalDouble.empty()))
                .layering(VIEW_OFFSET_Z_LAYERING)
                .transparency(NO_TRANSPARENCY)
                .target(ITEM_ENTITY_TARGET)
                .writeMaskState(ALL_MASK)
                .cull(DISABLE_CULLING)
                .texture(NO_TEXTURE)
                .depthTest(DepthTest("always", GL11.GL_ALWAYS))
                .build(false)
        );
    }}

}