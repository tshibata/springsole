package com.x7th.sole.update.description;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.x7th.sole.*;

@Component
public class Holder {

	@Autowired
	AccountService accountService;

	public class Post {

		@Param
		public String description;

		public String respond() throws AnonymousException {
			AccountEntity account = accountService.getCurrent();
			account.description = description;
			accountService.post(account);
			return "redirect:/accounts/" + account.username;
		}
	}
}

