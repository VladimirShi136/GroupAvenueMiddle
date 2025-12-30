package com.group.avenue.middle.atm.project.exception;

/**
 * Исключение, связанное с операциями в ячейке банкомата
 *
 * @author vladimir_shi
 * @since 29.12.2025
 */
public class CassetteException extends ATMException {
    public CassetteException(String message) {
        super(message);
    }
}