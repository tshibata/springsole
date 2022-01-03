package com.example.demo;

import com.gargoylesoftware.htmlunit.html.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SignupTests extends AbstractHtmlTests {

	@BeforeEach
	void setup() {
		properties.setOpenEntry(true);
	}

	@Test
	void tooShortName() throws java.io.IOException {
		HtmlPage page = signup("abcd", "tester");
		Assertions.assertEquals("Sign up", page.getTitleText());
		Assertions.assertTrue(page.getVisibleText().contains(getMessage("1_too_short_username")));
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
		Assertions.assertTrue(page.getVisibleText().contains(getMessage("2_too_long_username")));
	}

	@Test
	void almostTooLongName() throws java.io.IOException {
		HtmlPage page = signup("123456789012345", "tester");
		Assertions.assertEquals("Your account", page.getTitleText());
		Assertions.assertTrue(page.getVisibleText().contains("123456789012345"));
		delete(page, "tester");
	}

	@Test
	void nameConfliction() throws java.io.IOException {
		HtmlPage page = signup("admin", "tester");
		Assertions.assertEquals("Sign up", page.getTitleText());
		String message = getMessage("name_confliction", new String[] {"admin"});
		Assertions.assertTrue(page.getVisibleText().contains(message));
	}
}
