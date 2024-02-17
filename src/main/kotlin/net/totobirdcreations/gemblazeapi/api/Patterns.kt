package net.totobirdcreations.gemblazeapi.api

import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import net.minecraft.text.Text


object Patterns {

    // Network
    @JvmStatic val SERVER_ADDRESS = Regex(".*/51.222.245.178:\\d+");

    // Commands
    @JvmStatic val SWITCH_MODE         = Regex("(?:play|build|dev|code)(?: .*)?");
    @JvmStatic val DEFAULT_INVENTORY   = Regex("reset(?: .*)?");
    @JvmStatic val COMPACT_INVENTORY   = Regex("(?:resetcompact|rc)(?: .*)?");
    @JvmStatic val CREATE_VARIABLE     = "var";
    @JvmStatic val ADD_CODESPACE_LAYER = Regex("(?:p|plot) codespace add");

    // Screen
    @JvmStatic val DEV_MENU    = "Code Blocks";
    @JvmStatic val VALUES_MENU = "Value Items";

    // Overlay
    @JvmStatic val SPAWN_OVERLAY = Regex("(?:<![^>]+>)*<#2ad4d4>DiamondFire(?:</#2ad4d4>(?:</![^>]+>)*)?(?:(?:<![^>]+>)*<dark_gray> - </dark_gray>(?:</![^>]+>)*(?:<![^>]+>)*<#ffd42a>⧈ \\d+ Tokens(?:</#ffd42a>(?:</![^>]+>)*)?)?(?:(?:<![^>]+>)*<dark_gray> - </dark_gray>(?:</![^>]+>)*(?:<![^>]+>)*<#ff5500>⌛ </#ff5500>(?:</![^>]+>)*(?:<![^>]+>)*<white>\\d+(?:</white>(?:</![^>]+>)?)*)?");
    @JvmStatic val CPU_OVERLAY   = Regex("<gold>CPU Usage: </gold><gray>\\[</gray>(?:<(\\w+)>▮*</\\1>)+<gray>] </gray><white>\\((?<percent>[0-9\\.]+)</white><white>%\\)");

    // Game Messages
    @JvmStatic val PLAY_MESSAGE      = Regex("<bold><green>» </green></bold><gray>Joined game: </gray>(?<plotName>.+)<gray> by </gray><white>(?<plotOwner>.+)</white><gray>\\.");
    @JvmStatic val BUILD_MESSAGE     = "<bold><green>» </green></bold><gray>You are now in build mode.";
    @JvmStatic val DEV_MESSAGE       = "<bold><green>» </green></bold><gray>You are now in dev mode.";
    @JvmStatic val FIND_SELF_MESSAGE = Regex("<click:run_command:['\"]/.*['\"]><hover:show_text:['\"]<#aad4aa>Click to join\\.['\"]><strikethrough><#aad4aa> *</#aad4aa></strikethrough><#aad4aa><br><white>You</white> are currently (?:at )?<white>(?<mode>playing|building|coding|spawn)</white>(?: on)?:?<br>(?:<br><#2ad4d4>→</#2ad4d4> (?<plotName>.+) <dark_gray>\\[</dark_gray><#aad4aa>(?<plotId>\\d+)</#aad4aa><dark_gray>]</dark_gray><br><#2ad4d4>→</#2ad4d4> <#aad4aa>Owner:</#aad4aa> <white>(?<plotOwner>.+)</white> (?<whitelisted>§8\\[§7Whitelisted§8])?<br>)?<#2ad4d4>→</#2ad4d4> <#aad4aa>Server:</#aad4aa> <white>(?<server>.+)</white></#aad4aa><strikethrough><#aad4aa>\\n? *");

    /**
     * Converts a `Text` object to a MiniMessage component `String`.
     */
    @JvmStatic fun textToMiniMessage(text : Text) : String {
        return MiniMessage.miniMessage().serialize(JSONComponentSerializer.json().deserialize(Text.Serialization.toJsonString(text)));
    }

    /**
     * Converts a MiniMessage component `String` to a `Text` object.
     */
    @JvmStatic fun miniMessageToText(minimessage : String) : Text? {
        return Text.Serialization.fromJson(JSONComponentSerializer.json().serialize(MiniMessage.miniMessage().deserialize(minimessage)));
    }

}
