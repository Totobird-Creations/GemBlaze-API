package net.totobirdcreations.gemblazeapi.api

import net.totobirdcreations.gemblazeapi.detect.InboundPackets
import net.totobirdcreations.gemblazeapi.util.Event


object State {/**
 * Whether to log state changes when in `FabricLoader.isDevelopmentEnvironment`.
 *
 * *Can not be turned on when in production.*
 */
@JvmStatic var DEBUG_IN_DEVENV : Boolean = true;


    /**
     * Triggered when the player joins the DiamondFire server.
     */
    @JvmStatic val ENTER_DF    : Event<() -> Unit> = Event{ callbacks -> {      -> callbacks.forEach{ callback -> try{ callback()     } catch (_ : Exception) {}}}};
    /**
     * Triggered when the player leaves the DiamondFire server.
     */
    @JvmStatic val EXIT_DF     : Event<() -> Unit> = Event{ callbacks -> {      -> callbacks.forEach{ callback -> try{ callback()     } catch (_ : Exception) {}}}};

    /**
     * Triggered when the player joins a node on the DiamondFire server.
     */
    @JvmStatic val ENTER_NODE  : Event<(DiamondFireNode?) -> Unit> = Event{ callbacks -> { node -> callbacks.forEach{ callback -> try{ callback(node) } catch (_ : Exception) {}}}};
    /**
     * Triggered when the player leaves a node on the DiamondFire server.
     */
    @JvmStatic val EXIT_NODE   : Event<(DiamondFireNode?) -> Unit> = Event{ callbacks -> { node -> callbacks.forEach{ callback -> try{ callback(node) } catch (_ : Exception) {}}}};

    /**
     * Triggered when the player enters a plot on the DiamondFire server.
     */
    @JvmStatic val ENTER_PLOT  : Event<(DiamondFirePlot) -> Unit> = Event{ callbacks -> { plot -> callbacks.forEach{ callback -> try{ callback(plot) } catch (_ : Exception) {}}}};
    /**
     * Triggered when the player exits a plot on the DiamondFire server.
     */
    @JvmStatic val EXIT_PLOT   : Event<(DiamondFirePlot) -> Unit> = Event{ callbacks -> { plot -> callbacks.forEach{ callback -> try{ callback(plot) } catch (_ : Exception) {}}}};

    /**
     * Triggered when the player enters play mode within a plot on the DiamondFire server.
     */
    @JvmStatic val ENTER_PLAY  : Event<(DiamondFirePlot) -> Unit> = Event{ callbacks -> { plot -> callbacks.forEach{ callback -> try{ callback(plot) } catch (_ : Exception) {}}}};
    /**
     * Triggered when the player exits play mode within a plot on the DiamondFire server.
     */
    @JvmStatic val EXIT_PLAY   : Event<(DiamondFirePlot) -> Unit> = Event{ callbacks -> { plot -> callbacks.forEach{ callback -> try{ callback(plot) } catch (_ : Exception) {}}}};
    /**
     * Triggered when the player enters build mode within a plot on the DiamondFire server.
     */
    @JvmStatic val ENTER_BUILD : Event<(DiamondFirePlot) -> Unit> = Event{ callbacks -> { plot -> callbacks.forEach{ callback -> try{ callback(plot) } catch (_ : Exception) {}}}};
    /**
     * Triggered when the player exits build mode within a plot on the DiamondFire server.
     */
    @JvmStatic val EXIT_BUILD  : Event<(DiamondFirePlot) -> Unit> = Event{ callbacks -> { plot -> callbacks.forEach{ callback -> try{ callback(plot) } catch (_ : Exception) {}}}};
    /**
     * Triggered when the player enters dev/code mode within a plot on the DiamondFire server.
     */
    @JvmStatic val ENTER_DEV   : Event<(DiamondFirePlot) -> Unit> = Event{ callbacks -> { plot -> callbacks.forEach{ callback -> try{ callback(plot) } catch (_ : Exception) {}}}};
    /**
     * Triggered when the player exits dev/code mode within a plot on the DiamondFire server.
     */
    @JvmStatic val EXIT_DEV    : Event<(DiamondFirePlot) -> Unit> = Event{ callbacks -> { plot -> callbacks.forEach{ callback -> try{ callback(plot) } catch (_ : Exception) {}}}};

    /**
     * The current state the player is in, regarding the DiamondFire server.
     *
     * See [net.totobirdcreations.gemblazeapi.api.DiamondFire]
     */
    @JvmStatic var state : DiamondFire? = null
            private set;

    /**
     * Returns `true` if the player is currently on the DiamondFire server.
    **/
    @JvmStatic fun isOnDF() : Boolean {
        return state != null;
    }
    /**
     * Returns `true` if the player is currently in play mode.
     **/
    @JvmStatic fun isInPlay() : Boolean {
        if (state == null) {throw IllegalStateException();}
        return state!!.plot?.mode == DiamondFireMode.PLAY;
    }
    /**
     * Returns `true` if the player is currently in build mode.
     **/
    @JvmStatic fun isInBuild() : Boolean {
        if (state == null) {throw IllegalStateException();}
        return state!!.plot?.mode == DiamondFireMode.BUILD;
    }
    /**
     * Returns `true` if the player is currently in dev mode.
    **/
    @JvmStatic fun isInDev() : Boolean {
        if (state == null) {throw IllegalStateException();}
        return state!!.plot?.mode == DiamondFireMode.DEV;
    }

    /**
     * Returns the node the player is currently on, or `null` if it is unknown.
     *
     * Throws `IllegalStateException` if the player is not on teh DiamondFire server.
     */
    @Throws(IllegalStateException::class)
    @JvmStatic fun getNode() : DiamondFireNode? {
        if (state == null) {throw IllegalStateException();}
        return state!!.node.getOrNull();
    }

    /**
     * Returns the plot the player is currently on, or `null` if they are spawn.
     *
     * Throws `IllegalStateException` if the player is not on teh DiamondFire server.
     */
    @Throws(IllegalStateException::class)
    @JvmStatic fun getPlot() : DiamondFirePlot? {
        if (state == null) {throw IllegalStateException();}
        return state!!.plot;
    }


    internal object Internal {


        @Throws(IllegalStateChangeException::class)
        fun enterDF() {
            if (state != null) {
                throw IllegalStateChangeException(IllegalStateChangeReason.ALREADY_ON_DF);
            }
            state = DiamondFire();
            ENTER_DF.trigger();
            enterNodeUnsafe(null);
        }

        @Throws(IllegalStateChangeException::class)
        fun exitDF() {
            if (state == null) {
                throw IllegalStateChangeException(IllegalStateChangeReason.NOT_ON_DF);
            }
            exitNode();
            state = null;
            EXIT_DF.trigger();
        }


        fun enterNode(node : DiamondFireNode?) {
            if (state == null) {
                throw IllegalStateChangeException(IllegalStateChangeReason.NOT_ON_DF);
            }
            if (node == null || state!!.node.getOrNull() != node) {
                exitNode();
                enterNodeUnsafe(node);
            }
        }
        private fun enterNodeUnsafe(node : DiamondFireNode?) {
            state!!.node.putOrNull(node);
            ENTER_NODE.trigger(node);
        }

        private fun exitNode() {
            if (state == null) {
                throw IllegalStateChangeException(IllegalStateChangeReason.NOT_ON_DF);
            }
            if (state!!.plot != null) {
                exitPlot();
            }
            val lastNode = state!!.node.getOrNull();
            state!!.node.putNull();
            EXIT_NODE.trigger(lastNode);
        }


        fun enterPlot(plot : DiamondFirePlot) {
            if (state == null) {
                throw IllegalStateChangeException(IllegalStateChangeReason.NOT_ON_DF);
            }
            if (state!!.plot != null) {
                exitPlot();
            }
            state!!.plot = plot;
            ENTER_PLOT.trigger(plot);
            when (plot.mode) {
                DiamondFireMode.PLAY  -> ENTER_PLAY  .trigger(plot);
                DiamondFireMode.BUILD -> ENTER_BUILD .trigger(plot);
                DiamondFireMode.DEV   -> ENTER_DEV   .trigger(plot);
            }
        }

        fun exitPlot() {
            if (state == null) {
                throw IllegalStateChangeException(IllegalStateChangeReason.NOT_ON_DF);
            }
            if (state!!.plot == null) {
                throw IllegalStateChangeException(IllegalStateChangeReason.NOT_IN_PLOT);
            }
            InboundPackets.ModeSwitch.reset();
            val lastPlot = state!!.plot!!;
            state!!.plot = null;
            when (lastPlot.mode) {
                DiamondFireMode.PLAY  -> EXIT_PLAY  .trigger(lastPlot);
                DiamondFireMode.BUILD -> EXIT_BUILD .trigger(lastPlot);
                DiamondFireMode.DEV   -> EXIT_DEV   .trigger(lastPlot);
            }
            EXIT_PLOT.trigger(lastPlot);
        }

        fun switchPlotMode(mode : DiamondFireMode) {
            if (state == null) {
                throw IllegalStateChangeException(IllegalStateChangeReason.NOT_ON_DF);
            }
            if (state!!.plot == null) {
                throw IllegalStateChangeException(IllegalStateChangeReason.NOT_IN_PLOT);
            }
            val lastPlot = state!!.plot!!
            when (lastPlot.mode) {
                DiamondFireMode.PLAY  -> EXIT_PLAY  .trigger(lastPlot);
                DiamondFireMode.BUILD -> EXIT_BUILD .trigger(lastPlot);
                DiamondFireMode.DEV   -> EXIT_DEV   .trigger(lastPlot);
            }
            state!!.plot!!.mode = mode;
            when (mode) {
                DiamondFireMode.PLAY  -> ENTER_PLAY  .trigger(lastPlot);
                DiamondFireMode.BUILD -> ENTER_BUILD .trigger(lastPlot);
                DiamondFireMode.DEV   -> ENTER_DEV   .trigger(lastPlot);
            }
        }


        class IllegalStateChangeException(
            reason : IllegalStateChangeReason
        ) : Exception(reason.toString());
        enum class IllegalStateChangeReason {
            ALREADY_ON_DF,
            NOT_ON_DF,
            NOT_IN_PLOT
        }


    }

}