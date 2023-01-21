package com.x7th.sole.validity.__;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import com.x7th.sole.*;

@Component
public class Holder {

	@Autowired
	AccountService accountService;

	Parser parser = Parser.builder().build();

	HtmlRenderer renderer = HtmlRenderer.builder().build();

	public class Get {

		public Model model;

		public String exe(String id) throws AnonymousException, ForbiddenException {
			if (! accountService.isAdmin(accountService.getCurrent())) {
				throw new ForbiddenException();
			}
			AccountEntity account = accountService.get(Long.parseLong(id)).orElseThrow(NotFoundException::new);
			model.addAttribute("account", account);
			model.addAttribute("description", renderer.render(parser.parse(account.description)));
			return "validity";
		}
	}

	public class Post {

		@Param
		public boolean valid;

		public String exe(String id) throws AnonymousException, ForbiddenException {
			if (! accountService.isAdmin(accountService.getCurrent())) {
				throw new ForbiddenException();
			}
			AccountEntity account = accountService.get(Long.parseLong(id)).orElseThrow(NotFoundException::new);
			accountService.setValid(account, valid);
			return "redirect:/validity";
		}
	}
}

