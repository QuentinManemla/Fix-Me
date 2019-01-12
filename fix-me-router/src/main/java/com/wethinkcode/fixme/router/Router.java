package com.wethinkcode.fixme.router;
import com.wethinkcode.fixme.router.Attachment.Attachment;
import com.wethinkcode.fixme.router.Server.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Router {
    public static List<Attachment> clientGroup = new ArrayList<>();
    public static Integer clientID = 1000000;
    public static void newClient(Attachment _client) {
        clientGroup.add(_client);
    }
    public static Attachment getClient(int _clientID) {
        for (Attachment client: clientGroup) {
            if (client._ClientId.equals(_clientID))
                return client;
        }
        return null;
    }
    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.submit(new Server("localhost",5000, "Broker"));
        executor.submit(new Server("localhost", 5001, "Market"));
//        Thread.currentThread().join();
    }
}
