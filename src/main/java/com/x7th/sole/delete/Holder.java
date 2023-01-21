package com.x7th.sole.delete;

import jakarta.servlet.http.HttpSession;
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
	HttpSession session;  

	@Autowired
	PasswordEncoder passwordEncoder;

	public class Post {

		public RedirectAttributes attr;

		@Param
		public String password;

		public String exe() throws AnonymousException {
		AccountEntity account = accountService.getCurrent();
			if (! passwordEncoder.matches(password, account.password)) {
				attr.addFlashAttribute("err", "Password didn't match.");
				return "redirect:/update";
			}
			session.setAttribute("account_id", null);
			accountService.delete(account.id);
			return "redirect:/accounts";
		}
	}
}

