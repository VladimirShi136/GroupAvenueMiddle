package com.group.avenue.middle.atm.project.exception;

/**
 * @author vladimir_shi
 * @since 29.12.2025
 *
 * Исключение, связанное с операциями в ячейке банкомата
 */
public class CassetteException extends ATMException {
    public CassetteException(String message) {
        super(message);
    }
}