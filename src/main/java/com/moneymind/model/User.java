package main.java.com.moneymind.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * User model class for sharing user preferences and settings
 */

public class User {
    private String name;
    private String email;
    private String currency;
    private LocalDate registrationDate;
    private BigDecimal monthlyIncomeTarget;
    private BigDecimal monthlySavingsTarget;

    // Constructors
    public User() {
        this.currency = "INR";
        this.registrationDate = LocalDate.now();
        this.monthlyIncomeTarget = BigDecimal.ZERO;
        this.monthlySavingsTarget = BigDecimal.ZERO;
    }

    public  User(String name, String email) {
        this();
        this.name = name;
        this.email = email;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }

    public BigDecimal getMonthlyIncomeTarget() { return monthlyIncomeTarget; }
    public void setMonthlyIncomeTarget(BigDecimal monthlyIncomeTarget) {
        this.monthlyIncomeTarget = monthlyIncomeTarget;
    }

    public BigDecimal getMonthlySavingsTarget() { return monthlySavingsTarget; }
    public void setMonthlySavingsTarget(BigDecimal monthlySavingsTarget) {
        this.monthlySavingsTarget = monthlySavingsTarget;
    }

    // Utility methods
    public String getCurrencySymbol() {
        switch (currency) {
            case "INR": return "₹";
            case "USD": return "$";
            case "EUR": return "€";
            case "GBP": return "£";
            case "JPY": return "¥";
            default: return currency;
        }
    }

    @Override
    public String toString() {
        return String.format("User: %s (%s)", name, email);
    }
}
