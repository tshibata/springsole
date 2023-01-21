package com.x7th.sole.accounts.__;

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

		public String exe(String username) {
			AccountEntity account = accountService.get(username).orElseThrow(NotFoundException::new);
			if (! account.valid) {
				throw new NotFoundException();
			}
			model.addAttribute("username", account.username);
			model.addAttribute("description", renderer.render(parser.parse(account.description)));
			return "account";
		}
	}
}

