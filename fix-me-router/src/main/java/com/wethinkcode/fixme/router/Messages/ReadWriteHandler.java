package com.wethinkcode.fixme.router.Messages;

import com.wethinkcode.fixme.core.Core;
import com.wethinkcode.fixme.router.Attachment.Attachment;

import java.io.IOException;
import java.nio.channels.CompletionHandler;
import java.nio.channels.ReadPendingException;
import java.nio.charset.Charset;

public class ReadWriteHandler implements CompletionHandler<Integer, Attachment> {
    Charset cs = Charset.forName("UTF-8");
    @Override
    public void completed(Integer result, Attachment attachment) {
        if (result == -1) {
            try {
                attachment._Channel.close();
                System.out.format("Stopped listening to the client %s%n", attachment._ClientAddr);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        if (attachment.isRead) {
            attachment._Buffer.flip();
            int limits = attachment._Buffer.limit();
            byte bytes[] = new byte[limits];
            attachment._Buffer.get(bytes, 0, limits);
            String msg = new String(bytes, cs);
            System.out.println("[ " + Core.getServerType(attachment._Server.toString()) + " ] " +
                    "[ " + attachment._ClientId + " ]: " + msg);
            Parser.parseMessage(attachment, msg, this);
        } else {
            try {
                attachment.isRead = true;
                attachment._Buffer.clear();
                attachment._Channel.read(attachment._Buffer, attachment, attachment.rwHandler);
            } catch (ReadPendingException exc) {}
        }
    }

    @Override
    public void failed(Throwable exc, Attachment attachment) {
        exc.printStackTrace();
    }

}
