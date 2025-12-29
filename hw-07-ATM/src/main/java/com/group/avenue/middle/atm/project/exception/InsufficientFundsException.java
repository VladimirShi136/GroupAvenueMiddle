package com.group.avenue.middle.atm.project.exception;

/**
 * @author vladimir_shi
 * @since 29.12.2025
 *
 * Исключение, связанное с недостаточными средствами
 */
public class InsufficientFundsException extends ATMException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
