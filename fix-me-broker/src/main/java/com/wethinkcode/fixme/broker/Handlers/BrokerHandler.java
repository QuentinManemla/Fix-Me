package com.wethinkcode.fixme.broker.Handlers;

import com.sun.org.apache.xpath.internal.operations.Or;
import com.wethinkcode.fixme.broker.Broker;
import com.wethinkcode.fixme.core.Instrument.Instrument;
import com.wethinkcode.fixme.core.Message.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BrokerHandler {
    public static Integer sells = 0;
    public static Integer buys = 0;

    public static Integer orderID;
    public static Integer Orderprice;
    public static Integer Orderquantity;
    public static String OrderType;

    public static List<Instrument> acquiredInstruments = new ArrayList<>();
    public static Integer brokerValue = 500;

    public static String Buy() {
        StringBuilder body = new StringBuilder();
        body.append("54=1 |");
        body.append("37=" + orderID + " |");
        body.append("38=" + Orderquantity + " |");
        body.append("44=" + Orderprice + " |");
        String header = Message.constructHeader("6",body.toString(), ++Broker.msgSeqNum,Broker.getClientID(),Broker.marketID);
        String message = header + Message.constructTrailer(header);
        return message;
    }

//    public static String Buy() {
//        System.out.println("Buying");
//        Random rand = new Random();
//        if (brokerValue < 0)
//            return Sell();
//        else {
//            int instrumentID = rand.nextInt(5 ) + 1;
//            int quantity = 1;
//            int price;
//            StringBuilder body = new StringBuilder();
//
//            body.append("54=1 |");
//            body.append("37=" + instrumentID + " |");
//            if (buys > 0) {
//                price = (buys + sells) * 20;
//                Instrument _instrument = getInstuement(instrumentID);
//                if (_instrument == null)
//                    quantity = 3;
//                else
//                    quantity = _instrument.Quantity;
//            } else
//                price = 50;
//
//            if (price * quantity > brokerValue) {
//                System.out.println("You can not afford that");
//                quantity--;
//                price = brokerValue / 2;
//                System.out.println("New Price: " + price);
//            }
//            body.append("38=" + quantity + " |");
//            body.append("44=" + price + " |");
//            String header = Message.constructHeader("6",body.toString(), ++Broker.msgSeqNum,Broker.getClientID(),Broker.marketID);
//            String message = header + Message.constructTrailer(header);
//
//            return message;
//        }
//    }

    public static String Sell() {
        System.out.println("Selling");
        int instrumentID = getSellInstuementID();
        System.out.println("Selling :" + instrumentID);
        int quantity ;
        int price;
        StringBuilder body = new StringBuilder();

        body.append("54=2 |");
        body.append("37=" + instrumentID + " |");
        if (sells > 0)
            price = (buys + sells) * 20;
        else
            price = 50;

        Instrument _instrument = getInstuement(instrumentID);
        if (_instrument.Quantity > 1)
            quantity = _instrument.Quantity / 2;
        else
            quantity = _instrument.Quantity;
        body.append("38=" + quantity + " |");
        body.append("44=" + price + " |");
        String header = Message.constructHeader("6",body.toString(), ++Broker.msgSeqNum,Broker.getClientID(),Broker.marketID);
        String message = header + Message.constructTrailer(header);

        return message;
    }

    public static boolean Execute(String _message) {
        String marketId = Message.getValue("49", _message);
        String orderType = Message.getValue("54", _message);
        String instrumentId = Message.getValue("37", _message);
        String quantity = Message.getValue("38", _message);
        String price = Message.getValue("44", _message);

        if (marketId != null && orderType != null && instrumentId != null && quantity != null && price != null) {
            Integer _marketId = Integer.parseInt(marketId);
            if (!_marketId.equals(Broker.marketID))
                return false;

            if (orderType.equals("1")) {
                Integer _instrumentId = Integer.parseInt(instrumentId);
                Integer _quantity = Integer.parseInt(quantity);
                if (!updateInstrument(_instrumentId,_quantity))
                    acquiredInstruments.add(new Instrument(_instrumentId, "", _quantity));
                brokerValue -= (Integer.parseInt(price) * _quantity);
                buys++;
                return true;
            } else if (orderType.equals("2")) {
                Integer _instrumentId = Integer.parseInt(instrumentId);
                Integer _quantity = Integer.parseInt(quantity);
                Instrument instrument = getInstuement(_instrumentId);
                Integer newquantity = instrument.Quantity - _quantity;

                System.out.println("New Quantity: " + newquantity);
                if (!updateInstrument(_instrumentId,newquantity))
                    acquiredInstruments.add(new Instrument(_instrumentId, "", newquantity));
                brokerValue += (Integer.parseInt(price) * _quantity);
                sells++;
                return true;
            }
        }
        return false;
    }

    public static String Decide() {
        if (OrderType.equals("Buy"))
            return Buy();
        else if (OrderType.equals("Sell"))
            return Sell();
        return null;
    }

    private static boolean updateInstrument(int _instrumentId, int quantity) {
        for (Instrument instrument: acquiredInstruments) {
            if (instrument.ID.equals(_instrumentId)) {
                instrument.Quantity += quantity;
                return true;
            }
        }
        return false;
    }

    private static Instrument getInstuement(int _instrumentId) {
        for (Instrument instrument: acquiredInstruments) {
            if (instrument.ID.equals(_instrumentId))
                return instrument;
        }
        return null;
    }

    private static Integer getSellInstuementID() {
        List<Integer> Ids = new ArrayList<>();
        Random random = new Random();
        if (acquiredInstruments.size() > 1) {
            for (Instrument instrument : acquiredInstruments) {
                Ids.add(instrument.ID);
            }
            int id = random.nextInt(Ids.size());
            return (acquiredInstruments.get(id).ID);
        }
        return acquiredInstruments.get(0).ID;
    }
}
