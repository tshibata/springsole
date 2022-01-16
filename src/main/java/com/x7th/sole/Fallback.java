package com.x7th.sole;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
@Component
public class Fallback {

	@Autowired
	AppProperties properties;

	@Autowired
	AccountService accountService;

	@ModelAttribute("bar")
	public Bar bar() {
		AccountEntity account = accountService.getCurrentOrNull();
		if (account == null) {
			if (properties.getOpenEntry()) {
				return new OpenBar();
			} else {
				return new ClosedBar();
			}
		} else if (accountService.isAdmin(account)) {
			return new AdminBar(account.username);
		} else {
			return new UserBar(account.username);
		}
	}

	@ExceptionHandler(AnonymousException.class)
	public String handler(AnonymousException ex, RedirectAttributes attr) {
		attr.addFlashAttribute("err", "You need to sign in.");
		return "redirect:/signin";
	}

	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handler(NotFoundException ex, Model model) {
		model.addAttribute("bar", bar());
		return "not_found";
	}

	@ExceptionHandler(ForbiddenException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public String handler(ForbiddenException ex, Model model) {
		model.addAttribute("bar", bar());
		return "forbidden";
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public String handler(Exception ex, Model model) {
		model.addAttribute("bar", bar());
		return "error";
	}
}

