package com.x7th.sole.signout;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.x7th.sole.*;

@Component
public class Holder {

	@Autowired
	HttpSession session;  

	public class Post {

		public String respond() {
			session.setAttribute("account_id", null);
			return "redirect:/signin";
		}
	}
}

