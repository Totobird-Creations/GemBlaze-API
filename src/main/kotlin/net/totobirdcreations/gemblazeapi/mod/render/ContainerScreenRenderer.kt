package net.totobirdcreations.gemblazeapi.mod.render

import net.minecraft.block.entity.SignBlockEntity
import net.minecraft.client.gui.DrawContext
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtString
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket
import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.totobirdcreations.gemblazeapi.Main
import net.totobirdcreations.gemblazeapi.api.Packets
import net.totobirdcreations.gemblazeapi.api.State
import net.totobirdcreations.gemblazeapi.api.hypercube.InstructionInfo
import net.totobirdcreations.gemblazeapi.api.hypercube.InstructionType
import net.totobirdcreations.gemblazeapi.api.hypercube.Inventory
import net.totobirdcreations.gemblazeapi.mod.Mod
import net.totobirdcreations.gemblazeapi.mod.util.ColourUtil
import net.totobirdcreations.gemblazeapi.util.ExpirableValue
import net.totobirdcreations.gemblazeapi.util.InventoryBuilder
import java.util.*


internal object ContainerScreenRenderer {

    private var active : Boolean         = false;
    private var lines  : ArrayList<Text> = arrayListOf();
    private fun clear() {
        this.active = false;
        this.lines.clear();
    }
    private fun addLine(line : Text) {
        this.lines.add(line);
        this.active = true;
    }
    private fun addLines(vararg lines : Text) {
        this.lines.addAll(lines);
        this.active = true;
    }


    fun render(context : DrawContext) {
        if (Mod.CONFIG.interfaceInstructionBlockDocs && State.isOnDF() && State.isInDev() && this.active) {
            var y = 10;
            for (line in this.lines) {
                context.drawText(Main.CLIENT.textRenderer, line, 10, y, 0xffffff, true);
                y += Main.CLIENT.textRenderer.fontHeight;
            }
        }
    }


    val lastOpenedPos       : ExpirableValue<BlockPos> = ExpirableValue(250);
    var referenceBook       : ItemStack?               = null;
    var referenceBookSlot   : Int?                     = null;
    var referenceBookRevert : ItemStack?               = null;
    var referenceBookUUID   : String?                  = null;

    fun openChest(pos : BlockPos) {
        this.clear();
        if (State.isOnDF() && State.isInDev() && State.getPlot()?.area?.isInDevArea(pos) == true) {
            this.referenceBook       = null;
            this.referenceBookSlot   = null;
            this.referenceBookRevert = null;
            this.referenceBookUUID   = null;

            val inventory = Main.CLIENT.player?.inventory?.main ?: return;
            for (i in inventory.indices) {
                val stack = inventory[i];
                try {
                    if (stack.isOf(Items.WRITTEN_BOOK) &&
                        stack.getSubNbt("PublicBukkitValues")?.getString("hypercube:item_instance")
                            == Inventory.referenceBook?.getSubNbt("PublicBukkitValues")?.getString("hypercube:item_instance")
                    ) {
                        this.referenceBookSlot = i;
                        break
                    }
                } catch (ignored: NullPointerException) {
                }
            }

            if (this.referenceBookSlot == null) {
                this.referenceBookSlot = 17;
                var referenceBook = Inventory.referenceBook;
                if (referenceBook != null) {
                    referenceBook = referenceBook.copy();
                    this.referenceBookUUID = UUID.randomUUID().toString();
                    referenceBook.orCreateNbt.put(this.referenceBookUUID, NbtString.of(""));
                    this.referenceBookRevert = inventory[this.referenceBookSlot!!];
                    InventoryBuilder.putInventoryItem(this.referenceBookSlot!!, referenceBook);
                }
            }

            Thread{->
                Packets.waitForPacket(OpenScreenS2CPacket::class.java, 250);
                if (this.referenceBookSlot != null && this.referenceBookRevert != null) {
                    InventoryBuilder.putInventoryItem(this.referenceBookSlot!!, this.referenceBookRevert!!);
                    this.referenceBookSlot = null;
                }
            }.start();

            this.lastOpenedPos.put(pos);
        }
    }

    fun openScreen() {
        this.clear();

        if (this.referenceBookSlot != null && this.referenceBookRevert != null) {
            InventoryBuilder.putInventoryItem(this.referenceBookSlot!!, this.referenceBookRevert!!);
            this.referenceBookSlot = null;
        }

        val world         = Main.CLIENT.world              ?: return;
        val lastOpenedPos = this.lastOpenedPos.getOrNull() ?: return;

        var info : InstructionInfo? = null;
        for (type in InstructionType.entries) {
            if (world.getBlockState(lastOpenedPos.add(0, -1, 0)).isOf(type.block)) {
                info = InstructionInfo.get(type);
            }
        }
        if (info != null) {
            val lines = mutableListOf<MutableText>(
                Text.literal(info.name.string.uppercase()).setStyle(info.name.style.withBold(true))
            );
            val entity = world.getBlockEntity(lastOpenedPos.add(-1, -1, 0));
            if (entity is SignBlockEntity) {
                val signText = entity.getText(true);
                if (signText.getMessage(3, false).string == "NOT") {
                    lines[0].append(Text.literal(" ɴᴏᴛ").setStyle(Style.EMPTY.withColor(0xff0000).withBold(false)))
                }
                val subColour = ColourUtil.intToHsv(info.name.style?.color?.rgb ?: 0);
                val subStyle = info.name.style.withColor(ColourUtil.hsvToInt(
                    subColour.first, subColour.second * 0.5f, subColour.third * 0.875f
                ));
                for (i in 1..2) {
                    val signLine = signText.getMessage(i, false)
                    if (signLine.string.replace(" ", "").isNotEmpty()) {
                        lines.add(Text.literal("  ").append(signLine.copy().setStyle(subStyle)))
                    }
                }
            }
            this.addLines(*lines.toTypedArray());
            if (this.referenceBook != null && (this.referenceBookUUID == null || this.referenceBook!!.nbt?.contains(this.referenceBookUUID!!) != true)) {
                this.addLine(Text.empty());
                val lore = this.referenceBook!!.getSubNbt(ItemStack.DISPLAY_KEY)?.getList(ItemStack.LORE_KEY, NbtElement.STRING_TYPE.toInt())?.mapNotNull{line -> Text.Serialization.fromJson((line as NbtString).asString())}
                if (lore != null) {
                    this.addLines(*lore.toTypedArray());
                }
            }
        }
    }

}