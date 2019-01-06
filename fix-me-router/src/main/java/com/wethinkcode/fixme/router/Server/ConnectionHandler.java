package com.wethinkcode.fixme.router.Server;

import com.wethinkcode.fixme.router.Attachment.Attachment;
import com.wethinkcode.fixme.router.Messages.ReadWriteHandler;
import com.wethinkcode.fixme.router.Router;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.Random;

public class ConnectionHandler implements CompletionHandler<AsynchronousSocketChannel, Attachment> {
    private ReadWriteHandler rwHandler = new ReadWriteHandler();
    @Override
    public void completed(AsynchronousSocketChannel channel, Attachment attachment) {
        try {
            SocketAddress clientAddr = channel.getRemoteAddress();
            System.out.format("Accepted a  connection from  %s%n", clientAddr);
            attachment._Server.accept(attachment, this);
            Attachment newAttach = new Attachment();
            newAttach._Server = attachment._Server;
            newAttach.rwHandler = new ReadWriteHandler();
            newAttach._Channel = channel;
            newAttach._Buffer = ByteBuffer.allocate(2048);
            newAttach.isRead = false;
            newAttach._ClientAddr = clientAddr;
            newClient(newAttach);
//            newAttach._Buffer.clear();
//            client.read(newAttach._Buffer, newAttach, rwHandler);
        } catch (IOException ex) {
            System.out.println(ex);
            System.exit(1);
        }
    }

    @Override
    public void failed(Throwable exc, Attachment attachment) {
        System.out.println("Failed to accept a connection");
        exc.printStackTrace();
    }

    private void newClient(Attachment attachment) {
        attachment._ClientId =  ++Router.clientID;
        Router.newClient(attachment);
        Charset cs = Charset.forName("UTF-8");
        String msg = "ID:" + attachment._ClientId;
        byte[] data = msg.getBytes(cs);
        attachment._Buffer.put(data);
        attachment._Buffer.flip();
        attachment._Channel.write(attachment._Buffer, attachment, rwHandler);
    }
}
