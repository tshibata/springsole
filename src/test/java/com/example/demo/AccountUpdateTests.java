package com.example.demo;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.html.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AccountUpdateTests extends AbstractHtmlTests {

	@BeforeEach
	void setup() {
		properties.setOpenEntry(true);
	}

	HtmlPage changeName(HtmlPage page, String username) throws java.io.IOException {
		page = page.getAnchorByText(getMessage("update_yours")).click();
		HtmlForm form = page.getFormByName("username");
		form.getInputByName("username").setValueAttribute(username);
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
