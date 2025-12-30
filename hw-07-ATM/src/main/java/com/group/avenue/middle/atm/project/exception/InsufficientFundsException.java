package com.group.avenue.middle.atm.project.exception;

/**
 * Исключение, связанное с недостаточными средствами
 *
 * @author vladimir_shi
 * @since 29.12.2025
 */
public class InsufficientFundsException extends ATMException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
