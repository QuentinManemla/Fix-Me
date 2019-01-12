package com.wethinkcode.fixme.broker;

import com.wethinkcode.fixme.broker.Attachment.Attachment;
import com.wethinkcode.fixme.broker.Handlers.BrokerHandler;
import com.wethinkcode.fixme.broker.Handlers.ReadWriteHandler;
import com.wethinkcode.fixme.core.Instrument.Instrument;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class Broker {
    private static Integer clientID;
    public static Integer marketID;
    public static Integer msgSeqNum = 0;
    public static Boolean Auto = false;

    public static void setClientID(int _clientID) { clientID = _clientID; }
    public static Integer getClientID() { return clientID; }

    public static void main(String[] args) throws Exception {
        if (args.length == 5) {
            try {
                marketID = Integer.parseInt(args[0]);
                BrokerHandler.orderID = Integer.parseInt(args[1]);
                BrokerHandler.Orderquantity = Integer.parseInt(args[2]);
                BrokerHandler.Orderprice = Integer.parseInt(args[3]);
                BrokerHandler.OrderType = args[4];
                Connect();
            } catch (NumberFormatException exc) {
                System.out.println("[Broker]: Invalid Program Arguments [MarketID InstrumentID Quantity" +
                        " Price/per unit OrderType(Buy / Sell)]");
            }
        } else if (args.length == 1){
            marketID = Integer.parseInt(args[0]);
            Auto = true;
            Connect();
        } else {
            System.out.println("[Broker]: Incomplete Program Arguments [MarketID InstrumentID Quantity" +
                    " Price/per unit OrderType(Buy / Sell)]");
        }
    }

    private static void Connect() throws Exception{
        AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
        SocketAddress serverAddr = new InetSocketAddress("localhost", 5000);
        Future<Void> result = channel.connect(serverAddr);
        result.get();
        System.out.format("[Market] Connected to Router on : %s%n", serverAddr);
        Attachment attach = new Attachment();
        attach._Channel = channel;
        attach._Buffer = ByteBuffer.allocate(2048);
        attach.isRead = true;
        attach.mainThread = Thread.currentThread();

        attach.rwHandler = new ReadWriteHandler();
        channel.read(attach._Buffer, attach, attach.rwHandler);
        attach.mainThread.join();
    }
}
