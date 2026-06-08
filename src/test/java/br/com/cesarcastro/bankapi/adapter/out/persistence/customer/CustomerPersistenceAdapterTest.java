package br.com.cesarcastro.bankapi.adapter.out.persistence.customer;

import br.com.cesarcastro.bankapi.domain.model.Customer;
import br.com.cesarcastro.bankapi.domain.model.CustomerStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerPersistenceAdapter")
class CustomerPersistenceAdapterTest {

    @Mock
    CustomerJpaRepository jpaRepository;

    @InjectMocks
    CustomerPersistenceAdapter adapter;

    @Test
    @DisplayName("loadById deve delegar ao repositorio")
    void loadByIdDelegatesToRepository() {
        UUID id = UUID.randomUUID();
        Customer customer = Customer.builder().id(id).build();
        when(jpaRepository.findById(id)).thenReturn(Optional.of(customer));

        Optional<Customer> result = adapter.loadById(id);

        assertThat(result).contains(customer);
    }

    @Test
    @DisplayName("loadByDocument deve delegar ao repositorio")
    void loadByDocumentDelegatesToRepository() {
        String document = "12345678901";
        Customer customer = Customer.builder().document(document).build();
        when(jpaRepository.findByDocument(document)).thenReturn(Optional.of(customer));

        Optional<Customer> result = adapter.loadByDocument(document);

        assertThat(result).contains(customer);
    }

    @Test
    @DisplayName("loadAll sem status deve buscar todos os clientes")
    void loadAllWithoutStatusFetchesAll() {
        Pageable pageable = Pageable.unpaged();
        Page<Customer> page = new PageImpl<>(List.of(Customer.builder().build()));
        when(jpaRepository.findAll(pageable)).thenReturn(page);

        Page<Customer> result = adapter.loadAll(null, pageable);

        assertThat(result).isSameAs(page);
    }

    @Test
    @DisplayName("loadAll com status deve filtrar por status")
    void loadAllWithStatusFiltersbyStatus() {
        Pageable pageable = Pageable.unpaged();
        Page<Customer> page = new PageImpl<>(List.of(Customer.builder().build()));
        when(jpaRepository.findByStatus(CustomerStatus.ACTIVE, pageable)).thenReturn(page);

        Page<Customer> result = adapter.loadAll(CustomerStatus.ACTIVE, pageable);

        assertThat(result).isSameAs(page);
    }

    @Test
    @DisplayName("existsByDocument deve delegar ao repositorio")
    void existsByDocumentDelegatesToRepository() {
        when(jpaRepository.existsByDocument("12345678901")).thenReturn(true);

        assertThat(adapter.existsByDocument("12345678901")).isTrue();
    }

    @Test
    @DisplayName("hasActiveAccounts deve delegar ao repositorio")
    void hasActiveAccountsDelegatesToRepository() {
        UUID customerId = UUID.randomUUID();
        when(jpaRepository.hasActiveAccounts(customerId)).thenReturn(true);

        assertThat(adapter.hasActiveAccounts(customerId)).isTrue();
    }

    @Test
    @DisplayName("hasAccounts deve delegar ao repositorio")
    void hasAccountsDelegatesToRepository() {
        UUID customerId = UUID.randomUUID();
        when(jpaRepository.hasAccounts(customerId)).thenReturn(false);

        assertThat(adapter.hasAccounts(customerId)).isFalse();
    }

    @Test
    @DisplayName("save deve persistir e retornar o cliente")
    void savePersistsCustomer() {
        Customer customer = Customer.builder().id(UUID.randomUUID()).build();
        when(jpaRepository.save(customer)).thenReturn(customer);

        Customer result = adapter.save(customer);

        assertThat(result).isSameAs(customer);
    }

    @Test
    @DisplayName("deleteById deve delegar ao repositorio")
    void deleteByIdDelegatesToRepository() {
        UUID id = UUID.randomUUID();

        adapter.deleteById(id);

        verify(jpaRepository).deleteById(id);
    }
}
