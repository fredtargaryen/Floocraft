package com.fredtargaryen.floocraft.particle;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftParticleTypes;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ParticleDescriptionProvider;

public class FloocraftParticles extends ParticleDescriptionProvider {
    public FloocraftParticles(PackOutput output, ExistingFileHelper helper) {
        super(output, helper);
    }

    @Override
    protected void addDescriptions() {
        this.sprite(FloocraftParticleTypes.FLOO_TORCH_FLAME.get(), DataReference.FLOO_TORCH_PARTICLE_RL);
    }
}
