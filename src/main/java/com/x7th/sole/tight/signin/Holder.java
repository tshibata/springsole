package com.x7th.sole.tight.signin;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Optional;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
	HttpSession session;  

	@Autowired
	PasswordEncoder passwordEncoder;

	public class Post {

		public RedirectAttributes attr;

		@Param
		public String username;

		@Param
		public String password;

		@Param
		public String from;

		public String exe() {
			Optional<AccountEntity> account = accountService.get(username).filter(a -> accountService.canSignin(a) && passwordEncoder.matches(password, a.password));
			if (account.isPresent()) {
				session.setAttribute("account_id", account.orElseThrow(NotFoundException::new).id);
				try {
					String decoded = URLDecoder.decode(from, "UTF-8");
					if (decoded.startsWith("/")) {
						return "redirect:" + decoded;
					}
				} catch (UnsupportedEncodingException ex) {
				}
				return "redirect:/update";
			} else {
				String message = messageSource.getMessage("authentication_failed", new String[] {username}, LocaleContextHolder.getLocale());
				attr.addFlashAttribute("err", message);
				return "redirect:/signin" + (from == null ? "" : "?from=" + from);
			}
		}
	}
}

