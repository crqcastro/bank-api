package br.com.cesarcastro.bankapi.adapter.out.persistence.account;

import br.com.cesarcastro.bankapi.application.port.out.account.DeleteAccountPort;
import br.com.cesarcastro.bankapi.application.port.out.account.LoadAccountPort;
import br.com.cesarcastro.bankapi.application.port.out.account.SaveAccountPort;
import br.com.cesarcastro.bankapi.domain.model.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccountPersistenceAdapter implements LoadAccountPort, SaveAccountPort, DeleteAccountPort {

    private final AccountJpaRepository jpaRepository;

    @Override
    public Optional<Account> loadById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Account> loadByIdForUpdate(UUID id) {
        return jpaRepository.findByIdWithLock(id);
    }

    @Override
    public Page<Account> loadByCustomerId(UUID customerId, Pageable pageable) {
        return jpaRepository.findByCustomerId(customerId, pageable);
    }

    @Override
    public Account save(Account account) {
        return jpaRepository.save(account);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean hasZeroBalance(UUID accountId) {
        return jpaRepository.findById(accountId)
                .map(a -> a.getBalance().compareTo(BigDecimal.ZERO) == 0)
                .orElse(true);
    }
}
