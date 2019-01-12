package com.wethinkcode.fixme.core.Instrument;

public class Instrument {
    public Integer ID;
    public String Name;
    public Integer Quantity;
    public Integer MinValue = 0;

    public Instrument(Integer _id, String _name, Integer _quantity) {
        ID = _id;
        Name = _name;
        Quantity = _quantity;
    }

    public Instrument(Integer _id, String _name, Integer _quantity, Integer _minValue) {
        ID = _id;
        Name = _name;
        Quantity = _quantity;
        MinValue = _minValue;
    }
}
