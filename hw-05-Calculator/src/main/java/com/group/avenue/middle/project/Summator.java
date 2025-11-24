package com.group.avenue.middle.project;

import java.util.ArrayDeque;
import java.util.Deque;

public class Summator {
    private int sum = 0;
    private int prevValue = 0;
    private int prevPrevValue = 0;
    private int sumLastThreeValues = 0;
    private int someValue = 0;
    private final Deque<Integer> listValues = new ArrayDeque<>(6_600_000); // Ограничиваем емкость очереди


    //!!! сигнатуру метода менять нельзя
    public void calc(Data data) {
        int val = data.value(); // извлекаем сразу значение

        // Добавляем в очередь, лимитируем размеры
        listValues.offer(val);
        if (listValues.size() >= 6_600_000) { // очистим старые элементы автоматически
            listValues.pollFirst();
        }

        sum += val;

        sumLastThreeValues = val + prevValue + prevPrevValue;

        prevPrevValue = prevValue;
        prevValue = val;

        // Оптимизируем расчет someValue, используя локальные переменные
        for (int i = 0; i < 3; i++) {
            someValue += ((val + prevValue + prevPrevValue) * (val + prevValue + prevPrevValue)) /
                    (val + 1) - sum;
            someValue = Math.abs(someValue) + listValues.size();
        }
    }

    public int getSum() {
        return sum;
    }

    public int getPrevValue() {
        return prevValue;
    }

    public int getPrevPrevValue() {
        return prevPrevValue;
    }

    public int getSumLastThreeValues() {
        return sumLastThreeValues;
    }

    public int getSomeValue() {
        return someValue;
    }

}