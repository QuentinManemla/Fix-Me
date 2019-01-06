package com.wethinkcode.fixme.market;

import com.wethinkcode.fixme.core.Instrument.Instrument;
import com.wethinkcode.fixme.market.Attachment.Attachment;
import com.wethinkcode.fixme.market.Handlers.ReadWriteHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Market {
    public static Integer clientID;
    public static Integer msgSeqNum = 0;
    public static List<Instrument> marketInstruments = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        marketInstruments.add(new Instrument(1, "Simple Bond", 10));
        marketInstruments.add(new Instrument(2, "Treasury Bill", 10));
        marketInstruments.add(new Instrument(3, "Equity Loan", 10));
        marketInstruments.add(new Instrument(4, "Profit Participative Bonds", 10));
        marketInstruments.add(new Instrument(5, "Tracker-Certificate", 10));
        Connect();
    }

    private static void Connect()  {
        try {
            AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
            SocketAddress serverAddr = new InetSocketAddress("localhost", 5001);
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
        } catch (IOException exc) {
            System.out.println("IoException");
        } catch (InterruptedException exc) {
            System.out.println("Interrupted Exception");
        } catch (ExecutionException exc) {
            System.out.println("Execution Excpetion: " + exc.getLocalizedMessage());
            System.exit(1);
        }
    }
}
