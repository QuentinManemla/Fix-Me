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

    public static String Operation(boolean buy) {
        StringBuilder body = new StringBuilder();
        if (buy)
            body.append("54=1 |");
        else
            body.append("54=2 |");
        body.append("37=" + orderID + " |");
        body.append("38=" + Orderquantity + " |");
        body.append("44=" + Orderprice + " |");
        String header = Message.constructHeader("6",body.toString(), ++Broker.msgSeqNum,Broker.getClientID(),Broker.marketID);
        String message = header + Message.constructTrailer(header);
        return message;
    }

    public static String AutoBuy() {
        Random rand = new Random();
        if (brokerValue < 0)
            return AutoSell();
        else {
            int instrumentID = rand.nextInt(4 ) + 1;
            int quantity = 1;
            int price;
            StringBuilder body = new StringBuilder();

            body.append("54=1 |");
            body.append("37=" + instrumentID + " |");
            if (buys > 0) {
                price = (buys + sells) * 20;
                Instrument _instrument = getInstuement(instrumentID);
                if (_instrument == null)
                    quantity = 3;
                else
                    quantity = _instrument.Quantity;
            } else
                price = 80;

            if (price * quantity > brokerValue) {
                System.out.println("You can not afford that");
                quantity--;
                price = brokerValue / 2;
                System.out.println("New Price: " + price);
            }
            body.append("38=" + quantity + " |");
            body.append("44=" + price + " |");
            String header = Message.constructHeader("6",body.toString(), ++Broker.msgSeqNum,Broker.getClientID(),Broker.marketID);
            String message = header + Message.constructTrailer(header);

            System.out.println("Buying Instrument: " + instrumentID +" , Quantity: " + quantity + " For Price: " + price);
            return message;
        }
    }

    public static String AutoSell() {
        if (acquiredInstruments.size() > 0) {
            int instrumentID = getSellInstuementID();
            int quantity;
            int price;
            StringBuilder body = new StringBuilder();

            body.append("54=2 |");
            body.append("37=" + instrumentID + " |");
            if (sells > 0)
                price = (buys + sells) * 20;
            else
                price = 50;

            Instrument _instrument = getInstuement(instrumentID);
            if (_instrument.Quantity.equals(0))
                return AutoBuy();
            else if (_instrument.Quantity > 1)
                quantity = _instrument.Quantity / 2;
            else
                quantity = _instrument.Quantity;
            body.append("38=" + quantity + " |");
            body.append("44=" + price + " |");
            String header = Message.constructHeader("6", body.toString(), ++Broker.msgSeqNum, Broker.getClientID(), Broker.marketID);
            String message = header + Message.constructTrailer(header);

            System.out.println("Selling Instrument: " + instrumentID + " , Quantity: " + quantity + " For Price: " + price);
            return message;
        }
        return AutoBuy();
    }

    public static boolean Execute(String _message) {
        String marketId = Message.getValue("49", _message);
        String orderType = Message.getValue("54", _message);
        String instrumentId = Message.getValue("37", _message);
        String quantity = Message.getValue("38", _message);
        String price = Message.getValue("44", _message);
        String name = Message.getValue("58", _message);

        if (marketId != null && orderType != null && instrumentId != null && quantity != null && price != null &&
                name != null) {
            Integer _marketId = Integer.parseInt(marketId);
            if (!_marketId.equals(Broker.marketID))
                return false;

            if (orderType.equals("1")) {
                Integer _instrumentId = Integer.parseInt(instrumentId);
                Integer _quantity = Integer.parseInt(quantity);
                if (!updateInstrument(_instrumentId,_quantity, true))
                    acquiredInstruments.add(new Instrument(_instrumentId, name, _quantity));
                brokerValue -= (Integer.parseInt(price) * _quantity);
                buys++;
                return true;
            } else if (orderType.equals("2")) {
                if (Broker.Auto) {
                    Integer _instrumentId = Integer.parseInt(instrumentId);
                    Integer _quantity = Integer.parseInt(quantity);
                    Instrument instrument = getInstuement(_instrumentId);
                    Integer newquantity = instrument.Quantity - _quantity;

                    System.out.println("New Quantity: " + newquantity);
                    if (!updateInstrument(_instrumentId, newquantity, false))
                        acquiredInstruments.add(new Instrument(_instrumentId, name, newquantity));
                    brokerValue += (Integer.parseInt(price) * _quantity);
                    sells++;
                }
                return true;
            }
        }
        return false;
    }

    public static String Reject(String _message) {
        String marketId = Message.getValue("49", _message);
        String orderType = Message.getValue("54", _message);
        String instrumentId = Message.getValue("37", _message);
        String quantity = Message.getValue("38", _message);
        String reason = Message.getValue("58", _message);
        String reasonID = Message.getValue("103", _message);
        String price = Message.getValue("44", _message);

        System.out.println("[Broker]: Rejected Operation");

        if (Broker.Auto) {
            if (marketId != null && orderType != null && instrumentId != null && quantity != null && price != null &&
                    reason != null && reasonID != null) {
                if (reasonID.equals("14")) {
                    StringBuilder body = new StringBuilder();

                    body.append("54=" + orderType + " |");
                    body.append("37=" + instrumentId + " |");
                    body.append("38=" + quantity + " |");
                    body.append("44=" + reason.split(":")[1] + " |");
                    String header = Message.constructHeader("6", body.toString(), ++Broker.msgSeqNum,
                            Broker.getClientID(), Broker.marketID);
                    String message = header + Message.constructTrailer(header);
                    return message;
                }
            }
        }
        return "";
    }

    public static String Decide() {
        if (!Broker.Auto) {
            if (OrderType.equals("Buy"))
                return Operation(true);
            else if (OrderType.equals("Sell"))
                return Operation(false);
        } else {
            Random rand = new Random();
            if (buys < sells)
                return AutoBuy();
            else if (sells < buys)
                return AutoSell();
            else {
                if (rand.nextInt(100) < 50)
                    return AutoSell();
                else
                    return AutoBuy();
            }
        }
        return null;
    }

    private static boolean updateInstrument(int _instrumentId, int quantity, boolean buy) {
        for (Instrument instrument: acquiredInstruments) {
            if (instrument.ID.equals(_instrumentId)) {
                if (buy)
                    instrument.Quantity += quantity;
                else {
                    instrument.Quantity = quantity;
                    if (instrument.Quantity == 0)
                        acquiredInstruments.remove(instrument);
                }
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
