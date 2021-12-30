package com.example.demo;

import java.util.*;
import java.net.URL;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
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
public class ClosedEntryTests {

	@Autowired
	AppProperties properties;

	@Autowired
	MessageSource messageSource;

	@Autowired
	WebApplicationContext webApplicationContext;

	WebClient webClient; 

	@BeforeEach
	void beforeEach() {
		properties.setOpenEntry(false);
		webClient = MockMvcWebClientBuilder.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	void CantFindSignUpPage() throws java.io.IOException {
		HtmlPage page;
		page = webClient.getPage("http://localhost:8080/accounts");
		try {
			page.getAnchorByText(messageSource.getMessage("sign_up", new String[]{}, LocaleContextHolder.getLocale()));
			Assertions.fail("ElementNotFoundException must be thrown");
		} catch (ElementNotFoundException ex) {
		}
	}

	@Test
	void CantSignUp() throws java.io.IOException {
		HtmlPage page;
		try {
			page = webClient.getPage("http://localhost:8080/signup");
			Assertions.fail("FailingHttpStatusCodeException must be thrown");
		} catch (FailingHttpStatusCodeException ex) {
			Assertions.assertEquals(403, ex.getStatusCode());
		}
	}
}
