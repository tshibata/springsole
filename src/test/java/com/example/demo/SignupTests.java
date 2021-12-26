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
public class SignupTests {

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

	HtmlPage delete(HtmlPage page, String password) throws java.io.IOException {
		page = page.getAnchorByText(messageSource.getMessage("update_yours", new String[]{}, Locale.ENGLISH)).click();
		HtmlForm form = page.getFormByName("delete");
		form.getInputByName("password").setValueAttribute(password);
		return form.getButtonByName("button").click();
	}

	@Test
	void tooShortName() throws java.io.IOException {
		HtmlPage page = signup("abcd", "tester");
		Assertions.assertEquals("Sign up", page.getTitleText());
		Assertions.assertTrue(page.getVisibleText().toLowerCase().contains("too short"));
	}

	@Test
	void almostTooShortName() throws java.io.IOException {
		HtmlPage page = signup("abcde", "tester");
		Assertions.assertEquals("Your account", page.getTitleText());
		Assertions.assertTrue(page.getVisibleText().contains("abcde"));
		delete(page, "tester");
	}

	@Test
	void tooLongName() throws java.io.IOException {
		HtmlPage page = signup("1234567890123456", "tester");
		Assertions.assertEquals("Sign up", page.getTitleText());
		Assertions.assertTrue(page.getVisibleText().toLowerCase().contains("too long"));
	}

	@Test
	void almostTooLongName() throws java.io.IOException {
		HtmlPage page = signup("123456789012345", "tester");
		Assertions.assertEquals("Your account", page.getTitleText());
		Assertions.assertTrue(page.getVisibleText().contains("123456789012345"));
		delete(page, "tester");
	}
}
