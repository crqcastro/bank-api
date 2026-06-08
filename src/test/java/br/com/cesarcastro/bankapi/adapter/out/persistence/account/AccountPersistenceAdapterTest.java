package br.com.cesarcastro.bankapi.adapter.out.persistence.account;

import br.com.cesarcastro.bankapi.domain.model.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccountPersistenceAdapter")
class AccountPersistenceAdapterTest {

    @Mock
    AccountJpaRepository jpaRepository;

    @InjectMocks
    AccountPersistenceAdapter adapter;

    @Test
    @DisplayName("loadById deve delegar ao repositorio")
    void loadByIdDelegatesToRepository() {
        UUID id = UUID.randomUUID();
        Account account = Account.builder().id(id).build();
        when(jpaRepository.findById(id)).thenReturn(Optional.of(account));

        Optional<Account> result = adapter.loadById(id);

        assertThat(result).contains(account);
    }

    @Test
    @DisplayName("loadByIdForUpdate deve usar lock pessimista")
    void loadByIdForUpdateUsesLock() {
        UUID id = UUID.randomUUID();
        Account account = Account.builder().id(id).build();
        when(jpaRepository.findByIdWithLock(id)).thenReturn(Optional.of(account));

        Optional<Account> result = adapter.loadByIdForUpdate(id);

        assertThat(result).contains(account);
    }

    @Test
    @DisplayName("loadByCustomerId deve retornar pagina de contas")
    void loadByCustomerIdReturnsPaginatedAccounts() {
        UUID customerId = UUID.randomUUID();
        Pageable pageable = Pageable.unpaged();
        Page<Account> page = new PageImpl<>(List.of(Account.builder().build()));
        when(jpaRepository.findByCustomerId(customerId, pageable)).thenReturn(page);

        Page<Account> result = adapter.loadByCustomerId(customerId, pageable);

        assertThat(result).isSameAs(page);
    }

    @Test
    @DisplayName("save deve persistir e retornar a conta")
    void savePersistsAccount() {
        Account account = Account.builder().id(UUID.randomUUID()).build();
        when(jpaRepository.save(account)).thenReturn(account);

        Account result = adapter.save(account);

        assertThat(result).isSameAs(account);
    }

    @Test
    @DisplayName("deleteById deve delegar ao repositorio")
    void deleteByIdDelegatesToRepository() {
        UUID id = UUID.randomUUID();

        adapter.deleteById(id);

        verify(jpaRepository).deleteById(id);
    }

    @Test
    @DisplayName("hasZeroBalance deve retornar true quando saldo for zero")
    void hasZeroBalanceReturnsTrueWhenBalanceIsZero() {
        UUID id = UUID.randomUUID();
        Account account = Account.builder().balance(BigDecimal.ZERO).build();
        when(jpaRepository.findById(id)).thenReturn(Optional.of(account));

        assertThat(adapter.hasZeroBalance(id)).isTrue();
    }

    @Test
    @DisplayName("hasZeroBalance deve retornar false quando houver saldo")
    void hasZeroBalanceReturnsFalseWhenBalanceIsPositive() {
        UUID id = UUID.randomUUID();
        Account account = Account.builder().balance(BigDecimal.valueOf(100)).build();
        when(jpaRepository.findById(id)).thenReturn(Optional.of(account));

        assertThat(adapter.hasZeroBalance(id)).isFalse();
    }

    @Test
    @DisplayName("hasZeroBalance deve retornar true quando conta nao existir")
    void hasZeroBalanceReturnsTrueWhenAccountNotFound() {
        UUID id = UUID.randomUUID();
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        assertThat(adapter.hasZeroBalance(id)).isTrue();
    }
}
