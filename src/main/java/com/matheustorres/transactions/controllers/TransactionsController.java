package com.matheustorres.transactions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matheustorres.transactions.dtos.CreateTransactionDTO;
import com.matheustorres.transactions.dtos.TranscationResponseDTO;
import com.matheustorres.transactions.exceptions.InvalidTransactionException;
import com.matheustorres.transactions.exceptions.TransactionCreationException;
import com.matheustorres.transactions.services.TransactionsService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/transactions")
public class TransactionsController {

    @Autowired
    private TransactionsService transactionsService;

    @PostMapping
    public ResponseEntity<TranscationResponseDTO> createTransaction(
            @Valid @RequestBody CreateTransactionDTO createTransactionDTO) {
        try {
            if (!createTransactionDTO.isValidType()) {
                throw new InvalidTransactionException("O tipo da transação deve ser 'credit' ou 'debit'");
            }

            TranscationResponseDTO transactionResponse = transactionsService.createTransaction(createTransactionDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(transactionResponse);
        } catch (Exception e) {
            throw new TransactionCreationException("Não foi possível criar a tarefa");
        }
    }
}
