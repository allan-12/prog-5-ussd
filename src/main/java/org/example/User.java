package org.example;

public class User {
    private final String phoneNumber;
    private double balance;
    private double savings;
    private String pin;

    User(String phoneNumber, double balance, String pin) {
        this.phoneNumber = phoneNumber;
        this.balance = balance;
        this.savings = 0;
        this.pin = pin;
    }

    String getPhoneNumber() {
        return phoneNumber;
    }

    double getBalance() {
        return balance;
    }

    void setBalance(double balance) {
        this.balance = balance;
    }

    double getSavings() {
        return savings;
    }

    void setSavings(double savings) {
        this.savings = savings;
    }

    boolean verifyPin(String pin) {
        return this.pin.equals(pin);
    }

    void changePin(String newPin) {
        this.pin = newPin;
    }
}
