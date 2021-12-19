package com.example.demo;

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

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

@Controller
public class AccountController {

	@Autowired
	AccountService accountService;

	Parser parser = Parser.builder().build();

	HtmlRenderer renderer = HtmlRenderer.builder().build();

	static SortedSet pages(Page current) { // FIXME rename
		SortedSet set = new TreeSet<Integer>();
		if (! set.isEmpty()) {
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

	@RequestMapping(value = "/accounts/{name}", method = RequestMethod.GET)
	public String accounts(Model model, @PathVariable("name") String name) throws AnonymousException, ForbiddenException, NotFoundException {
		AccountEntity account = accountService.get(name).orElseThrow(NotFoundException::new);
		if (! account.valid) {
			throw new NotFoundException();
		}
		model.addAttribute("name", account.name);
		model.addAttribute("description", renderer.render(parser.parse(account.description)));
		return "account";
	}

	@RequestMapping(value = "/update", method = RequestMethod.GET)
	public String index(Model model) throws AnonymousException {
		AccountEntity account = accountService.getCurrent();
		model.addAttribute("account", account);
		return "account_update";
	}

	@RequestMapping(value = "/update/username", method = RequestMethod.POST)
	public String updateUsername(/*Model model, */@RequestParam("name") String name) throws AnonymousException {
		AccountEntity account = accountService.getCurrent();
		account.name = name;
		accountService.post(account);
		return "redirect:/accounts/" + account.name;
	}

	@RequestMapping(value = "/update/description", method = RequestMethod.POST)
	public String updateDescription(/*Model model, */@RequestParam("description") String description) throws AnonymousException {
		AccountEntity account = accountService.getCurrent();
		account.description = description;
		accountService.post(account);
		return "redirect:/accounts/" + account.name;
	}

	@RequestMapping(value = "/admin", method = RequestMethod.GET)
	public String admin(Model model, @PageableDefault(2) Pageable pageable) throws AnonymousException, ForbiddenException {
		if (! accountService.isAdmin(accountService.getCurrent())) {
			throw new ForbiddenException();
		}
		Page<AccountEntity> page = accountService.getAll(pageable);
		model.addAttribute("accounts", page);
		model.addAttribute("pages", pages(page));  // FIXME rename
		return "admin_list";
	}

	@RequestMapping(value = "/shutdown", method = RequestMethod.GET)
	public String shutdown(Model model) throws AnonymousException, ForbiddenException {
		if (! accountService.isAdmin(accountService.getCurrent())) {
			throw new ForbiddenException();
		}
		return "shutdown";
	}

	@RequestMapping(value = "/shutdown", method = RequestMethod.POST)
	public String shutdown(Model model, @RequestParam("button") String button) throws AnonymousException, ForbiddenException {
		if (! accountService.isAdmin(accountService.getCurrent())) {
			throw new ForbiddenException();
		}
		if (button.equals("shutdown")) {
			DemoApplication.shutdown();
		}
		return "redirect:/shutdown"; // this doesn't do anyway.
	}

	@RequestMapping(value = "/admin/{id}", method = RequestMethod.GET)
	public String admin(Model model, @PathVariable("id") long id) throws AnonymousException, ForbiddenException, NotFoundException {
		if (! accountService.isAdmin(accountService.getCurrent())) {
			throw new ForbiddenException();
		}
		AccountEntity account = accountService.get(id).orElseThrow(NotFoundException::new);
		model.addAttribute("account", account);
		model.addAttribute("description", renderer.render(parser.parse(account.description)));
		return "admin";
	}

	@RequestMapping(value = "/admin/{id}", method = RequestMethod.POST)
	public String admin(Model model, @PathVariable("id") long id, @RequestParam("valid") Optional<Boolean> valid) throws AnonymousException, ForbiddenException, NotFoundException {
		if (! accountService.isAdmin(accountService.getCurrent())) {
			throw new ForbiddenException();
		}
		AccountEntity account = accountService.get(id).orElseThrow(NotFoundException::new);
		account.valid = valid.orElse(false);
		accountService.post(account);
		return "redirect:/admin/?valid=" + account.valid;
	}
}
