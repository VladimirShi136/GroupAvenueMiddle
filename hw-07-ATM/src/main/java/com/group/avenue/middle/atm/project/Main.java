package com.group.avenue.middle.atm.project;

import com.group.avenue.middle.atm.project.exception.InsufficientFundsException;
import com.group.avenue.middle.atm.project.model.Banknote;
import com.group.avenue.middle.atm.project.model.User;
import com.group.avenue.middle.atm.project.model.WithdrawalResult;

import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author vladimir_shi
 * @since 29.12.2025
 *
 * Класс для запуска программы
 */
public class Main {
    private static final Scanner scanner;
    private static ATM atm;
    private static User currentUser;
    private static volatile boolean running = true;

    // Статический блок инициализации для правильной кодировки
    static {
        // Принудительно устанавливаем UTF-8 для вывода
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

        // Создаем Scanner с правильной кодировкой
        scanner = new Scanner(new InputStreamReader(System.in, StandardCharsets.UTF_8));

        // Устанавливаем локаль для корректного ввода чисел
        Locale.setDefault(Locale.US);

        System.out.println("Кодировка вывода установлена: " + System.out.charset().name());
        System.out.println("Кодировка ввода установлена: UTF-8");

    }

    public static void main(String[] args) {
        try {
            System.out.println("=== Инициализация банкомата ===");
            System.out.println("Кодировка системы: " + Charset.defaultCharset().displayName());
            System.out.println("Кодировка консоли: " + System.out.charset().name());

            // Создаем банкомат
            atm = new ATM("ATM-001", 100_000); // лимит 100,000 руб за операцию

            // Загружаем начальные деньги в банкомат
            initializeATMWithMoney();

            // Создаем тестового пользователя
            currentUser = new User("Иван Иванов", "1234-5678-9012-3456", 50_000, 50_000);

            System.out.println("Банкомат инициализирован. Общий баланс: " + atm.getBalance() + " руб.");
            System.out.println("Текущий пользователь: " + currentUser.getName());
            System.out.println("Баланс пользователя: " + currentUser.getBalance() + " руб.");
            System.out.println();

            // Главный цикл меню
            while (running) {
                showMainMenu();
                int choice = readIntInput("Выберите действие: ");

                if (!running) {
                    break; // Выходим из цикла, если running = false
                }

                switch (choice) {
                    case 1 -> checkBalance();
                    case 2 -> depositMoney();
                    case 3 -> withdrawMoney();
                    case 4 -> showATMBalance();
                    case 5 -> showUserInfo();
                    case 6 -> resetDailyLimit();
                    case 0 -> exit();
                    default -> System.out.println("Неверный выбор. Попробуйте снова.");
                }

                System.out.println();
            }

            scanner.close();
        } catch (NoSuchElementException e) {
            // Ловим исключение при закрытии ввода
            System.out.println("\nПрограмма завершена.");
        } catch (Exception e) {
            // Ловим любые другие неожиданные исключения
            System.out.println("Неожиданная ошибка: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Этот блок выполнится ВСЕГДА, даже при исключениях
            // Гарантированно закрываем сканер
            if (scanner != null) {
                try {
                    scanner.close();
                } catch (Exception e) {
                    // Игнорируем ошибки при закрытии
                }
            }
            System.out.println("\n=== Банкомат выключен ===");
            System.out.println("Спасибо за использование!");
        }
    }

    private static void initializeATMWithMoney() {
        // Инициализируем банкомат начальными деньгами
        Map<Banknote, Integer> initialMoney = Map.of(
                Banknote.RUB_50, 20,
                Banknote.RUB_100, 30,
                Banknote.RUB_200, 25,
                Banknote.RUB_500, 40,
                Banknote.RUB_1000, 25,
                Banknote.RUB_2000, 15,
                Banknote.RUB_5000, 8
        );

        atm.deposit(initialMoney);
        System.out.println("Загружены начальные средства:");
        for (Map.Entry<Banknote, Integer> entry : initialMoney.entrySet()) {
            System.out.println("  " + entry.getKey().getValue() + " руб. x " + entry.getValue());
        }
    }

    private static void showMainMenu() {
        System.out.println("=== ГЛАВНОЕ МЕНЮ БАНКОМАТА ===");
        System.out.println("1. Проверить баланс");
        System.out.println("2. Внести наличные");
        System.out.println("3. Снять наличные");
        System.out.println("4. Статус банкомата");
        System.out.println("5. Информация о пользователе");
        System.out.println("6. Сбросить дневной лимит (админ)");
        System.out.println("0. Выход");
        System.out.println("===============================");
    }

    private static void checkBalance() {
        System.out.println("=== БАЛАНС ===");
        System.out.println("Баланс: " + ATMUtils.formatAmount(currentUser.getBalance()));
        System.out.println("Снято сегодня: " + ATMUtils.formatAmount(currentUser.getWithdrawnToday()));
        System.out.println("Доступно сегодня: " + ATMUtils.formatAmount(currentUser.getAvailableToday()));
        System.out.println("Дневной лимит: " + ATMUtils.formatAmount(currentUser.getDailyLimit()));
    }

    private static void depositMoney() {
        System.out.println("=== ВНЕСЕНИЕ НАЛИЧНЫХ ===");
        System.out.println("Доступные номиналы: 50, 100, 200, 500, 1000, 2000, 5000 руб.");
        System.out.println("Баланс до операции: " + ATMUtils.formatAmount(currentUser.getBalance()));

        Map<Banknote, Integer> banknotesToDeposit = new HashMap<>();

        for (Banknote banknote : Banknote.values()) {
            // Используем безопасный ввод
            int count = readIntInput("Сколько купюр по " + banknote.getValue() + " руб. вносите? (0 для пропуска): ");

            if (count > 0) {
                banknotesToDeposit.put(banknote, count);
            } else if (count < 0) {
                System.out.println("Количество не может быть отрицательным. Пропускаем эту купюру.");
            }
        }

        if (banknotesToDeposit.isEmpty()) {
            System.out.println("Не внесено ни одной купюры.");
            return;
        }

        // Используем ATMUtils для форматирования
        System.out.println("\nВы вносите:");
        System.out.println(ATMUtils.formatBanknotes(banknotesToDeposit));

        System.out.print("\nПодтвердить внесение? (да/нет): ");
        String confirm = scanner.nextLine().toLowerCase();

        if (confirm.equals("да") || confirm.equals("yes") || confirm.equals("y")) {
            try {
                Map<Banknote, Integer> accepted = atm.deposit(banknotesToDeposit);

                int userDepositAmount = accepted.entrySet().stream()
                        .mapToInt(entry -> entry.getKey().getValue() * entry.getValue())
                        .sum();

                if (userDepositAmount > 0) {
                    currentUser.deposit(userDepositAmount);

                    System.out.println("\nУспешно внесено в банкомат: " +
                            ATMUtils.formatAmount(userDepositAmount));
                    System.out.println("На ваш счет зачислено: " +
                            ATMUtils.formatAmount(userDepositAmount));
                    System.out.println("Новый баланс: " +
                            ATMUtils.formatAmount(currentUser.getBalance()));

                    if (!accepted.isEmpty()) {
                        System.out.println("\nДетали принятых банкнот:");
                        System.out.println(ATMUtils.formatBanknotes(accepted));
                    }
                } else {
                    System.out.println("Банкомат не смог принять ни одну купюру.");
                }

            } catch (Exception e) {
                System.out.println("Ошибка при внесении: " + e.getMessage());
            }
        } else {
            System.out.println("Операция отменена.");
        }
    }

    private static void withdrawMoney() {
        System.out.println("=== СНЯТИЕ НАЛИЧНЫХ ===");
        System.out.println("Баланс: " + ATMUtils.formatAmount(currentUser.getBalance()));
        System.out.println("Доступно сегодня: " + ATMUtils.formatAmount(currentUser.getAvailableToday()));
        System.out.println("Минимальная сумма: " + ATMUtils.formatAmount(atm.getMinBanknoteValue()));
        System.out.println("Максимум за операцию: " + ATMUtils.formatAmount(atm.getMaxWithdrawalAmount()));

        // Показываем доступные номиналы
        Map<Banknote, Integer> available = atm.getAvailableBanknotes();
        if (!available.isEmpty()) {
            System.out.println("Доступные в банкомате номиналы:");
            System.out.println(ATMUtils.formatBanknotes(available));
        }

        int amount = readIntInput("\nВведите сумму для снятия: ");

        // ПРЕДВАРИТЕЛЬНАЯ ВАЛИДАЦИЯ с помощью ATMUtils
        String validationError = ATMUtils.getAmountErrorMessage(amount, atm.getMinBanknoteValue());
        if (validationError != null) {
            System.out.println("Ошибка: " + validationError);
            System.out.println("Пожалуйста, введите сумму, кратную " +
                    atm.getMinBanknoteValue() + " руб.");
            return;
        }

        // Проверка максимальной суммы
        if (amount > atm.getMaxWithdrawalAmount()) {
            System.out.println("Ошибка: Превышен лимит за одну операцию.");
            System.out.println("Максимальная сумма: " +
                    ATMUtils.formatAmount(atm.getMaxWithdrawalAmount()));
            return;
        }

        // Проверка баланса пользователя
        if (!currentUser.canWithdraw(amount)) {
            System.out.println("Ошибка: Нельзя снять " + ATMUtils.formatAmount(amount));
            if (amount > currentUser.getBalance()) {
                System.out.println("  • Недостаточно средств на счете");
            }
            if ((currentUser.getWithdrawnToday() + amount) > currentUser.getDailyLimit()) {
                System.out.println("  • Превышен дневной лимит");
            }
            return;
        }

        System.out.print("Подтвердить снятие " + ATMUtils.formatAmount(amount) + "? (да/нет): ");
        String confirm = scanner.nextLine().toLowerCase();

        if (confirm.equals("да") || confirm.equals("yes") || confirm.equals("y")) {
            try {
                WithdrawalResult result = atm.withdraw(amount);

                if (result.isSuccess()) {
                    currentUser.withdraw(amount);

                    System.out.println("Операция успешна!");
                    System.out.println("Выдано: " + ATMUtils.formatAmount(result.getActualAmount()));
                    System.out.println("Состав: " + result.formatBanknotes());
                    System.out.println("Новый баланс: " + ATMUtils.formatAmount(currentUser.getBalance()));
                    System.out.println("Снято сегодня: " + ATMUtils.formatAmount(currentUser.getWithdrawnToday()));
                } else {
                    System.out.println("Ошибка: " + result.getErrorMessage());
                }

            } catch (InsufficientFundsException e) {
                System.out.println("Ошибка банкомата: " + e.getMessage());

            } catch (Exception e) {
                System.out.println("Неожиданная ошибка: " + e.getMessage());
            }
        } else {
            System.out.println("Операция отменена.");
        }
    }

    private static void showATMBalance() {
        System.out.println(atm.getStatus());
    }

    private static void showUserInfo() {
        System.out.println("=== ИНФОРМАЦИЯ О ПОЛЬЗОВАТЕЛЕ ===");
        System.out.println(currentUser.toString());
    }

    private static void resetDailyLimit() {
        System.out.println("=== СБРОС ДНЕВНОГО ЛИМИТА ===");
        System.out.print("Введите пароль администратора: ");
        String password = scanner.nextLine();

        // Простейшая проверка пароля
        if ("admin123".equals(password)) {
            int oldValue = currentUser.getWithdrawnToday();
            currentUser.resetDailyLimit();
            System.out.println("Лимит сброшен! Было снято: " + oldValue + " руб.");
        } else {
            System.out.println("Неверный пароль!");
        }
    }

    private static void exit() {
        System.out.print("Вы уверены, что хотите выйти? (да/нет): ");
        String confirm = scanner.nextLine().toLowerCase();

        if (confirm.equals("да") || confirm.equals("yes") || confirm.equals("y")) {
            running = false;  // Устанавливаем флаг завершения
            System.out.println("Спасибо за использование нашего банкомата!");
            System.out.println("Не забудьте забрать карту.");
        }
    }

    private static int readIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);

                // Проверяем, есть ли следующий токен
                if (!scanner.hasNext()) {
                    // Если ввод закрыт, выходим из программы
                    System.out.println("\nВвод закрыт. Завершение программы...");
                    running = false;
                    return 0;
                }

                // Пробуем прочитать целое число
                int value = scanner.nextInt();
                scanner.nextLine(); // очистка буфера

                 // Проверяем на отрицательные числа
                 if (value < 0) {
                     System.out.println("Число не может быть отрицательным. Попробуйте снова.");
                     continue;
                 }

                return value;

            } catch (InputMismatchException e) {
                // Пользователь ввел не число
                System.out.println("Ошибка: пожалуйста, введите целое число!");
                scanner.nextLine(); // ОЧИСТКА НЕКОРРЕКТНОГО ВВОДА - ВАЖНО!

            } catch (NoSuchElementException e) {
                // Сканер не может найти элемент (ввод закрыт)
                System.out.println("\nПрограмма завершена пользователем.");
                running = false;
                return 0;

            } catch (IllegalStateException e) {
                // Сканер был закрыт
                System.out.println("\nСканер закрыт. Завершение...");
                running = false;
                return 0;
            }
        }
    }
}