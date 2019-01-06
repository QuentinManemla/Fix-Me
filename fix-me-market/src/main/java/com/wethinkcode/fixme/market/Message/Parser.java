package com.wethinkcode.fixme.market.Message;

import com.wethinkcode.fixme.core.Message.Message;
import com.wethinkcode.fixme.market.Attachment.Attachment;
import com.wethinkcode.fixme.market.Handlers.MarketHandler;
import com.wethinkcode.fixme.market.Market;

import java.nio.channels.ReadPendingException;
import java.nio.charset.Charset;
import java.util.Objects;

public class Parser {
    public static Charset cs = Charset.forName("UTF-8");
    public static void parseMessage(Attachment attachment, String msg) {
        String message = "";
        String header;
        attachment._Buffer.clear();
        if (msg.contains("ID")) {
            Market.clientID = Integer.parseInt(msg.split(":")[1]);
        } else {
            String msgType = Message.getValue("35", msg);
            String targetID = Message.getValue("49", msg);
            if (msgType != null && targetID != null) {
                switch (msgType) {
                    case "A":
                        header = Message.constructHeader("C", "85=45", ++Market.msgSeqNum, Market.clientID,
                                Integer.parseInt(targetID));
                        message = header + Message.constructTrailer(header);
                        break;
                    case "6":
                        String orderType = Message.getValue("54", msg);
                        if (orderType != null) {
                            switch (orderType) {
                                case "1":
                                    message = MarketHandler.Buy(msg);
                                    break;
                                case "2":
                                    message = MarketHandler.Sell(msg);
                                    break;
                                default:
                                    break;
                            }
                        }
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
