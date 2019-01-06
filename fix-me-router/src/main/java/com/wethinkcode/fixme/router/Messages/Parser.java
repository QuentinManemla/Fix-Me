package com.wethinkcode.fixme.router.Messages;

import com.sun.media.jfxmedia.logging.Logger;
import com.wethinkcode.fixme.core.Message.Message;
import com.wethinkcode.fixme.router.Attachment.Attachment;

import java.util.ArrayList;
import java.util.List;

import static com.wethinkcode.fixme.router.Messages.Forward.cs;

public class Parser {
    public static List<Attachment> noMarketGroup = new ArrayList<>();

    public static void parseMessage(Attachment attachment, String _message, ReadWriteHandler rwHandler) {
        String port = attachment._Server.toString().split(":")[1];
        port = port.substring(0, port.length() - 1);

        try {
            if (true) {
                if (!_message.contains("57=") && port.equals("5000")) {
                    if (!Forward.availableMarket(attachment, _message, rwHandler)) {
                        noMarketGroup.add(attachment);
                        return;
                    }
                } else if (!_message.contains("57=") && port.equals("5001") && noMarketGroup.size() > 0) {
                    for (Attachment client : noMarketGroup) {
                        client._Buffer.flip();
                        int limits = client._Buffer.limit();
                        byte bytes[] = new byte[limits];
                        client._Buffer.get(bytes, 0, limits);
                        String msg = new String(bytes, cs);
                        Forward.availableMarket(client, msg, rwHandler);
                        Thread.sleep(1000); // An issue to be dealt with later
                    }
                } else if (_message.contains("57=")) {
                    Forward.routeMessage(attachment, _message, rwHandler);
                }
            } else {
                System.out.println("Message is not seen as valid");
            }
        } catch (InterruptedException exc) {
            System.out.println("Interupted Exception");
        }
    }

    private static boolean validMessage(String _message) {
        try {
            if (_message.contains("10=")) {
                String[] tags = _message.replaceAll("\\s+", "").split("\\|");
                for (int i = 0; i < tags.length; i++) {
                    if (tags[i].split("=")[0].equals("10")) {
                        Integer Checksum = Integer.parseInt(Message.CalculateCheckSum(_message));
                        Integer Tag = Integer.parseInt(tags[i].split("=")[1]);
                        System.out.println("Checksum = " + Message.CalculateCheckSum(_message));
                        System.out.println("Message Tag = " + tags[i].split("=")[1]);
                        if (Checksum == Tag)
                            return true;
                    }
                }
            }
            return false;
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
