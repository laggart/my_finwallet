package com.myfinwallet.finwallet_api.repository;

import com.myfinwallet.finwallet_api.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;


@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query("SELECT t FROM Transaction t WHERE " +
           "(t.senderAccount.id = :accountId OR t.receiverAccount.id = :accountId) " +
           "AND t.createdAt BETWEEN :from AND :to")
    Page<Transaction> findByAccountIdAndDateRange(
        @Param("accountId") UUID accountId,
        @Param("from") LocalDateTime from,
        @Param("to") LocalDateTime to,
        Pageable pageable);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE " +
           "t.senderAccount.id = :accountId " +
           "AND t.createdAt >= :since")
    Long countByAccountInLastHour(
        @Param("accountId") UUID accountId,
        @Param("since") LocalDateTime since);


    
} 
