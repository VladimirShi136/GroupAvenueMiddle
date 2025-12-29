package com.group.avenue.middle.atm.project;

import com.group.avenue.middle.atm.project.exception.CassetteException;
import com.group.avenue.middle.atm.project.exception.InsufficientFundsException;
import com.group.avenue.middle.atm.project.exception.InvalidAmountException;
import com.group.avenue.middle.atm.project.exception.UnsupportedBanknoteException;
import com.group.avenue.middle.atm.project.model.Banknote;
import com.group.avenue.middle.atm.project.model.Cassette;
import com.group.avenue.middle.atm.project.model.WithdrawalResult;

import java.util.*;

/**
 * @author vladimir_shi
 * @since 29.12.2025
 *
 * Основной класс банкомата
 */
public class ATM {
    private final String id;
    private final Map<Banknote, Cassette> cassettes;
    private final int maxWithdrawalAmount; // максимум за одну операцию
    private final int minBanknoteValue; // минимальный номинал

    public ATM(String id, int maxWithdrawalAmount) {
        this.id = id;
        this.cassettes = new LinkedHashMap<>(); // сохраняем порядок (от крупных к мелким)
        this.maxWithdrawalAmount = maxWithdrawalAmount;
        this.minBanknoteValue = Banknote.RUB_50.getValue();

        // Инициализируем ячейки для всех номиналов (пустые)
        initializeCassettes();
    }

    private void initializeCassettes() {
        // Создаем ячейки с разной вместимостью в зависимости от номинала
        Map<Banknote, Integer> capacities = Map.of(
                Banknote.RUB_50, 100,
                Banknote.RUB_100, 100,
                Banknote.RUB_200, 50,
                Banknote.RUB_500, 50,
                Banknote.RUB_1000, 30,
                Banknote.RUB_2000, 20,
                Banknote.RUB_5000, 10
        );

        // Сортируем номиналы по убыванию (для удобства выдачи)
        List<Banknote> sortedNotes = Arrays.asList(Banknote.values());
        sortedNotes.sort((b1, b2) -> Integer.compare(b2.getValue(), b1.getValue()));

        for (Banknote banknote : sortedNotes) {
            cassettes.put(banknote, new Cassette(banknote, capacities.get(banknote)));
        }
    }

    // --- Основные методы ---

    /**
     * Внести наличные
     * @param banknotes мапа: номинал -> количество
     * @return мапа принятых банкнот (номинал -> количество)
     */
    public Map<Banknote, Integer> deposit(Map<Banknote, Integer> banknotes) {
        if (banknotes == null || banknotes.isEmpty()) {
            throw new IllegalArgumentException("Пустой список банкнот");
        }

        Map<Banknote, Integer> accepted = new HashMap<>();

        for (Map.Entry<Banknote, Integer> entry : banknotes.entrySet()) {
            Banknote banknote = entry.getKey();
            int quantity = entry.getValue();

            if (quantity <= 0) {
                // Используем InvalidAmountException
                throw new InvalidAmountException(
                        "Количество банкнот " + banknote.getValue() + " руб. должно быть положительным");
            }

            Cassette cassette = cassettes.get(banknote);
            if (cassette == null) {
                // Используем UnsupportedBanknoteException
                throw new UnsupportedBanknoteException(
                        "Банкомат не поддерживает номинал: " + banknote.getValue() + " руб.");
            }

            try {
                int actuallyAdded = cassette.deposit(quantity);
                accepted.put(banknote, actuallyAdded);

            } catch (CassetteException e) {
                // CassetteException уже наше кастомное исключение
                System.err.println("Ошибка при внесении " + banknote.getValue() + " руб.: " + e.getMessage());
            }
        }

        return accepted;
    }

    /**
     * Снять наличные (простейший алгоритм без оптимизации)
     * @param amount запрашиваемая сумма в рублях
     * @return результат операции
     */
    public WithdrawalResult withdraw(int amount) {
        // Валидация суммы
        if (amount <= 0) {
            // Вместо return new WithdrawalResult(amount, "...")
            throw new InvalidAmountException("Сумма должна быть положительной");
        }

        if (amount < minBanknoteValue) {
            throw new InvalidAmountException(
                    "Минимальная сумма для выдачи: " + minBanknoteValue + " руб.");
        }

        if (amount % minBanknoteValue != 0) {
            throw new InvalidAmountException(
                    "Сумма должна быть кратна " + minBanknoteValue + " руб.");
        }

        if (amount > maxWithdrawalAmount) {
            throw new InvalidAmountException(
                    "Превышен лимит за одну операцию. Максимум: " + maxWithdrawalAmount + " руб.");
        }

        if (amount > getBalance()) {
            throw new InsufficientFundsException("В банкомате недостаточно средств");
        }

        // Пытаемся выдать сумму
        Map<Banknote, Integer> resultBanknotes = new HashMap<>();
        int remainingAmount = amount;

        // Идем от крупных номиналов к мелким
        for (Banknote banknote : cassettes.keySet()) {
            Cassette cassette = cassettes.get(banknote);
            int noteValue = banknote.getValue();

            if (noteValue <= remainingAmount && !cassette.isEmpty()) {
                int maxNotesFromCassette = Math.min(
                        remainingAmount / noteValue,
                        cassette.getCount()
                );

                if (maxNotesFromCassette > 0) {
                    resultBanknotes.put(banknote, maxNotesFromCassette);
                    remainingAmount -= maxNotesFromCassette * noteValue;
                }
            }

            if (remainingAmount == 0) {
                break;
            }
        }

        // Проверяем, удалось ли набрать сумму
        if (remainingAmount > 0) {
            return new WithdrawalResult(amount,
                    "Невозможно выдать запрошенную сумму имеющимися банкнотами");
        }

        // Фактически списываем банкноты из ячеек
        for (Map.Entry<Banknote, Integer> entry : resultBanknotes.entrySet()) {
            Cassette cassette = cassettes.get(entry.getKey());
            cassette.withdraw(entry.getValue());
        }

        return new WithdrawalResult(amount, resultBanknotes);
    }

    /**
     * Получить общий баланс банкомата
     */
    public int getBalance() {
        return cassettes.values().stream()
                .mapToInt(Cassette::getCurrentAmount)
                .sum();
    }

    /**
     * Получить доступные банкноты (номинал -> количество)
     */
    public Map<Banknote, Integer> getAvailableBanknotes() {
        Map<Banknote, Integer> available = new LinkedHashMap<>();
        for (Map.Entry<Banknote, Cassette> entry : cassettes.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                available.put(entry.getKey(), entry.getValue().getCount());
            }
        }
        return available;
    }

    /**
     * Получить статус банкомата
     */
    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== СТАТУС БАНКОМАТА ").append(id).append(" ===\n");
        sb.append("Общий баланс: ").append(ATMUtils.formatAmount(getBalance())).append("\n");
        sb.append("Доступные банкноты:");

        Map<Banknote, Integer> available = getAvailableBanknotes();
        if (available.isEmpty()) {
            sb.append(" нет доступных банкнот\n");
        } else {
            sb.append("\n");
            for (Cassette cassette : cassettes.values()) {
                if (!cassette.isEmpty()) {
                    sb.append("  • ")
                            .append(cassette.getBanknote().getValue())
                            .append(" руб. — ")
                            .append(cassette.getCount())
                            .append("/")
                            .append(cassette.getCapacity())
                            .append(" шт. (")
                            .append(ATMUtils.formatAmount(cassette.getCurrentAmount()))
                            .append(")\n");
                }
            }
        }

        sb.append("Максимальная сумма выдачи за операцию: ")
                .append(ATMUtils.formatAmount(maxWithdrawalAmount))
                .append("\n");
        sb.append("Минимальный номинал: ")
                .append(ATMUtils.formatAmount(minBanknoteValue));

        return sb.toString();
    }

    // --- Геттеры ---

    public int getMaxWithdrawalAmount() {
        return maxWithdrawalAmount;
    }

    public int getMinBanknoteValue() {
        return minBanknoteValue;
    }
}