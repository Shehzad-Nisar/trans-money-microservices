package com.transmoney.accountservice.repository;

import com.transmoney.accountservice.Entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account,String> {
}
