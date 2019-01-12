package com.wethinkcode.fixme.broker.Handlers;
import com.wethinkcode.fixme.broker.Attachment.Attachment;
import com.wethinkcode.fixme.broker.Message.Parser;
import com.wethinkcode.fixme.core.Core;

import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;

public class ReadWriteHandler implements CompletionHandler<Integer, Attachment> {
    Charset cs = Charset.forName("UTF-8");
    @Override
    public void completed(Integer result, Attachment attachment) {
        if (attachment.isRead) {
            attachment._Buffer.flip();
            int limits = attachment._Buffer.limit();
            byte bytes[] = new byte[limits];
            attachment._Buffer.get(bytes, 0, limits);
            String msg = new String(bytes, cs);
            System.out.println("[ Server ]: " + msg);
            Parser.parseMessage(attachment, msg, this);
        } else {
            attachment.isRead = true;
            attachment._Buffer.clear();
            attachment._Channel.read(attachment._Buffer, attachment, attachment.rwHandler);
        }
    }

    @Override
    public void failed(Throwable exc, Attachment attachment) {
        exc.printStackTrace();
    }
}
