package com.matheustorres.transactions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.matheustorres.transactions.dtos.CreateTransactionDTO;
import com.matheustorres.transactions.dtos.TransactionResponseDTO;
import com.matheustorres.transactions.dtos.TransactionSummaryDTO;
import com.matheustorres.transactions.enums.TransactionType;
import com.matheustorres.transactions.models.TransactionsModel;
import com.matheustorres.transactions.repositories.TransactionsRepository;
import com.matheustorres.transactions.services.SessionService;
import com.matheustorres.transactions.services.TransactionsService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
public class TransactionsServiceTest {

    @Mock
    private TransactionsRepository transactionsRepository;

    @Mock
    private SessionService sessionService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private TransactionsService transactionsService;

    @Test
    public void testCreateTransaction() {

        UUID sessionId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        // Dados de entrada
        CreateTransactionDTO dto = new CreateTransactionDTO("Salário", 1000.00, TransactionType.credit);

        when(sessionService.getOrCreateSessionId(request, response)).thenReturn(sessionId);

        // Mock da entidade que será salva no banco
        TransactionsModel savedTransaction = new TransactionsModel();
        savedTransaction.setId(transactionId);
        savedTransaction.setTitle("Salário");
        savedTransaction.setAmount(BigDecimal.valueOf(1000.00));
        savedTransaction.setSessionId(sessionId);
        savedTransaction.setCreatedAt(now);

        // Mock do comportamento do repositório
        when(transactionsRepository.save(any(TransactionsModel.class))).thenReturn(savedTransaction);

        // Act
        TransactionResponseDTO result = transactionsService.createTransaction(dto, request, response);

        // Assert
        assertNotNull(result);
        assertEquals(transactionId, result.id());
        assertEquals("Salário", result.title());
        assertEquals(BigDecimal.valueOf(1000.00), result.amount());
        assertEquals(TransactionType.credit, result.type());
        assertEquals(now, result.createdAt());

        verify(sessionService).getOrCreateSessionId(request, response);
        verify(transactionsRepository).save(any(TransactionsModel.class));
    }

    @Test
    public void testGetAllTransactions() {
        // Arrange
        UUID sessionId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        // Mock do comportamento do sessionService
        when(sessionService.getOrCreateSessionId(request, response)).thenReturn(sessionId);

        // Criar algumas transações de teste
        TransactionsModel transaction1 = new TransactionsModel();
        transaction1.setId(UUID.randomUUID());
        transaction1.setTitle("Salário");
        transaction1.setAmount(BigDecimal.valueOf(2000.00));
        transaction1.setSessionId(sessionId);
        transaction1.setCreatedAt(now);

        TransactionsModel transaction2 = new TransactionsModel();
        transaction2.setId(UUID.randomUUID());
        transaction2.setTitle("Aluguel");
        transaction2.setAmount(BigDecimal.valueOf(-800.00));
        transaction2.setSessionId(sessionId);
        transaction2.setCreatedAt(now.plusDays(1));

        List<TransactionsModel> transactionsList = Arrays.asList(transaction1, transaction2);

        // Mock do comportamento do repositório
        when(transactionsRepository.findBySessionId(sessionId)).thenReturn(transactionsList);

        // Act
        List<TransactionResponseDTO> result = transactionsService.getAllTransactions(request, response);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        // Verificar a primeira transação
        TransactionResponseDTO dto1 = result.get(0);
        assertEquals(transaction1.getId(), dto1.id());
        assertEquals("Salário", dto1.title());
        assertEquals(BigDecimal.valueOf(2000.00), dto1.amount());
        assertEquals(TransactionType.credit, dto1.type());
        assertEquals(transaction1.getCreatedAt(), dto1.createdAt());

        // Verificar a segunda transação
        TransactionResponseDTO dto2 = result.get(1);
        assertEquals(transaction2.getId(), dto2.id());
        assertEquals("Aluguel", dto2.title());
        assertEquals(BigDecimal.valueOf(800.00), dto2.amount()); // Valor absoluto para débito
        assertEquals(TransactionType.debit, dto2.type());
        assertEquals(transaction2.getCreatedAt(), dto2.createdAt());

        // Verify
        verify(sessionService).getOrCreateSessionId(request, response);
        verify(transactionsRepository).findBySessionId(sessionId);
    }

    @Test
    public void testGetAllTransactions_EmptyList() {
        // Arrange
        UUID sessionId = UUID.randomUUID();

        // Mock do comportamento do sessionService
        when(sessionService.getOrCreateSessionId(request, response)).thenReturn(sessionId);

        // Mock do comportamento do repositório - lista vazia
        when(transactionsRepository.findBySessionId(sessionId)).thenReturn(List.of());

        // Act
        List<TransactionResponseDTO> result = transactionsService.getAllTransactions(request, response);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify
        verify(sessionService).getOrCreateSessionId(request, response);
        verify(transactionsRepository).findBySessionId(sessionId);
    }

    @Test
    public void testGetSummary() {
        // Arrange
        UUID sessionId = UUID.randomUUID();

        // Mock do comportamento do sessionService
        when(sessionService.getOrCreateSessionId(request, response)).thenReturn(sessionId);

        // Criar algumas transações de teste com valores diferentes
        TransactionsModel transaction1 = new TransactionsModel();
        transaction1.setAmount(BigDecimal.valueOf(1000.00)); // Crédito
        transaction1.setSessionId(sessionId);

        TransactionsModel transaction2 = new TransactionsModel();
        transaction2.setAmount(BigDecimal.valueOf(-300.00)); // Débito
        transaction2.setSessionId(sessionId);

        TransactionsModel transaction3 = new TransactionsModel();
        transaction3.setAmount(BigDecimal.valueOf(500.00)); // Outro crédito
        transaction3.setSessionId(sessionId);

        List<TransactionsModel> transactionsList = Arrays.asList(transaction1, transaction2, transaction3);

        // Mock do comportamento do repositório
        when(transactionsRepository.findBySessionId(sessionId)).thenReturn(transactionsList);

        // Act
        TransactionSummaryDTO result = transactionsService.getSummary(request, response);

        // Assert
        assertNotNull(result);
        // O total esperado é 1000 - 300 + 500 = 1200
        assertEquals(BigDecimal.valueOf(1200.00), result.amount());

        // Verify
        verify(sessionService).getOrCreateSessionId(request, response);
        verify(transactionsRepository).findBySessionId(sessionId);
    }

    @Test
    public void testGetTransactionById_Success() {
        // Arrange
        UUID sessionId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        // Mock do comportamento do sessionService
        when(sessionService.getOrCreateSessionId(request, response)).thenReturn(sessionId);

        // Criar uma transação de teste
        TransactionsModel transaction = new TransactionsModel();
        transaction.setId(transactionId);
        transaction.setTitle("Salário");
        transaction.setAmount(BigDecimal.valueOf(1500.00));
        transaction.setSessionId(sessionId);
        transaction.setCreatedAt(now);

        // Mock do comportamento do repositório
        when(transactionsRepository.findByIdAndSessionId(transactionId, sessionId))
                .thenReturn(Optional.of(transaction));

        // Act
        TransactionResponseDTO result = transactionsService.getTransactionById(transactionId, request, response);

        // Assert
        assertNotNull(result);
        assertEquals(transactionId, result.id());
        assertEquals("Salário", result.title());
        assertEquals(BigDecimal.valueOf(1500.00), result.amount());
        assertEquals(TransactionType.credit, result.type());
        assertEquals(now, result.createdAt());

        // Verify
        verify(sessionService).getOrCreateSessionId(request, response);
        verify(transactionsRepository).findByIdAndSessionId(transactionId, sessionId);
    }
}
