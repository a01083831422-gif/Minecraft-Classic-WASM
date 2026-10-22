/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package com.mojang.minecraft.gui;

import com.mojang.minecraft.renderer.Tesselator;
import com.mojang.minecraft.renderer.Textures;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.lwjgl.opengl.GL11;

public class Font {
    private int[] charWidths = new int[256];
    private int fontTexture = 0;

    public Font(String name, Textures textures) {
        BufferedImage img;
        try {
            img = ImageIO.read(Textures.class.getResourceAsStream(name));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        int w = img.getWidth();
        int h = img.getHeight();
        int[] rawPixels = new int[w * h];
        img.getRGB(0, 0, w, h, rawPixels, 0, w);
        int i = 0;
        while (i < 128) {
            int xt = i % 16;
            int yt = i / 16;
            int x = 0;
            boolean emptyColumn = false;
            while (x < 8 && !emptyColumn) {
                int xPixel = xt * 8 + x;
                emptyColumn = true;
                int y = 0;
                while (y < 8 && emptyColumn) {
                    int yPixel = (yt * 8 + y) * w;
                    int pixel = rawPixels[xPixel + yPixel] & 0xFF;
                    if (pixel > 128) {
                        emptyColumn = false;
                    }
                    ++y;
                }
                ++x;
            }
            if (i == 32) {
                x = 4;
            }
            this.charWidths[i] = x;
            ++i;
        }
        this.fontTexture = textures.loadTexture(name, 9728);
    }

    public void drawShadow(String str, int x, int y, int color) {
        this.draw(str, x + 1, y + 1, color, true);
        this.draw(str, x, y, color);
    }

    public void draw(String str, int x, int y, int color) {
        this.draw(str, x, y, color, false);
    }

    public void draw(String str, int x, int y, int color, boolean darken) {
        char[] chars = str.toCharArray();
        if (darken) {
            color = (color & 0xFCFCFC) >> 2;
        }
        GL11.glEnable((int)3553);
        GL11.glBindTexture((int)3553, (int)this.fontTexture);
        Tesselator t = Tesselator.instance;
        t.init();
        t.color(color);
        int xo = 0;
        int i = 0;
        while (i < chars.length) {
            if (chars[i] == '&') {
                int cc = "0123456789abcdef".indexOf(chars[i + 1]);
                int br = (cc & 8) * 8;
                int b = (cc & 1) * 191 + br;
                int g = ((cc & 2) >> 1) * 191 + br;
                int r = ((cc & 4) >> 2) * 191 + br;
                color = r << 16 | g << 8 | b;
                i += 2;
                if (darken) {
                    color = (color & 0xFCFCFC) >> 2;
                }
                t.color(color);
            }
            int ix = chars[i] % 16 * 8;
            int iy = chars[i] / 16 * 8;
            t.vertexUV(x + xo, y + 8, 0.0f, (float)ix / 128.0f, (float)(iy + 8) / 128.0f);
            t.vertexUV(x + xo + 8, y + 8, 0.0f, (float)(ix + 8) / 128.0f, (float)(iy + 8) / 128.0f);
            t.vertexUV(x + xo + 8, y, 0.0f, (float)(ix + 8) / 128.0f, (float)iy / 128.0f);
            t.vertexUV(x + xo, y, 0.0f, (float)ix / 128.0f, (float)iy / 128.0f);
            xo += this.charWidths[chars[i]];
            ++i;
        }
        t.flush();
        GL11.glDisable((int)3553);
    }

    public int width(String str) {
        char[] chars = str.toCharArray();
        int len = 0;
        int i = 0;
        while (i < chars.length) {
            if (chars[i] == '&') {
                ++i;
            } else {
                len += this.charWidths[chars[i]];
            }
            ++i;
        }
        return len;
    }
}

