/*
 * Decompiled with CFR 0.152.
 */
package com.mojang.minecraft.comm;

import com.mojang.minecraft.comm.ServerListener;
import com.mojang.minecraft.comm.SocketConnection;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketServer {
    private ServerSocketChannel ssc;
    private ServerListener serverListener;
    private List<SocketConnection> connections = new LinkedList<SocketConnection>();
    protected static Logger logger = Logger.getLogger("SocketServer");

    public SocketServer(byte[] ips, int port, ServerListener serverListener) throws IOException {
        this.serverListener = serverListener;
        InetAddress hostip = InetAddress.getByAddress(ips);
        this.ssc = ServerSocketChannel.open();
        this.ssc.socket().bind(new InetSocketAddress(hostip, port));
        this.ssc.configureBlocking(false);
    }

    public void tick() throws IOException {
        SocketChannel socketChannel;
        while ((socketChannel = this.ssc.accept()) != null) {
            try {
                logger.log(Level.INFO, socketChannel.socket().getRemoteSocketAddress() + " connected");
                socketChannel.configureBlocking(false);
                SocketConnection socketConnection = new SocketConnection(socketChannel);
                this.connections.add(socketConnection);
                this.serverListener.clientConnected(socketConnection);
            }
            catch (IOException e) {
                socketChannel.close();
                throw e;
            }
        }
        int i = 0;
        while (i < this.connections.size()) {
            SocketConnection socketConnection = this.connections.get(i);
            if (!socketConnection.isConnected()) {
                socketConnection.disconnect();
                this.connections.remove(i--);
            } else {
                try {
                    socketConnection.tick();
                }
                catch (Exception e) {
                    socketConnection.disconnect();
                    this.serverListener.clientException(socketConnection, e);
                }
            }
            ++i;
        }
    }
}

