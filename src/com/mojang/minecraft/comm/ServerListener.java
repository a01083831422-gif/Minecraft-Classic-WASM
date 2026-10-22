/*
 * Decompiled with CFR 0.152.
 */
package com.mojang.minecraft.comm;

import com.mojang.minecraft.comm.SocketConnection;

public interface ServerListener {
    public void clientConnected(SocketConnection var1);

    public void clientException(SocketConnection var1, Exception var2);
}

