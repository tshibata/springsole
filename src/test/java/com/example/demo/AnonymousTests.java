package com.example.demo;

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
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
public class AnonymousTests {

	@Autowired
	WebApplicationContext webApplicationContext;

	WebClient webClient; 

	@BeforeEach
	void setup() {
		webClient = MockMvcWebClientBuilder.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	void listWhenEmpty() throws java.io.IOException {
		HtmlPage page;
		page = webClient.getPage("http://localhost:8080/accounts");
		// TODO check that no link has "accounts?page="
	}

	@Test
	void updateWithoutSignin() throws java.io.IOException {
		HtmlPage page;
		page = webClient.getPage("http://localhost:8080/update");
		Assertions.assertEquals(page.getTitleText(), "Sign in");
	}
}
