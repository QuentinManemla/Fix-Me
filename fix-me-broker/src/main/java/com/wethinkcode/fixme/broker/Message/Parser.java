package com.wethinkcode.fixme.broker.Message;

import com.wethinkcode.fixme.broker.Attachment.Attachment;
import com.wethinkcode.fixme.broker.Broker;
import com.wethinkcode.fixme.broker.Handlers.BrokerHandler;
import com.wethinkcode.fixme.broker.Handlers.ReadWriteHandler;
import com.wethinkcode.fixme.core.Instrument.InstrumentController;
import com.wethinkcode.fixme.core.Message.Message;

import java.nio.charset.Charset;

public class Parser {
    public static Charset cs = Charset.forName("UTF-8");
    public static void parseMessage(Attachment attachment, String msg, ReadWriteHandler rwHandler) {
        attachment._Buffer.clear();
        String message = "";
        if (msg.contains("ID")) {
            Broker.setClientID(Integer.parseInt(msg.split(":")[1]));
            message = BrokerHandler.Decide();
        } else {
            String msgType = Message.getValue("35", msg);
            if (msgType != null) {
                switch (msgType) {
                    case "C":
                        Broker.marketID = Integer.parseInt(Message.getValue("49", msg));
                        message = BrokerHandler.Buy();
                        break;
                    case "8":
                        if (BrokerHandler.Execute(msg)) {
                            System.out.println("Sale Executed");
                            InstrumentController.viewInstruements(BrokerHandler.acquiredInstruments);
//                            System.out.println("Broker Value: " + BrokerHandler.brokerValue);
//                            message = BrokerHandler.Decide();
                        }
                        else
                            System.out.println("Execution Failed");
                        break;
                    default:
                        message = "Unknown Message";
                        break;
                }
            }
        }
        byte[] data = message.getBytes(cs);
        attachment._Buffer.put(data);
        attachment._Buffer.flip();
        attachment.isRead = false;
        attachment._Channel.write(attachment._Buffer, attachment, attachment.rwHandler);
    }
}
