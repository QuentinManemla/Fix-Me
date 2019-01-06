package com.wethinkcode.fixme.router.Attachment;

import com.wethinkcode.fixme.router.Messages.ReadWriteHandler;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;

public class Attachment {
    public AsynchronousServerSocketChannel _Server;
    public AsynchronousSocketChannel _Channel;
    public Integer _ClientId;
    public ByteBuffer _Buffer;
    public SocketAddress _ClientAddr;
    public ReadWriteHandler rwHandler;
    public boolean isRead;
}
