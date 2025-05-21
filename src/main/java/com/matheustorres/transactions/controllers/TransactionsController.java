package com.matheustorres.transactions.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matheustorres.transactions.dtos.CreateTransactionDTO;
import com.matheustorres.transactions.dtos.TransactionResponseDTO;
import com.matheustorres.transactions.dtos.TransactionSummaryDTO;
import com.matheustorres.transactions.exceptions.InvalidTransactionException;
import com.matheustorres.transactions.exceptions.TransactionCreationException;
import com.matheustorres.transactions.services.TransactionsService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/transactions")
public class TransactionsController {

    @Autowired
    private TransactionsService transactionsService;

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(
            @Valid @RequestBody CreateTransactionDTO createTransactionDTO,
            HttpServletResponse response) {
        try {
            if (!createTransactionDTO.isValidType()) {
                throw new InvalidTransactionException("O tipo da transação deve ser 'credit' ou 'debit'");
            }

            TransactionResponseDTO transactionResponse = transactionsService.createTransaction(
                    createTransactionDTO, response);
            return ResponseEntity.status(HttpStatus.CREATED).body(transactionResponse);
        } catch (Exception e) {
            throw new TransactionCreationException("Não foi possível criar a tarefa");
        }
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactions() {
        List<TransactionResponseDTO> transactions = transactionsService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> getTransactionById(@PathVariable UUID id) {
        try {
            TransactionResponseDTO transaction = transactionsService.getTransactionById(id);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar transação: " + e.getMessage());
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<TransactionSummaryDTO> getSummary() {
        try {
            TransactionSummaryDTO summary = transactionsService.getSummary();
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao calcular o resumo das transações: " + e.getMessage());
        }
    }
}