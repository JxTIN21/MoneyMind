package main.java.com.moneymind.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Utility class for currency formatting and calculations
 */
public class CurrencyUtils {

    // Default currency settings
    private static String defaultCurrencyCode = "USD";
    private static Locale defaultLocale = Locale.US;

    // Currency symbol mappings
    private static final Map<String, String> CURRENCY_SYMBOLS = new HashMap<>();

    static {
        CURRENCY_SYMBOLS.put("USD", "$");
        CURRENCY_SYMBOLS.put("EUR", "€");
        CURRENCY_SYMBOLS.put("GBP", "£");
        CURRENCY_SYMBOLS.put("JPY", "¥");
        CURRENCY_SYMBOLS.put("INR", "₹");
        CURRENCY_SYMBOLS.put("CAD", "C$");
        CURRENCY_SYMBOLS.put("AUD", "A$");
        CURRENCY_SYMBOLS.put("CHF", "Fr");
        CURRENCY_SYMBOLS.put("CNY", "¥");
        CURRENCY_SYMBOLS.put("SEK", "kr");
        CURRENCY_SYMBOLS.put("NOK", "kr");
        CURRENCY_SYMBOLS.put("MXN", "$");
        CURRENCY_SYMBOLS.put("SGD", "S$");
        CURRENCY_SYMBOLS.put("HKD", "HK$");
        CURRENCY_SYMBOLS.put("NZD", "NZ$");
        CURRENCY_SYMBOLS.put("KRW", "₩");
        CURRENCY_SYMBOLS.put("TRY", "₺");
        CURRENCY_SYMBOLS.put("RUB", "₽");
        CURRENCY_SYMBOLS.put("BRL", "R$");
        CURRENCY_SYMBOLS.put("ZAR", "R");
    }

    // Prevent instantiation
    private CurrencyUtils() {}

    // Configuration methods
    public static void setDefaultCurrency(String currencyCode) {
        if (isValidCurrencyCode(currencyCode)) {
            defaultCurrencyCode = currencyCode;
        } else {
            throw new IllegalArgumentException("Invalid currency code: " + currencyCode);
        }
    }

    public static void setDefaultLocale(Locale locale) {
        if (locale != null) {
            defaultLocale = locale;
        }
    }

    public static String getDefaultCurrencyCode() {
        return defaultCurrencyCode;
    }

    public static Locale getDefaultLocale() {
        return defaultLocale;
    }

    // Formatting methods
    public static String format(BigDecimal amount) {
        return format(amount, defaultCurrencyCode);
    }

    public static String format(BigDecimal amount, String currencyCode) {
        if (amount == null) {
            return formatZero(currencyCode);
        }

        try {
            NumberFormat formatter = NumberFormat.getCurrencyInstance(defaultLocale);
            formatter.setCurrency(Currency.getInstance(currencyCode));
            return formatter.format(amount);
        } catch (Exception e) {
            // Fallback to symbol-based formatting
            return formatWithSymbol(amount, currencyCode);
        }
    }

    public static String formatWithSymbol(BigDecimal amount, String currencyCode) {
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }

        String symbol = getCurrencySymbol(currencyCode);
        NumberFormat numberFormat = NumberFormat.getNumberInstance(defaultLocale);
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);

        return symbol + numberFormat.format(amount);
    }

    public static String formatSimple(BigDecimal amount) {
        return formatSimple(amount, defaultCurrencyCode);
    }

    public static String formatSimple(BigDecimal amount, String currencyCode) {
        if (amount == null) {
            return "0.00";
        }

        return String.format("%.2f %s", amount, currencyCode);
    }

    public static String formatWithoutSymbol(BigDecimal amount) {
        if (amount == null) {
            return "0.00";
        }

        NumberFormat numberFormat = NumberFormat.getNumberInstance(defaultLocale);
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);

        return numberFormat.format(amount);
    }

    public static String formatZero() {
        return formatZero(defaultCurrencyCode);
    }

    public static String formatZero(String currencyCode) {
        return format(BigDecimal.ZERO, currencyCode);
    }

    // Parsing methods
    public static BigDecimal parse(String amountString) {
        if (amountString == null || amountString.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        // Remove currency symbols and formatting
        String cleanAmount = cleanAmountString(amountString);

        try {
            return new BigDecimal(cleanAmount);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount format: " + amountString);
        }
    }

    public static BigDecimal parseSafely(String amountString) {
        try {
            return parse(amountString);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private static String cleanAmountString(String amountString) {
        if (amountString == null) {
            return "0";
        }

        // Remove common currency symbols and formatting characters
        String cleaned = amountString.trim()
                .replaceAll("[€$£¥₹₩₺₽]", "")  // Currency symbols
                .replaceAll("[,\\s]", "")        // Commas and spaces
                .replaceAll("^[A-Za-z]+", "")    // Currency codes at start
                .replaceAll("[A-Za-z]+$", "")    // Currency codes at end
                .trim();

        // Handle negative amounts in parentheses
        if (cleaned.startsWith("(") && cleaned.endsWith(")")) {
            cleaned = "-" + cleaned.substring(1, cleaned.length() - 1);
        }

        return cleaned.isEmpty() ? "0" : cleaned;
    }

    // Currency symbol and code utilities
    public static String getCurrencySymbol(String currencyCode) {
        return CURRENCY_SYMBOLS.getOrDefault(currencyCode.toUpperCase(), currencyCode);
    }

    public static boolean isValidCurrencyCode(String currencyCode) {
        try {
            Currency.getInstance(currencyCode);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static String getCurrencyDisplayName(String currencyCode) {
        try {
            Currency currency = Currency.getInstance(currencyCode);
            return currency.getDisplayName(defaultLocale);
        } catch (Exception e) {
            return currencyCode;
        }
    }

    // Mathematical operations with proper rounding
    public static BigDecimal add(BigDecimal amount1, BigDecimal amount2) {
        if (amount1 == null) amount1 = BigDecimal.ZERO;
        if (amount2 == null) amount2 = BigDecimal.ZERO;
        return amount1.add(amount2);
    }

    public static BigDecimal subtract(BigDecimal amount1, BigDecimal amount2) {
        if (amount1 == null) amount1 = BigDecimal.ZERO;
        if (amount2 == null) amount2 = BigDecimal.ZERO;
        return amount1.subtract(amount2);
    }

    public static BigDecimal multiply(BigDecimal amount, double multiplier) {
        if (amount == null) return BigDecimal.ZERO;
        return amount.multiply(BigDecimal.valueOf(multiplier))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal divide(BigDecimal amount, double divisor) {
        if (amount == null || divisor == 0) return BigDecimal.ZERO;
        return amount.divide(BigDecimal.valueOf(divisor), 2, RoundingMode.HALF_UP);
    }

    public static BigDecimal percentage(BigDecimal amount, double percentage) {
        if (amount == null) return BigDecimal.ZERO;
        return multiply(amount, percentage / 100);
    }

    public static double getPercentage(BigDecimal part, BigDecimal total) {
        if (part == null || total == null || total.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return part.divide(total, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    // Rounding utilities
    public static BigDecimal roundToCents(BigDecimal amount) {
        if (amount == null) return BigDecimal.ZERO;
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal roundToNearest(BigDecimal amount, BigDecimal roundTo) {
        if (amount == null || roundTo == null || roundTo.compareTo(BigDecimal.ZERO) == 0) {
            return amount;
        }

        return amount.divide(roundTo, 0, RoundingMode.HALF_UP).multiply(roundTo);
    }

    public static BigDecimal roundUp(BigDecimal amount) {
        if (amount == null) return BigDecimal.ZERO;
        return amount.setScale(2, RoundingMode.CEILING);
    }

    public static BigDecimal roundDown(BigDecimal amount) {
        if (amount == null) return BigDecimal.ZERO;
        return amount.setScale(2, RoundingMode.FLOOR);
    }

    // Comparison utilities
    public static boolean isZero(BigDecimal amount) {
        return amount == null || amount.compareTo(BigDecimal.ZERO) == 0;
    }

    public static boolean isPositive(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public static boolean isNegative(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) < 0;
    }

    public static boolean isEqual(BigDecimal amount1, BigDecimal amount2) {
        if (amount1 == null && amount2 == null) return true;
        if (amount1 == null || amount2 == null) return false;
        return amount1.compareTo(amount2) == 0;
    }

    public static BigDecimal max(BigDecimal amount1, BigDecimal amount2) {
        if (amount1 == null) return amount2;
        if (amount2 == null) return amount1;
        return amount1.max(amount2);
    }

    public static BigDecimal min(BigDecimal amount1, BigDecimal amount2) {
        if (amount1 == null) return amount2;
        if (amount2 == null) return amount1;
        return amount1.min(amount2);
    }

    public static BigDecimal abs(BigDecimal amount) {
        if (amount == null) return BigDecimal.ZERO;
        return amount.abs();
    }

    // Validation utilities
    public static boolean isValidAmount(String amountString) {
        try {
            parse(amountString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isValidAmount(BigDecimal amount) {
        return amount != null && amount.scale() <= 2;
    }

    public static String validateAndFormat(String amountString) {
        return validateAndFormat(amountString, defaultCurrencyCode);
    }

    public static String validateAndFormat(String amountString, String currencyCode) {
        try {
            BigDecimal amount = parse(amountString);
            return format(amount, currencyCode);
        } catch (Exception e) {
            return "Invalid Amount";
        }
    }

    // Display utilities for different contexts
    public static String formatForTable(BigDecimal amount) {
        return formatForTable(amount, defaultCurrencyCode);
    }

    public static String formatForTable(BigDecimal amount, String currencyCode) {
        if (amount == null) return "-";

        String formatted = formatWithoutSymbol(amount);
        String symbol = getCurrencySymbol(currencyCode);

        return formatted + " " + symbol;
    }

    public static String formatCompact(BigDecimal amount) {
        return formatCompact(amount, defaultCurrencyCode);
    }

    public static String formatCompact(BigDecimal amount, String currencyCode) {
        if (amount == null) return formatZero(currencyCode);

        String symbol = getCurrencySymbol(currencyCode);
        BigDecimal absAmount = amount.abs();

        if (absAmount.compareTo(BigDecimal.valueOf(1_000_000_000)) >= 0) {
            BigDecimal billions = absAmount.divide(BigDecimal.valueOf(1_000_000_000), 1, RoundingMode.HALF_UP);
            return symbol + billions + "B";
        } else if (absAmount.compareTo(BigDecimal.valueOf(1_000_000)) >= 0) {
            BigDecimal millions = absAmount.divide(BigDecimal.valueOf(1_000_000), 1, RoundingMode.HALF_UP);
            return symbol + millions + "M";
        } else if (absAmount.compareTo(BigDecimal.valueOf(1_000)) >= 0) {
            BigDecimal thousands = absAmount.divide(BigDecimal.valueOf(1_000), 1, RoundingMode.HALF_UP);
            return symbol + thousands + "K";
        } else {
            return format(amount, currencyCode);
        }
    }

    public static String formatWithSign(BigDecimal amount) {
        return formatWithSign(amount, defaultCurrencyCode);
    }

    public static String formatWithSign(BigDecimal amount, String currencyCode) {
        if (amount == null) return formatZero(currencyCode);

        String formatted = format(amount.abs(), currencyCode);

        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            return "+" + formatted;
        } else if (amount.compareTo(BigDecimal.ZERO) < 0) {
            return "-" + formatted;
        } else {
            return formatted;
        }
    }

    // Color coding for UI display
    public static String getAmountColorClass(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            return "neutral";
        } else if (amount.compareTo(BigDecimal.ZERO) > 0) {
            return "positive";
        } else {
            return "negative";
        }
    }

    // Budget and percentage utilities
    public static String formatBudgetUsage(BigDecimal spent, BigDecimal budget) {
        if (budget == null || budget.compareTo(BigDecimal.ZERO) == 0) {
            return "No Budget";
        }

        if (spent == null) {
            spent = BigDecimal.ZERO;
        }

        double percentage = getPercentage(spent, budget);
        String spentStr = format(spent);
        String budgetStr = format(budget);

        return String.format("%s / %s (%.1f%%)", spentStr, budgetStr, percentage);
    }

    public static String formatPercentage(double percentage) {
        return String.format("%.1f%%", percentage);
    }

    public static String formatPercentageChange(BigDecimal oldValue, BigDecimal newValue) {
        if (oldValue == null || oldValue.compareTo(BigDecimal.ZERO) == 0) {
            return newValue != null && newValue.compareTo(BigDecimal.ZERO) != 0 ? "∞%" : "0%";
        }

        if (newValue == null) {
            newValue = BigDecimal.ZERO;
        }

        BigDecimal change = newValue.subtract(oldValue);
        double percentage = getPercentage(change, oldValue.abs());

        String sign = change.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
        return sign + String.format("%.1f%%", percentage);
    }
}
