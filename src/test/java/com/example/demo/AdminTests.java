package com.example.demo;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
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
		form.getInputByName("username").setValueAttribute(username);
		form.getInputByName("password").setValueAttribute(password);
		form.getInputByName("verify").setValueAttribute(verify);
		return form.getButtonByName("button").click();
	}

	HtmlPage register(String username, String password) throws java.io.IOException {
		return register(username, password, password);
	}

	HtmlPage validate(HtmlPage page, String username, boolean valid) throws java.io.IOException {
		page = page.getAnchorByText(getMessage("validity")).click();
		page = page.getAnchorByText(username).click();
		HtmlForm form = page.getFormByName("validity");
		HtmlCheckBoxInput checkBox = form.getInputByName("valid");
		checkBox.setChecked(valid);
		return form.getButtonByName("button").click();
	}

	@Test
	void canRegisterANewAccount() throws java.io.IOException {
		HtmlPage page;
		page = register("invited", "invited");
		signout(page);
		page = signin("invited", "invited");
		delete(page, "invited");
	}

	@Test
	void canValidateAdmin() throws java.io.IOException {
		HtmlPage page;
		page = webClient.getPage("http://localhost:8080/");
		page = validate(page, "admin", false);
		try {
			page = webClient.getPage("http://localhost:8080/accounts/admin");
		} catch (FailingHttpStatusCodeException ex) {
			Assertions.assertEquals(404, ex.getStatusCode());
		}
		signout(page);
		page = signin("admin", "admin");
		page = validate(page, "admin", true);
		page = webClient.getPage("http://localhost:8080/accounts/admin");
	}

	@Test
	void canValidateAUser() throws java.io.IOException {
		HtmlPage page;
		page = register("invited", "invited");
		try {
			page = webClient.getPage("http://localhost:8080/");
			page = validate(page, "invited", false);
			try {
				try {
					page = webClient.getPage("http://localhost:8080/accounts/invited");
				} catch (FailingHttpStatusCodeException ex) {
					Assertions.assertEquals(404, ex.getStatusCode());
				}
				signout(page);
				page = signin("invited", "invited");
				Assertions.assertTrue(page.getVisibleText().contains(getMessage("authentication_failed")));
			} finally {
				page = signin("admin", "admin");
				page = validate(page, "invited", true);
				signout(page);
				page = webClient.getPage("http://localhost:8080/accounts/invited");
			}
		} finally {
			page = signin("invited", "invited");
			delete(page, "invited");
		}
	}
}

