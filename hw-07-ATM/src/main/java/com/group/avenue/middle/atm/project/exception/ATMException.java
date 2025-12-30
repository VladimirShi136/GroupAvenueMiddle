package com.group.avenue.middle.atm.project.exception;

/**
 * Базовое исключение для операций с банкоматом
 *
 * @author vladimir_shi
 * @since 29.12.2025
 */
public class ATMException extends RuntimeException {
    public ATMException(String message) {
        super(message);
    }
}
