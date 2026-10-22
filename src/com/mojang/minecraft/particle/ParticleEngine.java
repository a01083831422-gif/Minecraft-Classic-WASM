/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package com.mojang.minecraft.particle;

import com.mojang.minecraft.Player;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.particle.Particle;
import com.mojang.minecraft.renderer.Tesselator;
import com.mojang.minecraft.renderer.Textures;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.opengl.GL11;

public class ParticleEngine {
    protected Level level;
    private List<Particle> particles = new ArrayList<Particle>();
    private Textures textures;

    public ParticleEngine(Level level, Textures textures) {
        this.level = level;
        this.textures = textures;
    }

    public void add(Particle p) {
        this.particles.add(p);
    }

    public void tick() {
        int i = 0;
        while (i < this.particles.size()) {
            Particle p = this.particles.get(i);
            p.tick();
            if (p.removed) {
                this.particles.remove(i--);
            }
            ++i;
        }
    }

    public void render(Player player, float a, int layer) {
        if (this.particles.size() == 0) {
            return;
        }
        GL11.glEnable((int)3553);
        int id = this.textures.loadTexture("/terrain.png", 9728);
        GL11.glBindTexture((int)3553, (int)id);
        float xa = -((float)Math.cos((double)player.yRot * Math.PI / 180.0));
        float za = -((float)Math.sin((double)player.yRot * Math.PI / 180.0));
        float xa2 = -za * (float)Math.sin((double)player.xRot * Math.PI / 180.0);
        float za2 = xa * (float)Math.sin((double)player.xRot * Math.PI / 180.0);
        float ya = (float)Math.cos((double)player.xRot * Math.PI / 180.0);
        Tesselator t = Tesselator.instance;
        GL11.glColor4f((float)0.8f, (float)0.8f, (float)0.8f, (float)1.0f);
        t.init();
        int i = 0;
        while (i < this.particles.size()) {
            Particle p = this.particles.get(i);
            if (p.isLit() ^ layer == 1) {
                p.render(t, a, xa, ya, za, xa2, za2);
            }
            ++i;
        }
        t.flush();
        GL11.glDisable((int)3553);
    }
}

