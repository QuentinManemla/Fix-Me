package com.wethinkcode.fixme.router.Server;

import com.wethinkcode.fixme.router.Attachment.Attachment;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;

public class Server implements Runnable {
    private int port;
    private String host;
    private String name;
    private Thread mainThread;

    public Server(String _host, int _port, String _name) throws Exception {
        port = _port;
        host = _host;
        name = _name;
//        mainThread = new Thread(this);
//        mainThread.start();
    }

    @Override
    public void run() {
        try {
            AsynchronousServerSocketChannel Server = AsynchronousServerSocketChannel.open();

            InetSocketAddress Addr = new InetSocketAddress(host, port);
            Server.bind(Addr);
            System.out.format("[Router]  - " + name + " Server Listening at %s%n", Addr);
            Attachment attach = new Attachment();
            attach._Server = Server;
            Server.accept(attach, new ConnectionHandler());
        } catch (IOException ex ) {
            ex.printStackTrace();
        }
    }
}
