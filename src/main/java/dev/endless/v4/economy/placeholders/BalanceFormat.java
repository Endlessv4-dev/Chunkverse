package dev.endless.v4.economy.placeholders;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class BalanceFormat {
    private static final String[] SUFFIXES = {"", "K", "M", "B", "T"};

    private BalanceFormat() {
    }

    public static String format(double value) {
        double abs = Math.abs(value);
        int suffixIndex = 0;

        while (abs >= 1000 && suffixIndex < SUFFIXES.length - 1) {
            abs /= 1000.0;
            suffixIndex++;
        }

        double scaled = value / Math.pow(1000.0, suffixIndex);

        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.US);
        DecimalFormat formatter = new DecimalFormat(suffixIndex == 0 ? "#,##0" : "0.##", symbols);

        String number = formatter.format(scaled);
        String suffix = SUFFIXES[suffixIndex];

        if (suffix.isEmpty()) {
            return number;
        }

        return number + suffix;
    }
}
