package com.group.avenue.middle.atm.project.exception;

/**
 * Исключение, связанное с неподдерживаемой банкнотой
 *
 * @author vladimir_shi
 * @since 29.12.2025
 */
public class UnsupportedBanknoteException extends ATMException {
    public UnsupportedBanknoteException(String message) {
        super(message);
    }
}