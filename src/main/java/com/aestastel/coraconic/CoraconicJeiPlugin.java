package com.aestastel.coraconic;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class CoraconicJeiPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath("coraconic", "jei_plugin");
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
    }
}
