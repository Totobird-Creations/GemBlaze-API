package net.totobirdcreations.gemblaze.mixinternal

import dev.dfonline.codeclient.location.Dev
import dev.dfonline.codeclient.location.Plot
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.item.BlockItem
import net.minecraft.item.Items
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.totobirdcreations.gemblaze.Main
import net.totobirdcreations.gemblaze.mixinternal.codeclient.CCEventMixin
import net.totobirdcreations.gemblaze.render.DocsRenderer
import net.totobirdcreations.gemblaze.render.HudRenderer
import net.totobirdcreations.gemblaze.util.*
import net.totobirdcreations.gemblaze.util.item.InstructionBlock
import net.totobirdcreations.gemblaze.util.item.InstructionBlocks
import net.totobirdcreations.gemblaze.util.item.UtilityItems
import net.totobirdcreations.gemblaze.util.value.ExpirableValue


internal object ClientConnectionMixin {

    private val lastPlacedCodeBlock : ExpirableValue<BlockPos> = ExpirableValue(1000);
    fun putLastPlacedCodeBlock(pos : BlockPos) {
        this.lastPlacedCodeBlock.put(pos);
    }



    fun onSend(packet : Packet<*>) : Packet<*>? {

        if (packet is CommandExecutionC2SPacket) {
            var command = packet.command;

            if (command == "lagslayer") {
                CCEventMixin.lagslayerEnabled = ! CCEventMixin.lagslayerEnabled;
            }

            else if (command == "nightvis") {
                LightmapTextureManagerMixin.overrideNightVision = ! (LightmapTextureManagerMixin.overrideNightVision ?: Main.CONFIG.developmentNightVision);
                return null;
            }

            else if ((command == "reset" || command == "rs" || command == "rc" || command == "resetcompact") && Main.location is Dev) {
                UtilityItems.waitingForReset.put(true);
            }

            else if (CODESPACE_COMMAND.matchesAt(command, 0)) {
                val args = command.split(" ").toMutableList();
                Main.CONFIG.developmentAutoFlagCodespaceCompact .applyCommandFlags(args);
                Main.CONFIG.developmentAutoFlagCodespaceStyle   .applyCommandFlags(args);
                Main.CONFIG.developmentAutoFlagCodespaceColour  .applyCommandFlags(args);
                command = args.joinToString(" ");
            }

            else if (VARIABLE_COMMAND.matchesAt(command, 0)) {
                val args = command.split(" ").toMutableList();
                Main.CONFIG.developmentAutoFlagVariableScope.applyCommandFlags(args);
                command = args.joinToString(" ");
            }

            if (command != packet.command) {
                return CommandExecutionC2SPacket(command, packet.timestamp, packet.salt, packet.argumentSignatures, packet.acknowledgment);
            }
        }

        return packet;
    }



    fun onReceive(packet : Packet<*>) : Packet<*>? {

        if (packet is GameMessageS2CPacket && ! packet.overlay) {
            val mm = textToMm(packet.content);

            val playerChat = PLAYER_CHAT.matchEntire(mm);
            if (playerChat != null) {
                val username = playerChat.groups["username"]!!.value;
                val colour   = Integer.toHexString((if (Main.CLIENT.world?.players?.any { player -> player.gameProfile.name == username } == true) {
                    Main.CONFIG.chatColoursNameInrange
                } else { Main.CONFIG.chatColoursNameDefault }).rgb).substring(2);
                var mm = "<colour:#${colour}>${username}</colour><grey>:</grey> ${playerChat.groups["message"]!!.value}";
                if (! Main.CONFIG.chatHideTagsVIP   ) { mm = (playerChat.groups["vip"   ]?.value ?: "") + mm; }
                if (! Main.CONFIG.chatHideTagsRanks ) { mm = (playerChat.groups["ranks" ]?.value ?: "") + mm; }
                return GameMessageS2CPacket(mmToText(mm), false);
            }

            if (Main.CONFIG.chatSuppressPlotAds && PLOT_AD.matches(mm)) {
                return null;
            }
            if (Main.CONFIG.chatSuppressPlotBoosts && PLOT_BOOST.matches(mm)) {
                return null;
            }

            if (Main.CONFIG.developmentAutoEditValues) {
                val editValue = EDIT_VALUE.matchEntire(mm);
                if (editValue != null) {
                    Main.CLIENT.execute { -> Main.CLIENT.openChatScreen(editValue.groups["command"]!!.value); };
                }
            }

            if (FabricLoader.getInstance().isDevelopmentEnvironment) {
                Main.LOGGER.info("\u001B[0m\u001B[34m\u001B[96mCHAT\u001B[0m \u001B[36m${mm}\u001B[0m")
            }
        }

        else if (packet is OverlayMessageS2CPacket && Main.location is Plot) {
            val mm = textToMm(packet.message);

            val cpuOverlay = CPU_OVERLAY.matchEntire(mm);
            if (cpuOverlay != null) {
                HudRenderer.updateLagslayer(cpuOverlay.groups["percent"]!!.value.toFloat());
                if (Main.CONFIG.interfaceCpuOverlayPosition != CpuOverlayPosition.ACTIONBAR) { return null; }
            }

            if (FabricLoader.getInstance().isDevelopmentEnvironment) {
                Main.LOGGER.info("\u001B[0m\u001B[34m\u001B[95mOVERLAY\u001B[0m \u001B[35m${mm}\u001B[0m")
            }
        }

        else if (packet is ChunkDeltaUpdateS2CPacket) {
            val loc = Main.location;
            if (loc is Dev && Main.CONFIG.developmentCompactCoder) {
                val lastPos = this.lastPlacedCodeBlock.getOrNull();
                if (lastPos != null) {
                    val stonePos = lastPos.offset(Direction.SOUTH);

                    val pos     = BlockPos.Mutable();
                    val indices = (0..<(packet.positions.size)).filter{ i, ->
                        val s = packet.positions[i];
                        pos.set(packet.sectionPos.unpackBlockX(s), packet.sectionPos.unpackBlockY(s), packet.sectionPos.unpackBlockZ(s));
                        val state = packet.blockStates[i];
                        return@filter this.onReceiveUpdateBlock(stonePos, pos, state);
                    };
                    packet.positions   = indices.map{ i -> packet.positions   [i] }.toShortArray();
                    packet.blockStates = indices.map{ i -> packet.blockStates [i] }.toTypedArray();

                }
            }
        }

        else if (packet is BlockUpdateS2CPacket) {
            val lastPos = this.lastPlacedCodeBlock.getOrNull();
            if (lastPos != null) {
                val stonePos = lastPos.offset(Direction.SOUTH);

                if (! this.onReceiveUpdateBlock(stonePos, packet.pos, packet.state)) {
                    return null;
                }
            }
        }

        else if (packet is ScreenHandlerSlotUpdateS2CPacket) {
            val mm      = textToMm(packet.stack.name);
            var cancel0 = false;
            if (DocsRenderer.onSlotUpdate(packet.slot, packet.stack)) { cancel0 = true; }
            if (UtilityItems.waitingForReset.getOrNull() == true) {
                if (packet.slot == 37) {
                    UtilityItems.waitingForReset.invalidate();
                    if (Main.CONFIG.developmentAutoWorldeditWandDev) {
                        UtilityItems.putInventory(6, Items.WOODEN_AXE.defaultStack);
                        cancel0 = true;
                    }
                    val items = Main.CLIENT.player?.inventory?.main;
                    if (items != null) {
                        InstructionBlocks.instructionBlocks.clear();
                        for (stack in items) {
                            val item = stack.item;
                            if (item is BlockItem) {
                                InstructionBlocks.instructionBlocks[item.block] = InstructionBlock(stack,
                                    stack.name.string.uppercase(),
                                    stack.name.string.replace(" ", "")
                                )
                            }
                        }
                    }
                }
            }
            if (packet.slot == 17 && packet.stack.isOf(Items.WRITTEN_BOOK) && UtilityItems.REFERENCE_BOOK_NAME == mm) {
                UtilityItems.referenceBook = packet.stack.copyWithCount(1);
            }
            if (packet.stack.isOf(Items.STICK) && UtilityItems.GLITCH_STICK_NAME == mm) {
                val cancel1 = UtilityItems.waitingForGlitchStick.getOrNull() == true;
                UtilityItems.waitingForGlitchStick.invalidate();
                if (cancel1) {
                    UtilityItems.glitchStick.put(packet.stack.copyWithCount(1));
                    cancel0 = true;
                }
            }
            if (cancel0) { return null; }
        }

        else if (packet is OpenScreenS2CPacket) {
            val mm = textToMm(packet.name);
            if (mm == "<lang:container.chest>") {
                DocsRenderer.onOpenScreen();
            }
        }

        return packet;
    }


    private fun onReceiveUpdateBlock(stonePos : BlockPos, pos : BlockPos, state : BlockState) : Boolean {
        if (state.isOf(Blocks.STONE) && pos == stonePos) {
            this.lastPlacedCodeBlock.invalidate();
            val slot        = Main.CLIENT.player?.inventory?.selectedSlot;
            val glitchStick = UtilityItems.glitchStick.getOrNull();
            if (slot != null && glitchStick != null) {
                val resetTo = UtilityItems.getInventory(slot);
                UtilityItems.waitingForGlitchStick.put(true);
                UtilityItems.putInventory(slot, glitchStick, updateClient = false);
                Main.CLIENT.interactionManager?.attackBlock(pos, Direction.UP);
                UtilityItems.putInventory(slot, resetTo, updateClient = false);
                return false;
            }
        }
        return true;
    }



}