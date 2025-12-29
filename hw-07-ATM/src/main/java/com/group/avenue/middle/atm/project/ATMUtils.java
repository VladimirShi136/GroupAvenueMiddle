package com.group.avenue.middle.atm.project;

import com.group.avenue.middle.atm.project.model.Banknote;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

/**
 * @author vladimir_shi
 * @since 29.12.2025
 *
 * Утилиты для форматирования вывода
 */
public class ATMUtils {
    private static final NumberFormat RUB_FORMAT;

    static {
        RUB_FORMAT = NumberFormat.getNumberInstance(new Locale("ru", "RU"));
        RUB_FORMAT.setMinimumFractionDigits(0);
        RUB_FORMAT.setMaximumFractionDigits(0);
    }

    /**
     * Форматирует сумму в рублях с разделителями тысяч
     */
    public static String formatAmount(int amount) {
        return RUB_FORMAT.format(amount) + " руб.";
    }

    /**
     * Форматирует список банкнот в читаемый вид
     */
    public static String formatBanknotes(Map<Banknote, Integer> banknotes) {
        if (banknotes == null || banknotes.isEmpty()) {
            return "нет банкнот";
        }

        StringBuilder sb = new StringBuilder();
        int total = 0;
        int count = 0;

        for (Map.Entry<Banknote, Integer> entry : banknotes.entrySet()) {
            if (entry.getValue() > 0) {
                int value = entry.getKey().getValue() * entry.getValue();
                total += value;
                count += entry.getValue();

                sb.append("\n  ")
                        .append(entry.getKey().getValue())
                        .append(" руб. × ")
                        .append(entry.getValue())
                        .append(" = ")
                        .append(formatAmount(value));
            }
        }

        if (count > 0) {
            sb.append("\nИтого: ")
                    .append(count)
                    .append(" банкнот на сумму ")
                    .append(formatAmount(total));
        } else {
            sb.append("\n(все ячейки переполнены)");
        }

        return sb.toString();
    }

    /**
     * Форматирует банкноты в компактном виде для результата выдачи
     */
    public static String formatBanknotesCompact(Map<Banknote, Integer> banknotes) {
        if (banknotes == null || banknotes.isEmpty()) {
            return "нет";
        }

        StringBuilder sb = new StringBuilder();
        boolean first = true;

        for (Map.Entry<Banknote, Integer> entry : banknotes.entrySet()) {
            if (entry.getValue() > 0) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(entry.getKey().getValue())
                        .append(" руб. × ")
                        .append(entry.getValue());
                first = false;
            }
        }

        return sb.toString();
    }

    /**
     * Возвращает сообщение об ошибке для некорректной суммы
     */
    public static String getAmountErrorMessage(int amount, int minBanknoteValue) {
        if (amount <= 0) {
            return "Сумма должна быть положительной";
        }
        if (amount < minBanknoteValue) {
            return "Минимальная сумма: " + formatAmount(minBanknoteValue);
        }
        if (amount % minBanknoteValue != 0) {
            return "Сумма должна быть кратна " + formatAmount(minBanknoteValue);
        }
        return null; // нет ошибок
    }
}