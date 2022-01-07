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

	HtmlPage register(String username, String password, String verify) throws java.io.IOException {
		HtmlPage page;
		page = webClient.getPage("http://localhost:8080/");
		page = page.getAnchorByText(getMessage("invite")).click();
		HtmlForm form = page.getFormByName("signup");
		HtmlTextInput usernameInput = page.getElementByName("username");
		usernameInput.setValueAttribute(username);
		HtmlPasswordInput passwordInput = page.getElementByName("password");
		passwordInput.setValueAttribute(password);
		HtmlPasswordInput passwordInput2 = page.getElementByName("verify");
		passwordInput2.setValueAttribute(verify);
		HtmlButton submit = form.getOneHtmlElementByAttribute("button", "value", "invite");
		return submit.click();
	}

	HtmlPage register(String username, String password) throws java.io.IOException {
		return register(username, password, password);
	}

	@Test
	void canRegisterANewAccount() throws java.io.IOException {
		HtmlPage page;
		page = register("invited", "invited");
		signout(page);
		page = signin("invited", "invited");
		delete(page, "invited");
	}
}

