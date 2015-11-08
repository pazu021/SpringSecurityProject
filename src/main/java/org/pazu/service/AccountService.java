package org.pazu.service;

import org.pazu.model.Account;

public interface AccountService {

	Account findByUsername(String username);

}
