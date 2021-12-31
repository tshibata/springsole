package com.example.demo;

import java.util.*;
import java.net.URL;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
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
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
public class AccountUpdateTests {

	@Autowired
	AppProperties properties;

	@Autowired
	MessageSource messageSource;

	@Autowired
	WebApplicationContext webApplicationContext;

	WebClient webClient; 

	@BeforeEach
	void setup() {
		properties.setOpenEntry(true);
		webClient = MockMvcWebClientBuilder.webAppContextSetup(webApplicationContext).build();
	}

	HtmlPage signup(String username, String password) throws java.io.IOException {
		HtmlPage page;
		page = webClient.getPage("http://localhost:8080/signup");
		Assertions.assertEquals(page.getTitleText(), "Sign up");
		HtmlForm form = page.getFormByName("signup");
		HtmlTextInput usernameInput = page.getElementByName("username");
		usernameInput.setValueAttribute(username);
		HtmlPasswordInput passwordInput = page.getElementByName("password");
		passwordInput.setValueAttribute(password);

		HtmlPasswordInput passwordInput2 = page.getElementByName("verify");
		passwordInput2.setValueAttribute(password);

		HtmlButton submit = form.getOneHtmlElementByAttribute("button", "value", "signup");
		return submit.click();
	}

	HtmlPage changeName(HtmlPage page, String username) throws java.io.IOException {
		page = page.getAnchorByText(messageSource.getMessage("update_yours", new String[]{}, LocaleContextHolder.getLocale())).click();
		HtmlForm form = page.getFormByName("username");
		form.getInputByName("name").setValueAttribute(username);
		return form.getButtonByName("button").click();
	}

	HtmlPage delete(HtmlPage page, String password) throws java.io.IOException {
		page = page.getAnchorByText(messageSource.getMessage("update_yours", new String[]{}, LocaleContextHolder.getLocale())).click();
		HtmlForm form = page.getFormByName("delete");
		form.getInputByName("password").setValueAttribute(password);
		return form.getButtonByName("button").click();
	}

	@Test
	void canChangeName() throws java.io.IOException {
		HtmlPage page;
		page = signup("tester", "secret");
		page = changeName(page, "tester2");
		page = webClient.getPage("http://localhost:8080/accounts/tester2");
		try {
			page = webClient.getPage("http://localhost:8080/accounts/tester");
			Assertions.fail("FailingHttpStatusCodeException must be thrown");
		} catch (FailingHttpStatusCodeException ex) {
			Assertions.assertEquals(404, ex.getStatusCode());
		}
		delete(page, "secret");
	}

	@Test
	void almostTooShortName() throws java.io.IOException {
		HtmlPage page;
		page = signup("tester", "secret");
		page = changeName(page, "abcde");
		page = webClient.getPage("http://localhost:8080/accounts/abcde");
		try {
			page = webClient.getPage("http://localhost:8080/accounts/tester");
			Assertions.fail("FailingHttpStatusCodeException must be thrown");
		} catch (FailingHttpStatusCodeException ex) {
			Assertions.assertEquals(404, ex.getStatusCode());
		}
		delete(page, "secret");
	}

	@Test
	void almostTooLongName() throws java.io.IOException {
		HtmlPage page;
		page = signup("tester", "secret");
		page = changeName(page, "123456789012345");
		page = webClient.getPage("http://localhost:8080/accounts/123456789012345");
		try {
			page = webClient.getPage("http://localhost:8080/accounts/tester");
			Assertions.fail("FailingHttpStatusCodeException must be thrown");
		} catch (FailingHttpStatusCodeException ex) {
			Assertions.assertEquals(404, ex.getStatusCode());
		}
		delete(page, "secret");
	}

	@Test
	void tooShortName() throws java.io.IOException {
		HtmlPage page;
		page = signup("tester", "secret");
		page = changeName(page, "abcd");
		page = webClient.getPage("http://localhost:8080/accounts/tester");
		delete(page, "secret");
	}

	@Test
	void tooLongName() throws java.io.IOException {
		HtmlPage page;
		page = signup("tester", "secret");
		page = changeName(page, "1234567890123456");
		page = webClient.getPage("http://localhost:8080/accounts/tester");
		delete(page, "secret");
	}

	@Test
	void canNotShareAName() throws java.io.IOException {
		HtmlPage page;
		page = signup("tester", "secret");
		page = changeName(page, "admin");
		page = webClient.getPage("http://localhost:8080/accounts/tester");
		delete(page, "secret");
	}
}
