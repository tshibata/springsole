package com.x7th.sole.invite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.x7th.sole.*;

@Component
public class Holder {

	@Autowired
	AccountService accountService;

	@Autowired
	AccountUtil accountUtil;

	public class Get {

		public Model model;

		public String exe() throws AnonymousException, ForbiddenException {
			AccountEntity account = accountService.getCurrent();
			if (! accountService.isAdmin(account)) {
				throw new ForbiddenException();
			}
			return "invite";
		}
	}

	public class Post {

		public RedirectAttributes attr;

		@Param
		public String username;

		@Param
		public String password;

		@Param
		public String verify;

		public String exe() throws AnonymousException, ForbiddenException {
			AccountEntity account = accountService.getCurrent();
			if (! accountService.isAdmin(account)) {
				throw new ForbiddenException();
			}
			AccountEntity newAccount = accountUtil.create(attr, username, password, verify);
			if (newAccount == null) {
				return "redirect:/invite";
			}
			return "redirect:/validity/" + newAccount.id;
		}
	}
}

