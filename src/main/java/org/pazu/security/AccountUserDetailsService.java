package org.pazu.security;

import java.util.ArrayList;
import java.util.Collection;

import org.pazu.model.Account;
import org.pazu.model.Role;
import org.pazu.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AccountUserDetailsService implements UserDetailsService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AccountService accountService;

	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {

		logger.debug("> loadUserByUsername");

		Account account = accountService.findByUsername(username);

		if (account == null) {
			throw new UsernameNotFoundException("User " + username
					+ " not found");
		}

		if (account.getRoles() == null || account.getRoles().isEmpty()) {
			throw new UsernameNotFoundException("User not authorized");
		}

		Collection<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
		for (Role role : account.getRoles()) {
			grantedAuthorities
					.add(new SimpleGrantedAuthority((role.getCode())));
		}

		User userDetails = new User(account.getUsername(),
				account.getPassword(), account.isEnabled(),
				!account.isExpired(), !account.isCredentialsexpired(),
				!account.isLocked(), grantedAuthorities);

		logger.debug("< loadUserByUsername");

		return userDetails;
	}
}
