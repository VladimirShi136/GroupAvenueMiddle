package com.group.avenue.middle.atm.project.exception;

/**
 * @author vladimir_shi
 * @since 29.12.2025
 *
 * Исключение, связанное с некорректной суммой
 */
public class InvalidAmountException extends ATMException {
    public InvalidAmountException(String message) {
        super(message);
    }
}