package main.java.com.moneymind.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for date operations and formatting
 */
public class DateUtils {

    // Common date formatters
    public static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    public static final DateTimeFormatter SHORT_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yy");
    public static final DateTimeFormatter ISO_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter MONTH_YEAR_FORMAT = DateTimeFormatter.ofPattern("MMM yyyy");
    public static final DateTimeFormatter FULL_FORMAT = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");

    // Prevent instantiation
    private DateUtils() {}

    // Formatting methods
    public static String formatForDisplay(LocalDate date) {
        if (date == null) return "";
        return date.format(DISPLAY_FORMAT);
    }

    public static String formatShort(LocalDate date) {
        if (date == null) return "";
        return date.format(SHORT_FORMAT);
    }

    public static String formatISO(LocalDate date) {
        if (date == null) return "";
        return date.format(ISO_FORMAT);
    }

    public static String formatMonthYear(LocalDate date) {
        if (date == null) return "";
        return date.format(MONTH_YEAR_FORMAT);
    }

    public static String formatFull(LocalDate date) {
        if (date == null) return "";
        return date.format(FULL_FORMAT);
    }

    // Parsing methods
    public static LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }

        // Try different formats
        DateTimeFormatter[] formats = {
                ISO_FORMAT,
                DISPLAY_FORMAT,
                SHORT_FORMAT,
                DateTimeFormatter.ofPattern("MM/dd/yyyy"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("yyyy/MM/dd")
        };

        for (DateTimeFormatter format : formats) {
            try {
                return LocalDate.parse(dateString.trim(), format);
            } catch (DateTimeParseException e) {
                // Try next format
            }
        }

        throw new DateTimeParseException("Unable to parse date: " + dateString, dateString, 0);
    }

    public static LocalDate parseDateSafely(String dateString) {
        try {
            return parseDate(dateString);
        } catch (Exception e) {
            return null;
        }
    }

    // Date calculations
    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) return 0;
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    public static long monthsBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) return 0;
        return ChronoUnit.MONTHS.between(startDate, endDate);
    }

    public static long yearsBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) return 0;
        return ChronoUnit.YEARS.between(startDate, endDate);
    }

    // Date range generators
    public static List<LocalDate> getDateRange(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {
            dates.add(current);
            current = current.plusDays(1);
        }

        return dates;
    }

    public static List<YearMonth> getMonthRange(LocalDate startDate, LocalDate endDate) {
        List<YearMonth> months = new ArrayList<>();
        YearMonth start = YearMonth.from(startDate);
        YearMonth end = YearMonth.from(endDate);

        YearMonth current = start;
        while (!current.isAfter(end)) {
            months.add(current);
            current = current.plusMonths(1);
        }

        return months;
    }

    // Period helpers
    public static LocalDate getStartOfMonth(LocalDate date) {
        if (date == null) return null;
        return date.with(TemporalAdjusters.firstDayOfMonth());
    }

    public static LocalDate getEndOfMonth(LocalDate date) {
        if (date == null) return null;
        return date.with(TemporalAdjusters.lastDayOfMonth());
    }

    public static LocalDate getStartOfYear(LocalDate date) {
        if (date == null) return null;
        return date.with(TemporalAdjusters.firstDayOfYear());
    }

    public static LocalDate getEndOfYear(LocalDate date) {
        if (date == null) return null;
        return date.with(TemporalAdjusters.lastDayOfYear());
    }

    public static LocalDate getStartOfWeek(LocalDate date) {
        if (date == null) return null;
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    public static LocalDate getEndOfWeek(LocalDate date) {
        if (date == null) return null;
        return date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
    }

    // Predefined periods
    public static DateRange getCurrentMonth() {
        LocalDate today = LocalDate.now();
        return new DateRange(getStartOfMonth(today), getEndOfMonth(today));
    }

    public static DateRange getCurrentYear() {
        LocalDate today = LocalDate.now();
        return new DateRange(getStartOfYear(today), getEndOfYear(today));
    }

    public static DateRange getCurrentWeek() {
        LocalDate today = LocalDate.now();
        return new DateRange(getStartOfWeek(today), getEndOfWeek(today));
    }

    public static DateRange getLastMonth() {
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        return new DateRange(getStartOfMonth(lastMonth), getEndOfMonth(lastMonth));
    }

    public static DateRange getLastYear() {
        LocalDate lastYear = LocalDate.now().minusYears(1);
        return new DateRange(getStartOfYear(lastYear), getEndOfYear(lastYear));
    }

    public static DateRange getLastWeek() {
        LocalDate lastWeek = LocalDate.now().minusWeeks(1);
        return new DateRange(getStartOfWeek(lastWeek), getEndOfWeek(lastWeek));
    }

    public static DateRange getLast30Days() {
        LocalDate today = LocalDate.now();
        return new DateRange(today.minusDays(30), today);
    }

    public static DateRange getLast90Days() {
        LocalDate today = LocalDate.now();
        return new DateRange(today.minusDays(90), today);
    }

    public static DateRange getYearToDate() {
        LocalDate today = LocalDate.now();
        return new DateRange(getStartOfYear(today), today);
    }

    // Date validation
    public static boolean isValidDate(String dateString) {
        return parseDateSafely(dateString) != null;
    }

    public static boolean isDateInRange(LocalDate date, LocalDate startDate, LocalDate endDate) {
        if (date == null || startDate == null || endDate == null) {
            return false;
        }
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    public static boolean isWeekend(LocalDate date) {
        if (date == null) return false;
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    public static boolean isToday(LocalDate date) {
        return LocalDate.now().equals(date);
    }

    public static boolean isThisMonth(LocalDate date) {
        if (date == null) return false;
        YearMonth thisMonth = YearMonth.now();
        return YearMonth.from(date).equals(thisMonth);
    }

    public static boolean isThisYear(LocalDate date) {
        if (date == null) return false;
        return date.getYear() == LocalDate.now().getYear();
    }

    // Relative date descriptions
    public static String getRelativeDescription(LocalDate date) {
        if (date == null) return "Unknown";

        LocalDate today = LocalDate.now();
        long daysDiff = ChronoUnit.DAYS.between(today, date);

        if (daysDiff == 0) {
            return "Today";
        } else if (daysDiff == 1) {
            return "Tomorrow";
        } else if (daysDiff == -1) {
            return "Yesterday";
        } else if (daysDiff > 1 && daysDiff <= 7) {
            return "In " + daysDiff + " days";
        } else if (daysDiff < -1 && daysDiff >= -7) {
            return Math.abs(daysDiff) + " days ago";
        } else if (daysDiff > 7 && daysDiff <= 30) {
            long weeks = daysDiff / 7;
            return "In " + weeks + " week" + (weeks > 1 ? "s" : "");
        } else if (daysDiff < -7 && daysDiff >= -30) {
            long weeks = Math.abs(daysDiff) / 7;
            return weeks + " week" + (weeks > 1 ? "s" : "") + " ago";
        } else if (isThisYear(date)) {
            return formatForDisplay(date);
        } else {
            return formatForDisplay(date);
        }
    }

    // Age calculations
    public static int calculateAge(LocalDate birthDate) {
        if (birthDate == null) return 0;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public static String formatDuration(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) return "";

        Period period = Period.between(startDate, endDate);
        StringBuilder sb = new StringBuilder();

        if (period.getYears() > 0) {
            sb.append(period.getYears()).append(" year").append(period.getYears() > 1 ? "s" : "");
        }

        if (period.getMonths() > 0) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(period.getMonths()).append(" month").append(period.getMonths() > 1 ? "s" : "");
        }

        if (period.getDays() > 0) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(period.getDays()).append(" day").append(period.getDays() > 1 ? "s" : "");
        }

        return sb.length() > 0 ? sb.toString() : "Same day";
    }

    // Quarter operations
    public static int getQuarter(LocalDate date) {
        if (date == null) return 0;
        return (date.getMonthValue() - 1) / 3 + 1;
    }

    public static DateRange getQuarter(int year, int quarter) {
        if (quarter < 1 || quarter > 4) {
            throw new IllegalArgumentException("Quarter must be between 1 and 4");
        }

        int startMonth = (quarter - 1) * 3 + 1;
        LocalDate startDate = LocalDate.of(year, startMonth, 1);
        LocalDate endDate = startDate.plusMonths(3).minusDays(1);

        return new DateRange(startDate, endDate);
    }

    public static DateRange getCurrentQuarter() {
        LocalDate today = LocalDate.now();
        return getQuarter(today.getYear(), getQuarter(today));
    }

    // Fiscal year operations (assuming April-March fiscal year)
    public static int getFiscalYear(LocalDate date) {
        if (date == null) return 0;
        return date.getMonthValue() >= 4 ? date.getYear() : date.getYear() - 1;
    }

    public static DateRange getFiscalYear(int fiscalYear) {
        LocalDate startDate = LocalDate.of(fiscalYear, 4, 1);
        LocalDate endDate = LocalDate.of(fiscalYear + 1, 3, 31);
        return new DateRange(startDate, endDate);
    }

    public static DateRange getCurrentFiscalYear() {
        return getFiscalYear(getFiscalYear(LocalDate.now()));
    }

    // Date range utility class
    public static class DateRange {
        private final LocalDate startDate;
        private final LocalDate endDate;

        public DateRange(LocalDate startDate, LocalDate endDate) {
            if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
                throw new IllegalArgumentException("Start date cannot be after end date");
            }
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public LocalDate getStartDate() { return startDate; }
        public LocalDate getEndDate() { return endDate; }

        public boolean contains(LocalDate date) {
            return isDateInRange(date, startDate, endDate);
        }

        public long getDaysCount() {
            return daysBetween(startDate, endDate) + 1;
        }

        public boolean overlaps(DateRange other) {
            if (other == null) return false;
            return startDate.isBefore(other.endDate.plusDays(1)) &&
                    endDate.isAfter(other.startDate.minusDays(1));
        }

        @Override
        public String toString() {
            return formatForDisplay(startDate) + " - " + formatForDisplay(endDate);
        }

        public String toShortString() {
            return formatShort(startDate) + " - " + formatShort(endDate);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            DateRange dateRange = (DateRange) obj;
            return startDate.equals(dateRange.startDate) && endDate.equals(dateRange.endDate);
        }

        @Override
        public int hashCode() {
            return startDate.hashCode() * 31 + endDate.hashCode();
        }
    }

    // Common date range presets
    public enum DateRangePreset {
        TODAY("Today"),
        YESTERDAY("Yesterday"),
        THIS_WEEK("This Week"),
        LAST_WEEK("Last Week"),
        THIS_MONTH("This Month"),
        LAST_MONTH("Last Month"),
        THIS_QUARTER("This Quarter"),
        THIS_YEAR("This Year"),
        LAST_YEAR("Last Year"),
        LAST_30_DAYS("Last 30 Days"),
        LAST_90_DAYS("Last 90 Days"),
        YEAR_TO_DATE("Year to Date"),
        CURRENT_FISCAL_YEAR("Current Fiscal Year"),
        CUSTOM("Custom Range");

        private final String displayName;

        DateRangePreset(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public DateRange getDateRange() {
            LocalDate today = LocalDate.now();

            switch (this) {
                case TODAY:
                    return new DateRange(today, today);
                case YESTERDAY:
                    LocalDate yesterday = today.minusDays(1);
                    return new DateRange(yesterday, yesterday);
                case THIS_WEEK:
                    return getCurrentWeek();
                case LAST_WEEK:
                    return getLastWeek();
                case THIS_MONTH:
                    return getCurrentMonth();
                case LAST_MONTH:
                    return getLastMonth();
                case THIS_QUARTER:
                    return getCurrentQuarter();
                case THIS_YEAR:
                    return getCurrentYear();
                case LAST_YEAR:
                    return getLastYear();
                case LAST_30_DAYS:
                    return getLast30Days();
                case LAST_90_DAYS:
                    return getLast90Days();
                case YEAR_TO_DATE:
                    return getYearToDate();
                case CURRENT_FISCAL_YEAR:
                    return getCurrentFiscalYear();
                default:
                    return null; // Custom range
            }
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}
