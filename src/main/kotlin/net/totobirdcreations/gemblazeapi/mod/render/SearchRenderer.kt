package net.totobirdcreations.gemblazeapi.mod.render

import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.util.math.ChunkPos
import org.lwjgl.opengl.GL11
import java.util.*


internal object SearchRenderer {

    var term : String? = null;

    private val chunks : MutableSet<ChunkPos> = mutableSetOf();
    fun getChunks(block : (MutableSet<ChunkPos>) -> Unit) {
        synchronized(this.chunks){-> block(this.chunks)};
    }


    fun layer() : RenderLayer {
        return Layer.LAYER;
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