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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class TransactionsService {

    @Autowired
    private TransactionsRepository transactionsRepository;
    
    @Autowired
    private SessionService sessionService;

    public TransactionResponseDTO createTransaction(CreateTransactionDTO createTransactionDTO,
            HttpServletRequest request, HttpServletResponse response) {

        // Obtém ou cria o sessionId
        UUID sessionId = sessionService.getOrCreateSessionId(request, response);

        BigDecimal finalAmount = BigDecimal.valueOf(createTransactionDTO.amount());
        if (createTransactionDTO.type() == TransactionType.debit) {
            finalAmount = finalAmount.negate();
        }

        TransactionsModel transaction = new TransactionsModel();
        transaction.setTitle(createTransactionDTO.title());
        transaction.setAmount(finalAmount);
        transaction.setSessionId(sessionId);

        transaction = transactionsRepository.save(transaction);

        return convertToDTO(transaction);
    }

    public List<TransactionResponseDTO> getAllTransactions(HttpServletRequest request, HttpServletResponse response) {
        UUID sessionId = sessionService.getOrCreateSessionId(request, response);
        List<TransactionsModel> transactions = transactionsRepository.findBySessionId(sessionId);
        return transactions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TransactionResponseDTO getTransactionById(UUID id, HttpServletRequest request, HttpServletResponse response) {
        UUID sessionId = sessionService.getOrCreateSessionId(request, response);
        TransactionsModel transaction = transactionsRepository.findByIdAndSessionId(id, sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada com o ID: " + id));

        return convertToDTO(transaction);
    }

    public TransactionSummaryDTO getSummary(HttpServletRequest request, HttpServletResponse response) {
        UUID sessionId = sessionService.getOrCreateSessionId(request, response);
        List<TransactionsModel> transactions = transactionsRepository.findBySessionId(sessionId);

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