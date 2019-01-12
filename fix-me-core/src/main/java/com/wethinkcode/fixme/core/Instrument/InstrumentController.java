package com.wethinkcode.fixme.core.Instrument;

import java.util.List;

public class InstrumentController {

    public static void viewInstruements(List<Instrument> _instruments, boolean market) {
        if (market)
            System.out.println("ID |      Instrument Name      | Quantity  | Minimum Value |");
        else
            System.out.println("ID |      Instrument Name     | Quantity  |");
        System.out.println("-------------------------------------------------------------");
        for (Instrument instrument: _instruments) {
            System.out.print(instrument.ID + "  |");
            pad(instrument.Name, 27);
            pad(instrument.Quantity.toString(), 10);
            if (market)
                pad(instrument.MinValue.toString(), 14);
            System.out.print("\n");
        }
        System.out.println("-------------------------------------------------------------");
    }

    private static void pad(String string, int padlimit) {
        int spaces = padlimit - string.length();
        for (int i=0; i < spaces / 2; i++) {
            System.out.print(" ");
        }
        System.out.print(string);
        for (int i=0; i < spaces / 2; i++) {
            System.out.print(" ");
        }
        System.out.print("| ");
    }
}
