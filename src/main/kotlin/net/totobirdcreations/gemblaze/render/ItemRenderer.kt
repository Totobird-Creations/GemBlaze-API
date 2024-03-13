package net.totobirdcreations.gemblaze.render

import dev.dfonline.codeclient.location.Dev
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import net.totobirdcreations.gemblaze.Main
import net.totobirdcreations.gemblaze.util.hypercube.ParameterType
import net.totobirdcreations.gemblaze.util.hypercube.ValueType
import net.totobirdcreations.gemblaze.util.hypercube.VariableScope


// ResourcePackModels
internal object ItemRenderer : ModelLoadingPlugin {

    private val VARIABLE_ERROR  : Identifier = Identifier("hypercube", "item/hypercube/variable/error");
    private val PARAMETER_ERROR : Identifier = Identifier("hypercube", "item/hypercube/parameter/error");


    fun getModel(stack : ItemStack) : Identifier? {
        try {if (Main.location is Dev) {
            val nbt = stack.getSubNbt("PublicBukkitValues")?.getString("hypercube:varitem");
            if (! nbt.isNullOrEmpty()) {
                val varitem = JsonHelper.deserialize(nbt);
                val data    = JsonHelper.getObject(varitem, "data");
                val id      = JsonHelper.getString(varitem, "id");

                for (type in ValueType.entries) {
                    if (type.type == id) {
                        return type.model;
                    }
                }

                if (id == "var") {
                    try {
                        val scope = JsonHelper.getString(data, "scope");
                        return VariableScope.entries.first{ it -> scope == it.name.lowercase()}.model;
                    } catch (_ : Exception) {}
                    return VARIABLE_ERROR;
                }

                if (id == "pn_el") {
                    try {
                        val type = JsonHelper.getString(data, "type");
                        return ParameterType.entries.first{ it -> type == it.type}.model;
                    } catch (_ : Exception) {}
                    return PARAMETER_ERROR;
                }

            }
        }} catch (_ : Exception) {}
        return null;
    }


    override fun onInitializeModelLoader(context : ModelLoadingPlugin.Context) {
        for (type in ValueType.entries) {
            context.addModels(type.model);
        }
        for (scope in VariableScope.entries) {
            context.addModels(scope.model);
        }
        for (type in ParameterType.entries) {
            context.addModels(type.model);
        }
    }


}