package main.java.com.moneymind.utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;

/**
 * Utility class for input validation and data integrity checks
 */
public class ValidationUtils {

    // Regular expressions for common validations
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[+]?[0-9]{10,15}$"
    );

    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9\\s]+$"
    );

    private static final Pattern CURRENCY_CODE_PATTERN = Pattern.compile(
            "^[A-Z]{3}$"
    );

    // Prevent instantiation
    private ValidationUtils() {}

    // Basic string validations
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean hasMinLength(String str, int minLength) {
        return str != null && str.length() >= minLength;
    }

    public static boolean hasMaxLength(String str, int maxLength) {
        return str == null || str.length() <= maxLength;
    }

    public static boolean isLengthBetween(String str, int minLength, int maxLength) {
        return hasMinLength(str, minLength) && hasMaxLength(str, maxLength);
    }

    // Numeric validations
    public static boolean isValidAmount(String amountStr) {
        if (isEmpty(amountStr)) return false;

        try {
            BigDecimal amount = CurrencyUtils.parse(amountStr);
            return amount.compareTo(BigDecimal.ZERO) >= 0 && amount.scale() <= 2;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isValidAmount(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) >= 0 && amount.scale() <= 2;
    }

    public static boolean isPositiveAmount(String amountStr) {
        if (isEmpty(amountStr)) return false;

        try {
            BigDecimal amount = CurrencyUtils.parse(amountStr);
            return amount.compareTo(BigDecimal.ZERO) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isPositiveAmount(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public static boolean isAmountInRange(BigDecimal amount, BigDecimal min, BigDecimal max) {
        if (amount == null) return false;
        if (min != null && amount.compareTo(min) < 0) return false;
        if (max != null && amount.compareTo(max) > 0) return false;
        return true;
    }

    // Date validations
    public static boolean isValidDate(String dateStr) {
        return DateUtils.isValidDate(dateStr);
    }

    public static boolean isValidDate(LocalDate date) {
        return date != null;
    }

    public static boolean isDateInPast(LocalDate date) {
        return date != null && date.isBefore(LocalDate.now());
    }

    public static boolean isDateInFuture(LocalDate date) {
        return date != null && date.isAfter(LocalDate.now());
    }

    public static boolean isDateInRange(LocalDate date, LocalDate startDate, LocalDate endDate) {
        return DateUtils.isDateInRange(date, startDate, endDate);
    }

    public static boolean isValidDateRange(LocalDate startDate, LocalDate endDate) {
        return startDate != null && endDate != null && !startDate.isAfter(endDate);
    }

    // Email validation
    public static boolean isValidEmail(String email) {
        return isNotEmpty(email) && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    // Phone validation
    public static boolean isValidPhone(String phone) {
        if (isEmpty(phone)) return false;

        String cleanPhone = phone.replaceAll("[\\s()-]", "");
        return PHONE_PATTERN.matcher(cleanPhone).matches();
    }

    // Category name validation
    public static boolean isValidCategoryName(String categoryName) {
        return isNotEmpty(categoryName) &&
                isLengthBetween(categoryName.trim(), 1, 100) &&
                !categoryName.trim().contains("/") &&
                !categoryName.trim().contains("\\");
    }

    // Transaction description validation
    public static boolean isValidTransactionDescription(String description) {
        return isNotEmpty(description) &&
                isLengthBetween(description.trim(), 1, 255);
    }

    // Budget validation
    public static boolean isValidBudgetAmount(BigDecimal amount) {
        return isValidAmount(amount) && isPositiveAmount(amount);
    }

    public static boolean isValidBudgetPeriod(LocalDate startDate, LocalDate endDate) {
        if (!isValidDateRange(startDate, endDate)) {
            return false;
        }

        // Check if period is not too long (max 5 years)
        long daysBetween = DateUtils.daysBetween(startDate, endDate);
        return daysBetween <= 365 * 5;
    }

    // Currency validation
    public static boolean isValidCurrencyCode(String currencyCode) {
        return isNotEmpty(currencyCode) &&
                CURRENCY_CODE_PATTERN.matcher(currencyCode).matches() &&
                CurrencyUtils.isValidCurrencyCode(currencyCode);
    }

    // Alphanumeric validation
    public static boolean isAlphanumeric(String str) {
        return isNotEmpty(str) && ALPHANUMERIC_PATTERN.matcher(str).matches();
    }

    // Password validation
    public static boolean isValidPassword(String password) {
        return isNotEmpty(password) && hasMinLength(password, 8);
    }

    public static boolean isStrongPassword(String password) {
        if (!isValidPassword(password)) return false;

        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasLowercase = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*[0-9].*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");

        return hasUppercase && hasLowercase && hasDigit && hasSpecial;
    }

    // Validation result classes
    public static class ValidationResult {
        private boolean valid;
        private List<String> errors;

        public ValidationResult() {
            this.valid = true;
            this.errors = new ArrayList<>();
        }

        public ValidationResult(boolean valid) {
            this();
            this.valid = valid;
        }

        public ValidationResult(String error) {
            this();
            this.valid = false;
            this.errors.add(error);
        }

        public boolean isValid() { return valid && errors.isEmpty(); }
        public List<String> getErrors() { return errors; }

        public void addError(String error) {
            this.valid = false;
            this.errors.add(error);
        }

        public void addErrors(List<String> errors) {
            this.valid = false;
            this.errors.addAll(errors);
        }

        public String getFirstError() {
            return errors.isEmpty() ? "" : errors.get(0);
        }

        public String getAllErrors() {
            return String.join(", ", errors);
        }

        public static ValidationResult success() {
            return new ValidationResult(true);
        }

        public static ValidationResult error(String message) {
            return new ValidationResult(message);
        }
    }

    // Comprehensive validation methods
    public static ValidationResult validateTransaction(String description, String amountStr,
                                                       LocalDate date, Long categoryId) {
        ValidationResult result = new ValidationResult();

        // Validate description
        if (!isValidTransactionDescription(description)) {
            result.addError("Description must be between 1 and 255 characters");
        }

        // Validate amount
        if (!isValidAmount(amountStr)) {
            result.addError("Amount must be a valid positive number");
        } else {
            try {
                BigDecimal amount = CurrencyUtils.parse(amountStr);
                if (amount.compareTo(BigDecimal.valueOf(1000000)) > 0) {
                    result.addError("Amount cannot exceed 1,000,000");
                }
            } catch (Exception e) {
                result.addError("Invalid amount format");
            }
        }

        // Validate date
        if (!isValidDate(date)) {
            result.addError("Invalid transaction date");
        } else {
            LocalDate maxFutureDate = LocalDate.now().plusYears(1);
            LocalDate minPastDate = LocalDate.now().minusYears(10);

            if (date.isAfter(maxFutureDate)) {
                result.addError("Transaction date cannot be more than 1 year in the future");
            }

            if (date.isBefore(minPastDate)) {
                result.addError("Transaction date cannot be more than 10 years in the past");
            }
        }

        // Validate category
        if (categoryId == null || categoryId <= 0) {
            result.addError("Please select a valid category");
        }

        return result;
    }

    public static ValidationResult validateCategory(String name, String type, Long parentId) {
        ValidationResult result = new ValidationResult();

        // Validate name
        if (!isValidCategoryName(name)) {
            result.addError("Category name must be between 1 and 100 characters and cannot contain / or \\");
        }

        // Validate type
        if (isEmpty(type)) {
            result.addError("Category type is required");
        } else {
            try {
                main.java.com.moneymind.model.Category.CategoryType.valueOf(type);
            } catch (IllegalArgumentException e) {
                result.addError("Invalid category type");
            }
        }

        // Parent ID validation (if provided)
        if (parentId != null && parentId <= 0) {
            result.addError("Invalid parent category");
        }

        return result;
    }

    public static ValidationResult validateBudget(BigDecimal amount, LocalDate startDate,
                                                  LocalDate endDate, Long categoryId) {
        ValidationResult result = new ValidationResult();

        // Validate amount
        if (!isValidBudgetAmount(amount)) {
            result.addError("Budget amount must be a positive number");
        } else if (amount.compareTo(BigDecimal.valueOf(10000000)) > 0) {
            result.addError("Budget amount cannot exceed 10,000,000");
        }

        // Validate dates
        if (!isValidBudgetPeriod(startDate, endDate)) {
            result.addError("Invalid budget period");
        } else {
            // Check if start date is not too far in the past
            LocalDate minStartDate = LocalDate.now().minusYears(1);
            if (startDate.isBefore(minStartDate)) {
                result.addError("Budget start date cannot be more than 1 year in the past");
            }

            // Check if end date is not too far in the future
            LocalDate maxEndDate = LocalDate.now().plusYears(5);
            if (endDate.isAfter(maxEndDate)) {
                result.addError("Budget end date cannot be more than 5 years in the future");
            }
        }

        // Validate category
        if (categoryId == null || categoryId <= 0) {
            result.addError("Please select a valid category");
        }

        return result;
    }

    public static ValidationResult validateUser(String name, String email) {
        ValidationResult result = new ValidationResult();

        // Validate name
        if (!isNotEmpty(name)) {
            result.addError("Name is required");
        } else if (!isLengthBetween(name, 2, 100)) {
            result.addError("Name must be between 2 and 100 characters");
        }

        // Validate email
        if (!isValidEmail(email)) {
            result.addError("Please enter a valid email address");
        }

        return result;
    }

    // Sanitization methods
    public static String sanitizeString(String input) {
        if (input == null) return "";

        return input.trim()
                .replaceAll("[<>\"']", "")  // Remove potentially dangerous characters
                .replaceAll("\\s+", " ");    // Normalize whitespace
    }

    public static String sanitizeDescription(String description) {
        if (isEmpty(description)) return "";

        String sanitized = sanitizeString(description);

        // Limit length
        if (sanitized.length() > 255) {
            sanitized = sanitized.substring(0, 255);
        }

        return sanitized;
    }

    public static String sanitizeCategoryName(String categoryName) {
        if (isEmpty(categoryName)) return "";

        String sanitized = sanitizeString(categoryName)
                .replaceAll("[/\\\\]", "");  // Remove path separators

        // Limit length
        if (sanitized.length() > 100) {
            sanitized = sanitized.substring(0, 100);
        }

        return sanitized;
    }

    // Input format helpers
    public static boolean isNumeric(String str) {
        if (isEmpty(str)) return false;

        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isInteger(String str) {
        if (isEmpty(str)) return false;

        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isLong(String str) {
        if (isEmpty(str)) return false;

        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Business logic validations
    public static ValidationResult validateTransactionUpdate(String description, String amountStr,
                                                             LocalDate date, Long categoryId, Long transactionId) {
        ValidationResult result = validateTransaction(description, amountStr, date, categoryId);

        // Additional validation for updates
        if (transactionId == null || transactionId <= 0) {
            result.addError("Invalid transaction ID for update");
        }

        return result;
    }

    public static ValidationResult validateBudgetOverlap(LocalDate startDate, LocalDate endDate,
                                                         Long categoryId, Long excludeBudgetId) {
        ValidationResult result = new ValidationResult();

        // This would typically check against the database for overlapping budgets
        // For now, we'll just validate the basic constraints

        if (!isValidBudgetPeriod(startDate, endDate)) {
            result.addError("Invalid budget period");
        }

        if (categoryId == null || categoryId <= 0) {
            result.addError("Invalid category for budget");
        }

        // Note: In a real implementation, you would check the database here
        // for existing budgets in the same category and time period

        return result;
    }

    // Utility methods for form validation
    public static String getValidationMessage(String fieldName, String value, String validationType) {
        switch (validationType) {
            case "required":
                return isEmpty(value) ? fieldName + " is required" : null;
            case "email":
                return !isValidEmail(value) ? "Please enter a valid email address" : null;
            case "amount":
                return !isValidAmount(value) ? "Please enter a valid amount" : null;
            case "positive_amount":
                return !isPositiveAmount(value) ? fieldName + " must be greater than zero" : null;
            case "date":
                return !isValidDate(value) ? "Please enter a valid date" : null;
            case "phone":
                return !isValidPhone(value) ? "Please enter a valid phone number" : null;
            default:
                return null;
        }
    }

    // Quick validation methods for UI
    public static boolean isValidForSave(String... requiredFields) {
        for (String field : requiredFields) {
            if (isEmpty(field)) {
                return false;
            }
        }
        return true;
    }

    public static List<String> getEmptyFields(String... fieldPairs) {
        List<String> emptyFields = new ArrayList<>();

        for (int i = 0; i < fieldPairs.length; i += 2) {
            if (i + 1 < fieldPairs.length) {
                String fieldName = fieldPairs[i];
                String fieldValue = fieldPairs[i + 1];

                if (isEmpty(fieldValue)) {
                    emptyFields.add(fieldName);
                }
            }
        }

        return emptyFields;
    }
}
