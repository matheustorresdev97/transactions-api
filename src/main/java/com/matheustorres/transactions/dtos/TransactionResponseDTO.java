package com.matheustorres.transactions.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.matheustorres.transactions.enums.TransactionType;

public record TransactionResponseDTO(
        UUID id,
        String title,
        BigDecimal amount,
        TransactionType type,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime createdAt) {

}
