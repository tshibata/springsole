package com.example.demo;

import java.util.*;
import java.net.URL;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
public class AdminTests {

	@Autowired
	AppProperties properties;

	@Autowired
	MessageSource messageSource;

	@Autowired
	WebApplicationContext webApplicationContext;

	WebClient webClient; 

	@BeforeEach
	void setup() throws java.io.IOException {
		properties.setOpenEntry(true);
		webClient = MockMvcWebClientBuilder.webAppContextSetup(webApplicationContext).build();
		String username = "admin";
		String password = "admin";
		HtmlPage page;
		page = webClient.getPage("http://localhost:8080/signin");
		HtmlForm form = page.getFormByName("signin");
		HtmlTextInput usernameInput = page.getElementByName("username");
		usernameInput.setValueAttribute(username);
		HtmlPasswordInput passwordInput = page.getElementByName("password");
		passwordInput.setValueAttribute(password);
		HtmlButton submit = form.getOneHtmlElementByAttribute("button", "value", "signin");
		page = submit.click();
		properties.setOpenEntry(false);
	}

	@Test
	void canRegisterANewAccount() throws java.io.IOException {
		HtmlPage page;
		page = webClient.getPage("http://localhost:8080/accounts");
		page = page.getAnchorByText(messageSource.getMessage("invite", new String[]{}, Locale.ENGLISH)).click();

		String username = "invited";
		String password = "invited";
		HtmlForm form = page.getFormByName("signup");
		HtmlTextInput usernameInput = page.getElementByName("username");
		usernameInput.setValueAttribute(username);
		HtmlPasswordInput passwordInput = page.getElementByName("password");
		passwordInput.setValueAttribute(password);
		HtmlPasswordInput passwordInput2 = page.getElementByName("verify");
		passwordInput2.setValueAttribute(password);
		HtmlButton submit = form.getOneHtmlElementByAttribute("button", "value", "invite");
		submit.click();
	}
}

