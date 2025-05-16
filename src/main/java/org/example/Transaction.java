package org.example;

public class Transaction {
    static boolean transferMoney(User sender, User receiver, double amount, String pin) {
        if (!sender.verifyPin(pin) || amount <= 0 || sender.getBalance() < amount) {
            return false;
        }
        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);
        return true;
    }

    static boolean withdrawMoney(User user, double amount, String pin) {
        if (!user.verifyPin(pin) || amount <= 0 || user.getBalance() < amount) {
            return false;
        }
        user.setBalance(user.getBalance() - amount);
        return true;
    }

    static boolean buyCredit(User user, double amount, String phoneNumber, String pin) {
        if (!user.verifyPin(pin) || amount <= 0 || user.getBalance() < amount) {
            return false;
        }
        user.setBalance(user.getBalance() - amount);
        return true;
    }

    static boolean payBill(User user, String billType, double amount, String pin) {
        if (!user.verifyPin(pin) || amount <= 0 || user.getBalance() < amount) {
            return false;
        }
        user.setBalance(user.getBalance() - amount);
        return true;
    }

    static boolean saveMoney(User user, double amount, String pin) {
        if (!user.verifyPin(pin) || amount <= 0 || user.getBalance() < amount) {
            return false;
        }
        user.setBalance(user.getBalance() - amount);
        user.setSavings(user.getSavings() + amount);
        return true;
    }

    static boolean borrowMoney(User user, double amount, String pin) {
        if (!user.verifyPin(pin) || amount <= 0) {
            return false;
        }
        user.setBalance(user.getBalance() + amount);
        return true;
    }
}
