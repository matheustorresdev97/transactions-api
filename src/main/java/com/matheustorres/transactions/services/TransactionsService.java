package com.matheustorres.transactions.services;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.matheustorres.transactions.dtos.CreateTransactionDTO;
import com.matheustorres.transactions.dtos.TranscationResponseDTO;
import com.matheustorres.transactions.enums.TransactionType;
import com.matheustorres.transactions.models.TransactionsModel;
import com.matheustorres.transactions.repositories.TransactionsRepository;

@Service
public class TransactionsService {

    @Autowired
    private TransactionsRepository transactionsRepository;

    public TranscationResponseDTO createTransaction(CreateTransactionDTO createTransactionDTO) {

        BigDecimal finalAmount = BigDecimal.valueOf(createTransactionDTO.amount());
        if (createTransactionDTO.type() == TransactionType.debit) {
            finalAmount = finalAmount.negate();
        }

        TransactionsModel transaction = new TransactionsModel();

        transaction.setTitle(createTransactionDTO.title());
        transaction.setAmount(finalAmount);

        transaction = transactionsRepository.save(transaction);

        return convertToDTO(transaction);

    }

    private TranscationResponseDTO convertToDTO(TransactionsModel transaction) {
        TransactionType type = transaction.getAmount().compareTo(BigDecimal.ZERO) >= 0
                ? TransactionType.credit
                : TransactionType.debit;

        BigDecimal displayAmount = type == TransactionType.debit
                ? transaction.getAmount().abs()
                : transaction.getAmount();

        return new TranscationResponseDTO(
                transaction.getId(),
                transaction.getTitle(),
                displayAmount,
                type,
                transaction.getCreatedAt());
    }
}