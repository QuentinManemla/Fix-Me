package com.wethinkcode.fixme.market.Handlers;

import com.wethinkcode.fixme.core.Instrument.Instrument;
import com.wethinkcode.fixme.core.Message.Message;
import com.wethinkcode.fixme.market.Market;

import java.util.Objects;

public class MarketHandler {
    public static Integer marketValue = 500;

    public static String Buy(String _message) {
        int instrumentId = Integer.parseInt(Objects.requireNonNull(Message.getValue("37", _message)));
        int quantity = Integer.parseInt(Objects.requireNonNull(Message.getValue("38", _message)));
        int price = Integer.parseInt(Objects.requireNonNull(Message.getValue("44", _message)));

        if (instrumentId > 5 || instrumentId < 1)
            return Reject(_message, "An error has occured, Unknown Instrument Order", 5);
        else if (quantity < 0)
            return Reject(_message, "An error has occured, Incorrect Quanitity", 13);
         else if (price <= 0)
            return Reject(_message, "An error has occured, invalid Price", 99);

         Instrument instrument = getInstrument(instrumentId);
         if (instrument.Quantity < quantity)
             return Reject(_message, "An error has occured, Incorrect Quanitity", 13);
         buyInstrument(instrumentId, quantity);
         marketValue += (price * quantity);
         StringBuilder body = new StringBuilder();
         body.append("54=1 |");
         body.append("37=" + instrumentId + " |");
         body.append("38=" + quantity + " |");
         body.append("44=" + price + " |");

         String header = Message.constructHeader("8",body.toString(), ++Market.msgSeqNum, Market.clientID,
                 Integer.parseInt(Message.getValue("49", _message)));
         String message = header + Message.constructTrailer(header);
         return message;
    }

    public static String Sell(String _message) {
        int instrumentId = Integer.parseInt(Objects.requireNonNull(Message.getValue("37", _message)));
        int quantity = Integer.parseInt(Objects.requireNonNull(Message.getValue("38", _message)));
        int price = Integer.parseInt(Objects.requireNonNull(Message.getValue("44", _message)));

        if (instrumentId > 5 || instrumentId < 1)
            return Reject(_message, "An error has occured, Unknown Instrument Order", 5);
        else if (quantity < 0)
            return Reject(_message, "An error has occured, Incorrect Quanitity", 13);
        else if (price <= 0 || (price * quantity) > marketValue)
            return Reject(_message, "An error has occured, invalid Price", 99);


        sellInstrument(instrumentId, quantity);
        marketValue -= (price * quantity);
        StringBuilder body = new StringBuilder();
        body.append("54=2 |");
        body.append("37=" + instrumentId + " |");
        body.append("38=" + quantity + " |");
        body.append("44=" + price + " |");

        String header = Message.constructHeader("8",body.toString(), ++Market.msgSeqNum, Market.clientID,
                Integer.parseInt(Message.getValue("49", _message)));
        String message = header + Message.constructTrailer(header);
        return message;
    }

    private static String Reject(String _message, String _reason, Integer rejReason) {
        StringBuilder _body = new StringBuilder();
        _body.append("58=" + _reason + " |");
        _body.append("103=" + rejReason + " |");
        String header = Message.constructHeader("3", _body.toString(), ++Market.msgSeqNum, Market.clientID,
                Integer.parseInt(Message.getValue("49", _message)));
        String message = header + Message.constructTrailer(header);
        return message;
    }

    private static void buyInstrument(int _instrumentId, int _quantity) {
        for (Instrument instrument: Market.marketInstruments) {
            if (instrument.ID == _instrumentId) {
                instrument.Quantity -= _quantity;
            }
        }
    }

    private static void sellInstrument(int _instrumentId, int _quantity) {
        for (Instrument instrument: Market.marketInstruments) {
            if (instrument.ID == _instrumentId) {
                instrument.Quantity += _quantity;
            }
        }
    }

    private static Instrument getInstrument(int _instrumentID) {
        for (Instrument instrument: Market.marketInstruments) {
            if (instrument.ID == _instrumentID) {
                return instrument;
            }
        }
        return null;
    }
}
