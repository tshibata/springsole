package com.x7th.sole;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AnonymousTests extends AbstractHtmlTests {

	@BeforeEach
	void setup() {
		properties.setOpenEntryInterval(0);
	}

	@Test
	void listNothingWhenEmpty() throws java.io.IOException {
		HtmlPage page;
		page = webClient.getPage("http://localhost:8080/accounts");
		for (HtmlAnchor anchor: page.getAnchors()) {
			Assertions.assertFalse(anchor.getTextContent().contains("/accounts?page="));
		}
	}

	@Test
	void canFindSigninPage() throws java.io.IOException {
		HtmlPage page; 
		page = webClient.getPage("http://localhost:8080/accounts");
		page.getAnchorByText(getMessage("sign_in"));
		page.getAnchorByHref("/signin");
	}

	@Test
	void beRequestedToSignin() throws java.io.IOException {
		HtmlPage page;
		page = webClient.getPage("http://localhost:8080/update");
		Assertions.assertEquals(page.getTitleText(), "Sign in");
	}
}
