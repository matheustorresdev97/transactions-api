package com.matheustorres.transactions.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matheustorres.transactions.models.TransactionsModel;

public interface TransactionsRepository extends JpaRepository<TransactionsModel, UUID> {

}
