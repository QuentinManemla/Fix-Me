package com.wethinkcode.fixme.core.Message;

import java.util.Date;

public class Message {
    public static String constructHeader(String _type, String _body, Integer _msgSeqNum, Integer _clientID, Integer _targetID) {
        StringBuilder header = new StringBuilder();
        StringBuilder message = new StringBuilder();
        header.append("8=FIXME.0.0 |");
        message.append("35="+_type+" |");
        message.append("49="+_clientID+" |");
        message.append("57="+_targetID+" |");
        message.append("34="+_msgSeqNum+" |");
        message.append("52="+ new Date().toString().split("\\s+")[1] +
                " " + new Date().toString().split("\\s+")[2] +
                " " + new Date().toString().split("\\s+")[3] + " |");
        int length = message.length() + _body.length();
        header.append("9="+length+" |");
        header.append(message);
        header.append(_body);
        return (header.toString());
    }

    public static String constructHeader(String _type, String _body, Integer _msgSeqNum, Integer _clientID) {
        StringBuilder header = new StringBuilder();
        StringBuilder message = new StringBuilder();
        header.append("8=FIXME.0.0 |");
        message.append("35="+_type+" |");
        message.append("49="+_clientID+" |");
        message.append("34="+_msgSeqNum+" |");
        message.append("52="+ new Date().toString().split("\\s+")[1] +
                " " + new Date().toString().split("\\s+")[2] +
                " " + new Date().toString().split("\\s+")[3] + " |");
        int length = message.length() + _body.length();
        header.append("9="+length+" |");
        header.append(message);
        header.append(_body);
        return (header.toString());
    }

    public static String constructTrailer(String _message) {
        String trailer = "10=" + CalculateCheckSum(_message) + " |";
        return (trailer);
    }

    public static String CalculateCheckSum(String _message) {
        Integer sumASCII = 0;
        char[] chars;
        if (_message.contains("10=")) {
            chars = _message.substring(0, _message.indexOf("10=")).replaceAll("\\s+",
                    "").toCharArray();
        } else
            chars = _message.replaceAll("\\s+","").toCharArray();
        for(int i = 0; i < chars.length; i++) {
            sumASCII += (int)chars[i];
        }
        sumASCII = sumASCII % 256;
        return (sumASCII.toString());
    }

    public static String getValue(String tag, String _message) {
        String[] tags = _message.replaceAll("\\s+","").split("\\|");
        for (int i = 0; i < tags.length; i++) {
            if (tags[i].split("=")[0].equals(tag)) {
                return (tags[i].split("=")[1]);
            }
        }
        return null;
    }
}
