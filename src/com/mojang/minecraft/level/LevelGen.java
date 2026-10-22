/*
 * Decompiled with CFR 0.152.
 */
package com.mojang.minecraft.level;

import com.mojang.minecraft.level.NoiseMap;
import com.mojang.minecraft.level.tile.Tile;
import java.util.Random;

public class LevelGen {
    private int width;
    private int height;
    private int depth;
    private Random random = new Random();

    public LevelGen(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public byte[] generateMap() {
        int w = this.width;
        int h = this.height;
        int d = this.depth;
        int[] heightmap1 = new NoiseMap(0).read(w, h);
        int[] heightmap2 = new NoiseMap(0).read(w, h);
        int[] cf = new NoiseMap(1).read(w, h);
        int[] rockMap = new NoiseMap(1).read(w, h);
        byte[] blocks = new byte[this.width * this.height * this.depth];
        int x = 0;
        while (x < w) {
            int y = 0;
            while (y < d) {
                int z = 0;
                while (z < h) {
                    int dh;
                    int dh1 = heightmap1[x + z * this.width];
                    int dh2 = heightmap2[x + z * this.width];
                    int cfh = cf[x + z * this.width];
                    if (cfh < 128) {
                        dh2 = dh1;
                    }
                    if (dh2 > (dh = dh1)) {
                        dh = dh2;
                    } else {
                        dh2 = dh1;
                    }
                    dh = dh / 8 + d / 3;
                    int rh = rockMap[x + z * this.width] / 8 + d / 3;
                    if (rh > dh - 2) {
                        rh = dh - 2;
                    }
                    int i = (y * this.height + z) * this.width + x;
                    int id = 0;
                    if (y == dh) {
                        id = Tile.grass.id;
                    }
                    if (y < dh) {
                        id = Tile.dirt.id;
                    }
                    if (y <= rh) {
                        id = Tile.rock.id;
                    }
                    blocks[i] = (byte)id;
                    ++z;
                }
                ++y;
            }
            ++x;
        }
        int count = w * h * d / 256 / 64;
        int i = 0;
        while (i < count) {
            float x2 = this.random.nextFloat() * (float)w;
            float y = this.random.nextFloat() * (float)d;
            float z = this.random.nextFloat() * (float)h;
            int length = (int)(this.random.nextFloat() + this.random.nextFloat() * 150.0f);
            float dir1 = (float)((double)this.random.nextFloat() * Math.PI * 2.0);
            float dira1 = 0.0f;
            float dir2 = (float)((double)this.random.nextFloat() * Math.PI * 2.0);
            float dira2 = 0.0f;
            int l = 0;
            while (l < length) {
                x2 = (float)((double)x2 + Math.sin(dir1) * Math.cos(dir2));
                z = (float)((double)z + Math.cos(dir1) * Math.cos(dir2));
                y = (float)((double)y + Math.sin(dir2));
                dir1 += dira1 * 0.2f;
                dira1 *= 0.9f;
                dira1 += this.random.nextFloat() - this.random.nextFloat();
                dir2 += dira2 * 0.5f;
                dir2 *= 0.5f;
                dira2 *= 0.9f;
                dira2 += this.random.nextFloat() - this.random.nextFloat();
                float size = (float)(Math.sin((double)l * Math.PI / (double)length) * 2.5 + 1.0);
                int xx = (int)(x2 - size);
                while (xx <= (int)(x2 + size)) {
                    int yy = (int)(y - size);
                    while (yy <= (int)(y + size)) {
                        int zz = (int)(z - size);
                        while (zz <= (int)(z + size)) {
                            int ii;
                            float xd = (float)xx - x2;
                            float yd = (float)yy - y;
                            float zd = (float)zz - z;
                            float dd = xd * xd + yd * yd * 2.0f + zd * zd;
                            if (dd < size * size && xx >= 1 && yy >= 1 && zz >= 1 && xx < this.width - 1 && yy < this.depth - 1 && zz < this.height - 1 && blocks[ii = (yy * this.height + zz) * this.width + xx] == Tile.rock.id) {
                                blocks[ii] = 0;
                            }
                            ++zz;
                        }
                        ++yy;
                    }
                    ++xx;
                }
                ++l;
            }
            ++i;
        }
        return blocks;
    }
}

