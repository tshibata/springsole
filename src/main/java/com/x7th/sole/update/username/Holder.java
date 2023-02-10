package com.x7th.sole.update.username;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.x7th.sole.*;

@Component
public class Holder {

	@Autowired
	MessageSource messageSource;

	@Autowired
	AccountService accountService;

	@Autowired
	AccountUtil accountUtil;

	public class Post {

		public RedirectAttributes attr;

		@Param
		public String username;

		public String respond() throws AnonymousException {
			AccountEntity account = accountService.getCurrent();
			if (! accountUtil.checkUsername(attr, username)) {
				return "redirect:/update";
			}
			account.username = username;
			try {
				accountService.post(account);
			} catch (DataIntegrityViolationException ex) {
				String message = messageSource.getMessage("name_confliction", new String[] {username}, LocaleContextHolder.getLocale());
				attr.addFlashAttribute("err", message);
				return "redirect:/update";
			}
			return "redirect:/accounts/" + account.username;
		}
	}
}

