package com.example.demo;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AdminTests extends AbstractHtmlTests {

	@BeforeEach
	void setup() throws java.io.IOException {
		properties.setOpenEntry(true);
		signin("admin", "admin");
		properties.setOpenEntry(false);
	}

	@Test
	void canRegisterANewAccount() throws java.io.IOException {
		HtmlPage page;
		page = webClient.getPage("http://localhost:8080/accounts");
		page = page.getAnchorByText(getMessage("invite")).click();

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

