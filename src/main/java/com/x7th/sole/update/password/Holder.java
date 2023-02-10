package com.x7th.sole.update.password;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.x7th.sole.*;

@Component
public class Holder {

	@Autowired
	AccountService accountService;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	AccountUtil accountUtil;

	public class Post {

		public RedirectAttributes attr;

		@Param
		public String oldPassword;

		@Param
		public String newPassword;

		@Param
		public String verify;

		public String respond() throws AnonymousException {
			AccountEntity account = accountService.getCurrent();
			if (! passwordEncoder.matches(oldPassword, account.password)) {
				attr.addFlashAttribute("err", "Old password didn't match.");
				return "redirect:/update";
			}
			if (! accountUtil.checkPassword(attr, newPassword, verify)) {
				return "redirect:/update";
			}
			account.password = passwordEncoder.encode(newPassword);
			accountService.post(account);
			return "redirect:/accounts/" + account.username;
		}
	}
}

