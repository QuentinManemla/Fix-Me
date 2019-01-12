package com.wethinkcode.fixme.market.Attachment;

import com.wethinkcode.fixme.market.Handlers.ReadWriteHandler;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

public class Attachment {
    public AsynchronousSocketChannel _Channel;
    public ByteBuffer _Buffer;
    public Thread mainThread;
    public ReadWriteHandler rwHandler;
    public boolean isRead;
}
