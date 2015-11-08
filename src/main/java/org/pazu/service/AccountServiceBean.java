package org.pazu.service;

import org.pazu.model.Account;
import org.pazu.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceBean implements AccountService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AccountRepository accountRepository;

	@Override
	public Account findByUsername(String username) {

		logger.debug("> findByUsername");

		Account account = accountRepository.findByUsername(username);

		logger.debug("< findByUsername");
		return account;
	}

}
