package com.matheustorres.transactions.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.matheustorres.transactions.dtos.CreateTransactionDTO;
import com.matheustorres.transactions.dtos.TransactionResponseDTO;
import com.matheustorres.transactions.dtos.TransactionSummaryDTO;
import com.matheustorres.transactions.enums.TransactionType;
import com.matheustorres.transactions.exceptions.ResourceNotFoundException;
import com.matheustorres.transactions.models.TransactionsModel;
import com.matheustorres.transactions.repositories.TransactionsRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class TransactionsService {

    @Autowired
    private TransactionsRepository transactionsRepository;

    public TransactionResponseDTO createTransaction(CreateTransactionDTO createTransactionDTO,
            HttpServletResponse response) {

        BigDecimal finalAmount = BigDecimal.valueOf(createTransactionDTO.amount());
        if (createTransactionDTO.type() == TransactionType.debit) {
            finalAmount = finalAmount.negate();
        }

        UUID sessionId = UUID.randomUUID();

        Cookie sessionCookie = new Cookie("sessionId", sessionId.toString());
        sessionCookie.setHttpOnly(true);
        sessionCookie.setSecure(false); // true se estiver em produção com HTTPS
        sessionCookie.setPath("/");
        sessionCookie.setMaxAge(7 * 24 * 60 * 60); // 7 dias

        response.addCookie(sessionCookie);

        TransactionsModel transaction = new TransactionsModel();

        transaction.setTitle(createTransactionDTO.title());
        transaction.setAmount(finalAmount);
        transaction.setSessionId(sessionId);

        transaction = transactionsRepository.save(transaction);

        return convertToDTO(transaction);

    }

    public List<TransactionResponseDTO> getAllTransactions() {
        List<TransactionsModel> transactions = transactionsRepository.findAll();
        return transactions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TransactionResponseDTO getTransactionById(UUID id) {
        TransactionsModel transaction = transactionsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada com o ID: " + id));

        return convertToDTO(transaction);
    }

    public TransactionSummaryDTO getSummary() {
        List<TransactionsModel> transactions = transactionsRepository.findAll();

        BigDecimal totalAmount = transactions.stream()
                .map(TransactionsModel::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new TransactionSummaryDTO(totalAmount);
    }

    private TransactionResponseDTO convertToDTO(TransactionsModel transaction) {
        TransactionType type = transaction.getAmount().compareTo(BigDecimal.ZERO) >= 0
                ? TransactionType.credit
                : TransactionType.debit;

        BigDecimal displayAmount = type == TransactionType.debit
                ? transaction.getAmount().abs()
                : transaction.getAmount();

        return new TransactionResponseDTO(
                transaction.getId(),
                transaction.getTitle(),
                displayAmount,
                type,
                transaction.getCreatedAt());
    }
}