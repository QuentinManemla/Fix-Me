package com.wethinkcode.fixme.router.Messages;

import com.wethinkcode.fixme.core.Message.Message;
import com.wethinkcode.fixme.router.Attachment.Attachment;
import com.wethinkcode.fixme.router.Router;

import java.nio.charset.Charset;

public class Forward {
    public static  Charset cs = Charset.forName("UTF-8");
    public static boolean availableMarket(Attachment attachment, String _message, ReadWriteHandler rwHandler) {
        for (Attachment client: Router.clientGroup) {
            String port = client._Server.toString().split(":")[1];
            port = port.substring(0, port.length() - 1);
            if (port.equals("5001")) {
                client._Buffer.clear();
                byte[] data = _message.getBytes(cs);
                client._Buffer.put(data);
                client._Buffer.flip();
                client.isRead = false;
                client._Channel.write(client._Buffer, client, client.rwHandler);
                return true;
            }
        }
        return false;
    }

    public static void routeMessage(Attachment attachment, String _message, ReadWriteHandler rwHandler) {
        String ClientID = Message.getValue("57", _message);
        Attachment clientAtach = Router.getClient(Integer.parseInt(ClientID));
        if (clientAtach != null) {
            clientAtach._Buffer.clear();
            byte[] data = _message.getBytes(cs);
            clientAtach._Buffer.put(data);
            clientAtach._Buffer.flip();
            clientAtach.isRead = false;
            clientAtach._Channel.write(clientAtach._Buffer, clientAtach, clientAtach.rwHandler);
        } else
            System.out.println("Something went wrong: Client not Found");
    }
}
