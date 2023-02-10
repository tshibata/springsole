package com.x7th.sole.signup;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.x7th.sole.*;

@Component
public class Holder {

	@Autowired
	AppProperties properties;

	@Autowired
	HttpSession session;  

	@Autowired
	AccountUtil accountUtil;

	public class Get {

		public String respond() throws ForbiddenException {
			if (! properties.getOpenEntry()) {
				throw new ForbiddenException();
			}
			return "signup";
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

		public String respond() throws ForbiddenException {
			if (! properties.getOpenEntry()) {
				throw new ForbiddenException();
			}
			AccountEntity account = accountUtil.create(attr, username, password, verify);
			if (account == null) {
				return "redirect:/signup";
			}
			properties.updateOpenEntry();
			session.setAttribute("account_id", account.id);
			return "redirect:/update";
		}
	}
}

