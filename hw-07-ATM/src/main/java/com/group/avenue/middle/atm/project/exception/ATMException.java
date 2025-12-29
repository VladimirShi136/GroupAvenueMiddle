package com.group.avenue.middle.atm.project.exception;

/**
 * @author vladimir_shi
 * @since 29.12.2025
 *
 * Базовое исключение для операций с банкоматом
 */
public class ATMException extends RuntimeException {
    public ATMException(String message) {
        super(message);
    }
}
