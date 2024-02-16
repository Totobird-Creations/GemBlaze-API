package net.totobirdcreations.gemblazeapi.mod.render

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import net.totobirdcreations.gemblazeapi.api.DiamondFireMode
import net.totobirdcreations.gemblazeapi.api.State
import net.totobirdcreations.gemblazeapi.api.hypercube.HYPERCUBE_PREFIX
import net.totobirdcreations.gemblazeapi.api.hypercube.ParameterType
import net.totobirdcreations.gemblazeapi.api.hypercube.ValueType
import net.totobirdcreations.gemblazeapi.api.hypercube.VariableScope


internal object ItemRenderer : ModelLoadingPlugin {

    private val VARIABLE_ERROR  : Identifier = Identifier(HYPERCUBE_PREFIX, "item/hypercube/variable/error");
    private val PARAMETER_ERROR : Identifier = Identifier(HYPERCUBE_PREFIX, "item/hypercube/parameter/error");


    fun getModel(stack : ItemStack) : Identifier? {
        try {if (State.state?.plot?.mode == DiamondFireMode.DEV) {
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
                        return VariableScope.entries.first{it -> scope == it.id}.model;
                    } catch (_ : Exception) {}
                    return VARIABLE_ERROR;
                }

                if (id == "pn_el") {
                    try {
                        val type = JsonHelper.getString(data, "type");
                        return ParameterType.entries.first{it -> type == it.type}.model;
                    } catch (_ : Exception) {}
                    return PARAMETER_ERROR;
                }

            }
        }} catch (_ : Exception) {}
        return null;
    }


    override fun onInitializeModelLoader(context : ModelLoadingPlugin.Context) {
        for (scope in ValueType.entries) {
            context.addModels(scope.model);
        }
        for (scope in VariableScope.entries) {
            context.addModels(scope.model);
        }
        for (scope in ParameterType.entries) {
            context.addModels(scope.model);
        }
    }

}