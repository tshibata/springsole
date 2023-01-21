package com.x7th.sole;

import java.io.UnsupportedEncodingException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Component
public class AccountUtil {

	@Autowired
	AppProperties properties;

	@Autowired
	MessageSource messageSource;

	@Autowired
	AccountService accountService;

	@Autowired
	HttpSession session;  

	@Autowired
	PasswordEncoder passwordEncoder;

	public boolean checkUsername(RedirectAttributes attr, String username) {
		String message = properties.checkProhibitedPatterns("username", username);
		if (message != null) {
			attr.addFlashAttribute("err", message);
			return false;
		}
		return true;
	}

	public boolean checkPassword(RedirectAttributes attr, String password, String verify) {
		String message = properties.checkProhibitedPatterns("password", password);
		if (message != null) {
			attr.addFlashAttribute("err", message);
			return false;
		}
		if (! password.equals(verify)) {
			message = messageSource.getMessage("password_inconsistency", new String[] {}, LocaleContextHolder.getLocale());
			attr.addFlashAttribute("err", message);
			return false;
		}
		return true;
	}

	public AccountEntity create(RedirectAttributes attr, String username, String password, String verify) {
		if (! checkUsername(attr, username)) {
			return null;
		}
		if (! checkPassword(attr, password, verify)) {
			return null;
		}
		AccountEntity account = new AccountEntity();
		account.username = username;
		account.password = passwordEncoder.encode(password);
		account.description = "";
		account.valid = true;
		try {
			accountService.post(account);
		} catch (DataIntegrityViolationException ex) {
			String message = messageSource.getMessage("name_confliction", new String[] {username}, LocaleContextHolder.getLocale());
			attr.addFlashAttribute("err", message);
			return null;
		}
		return account;
	}
}

