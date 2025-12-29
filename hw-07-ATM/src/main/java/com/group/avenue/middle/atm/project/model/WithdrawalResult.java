package com.group.avenue.middle.atm.project.model;

import com.group.avenue.middle.atm.project.ATMUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author vladimir_shi
 * @since 29.12.2025
 *
 * Результат операции выдачи наличных
 */
public class WithdrawalResult {
    private final boolean success;
    private final Map<Banknote, Integer> banknotes;
    private final String errorMessage;
    private final int requestedAmount;
    private final int actualAmount;

    // Успешный результат
    public WithdrawalResult(int requestedAmount, Map<Banknote, Integer> banknotes) {
        this.success = true;
        this.requestedAmount = requestedAmount;
        this.banknotes = new HashMap<>(banknotes);
        this.errorMessage = null;

        // Вычисляем фактически выданную сумму
        this.actualAmount = banknotes.entrySet().stream()
                .mapToInt(entry -> entry.getKey().getValue() * entry.getValue())
                .sum();
    }

    // Неуспешный результат
    public WithdrawalResult(int requestedAmount, String errorMessage) {
        this.success = false;
        this.requestedAmount = requestedAmount;
        this.banknotes = Collections.emptyMap();
        this.errorMessage = errorMessage;
        this.actualAmount = 0;
    }

    // --- Геттеры ---

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getActualAmount() {
        return actualAmount;
    }

    /**
     * Красивое форматирование выданных банкнот
     */
    public String formatBanknotes() {
        return ATMUtils.formatBanknotesCompact(banknotes);
    }

    @Override
    public String toString() {
        if (success) {
            return String.format("Выдано %s: %s",
                    ATMUtils.formatAmount(actualAmount),
                    formatBanknotes());
        } else {
            return String.format("Ошибка выдачи %s: %s",
                    ATMUtils.formatAmount(requestedAmount),
                    errorMessage);
        }
    }
}