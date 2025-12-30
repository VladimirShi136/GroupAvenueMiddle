package com.group.avenue.middle.atm.project.exception;

/**
 * Исключение, связанное с некорректной суммой
 *
 * @author vladimir_shi
 * @since 29.12.2025
 */
public class InvalidAmountException extends ATMException {
    public InvalidAmountException(String message) {
        super(message);
    }
}