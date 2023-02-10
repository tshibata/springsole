package com.x7th.sole.signin;

import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import com.x7th.sole.*;

@Component
public class Holder {

	public class Get {

		public Model model;

		@Param
		public String from;

		public String respond() {
			model.addAttribute("from", from);
			return "signin";
		}
	}
}

