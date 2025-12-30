package com.group.avenue.middle.atm.project.model;

import com.group.avenue.middle.atm.project.exception.CassetteException;
import com.group.avenue.middle.atm.project.exception.InvalidAmountException;

/**
 * Ячейка банкомата для хранения банкнот одного номинала.
 *
 * @author vladimir_shi
 * @since 29.12.2025
 */
public class Cassette {
    private final Banknote banknote; // номинал банкнот в ячейке
    private final int capacity; // максимальное количество банкнот
    private int count; // текущее количество банкнот

    public Cassette(Banknote banknote, int capacity) {
        this(banknote, capacity, 0);
    }

    public Cassette(Banknote banknote, int capacity, int initialCount) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Вместимость должна быть положительной");
        }
        if (initialCount < 0 || initialCount > capacity) {
            throw new IllegalArgumentException("Некорректное начальное количество");
        }

        this.banknote = banknote;
        this.capacity = capacity;
        this.count = initialCount;
    }

    // --- Основные операции ---

    /**
     * Добавить банкноты в ячейку
     * @param quantity количество для добавления
     * @return фактически добавленное количество
     * @throws CassetteException если превышена вместимость
     */
    public int deposit(int quantity) throws CassetteException {
        if (quantity < 0) {
            throw new InvalidAmountException("Количество не может быть отрицательным");
        }

        int availableSpace = capacity - count;
        if (quantity > availableSpace)
            throw new CassetteException("""
                    Невозможно добавить %d банкнот. Доступно места только для %d""".formatted(quantity, availableSpace));

        count += quantity;
        return quantity;
    }

    /**
     * Изъять банкноты из ячейки
     *
     * @param quantity запрашиваемое количество
     */
    public void withdraw(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Количество не может быть отрицательным");
        }

        int actualWithdrawn = Math.min(quantity, count);
        count -= actualWithdrawn;
    }

    // --- Геттеры и проверки ---

    public Banknote getBanknote() {
        return banknote;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getCount() {
        return count;
    }

    /**
     * Получить текущую сумму в ячейке (в рублях)
     */
    public int getCurrentAmount() {
        return count * banknote.getValue();
    }

    public boolean isEmpty() {
        return count == 0;
    }

    @Override
    public String toString() {
        return String.format("Ячейка: %d руб. (текущее: %d, макс: %d, сумма: %d руб.)",
                banknote.getValue(), count, capacity, getCurrentAmount());
    }
}
