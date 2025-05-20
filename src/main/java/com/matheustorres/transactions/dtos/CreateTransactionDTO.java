package com.matheustorres.transactions.dtos;

import com.matheustorres.transactions.enums.TransactionType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTransactionDTO(
        @NotBlank(message = "O título é obrigatório") String title,

        @NotNull(message = "O valor é obrigatório") Double amount,

        @NotNull(message = "O tipo é obrigatório") TransactionType type) {

    public boolean isValidType() {
       return type == TransactionType.credit || type == TransactionType.debit;
    }

}
