package br.com.cesarcastro.bankapi.adapter.out.persistence.customer;

import br.com.cesarcastro.bankapi.domain.model.Customer;
import br.com.cesarcastro.bankapi.domain.model.CustomerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CustomerJpaRepository extends JpaRepository<Customer, UUID> {

    Optional<Customer> findByDocument(String document);

    boolean existsByDocument(String document);

    Page<Customer> findByStatus(CustomerStatus status, Pageable pageable);

    @Query("SELECT COUNT(a) > 0 FROM Account a WHERE a.customer.id = :customerId AND a.status = 'ACTIVE'")
    boolean hasActiveAccounts(@Param("customerId") UUID customerId);

    @Query("SELECT COUNT(a) > 0 FROM Account a WHERE a.customer.id = :customerId")
    boolean hasAccounts(@Param("customerId") UUID customerId);
}
