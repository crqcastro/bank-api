package br.com.cesarcastro.bankapi.adapter.out.persistence.transaction;

import br.com.cesarcastro.bankapi.domain.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface TransactionJpaRepository extends JpaRepository<Transaction, UUID> {

    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId ORDER BY t.createdAt DESC")
    Page<Transaction> findByAccountId(@Param("accountId") UUID accountId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId " +
           "AND t.createdAt >= :from ORDER BY t.createdAt DESC")
    Page<Transaction> findByAccountIdFrom(
            @Param("accountId") UUID accountId,
            @Param("from") OffsetDateTime from,
            Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId " +
           "AND t.createdAt <= :to ORDER BY t.createdAt DESC")
    Page<Transaction> findByAccountIdTo(
            @Param("accountId") UUID accountId,
            @Param("to") OffsetDateTime to,
            Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId " +
           "AND t.createdAt >= :from AND t.createdAt <= :to ORDER BY t.createdAt DESC")
    Page<Transaction> findByAccountIdAndDateRange(
            @Param("accountId") UUID accountId,
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to,
            Pageable pageable);
}
