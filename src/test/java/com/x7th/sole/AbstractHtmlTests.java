package com.x7th.sole;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.web.context.WebApplicationContext;

public abstract class AbstractHtmlTests {

	@Autowired
	AppProperties properties;

	@Autowired
	MessageSource messageSource;

	@Autowired
	WebApplicationContext webApplicationContext;

	WebClient webClient; 

	static String adminPassword;

	@BeforeEach
	void init() throws java.io.IOException {
		if (adminPassword == null) {
			webClient = MockMvcWebClientBuilder.webAppContextSetup(webApplicationContext).build();
			signup("admin", "admin");
			adminPassword = "admin";			
		}
		webClient = MockMvcWebClientBuilder.webAppContextSetup(webApplicationContext).build();
	}

	String getMessage(String code, String[] args) {
		return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
	}

	String getMessage(String code) {
		return getMessage(code, new String[]{});
	}

	HtmlPage signup(String username, String password, String verify) throws java.io.IOException {
		HtmlPage page;
		page = webClient.getPage("http://localhost:8080/signup");
		HtmlForm form = page.getFormByName("signup");
		form.getInputByName("username").setValueAttribute(username);
		form.getInputByName("password").setValueAttribute(password);
		form.getInputByName("verify").setValueAttribute(verify);
		return form.getButtonByName("button").click();
	}

	HtmlPage signup(String username, String password) throws java.io.IOException {
		return signup(username, password, password);
	}

	HtmlPage signin(String username, String password) throws java.io.IOException {
		HtmlPage page;
		page = webClient.getPage("http://localhost:8080/signin");
		HtmlForm form = page.getFormByName("signin");
		form.getInputByName("username").setValueAttribute(username);
		form.getInputByName("password").setValueAttribute(password);
		return form.getButtonByName("button").click();
	}

	HtmlPage signout(HtmlPage page) throws java.io.IOException {
		HtmlForm form = page.getFormByName("auth");
		return form.getButtonByName("button").click();
	}

	HtmlPage delete(String password) throws java.io.IOException {
		HtmlPage page;
		page = webClient.getPage("http://localhost:8080/update");
		HtmlForm form = page.getFormByName("delete");
		form.getInputByName("password").setValueAttribute(password);
		return form.getButtonByName("button").click();
	}
}
