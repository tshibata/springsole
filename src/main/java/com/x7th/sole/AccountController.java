package com.x7th.sole;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

@Controller
public class AccountController {

	@Autowired
	AppProperties properties;

	@Autowired
	AccountService accountService;

	@Autowired
	PageNavigator pageNavigator;

	Parser parser = Parser.builder().build();

	HtmlRenderer renderer = HtmlRenderer.builder().build();

	@RequestMapping(value = "/accounts", method = RequestMethod.GET)
	public String accounts(Model model, @RequestParam(name = "p", defaultValue = "0") int p) {
		Pageable pageable = PageRequest.of(p, 2);
		Page<AccountEntity> page = accountService.getAll(true, pageable);
		model.addAttribute("accounts", page);
		model.addAttribute("pages", pageNavigator.navigation(page));
		return "account_list";
	}

	@RequestMapping(value = "/accounts/{username}", method = RequestMethod.GET)
	public String accounts(Model model, @PathVariable("username") String username) {
		AccountEntity account = accountService.get(username).orElseThrow(NotFoundException::new);
		if (! account.valid) {
			throw new NotFoundException();
		}
		model.addAttribute("username", account.username);
		model.addAttribute("description", renderer.render(parser.parse(account.description)));
		return "account";
	}

	@RequestMapping(value = "/update", method = RequestMethod.GET)
	public String update(Model model) throws AnonymousException {
		AccountEntity account = accountService.getCurrent();
		model.addAttribute("account", account);
		return "account_update";
	}

	@RequestMapping(value = "/update/description", method = RequestMethod.POST)
	public String updateDescription(/*Model model, */@RequestParam("description") String description) throws AnonymousException {
		AccountEntity account = accountService.getCurrent();
		account.description = description;
		accountService.post(account);
		return "redirect:/accounts/" + account.username;
	}

	@RequestMapping(value = "/validity", method = RequestMethod.GET)
	public String validity(Model model, @RequestParam(name = "p", defaultValue = "0") int p) throws AnonymousException, ForbiddenException {
		Pageable pageable = PageRequest.of(p, 2);
		if (! accountService.isAdmin(accountService.getCurrent())) {
			throw new ForbiddenException();
		}
		Page<AccountEntity> page = accountService.getAll(pageable);
		model.addAttribute("accounts", page);
		model.addAttribute("pages", pageNavigator.navigation(page));
		return "validity_list";
	}

	@RequestMapping(value = "/validity/{id}", method = RequestMethod.GET)
	public String validity(Model model, @PathVariable("id") long id) throws AnonymousException, ForbiddenException {
		if (! accountService.isAdmin(accountService.getCurrent())) {
			throw new ForbiddenException();
		}
		AccountEntity account = accountService.get(id).orElseThrow(NotFoundException::new);
		model.addAttribute("account", account);
		model.addAttribute("description", renderer.render(parser.parse(account.description)));
		return "validity";
	}

	@RequestMapping(value = "/validity/{id}", method = RequestMethod.POST)
	public String validity(Model model, @PathVariable("id") long id, @RequestParam("valid") Optional<Boolean> valid) throws AnonymousException, ForbiddenException {
		if (! accountService.isAdmin(accountService.getCurrent())) {
			throw new ForbiddenException();
		}
		AccountEntity account = accountService.get(id).orElseThrow(NotFoundException::new);
		accountService.setValid(account, valid.orElse(false));
		return "redirect:/validity";
	}
}
