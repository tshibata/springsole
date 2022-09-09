package com.x7th.sole;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import javax.servlet.http.HttpServletRequest;

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

	@Autowired
	HttpServletRequest request;

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
		String path;
		String query;
		try {
			if (request.getMethod().equals("GET")) {
				path = request.getRequestURI();
				query = request.getQueryString();
			} else {
				URL url = new URL(request.getHeader("Referer"));
				path = url.getPath();
				query = url.getQuery();
			}
			String from = path + (query == null ? "" : "?" + query);
			return "redirect:/signin?from=" + URLEncoder.encode(from, "UTF-8");
		} catch (UnsupportedEncodingException ueex) {
		} catch (MalformedURLException mux) {
		}
		return "redirect:/signin?from=/";
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

