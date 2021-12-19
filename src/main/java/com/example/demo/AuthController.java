package com.example.demo;

import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

	@Autowired
	AppProperties properties;

	@Autowired
	AccountService accountService;

	@Autowired
	HttpSession session;  

	@Autowired
	PasswordEncoder passwordEncoder;

	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public String signup(Model model) throws ForbiddenException {
		if (! properties.getOpenEntry()) {
			throw new ForbiddenException();
		}
		return "signup";
	}

	boolean checkUsername(RedirectAttributes attr, String name) {
		if (name.length() < 5) {
			attr.addFlashAttribute("err", "\"" + name + "\" is too short.");
			return false;
		}
		if (15 < name.length()) {
			attr.addFlashAttribute("err", "\"" + name + "\" is too long.");
			return false;
		}
		for (char c: name.toCharArray()) {
			if ('A' <= c && c <= 'Z') continue;
			if ('a' <= c && c <= 'z') continue;
			if ('0' <= c && c <= '9') continue;
			if (c == '-') continue;
			attr.addFlashAttribute("err", "User name can not contain \"" + c + "\".");
			return false;
		}
		return true;
	}

	boolean checkPassword(RedirectAttributes attr, String password, String verify) {
		if (password.length() < 5) {
			attr.addFlashAttribute("err", "Too short password.");
			return false;
		}
		if (15 < password.length()) {
			attr.addFlashAttribute("err", "Too long password.");
			return false;
		}
		for (char c: password.toCharArray()) {
			if ('!' <= c && c <= '~') continue;
			attr.addFlashAttribute("err", "Password can not contain \"" + c + "\".");
			return false;
		}
		if (! password.equals(verify)) {
			attr.addFlashAttribute("err", "Password verify failed. Type twice exactly the same.");
			return false;
		}
		return true;
	}

	AccountEntity create(RedirectAttributes attr, String name, String password, String verify) {
		if (! checkUsername(attr, name)) {
			return null;
		}
		if (accountService.get(name).isPresent()) {
			attr.addFlashAttribute("err", "\"" + name + "\" already exists.");
			return null;
		}
		if (! checkPassword(attr, password, verify)) {
			return null;
		}
		AccountEntity account = new AccountEntity();
		account.name = name;
		account.password = passwordEncoder.encode(password);
		account.description = "";
		account.valid = true;
		accountService.post(account);
		return account;
	}

	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public String signup(RedirectAttributes attr, @RequestParam("username")String name, @RequestParam("password")String password, @RequestParam("verify")String verify) throws ForbiddenException {
		if (! properties.getOpenEntry()) {
			throw new ForbiddenException();
		}
		AccountEntity account = create(attr, name, password, verify);
		if (account == null) {
			return "redirect:/signup";
		}
		session.setAttribute("account_id", account.id);
		return "redirect:/update";
	}

	@RequestMapping(value = "/invite", method = RequestMethod.GET)
	public String invite(Model model) throws AnonymousException, ForbiddenException {
		AccountEntity account = accountService.getCurrent();
		if (! accountService.isAdmin(account)) {
			throw new ForbiddenException();
		}
		return "invite";
	}

	@RequestMapping(value = "/invite", method = RequestMethod.POST)
	public String invite(RedirectAttributes attr, @RequestParam("username")String name, @RequestParam("password")String password, @RequestParam("verify")String verify) throws AnonymousException, ForbiddenException {
		AccountEntity account = accountService.getCurrent();
		if (! accountService.isAdmin(account)) {
			throw new ForbiddenException();
		}
		AccountEntity newAccount = create(attr, name, password, verify);
		if (newAccount == null) {
			return "redirect:/invite";
		}
		return "redirect:/admin/" + newAccount.id;
	}

	@RequestMapping(value = "/signin", method = RequestMethod.GET)
	public String signin() {
		return "signin";
	}

	@RequestMapping(value = "/signin", method = RequestMethod.POST)
	public String signin(RedirectAttributes attr, @RequestParam("username")String name, @RequestParam("password")String password) {
		Optional<AccountEntity> account = accountService.get(name).filter(a -> accountService.canSignin(a) && passwordEncoder.matches(password, a.password));
		if (account.isPresent()) {
			session.setAttribute("account_id", account.orElseThrow(RuntimeException::new).id);
			return "redirect:/update";
		} else {
			attr.addFlashAttribute("err", "Wrong username or password.");
			return "redirect:/signin";
		}
	}

	@RequestMapping(value = "/signout", method = RequestMethod.POST)
	public String signout(/*Model model*/) {
		session.setAttribute("account_id", null);
		return "redirect:/signin";
	}

	@RequestMapping(value = "/update/password", method = RequestMethod.POST)
	public String password(RedirectAttributes attr, @RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword, @RequestParam("verify") String verify) throws AnonymousException {
		AccountEntity account = accountService.getCurrent();
		if (! passwordEncoder.matches(oldPassword, account.password)) {
			attr.addFlashAttribute("err", "Old password didn't match.");
		}
		if (! checkPassword(attr, newPassword, verify)) {
			return "redirect:/password";
		}
		account.password = passwordEncoder.encode(newPassword);
		accountService.post(account);
		return "redirect:/accounts/" + account.name;
	}
}
