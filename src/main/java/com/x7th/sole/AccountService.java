package com.x7th.sole;

import java.util.*;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccountService {

	@Autowired
	AppProperties properties;

	@Autowired
	HttpSession session;

	AccountRepository repository;

	AccountService(AccountRepository repository) {
		this.repository = repository;
	}

	public AccountEntity post(AccountEntity entity) {
		return repository.save(entity);
	}

	public Optional<AccountEntity> get(Long id) {
		return repository.findById(id);
	}

	public Optional<AccountEntity> get(String username) {
		return repository.findByUsername(username);
	}

	public void delete(Long id) {
		repository.deleteById(id);
	}

	public Page<AccountEntity> getAll(Pageable pageable) {
		return repository.findAll(pageable);
	}

	public Page<AccountEntity> getAll(boolean valid, Pageable pageable) {
		if (valid) {
			return repository.findByValidTrue(pageable);
		} else {
			return repository.findByValidFalse(pageable);
		}
	}

	public AccountEntity getCurrentOrNull() {
		Long accountId = (Long) session.getAttribute("account_id");
		if (accountId == null) {
			return null;
		}
		return repository.findById(accountId.longValue()).orElse(null);
	}

	public AccountEntity getCurrent() throws AnonymousException {
		AccountEntity account = getCurrentOrNull();
		if (account == null) {
			throw new AnonymousException();
		}
		return account;
	}

	public boolean isAdmin(AccountEntity account) {
		return account.id == properties.getAdminId();
	}

	public boolean canSignin(AccountEntity account) {
		return account.valid || isAdmin(account);
	}
}
