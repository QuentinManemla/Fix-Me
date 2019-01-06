package com.wethinkcode.fixme.core.Instrument;

import java.util.List;

public class InstrumentController {
    public static void viewInstruements(List<Instrument> _instruments) {
        for (Instrument instrument: _instruments) {
            System.out.println("Instument ID : " + instrument.ID);
            System.out.println("Instument Quantitiy : " + instrument.Quantity);
        }
    }
}
