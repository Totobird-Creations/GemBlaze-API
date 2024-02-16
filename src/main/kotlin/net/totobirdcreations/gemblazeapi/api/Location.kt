package net.totobirdcreations.gemblazeapi.api

import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.totobirdcreations.gemblazeapi.util.RequestableValue


class DiamondFire internal constructor() {
    /**
     * The current node that the players is on.
     *
     * *May be unknown.*
     */
    val node : RequestableValue<DiamondFireNode> = RequestableValue{-> TODO()};
    /**
     * The current plot that the players is on.
     *
     * *Is null if the player is at spawn.*
     */
    var plot : DiamondFirePlot?                  = null
            internal set;

    override fun toString() : String {
        return "NODE( ${this.node} ) PLOT( ${this.plot} )";
    }

}


sealed class DiamondFireNode private constructor(
    /**
     * The argument used in the `/server [id]` command.
     */
    val id   : String?,
    /**
     * The node's display name.
     */
    val name : String
) {

    class Indexed(
        val index : UInt
    ) : DiamondFireNode("node${index}", "Node ${index}");

    object Event : DiamondFireNode("event", "Event");

    object Beta : DiamondFireNode("beta", "Beta");

    class Other(
        name : String
    ) : DiamondFireNode(null, name);

    override fun toString() : String {
        return "ID( ${this.id}) NAME( ${this.name.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)")} )";
    }

    override fun equals(other : Any?) : Boolean {
        return other is DiamondFireNode && this::class == other::class && this.id == other.id;
    }

    override fun hashCode() : Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + name.hashCode()
        return result;
    }

    companion object {
        fun from(original : String) : DiamondFireNode {
            val string = original.lowercase().removePrefix("node ");
            if (string == "event") {
                return Event;
            } else if (string == "beta") {
                return Beta;
            }
            val int = string.toUIntOrNull();
            if (int != null) {
                return Indexed(int);
            }
            return Other(string);
        }
    }

}


class DiamondFirePlot internal constructor(mode : DiamondFireMode) {
    /**
     * The current mode the player is in.
     */
    var mode        : DiamondFireMode = mode
        internal set;
    /**
     * The id of the current plot.
     *
     * *May be unknown.*
     */
    val id          : RequestableValue<Int>     = RequestableValue{-> TODO()};
    /**
     * The name of the current plot.
     *
     * *May be unknown.*
     */
    val name        : RequestableValue<Text>    = RequestableValue{-> TODO()};
    /**
     * The username of the owner of the current plot.
     *
     * *May be unknown.*
     */
    val owner       : RequestableValue<String>  = RequestableValue{-> TODO()};
    /**
     * Whether the current plot is whitelisted.
     *
     * *May be unknown.*
     */
    val whitelisted : RequestableValue<Boolean> = RequestableValue{-> TODO()};
    /**
     * Information about the plot in the world.
     * Includes details like coordinates and size.
     */
    val area        : DiamondFirePlotArea       = DiamondFirePlotArea();

    /**
     * Information about the permissions that the
     * player has on the current plot.
     */
    val permissions : DiamondFirePlotPerms      = DiamondFirePlotPerms();

    override fun toString() : String {
        return "MODE( ${this.mode} ) ID( ${this.id} ) NAME( `${this.name.getOrNull()?.string?.replace("\\", "\\\\")?.replace("`", "\\`")}` ) OWNER( ${this.owner} ) WHITELISTED( ${this.whitelisted} ) AREA( ${this.area} ) PERMS( ${this.permissions} )"
    }

};


class DiamondFirePlotArea internal constructor() {
    /**
     * The North-West corner of the build area of the current plot.
     *
     * *May be unknown.*
     */
    var buildCorner : BlockPos?                = null
        internal set;
    /**
     * The North-West corner of the dev/code area of the current plot.
     *
     * *May be unknown.*
     */
    var devCorner   : BlockPos?                = null
        internal set;
    /**
     * The size of the current plot.
     *
     * *May be unknown.*
     */
    var size        : DiamondFirePlotAreaSize? = null
            internal set;

    fun isInBuildArea(pos : BlockPos) : Boolean? {
        val buildCorner = this.buildCorner ?: return null;
        val size        = this.size        ?: return null;
        return (
                pos.z >= buildCorner.z
             && pos.z < buildCorner.z + size.size
             && pos.x >= buildCorner.x
             && pos.x < buildCorner.x + size.size
        );
    }

    fun isInDevArea(pos : BlockPos) : Boolean? {
        val devCorner = this.devCorner ?: return null;
        val size      = this.size      ?: return null;
        return (
                pos.z >= devCorner.z
             && pos.z < devCorner.z + size.size
             && pos.x >= devCorner.x - 1
             && pos.x < devCorner.x + 19
        );
    }

    override fun toString() : String {
        return "BUILDC( ${this.buildCorner} ) DEVC( ${this.devCorner} ) SIZE( ${this.size} )";
    }

}


enum class DiamondFirePlotAreaSize(
    val size : Int
) {
    SMALL   (51),
    LARGE   (101),
    MASSIVE (301)
}


enum class DiamondFireMode {
    PLAY,
    BUILD,
    DEV;

    companion object {
        fun from(string : String) : DiamondFireMode? {
            return when (string) {
                "playing"  -> PLAY;
                "building" -> BUILD;
                "coding"   -> DEV;
                else       -> null;
            }
        }
    }
}


class DiamondFirePlotPerms internal constructor() {
    /**
     * Whether the player is whitelisted on the
     * current plot.
     *
     * *May be unknown.*
     * *If known, is always true.*
     */
    var hasPlay  : Boolean ? = null;
    /**
     * Whether the player has access to build mode
     * on the current plot.
     *
     * *May be unknown.*
     */
    var hasBuild : Boolean ? = null;
    /**
     * Whether the player has access to dev mode
     * on the current plot.
     *
     * *May be unknown.*
     */
    var hasDev   : Boolean ? = null;

    override fun toString() : String {
        return "PLAY( ${this.hasPlay} ) BUILD( ${this.hasBuild} ) DEV( ${this.hasDev} )";
    }
}
