package com.x7th.sole.accounts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import com.x7th.sole.*;

@Component
public class Holder {

	@Autowired
	AccountService accountService;

	@Autowired
	PageNavigator pageNavigator;

	public class Get {

		public Model model;

		@Param
		public int p;

		public String exe() {
			Pageable pageable = PageRequest.of(p, 2);
			Page<AccountEntity> page = accountService.getAll(true, pageable);
			model.addAttribute("accounts", page);
			model.addAttribute("pages", pageNavigator.navigation(page));
			return "account_list";
		}
	}
}

