package com.group.avenue.middle.atm.project.model;

import com.group.avenue.middle.atm.project.exception.InsufficientFundsException;
import com.group.avenue.middle.atm.project.exception.InvalidAmountException;

/**
 * @author vladimir_shi
 * @since 29.12.2025
 *
 * Пользователь банкомата с лимитом средств
 */
public class User {
    private final String name;
    private final String cardNumber;
    private int balance; // баланс в рублях
    private final int dailyLimit; // дневной лимит снятия
    private int withdrawnToday; // уже снято сегодня

    public User(String name, String cardNumber, int initialBalance, int dailyLimit) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Баланс не может быть отрицательным");
        }
        if (dailyLimit <= 0) {
            throw new IllegalArgumentException("Лимит должен быть положительным");
        }

        this.name = name;
        this.cardNumber = cardNumber;
        this.balance = initialBalance;
        this.dailyLimit = dailyLimit;
        this.withdrawnToday = 0;
    }

    public boolean canWithdraw(int amount) {
        return amount > 0 &&
                amount <= balance &&
                (withdrawnToday + amount) <= dailyLimit;
    }

    public void withdraw(int amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Сумма снятия должна быть положительной");
        }

        if (amount > balance) {
            throw new InsufficientFundsException("Недостаточно средств на счете");
        }

        if ((withdrawnToday + amount) > dailyLimit) {
            // Можно создать новое исключение или использовать существующее
            throw new InvalidAmountException("Превышен дневной лимит снятия");
        }
        balance -= amount;
        withdrawnToday += amount;
    }

    public void deposit(int amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Сумма пополнения должна быть положительной");
        }
        balance += amount;
    }

    public void resetDailyLimit() {
        withdrawnToday = 0;
    }

    // --- Геттеры ---

    public String getName() {
        return name;
    }

    public int getBalance() {
        return balance;
    }

    public int getDailyLimit() {
        return dailyLimit;
    }

    public int getWithdrawnToday() {
        return withdrawnToday;
    }

    public int getAvailableToday() {
        return dailyLimit - withdrawnToday;
    }

    @Override
    public String toString() {
        return String.format("Пользователь: %s [%s], баланс: %d руб., лимит: %d/%d руб.",
                name, cardNumber, balance, withdrawnToday, dailyLimit);
    }
}
