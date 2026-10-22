/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.LWJGLException
 *  org.lwjgl.input.Cursor
 *  org.lwjgl.input.Keyboard
 *  org.lwjgl.input.Mouse
 *  org.lwjgl.opengl.Display
 *  org.lwjgl.opengl.DisplayMode
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.util.glu.GLU
 */
package com.mojang.minecraft;

import com.mojang.minecraft.Entity;
import com.mojang.minecraft.HitResult;
import com.mojang.minecraft.Player;
import com.mojang.minecraft.Timer;
import com.mojang.minecraft.character.Zombie;
import com.mojang.minecraft.gui.Font;
import com.mojang.minecraft.level.Chunk;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.LevelRenderer;
import com.mojang.minecraft.level.tile.Tile;
import com.mojang.minecraft.particle.ParticleEngine;
import com.mojang.minecraft.phys.AABB;
import com.mojang.minecraft.renderer.Frustum;
import com.mojang.minecraft.renderer.Tesselator;
import com.mojang.minecraft.renderer.Textures;
import java.awt.Canvas;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class Minecraft
implements Runnable {
    public static final String VERSION_STRING = "0.0.11a";
    private boolean fullscreen = false;
    private int width;
    private int height;
    private FloatBuffer fogColor0 = BufferUtils.createFloatBuffer((int)4);
    private FloatBuffer fogColor1 = BufferUtils.createFloatBuffer((int)4);
    private Timer timer = new Timer(20.0f);
    private Level level;
    private LevelRenderer levelRenderer;
    private Player player;
    private int paintTexture = 1;
    private ParticleEngine particleEngine;
    private ArrayList<Entity> entities = new ArrayList();
    private Canvas parent;
    public boolean appletMode = false;
    public volatile boolean pause = false;
    private Cursor emptyCursor;
    private int yMouseAxis = 1;
    public Textures textures;
    private Font font;
    private int editMode = 0;
    private volatile boolean running = false;
    private String fpsString = "";
    private boolean mouseGrabbed = false;
    private IntBuffer viewportBuffer = BufferUtils.createIntBuffer((int)16);
    private IntBuffer selectBuffer = BufferUtils.createIntBuffer((int)2000);
    private HitResult hitResult = null;
    FloatBuffer lb = BufferUtils.createFloatBuffer((int)16);

    public Minecraft(Canvas parent, int width, int height, boolean fullscreen) {
        this.parent = parent;
        this.width = width;
        this.height = height;
        this.fullscreen = fullscreen;
        this.textures = new Textures();
    }

    public void init() throws LWJGLException, IOException {
        int col0 = 16710650;
        int col1 = 920330;
        float fr = 0.5f;
        float fg = 0.8f;
        float fb = 1.0f;
        this.fogColor0.put(new float[]{(float)(col0 >> 16 & 0xFF) / 255.0f, (float)(col0 >> 8 & 0xFF) / 255.0f, (float)(col0 & 0xFF) / 255.0f, 1.0f});
        this.fogColor0.flip();
        this.fogColor1.put(new float[]{(float)(col1 >> 16 & 0xFF) / 255.0f, (float)(col1 >> 8 & 0xFF) / 255.0f, (float)(col1 & 0xFF) / 255.0f, 1.0f});
        this.fogColor1.flip();
        if (this.parent != null) {
            Display.setParent((Canvas)this.parent);
        } else if (this.fullscreen) {
            Display.setFullscreen((boolean)true);
            this.width = Display.getDisplayMode().getWidth();
            this.height = Display.getDisplayMode().getHeight();
        } else {
            Display.setDisplayMode((DisplayMode)new DisplayMode(this.width, this.height));
        }
        Display.setTitle((String)"Minecraft 0.0.11a");
        try {
            Display.create();
        }
        catch (LWJGLException e) {
            e.printStackTrace();
            try {
                Thread.sleep(1000L);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
            Display.create();
        }
        Keyboard.create();
        Mouse.create();
        this.checkGlError("Pre startup");
        GL11.glEnable((int)3553);
        GL11.glShadeModel((int)7425);
        GL11.glClearColor((float)fr, (float)fg, (float)fb, (float)0.0f);
        GL11.glClearDepth((double)1.0);
        GL11.glEnable((int)2929);
        GL11.glDepthFunc((int)515);
        GL11.glEnable((int)3008);
        GL11.glAlphaFunc((int)516, (float)0.0f);
        GL11.glMatrixMode((int)5889);
        GL11.glLoadIdentity();
        GL11.glMatrixMode((int)5888);
        this.checkGlError("Startup");
        this.level = new Level(256, 256, 64);
        this.levelRenderer = new LevelRenderer(this.level, this.textures);
        this.player = new Player(this.level);
        this.particleEngine = new ParticleEngine(this.level, this.textures);
        this.font = new Font("/default.gif", this.textures);
        int i = 0;
        while (i < 10) {
            Zombie zombie = new Zombie(this.level, this.textures, 128.0f, 0.0f, 128.0f);
            zombie.resetPos();
            this.entities.add(zombie);
            ++i;
        }
        IntBuffer imgData = BufferUtils.createIntBuffer((int)256);
        imgData.clear().limit(256);
        if (this.appletMode) {
            try {
                this.emptyCursor = new Cursor(16, 16, 0, 0, 1, imgData, null);
            }
            catch (LWJGLException e) {
                e.printStackTrace();
            }
        } else {
            this.grabMouse();
        }
        this.checkGlError("Post startup");
    }

    private void checkGlError(String string) {
        int errorCode = GL11.glGetError();
        if (errorCode != 0) {
            String errorString = GLU.gluErrorString((int)errorCode);
            System.out.println("########## GL ERROR ##########");
            System.out.println("@ " + string);
            System.out.println(String.valueOf(errorCode) + ": " + errorString);
            System.exit(0);
        }
    }

    public void destroy() {
        try {
            this.level.save();
        }
        catch (Exception exception) {
            // empty catch block
        }
        Mouse.destroy();
        Keyboard.destroy();
        Display.destroy();
    }

    public void run() {
        this.running = true;
        try {
            this.init();
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.toString(), "Failed to start Minecraft", 0);
            return;
        }
        long lastTime = System.currentTimeMillis();
        int frames = 0;
        try {
            try {
                while (this.running) {
                    if (this.pause) {
                        Thread.sleep(100L);
                        continue;
                    }
                    if (this.parent == null && Display.isCloseRequested()) {
                        this.stop();
                    }
                    this.timer.advanceTime();
                    int i = 0;
                    while (i < this.timer.ticks) {
                        this.tick();
                        ++i;
                    }
                    this.checkGlError("Pre render");
                    this.render(this.timer.a);
                    this.checkGlError("Post render");
                    ++frames;
                    while (System.currentTimeMillis() >= lastTime + 1000L) {
                        this.fpsString = String.valueOf(frames) + " fps, " + Chunk.updates + " chunk updates";
                        Chunk.updates = 0;
                        lastTime += 1000L;
                        frames = 0;
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                this.destroy();
            }
        }
        finally {
            this.destroy();
        }
    }

    public void stop() {
        this.running = false;
    }

    public void grabMouse() {
        if (this.mouseGrabbed) {
            return;
        }
        this.mouseGrabbed = true;
        if (this.appletMode) {
            try {
                Mouse.setNativeCursor((Cursor)this.emptyCursor);
                Mouse.setCursorPosition((int)(this.width / 2), (int)(this.height / 2));
            }
            catch (LWJGLException e) {
                e.printStackTrace();
            }
        } else {
            Mouse.setGrabbed((boolean)true);
        }
    }

    public void releaseMouse() {
        if (!this.mouseGrabbed) {
            return;
        }
        this.mouseGrabbed = false;
        if (this.appletMode) {
            try {
                Mouse.setNativeCursor(null);
            }
            catch (LWJGLException e) {
                e.printStackTrace();
            }
        } else {
            Mouse.setGrabbed((boolean)false);
        }
    }

    private void handleMouseClick() {
        if (this.editMode == 0) {
            if (this.hitResult != null) {
                Tile oldTile = Tile.tiles[this.level.getTile(this.hitResult.x, this.hitResult.y, this.hitResult.z)];
                boolean changed = this.level.setTile(this.hitResult.x, this.hitResult.y, this.hitResult.z, 0);
                if (oldTile != null && changed) {
                    oldTile.destroy(this.level, this.hitResult.x, this.hitResult.y, this.hitResult.z, this.particleEngine);
                }
            }
        } else if (this.hitResult != null) {
            AABB aabb;
            int x = this.hitResult.x;
            int y = this.hitResult.y;
            int z = this.hitResult.z;
            if (this.hitResult.f == 0) {
                --y;
            }
            if (this.hitResult.f == 1) {
                ++y;
            }
            if (this.hitResult.f == 2) {
                --z;
            }
            if (this.hitResult.f == 3) {
                ++z;
            }
            if (this.hitResult.f == 4) {
                --x;
            }
            if (this.hitResult.f == 5) {
                ++x;
            }
            if ((aabb = Tile.tiles[this.paintTexture].getAABB(x, y, z)) == null || this.isFree(aabb)) {
                this.level.setTile(x, y, z, this.paintTexture);
            }
        }
    }

    public void tick() {
        while (Mouse.next()) {
            if (!this.mouseGrabbed && Mouse.getEventButtonState()) {
                this.grabMouse();
                continue;
            }
            if (Mouse.getEventButton() == 0 && Mouse.getEventButtonState()) {
                this.handleMouseClick();
            }
            if (Mouse.getEventButton() != 1 || !Mouse.getEventButtonState()) continue;
            this.editMode = (this.editMode + 1) % 2;
        }
        while (Keyboard.next()) {
            if (!Keyboard.getEventKeyState()) continue;
            if (Keyboard.getEventKey() == 1 && (this.appletMode || !this.fullscreen)) {
                this.releaseMouse();
            }
            if (Keyboard.getEventKey() == 28) {
                this.level.save();
            }
            if (Keyboard.getEventKey() == 2) {
                this.paintTexture = 1;
            }
            if (Keyboard.getEventKey() == 3) {
                this.paintTexture = 3;
            }
            if (Keyboard.getEventKey() == 4) {
                this.paintTexture = 4;
            }
            if (Keyboard.getEventKey() == 5) {
                this.paintTexture = 5;
            }
            if (Keyboard.getEventKey() == 7) {
                this.paintTexture = 6;
            }
            if (Keyboard.getEventKey() == 21) {
                this.yMouseAxis *= -1;
            }
            if (Keyboard.getEventKey() != 34) continue;
            this.entities.add(new Zombie(this.level, this.textures, this.player.x, this.player.y, this.player.z));
        }
        this.level.tick();
        this.particleEngine.tick();
        int i = 0;
        while (i < this.entities.size()) {
            this.entities.get(i).tick();
            if (this.entities.get((int)i).removed) {
                this.entities.remove(i--);
            }
            ++i;
        }
        this.player.tick();
    }

    private boolean isFree(AABB aabb) {
        if (this.player.bb.intersects(aabb)) {
            return false;
        }
        int i = 0;
        while (i < this.entities.size()) {
            if (this.entities.get((int)i).bb.intersects(aabb)) {
                return false;
            }
            ++i;
        }
        return true;
    }

    private void moveCameraToPlayer(float a) {
        GL11.glTranslatef((float)0.0f, (float)0.0f, (float)-0.3f);
        GL11.glRotatef((float)this.player.xRot, (float)1.0f, (float)0.0f, (float)0.0f);
        GL11.glRotatef((float)this.player.yRot, (float)0.0f, (float)1.0f, (float)0.0f);
        float x = this.player.xo + (this.player.x - this.player.xo) * a;
        float y = this.player.yo + (this.player.y - this.player.yo) * a;
        float z = this.player.zo + (this.player.z - this.player.zo) * a;
        GL11.glTranslatef((float)(-x), (float)(-y), (float)(-z));
    }

    private void setupCamera(float a) {
        GL11.glMatrixMode((int)5889);
        GL11.glLoadIdentity();
        GLU.gluPerspective((float)70.0f, (float)((float)this.width / (float)this.height), (float)0.05f, (float)1000.0f);
        GL11.glMatrixMode((int)5888);
        GL11.glLoadIdentity();
        this.moveCameraToPlayer(a);
    }

    private void setupPickCamera(float a, int x, int y) {
        GL11.glMatrixMode((int)5889);
        GL11.glLoadIdentity();
        this.viewportBuffer.clear();
        GL11.glGetInteger((int)2978, (IntBuffer)this.viewportBuffer);
        this.viewportBuffer.flip();
        this.viewportBuffer.limit(16);
        GLU.gluPickMatrix((float)x, (float)y, (float)5.0f, (float)5.0f, (IntBuffer)this.viewportBuffer);
        GLU.gluPerspective((float)70.0f, (float)((float)this.width / (float)this.height), (float)0.05f, (float)1000.0f);
        GL11.glMatrixMode((int)5888);
        GL11.glLoadIdentity();
        this.moveCameraToPlayer(a);
    }

    private void pick(float a) {
        this.selectBuffer.clear();
        GL11.glSelectBuffer((IntBuffer)this.selectBuffer);
        GL11.glRenderMode((int)7170);
        this.setupPickCamera(a, this.width / 2, this.height / 2);
        this.levelRenderer.pick(this.player, Frustum.getFrustum());
        int hits = GL11.glRenderMode((int)7168);
        this.selectBuffer.flip();
        this.selectBuffer.limit(this.selectBuffer.capacity());
        long closest = 0L;
        int[] names = new int[10];
        int hitNameCount = 0;
        int i = 0;
        while (i < hits) {
            int j;
            int nameCount = this.selectBuffer.get();
            long minZ = this.selectBuffer.get();
            this.selectBuffer.get();
            long dist = minZ;
            if (dist < closest || i == 0) {
                closest = dist;
                hitNameCount = nameCount;
                j = 0;
                while (j < nameCount) {
                    names[j] = this.selectBuffer.get();
                    ++j;
                }
            } else {
                j = 0;
                while (j < nameCount) {
                    this.selectBuffer.get();
                    ++j;
                }
            }
            ++i;
        }
        this.hitResult = hitNameCount > 0 ? new HitResult(names[0], names[1], names[2], names[3], names[4]) : null;
    }

    public void render(float a) {
        if (!Display.isActive()) {
            this.releaseMouse();
        }
        GL11.glViewport((int)0, (int)0, (int)this.width, (int)this.height);
        if (this.mouseGrabbed) {
            float xo = 0.0f;
            float yo = 0.0f;
            xo = Mouse.getDX();
            yo = Mouse.getDY();
            if (this.appletMode) {
                Display.processMessages();
                Mouse.poll();
                xo = Mouse.getX() - this.width / 2;
                yo = Mouse.getY() - this.height / 2;
                Mouse.setCursorPosition((int)(this.width / 2), (int)(this.height / 2));
            }
            this.player.turn(xo, yo * (float)this.yMouseAxis);
        }
        this.checkGlError("Set viewport");
        this.pick(a);
        this.checkGlError("Picked");
        GL11.glClear((int)16640);
        this.setupCamera(a);
        this.checkGlError("Set up camera");
        GL11.glEnable((int)2884);
        Frustum frustum = Frustum.getFrustum();
        this.levelRenderer.updateDirtyChunks(this.player);
        this.checkGlError("Update chunks");
        this.setupFog(0);
        GL11.glEnable((int)2912);
        this.levelRenderer.render(this.player, 0);
        this.checkGlError("Rendered level");
        int i = 0;
        while (i < this.entities.size()) {
            Entity entity = this.entities.get(i);
            if (entity.isLit() && frustum.isVisible(entity.bb)) {
                this.entities.get(i).render(a);
            }
            ++i;
        }
        this.checkGlError("Rendered entities");
        this.particleEngine.render(this.player, a, 0);
        this.checkGlError("Rendered particles");
        this.setupFog(1);
        this.levelRenderer.render(this.player, 1);
        i = 0;
        while (i < this.entities.size()) {
            Entity zombie = this.entities.get(i);
            if (!zombie.isLit() && frustum.isVisible(zombie.bb)) {
                this.entities.get(i).render(a);
            }
            ++i;
        }
        this.particleEngine.render(this.player, a, 1);
        GL11.glDisable((int)2896);
        GL11.glDisable((int)3553);
        GL11.glDisable((int)2912);
        this.checkGlError("Rendered rest");
        if (this.hitResult != null) {
            GL11.glDisable((int)3008);
            this.levelRenderer.renderHit(this.hitResult, this.editMode, this.paintTexture);
            GL11.glEnable((int)3008);
        }
        this.checkGlError("Rendered hit");
        this.drawGui(a);
        this.checkGlError("Rendered gui");
        Display.update();
    }

    private void drawGui(float a) {
        int screenWidth = this.width * 240 / this.height;
        int screenHeight = this.height * 240 / this.height;
        GL11.glClear((int)256);
        GL11.glMatrixMode((int)5889);
        GL11.glLoadIdentity();
        GL11.glOrtho((double)0.0, (double)screenWidth, (double)screenHeight, (double)0.0, (double)100.0, (double)300.0);
        GL11.glMatrixMode((int)5888);
        GL11.glLoadIdentity();
        GL11.glTranslatef((float)0.0f, (float)0.0f, (float)-200.0f);
        this.checkGlError("GUI: Init");
        GL11.glPushMatrix();
        GL11.glTranslatef((float)(screenWidth - 16), (float)16.0f, (float)0.0f);
        Tesselator t = Tesselator.instance;
        GL11.glScalef((float)16.0f, (float)16.0f, (float)16.0f);
        GL11.glRotatef((float)30.0f, (float)1.0f, (float)0.0f, (float)0.0f);
        GL11.glRotatef((float)45.0f, (float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glTranslatef((float)-1.5f, (float)0.5f, (float)-0.5f);
        GL11.glScalef((float)-1.0f, (float)-1.0f, (float)1.0f);
        int id = this.textures.loadTexture("/terrain.png", 9728);
        GL11.glBindTexture((int)3553, (int)id);
        GL11.glEnable((int)3553);
        t.init();
        Tile.tiles[this.paintTexture].render(t, this.level, 0, -2, 0, 0);
        t.flush();
        GL11.glDisable((int)3553);
        GL11.glPopMatrix();
        this.checkGlError("GUI: Draw selected");
        this.font.drawShadow(VERSION_STRING, 2, 2, 0xFFFFFF);
        this.font.drawShadow(this.fpsString, 2, 12, 0xFFFFFF);
        this.checkGlError("GUI: Draw text");
        int wc = screenWidth / 2;
        int hc = screenHeight / 2;
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        t.init();
        t.vertex(wc + 1, hc - 4, 0.0f);
        t.vertex(wc - 0, hc - 4, 0.0f);
        t.vertex(wc - 0, hc + 5, 0.0f);
        t.vertex(wc + 1, hc + 5, 0.0f);
        t.vertex(wc + 5, hc - 0, 0.0f);
        t.vertex(wc - 4, hc - 0, 0.0f);
        t.vertex(wc - 4, hc + 1, 0.0f);
        t.vertex(wc + 5, hc + 1, 0.0f);
        t.flush();
        this.checkGlError("GUI: Draw crosshair");
    }

    private void setupFog(int i) {
        if (i == 0) {
            GL11.glFogi((int)2917, (int)2048);
            GL11.glFogf((int)2914, (float)0.001f);
            GL11.glFog((int)2918, (FloatBuffer)this.fogColor0);
            GL11.glDisable((int)2896);
        } else if (i == 1) {
            GL11.glFogi((int)2917, (int)2048);
            GL11.glFogf((int)2914, (float)0.01f);
            GL11.glFog((int)2918, (FloatBuffer)this.fogColor1);
            GL11.glEnable((int)2896);
            GL11.glEnable((int)2903);
            float br = 0.6f;
            GL11.glLightModel((int)2899, (FloatBuffer)this.getBuffer(br, br, br, 1.0f));
        }
    }

    private FloatBuffer getBuffer(float a, float b, float c, float d) {
        this.lb.clear();
        this.lb.put(a).put(b).put(c).put(d);
        this.lb.flip();
        return this.lb;
    }

    public static void checkError() {
        int e = GL11.glGetError();
        if (e != 0) {
            throw new IllegalStateException(GLU.gluErrorString((int)e));
        }
    }

    public static void main(String[] args) throws LWJGLException {
        Minecraft minecraft = new Minecraft(null, 854, 480, false);
        new Thread(minecraft).start();
    }
}

