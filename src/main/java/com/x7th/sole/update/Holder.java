package com.x7th.sole.update;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import com.x7th.sole.*;

@Component
public class Holder {

	@Autowired
	AccountService accountService;

	public class Get {

		public Model model;

		public String respond() throws AnonymousException {
			AccountEntity account = accountService.getCurrent();
			model.addAttribute("account", account);
			return "account_update";
		}
	}
}

