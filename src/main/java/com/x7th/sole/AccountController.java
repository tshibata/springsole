package com.x7th.sole;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
	AccountService accountService;

	@Autowired
	AppProperties properties;

	Parser parser = Parser.builder().build();

	HtmlRenderer renderer = HtmlRenderer.builder().build();

	static SortedSet pages(Page current) { // FIXME rename
		SortedSet set = new TreeSet<Integer>();
		if (! current.isEmpty()) {
			set.add(0);
			set.add(current.previousOrFirstPageable().getPageNumber());
			set.add(current.getNumber());
			set.add(current.nextOrLastPageable().getPageNumber());
			set.add(current.getTotalPages() - 1);
		}
		return set;
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String root(Model model) throws AnonymousException {
		return "redirect:/accounts";
	}

	@RequestMapping(value = "/accounts", method = RequestMethod.GET)
	public String accounts(Model model, @PageableDefault(2) Pageable pageable) throws AnonymousException {
		Page<AccountEntity> page = accountService.getAll(true, pageable);
		model.addAttribute("accounts", page);
		model.addAttribute("pages", pages(page));
		return "account_list";
	}

	@RequestMapping(value = "/accounts/{username}", method = RequestMethod.GET)
	public String accounts(Model model, @PathVariable("username") String username) throws AnonymousException, NotFoundException {
		AccountEntity account = accountService.get(username).orElseThrow(NotFoundException::new);
		if (! account.valid) {
			throw new NotFoundException();
		}
		model.addAttribute("username", account.username);
		model.addAttribute("description", renderer.render(parser.parse(account.description)));
		return "account";
	}

	@RequestMapping(value = "/update", method = RequestMethod.GET)
	public String index(Model model) throws AnonymousException {
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
	public String admin(Model model, @PageableDefault(2) Pageable pageable) throws AnonymousException, ForbiddenException {
		if (! accountService.isAdmin(accountService.getCurrent())) {
			throw new ForbiddenException();
		}
		Page<AccountEntity> page = accountService.getAll(pageable);
		model.addAttribute("accounts", page);
		model.addAttribute("pages", pages(page));  // FIXME rename
		return "validity_list";
	}

	@RequestMapping(value = "/validity/{id}", method = RequestMethod.GET)
	public String admin(Model model, @PathVariable("id") long id) throws AnonymousException, ForbiddenException, NotFoundException {
		if (! accountService.isAdmin(accountService.getCurrent())) {
			throw new ForbiddenException();
		}
		AccountEntity account = accountService.get(id).orElseThrow(NotFoundException::new);
		model.addAttribute("account", account);
		model.addAttribute("description", renderer.render(parser.parse(account.description)));
		return "validity";
	}

	@RequestMapping(value = "/validity/{id}", method = RequestMethod.POST)
	public String admin(Model model, @PathVariable("id") long id, @RequestParam("valid") Optional<Boolean> valid) throws AnonymousException, ForbiddenException, NotFoundException {
		if (! accountService.isAdmin(accountService.getCurrent())) {
			throw new ForbiddenException();
		}
		AccountEntity account = accountService.get(id).orElseThrow(NotFoundException::new);
		account.valid = valid.orElse(false);
		accountService.post(account);
		return "redirect:/validity";
	}
}
