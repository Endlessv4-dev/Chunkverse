package dev.endless.v4.economy.utils;

public final class AmountParser {
    private AmountParser() {
    }

    public static double parse(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Amount is null");
        }

        String value = input.trim().replace("_", "");
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Amount is empty");
        }

        double multiplier = 1.0;
        char last = value.charAt(value.length() - 1);
        if (Character.isLetter(last)) {
            switch (Character.toLowerCase(last)) {
                case 'k' -> multiplier = 1_000.0;
                case 'm' -> multiplier = 1_000_000.0;
                case 'b' -> multiplier = 1_000_000_000.0;
                case 't' -> multiplier = 1_000_000_000_000.0;
                default -> throw new IllegalArgumentException("Unknown suffix");
            }
            value = value.substring(0, value.length() - 1);
            if (value.isEmpty()) {
                throw new IllegalArgumentException("Missing number");
            }
        }

        double number = Double.parseDouble(value);
        return number * multiplier;
    }
}
