package com.group.avenue.middle.atm.project.exception;

/**
 * @author vladimir_shi
 * @since 29.12.2025
 *
 * Исключение, связанное с неподдерживаемой банкнотой
 */
public class UnsupportedBanknoteException extends ATMException {
    public UnsupportedBanknoteException(String message) {
        super(message);
    }
}