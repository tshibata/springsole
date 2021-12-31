package com.example.demo;

import java.util.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
	public Optional<AccountEntity> findByUsername(String username);
	public Page<AccountEntity> findByValidTrue(Pageable pageable);
	public Page<AccountEntity> findByValidFalse(Pageable pageable);
}
