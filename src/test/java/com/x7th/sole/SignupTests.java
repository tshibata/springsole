package com.x7th.sole;

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
		try {
			Assertions.assertEquals("Your account", page.getTitleText());
			Assertions.assertTrue(page.getVisibleText().contains("abcde"));
		} finally {
			delete(page, "tester");
		}
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
		try {
			Assertions.assertEquals("Your account", page.getTitleText());
			Assertions.assertTrue(page.getVisibleText().contains("123456789012345"));
		} finally {
			delete(page, "tester");
		}
	}

	@Test
	void canNotShareAName() throws java.io.IOException {
		HtmlPage page = signup("admin", "tester");
		Assertions.assertEquals("Sign up", page.getTitleText());
		String message = getMessage("name_confliction", new String[] {"admin"});
		Assertions.assertTrue(page.getVisibleText().contains(message));
	}

	@Test
	void tooShortPassword() throws java.io.IOException {
		HtmlPage page = signup("tester", "abcd");
		Assertions.assertEquals("Sign up", page.getTitleText());
		Assertions.assertTrue(page.getVisibleText().contains(getMessage("1_too_short_password")));
	}

	@Test
	void almostTooShortPassword() throws java.io.IOException {
		HtmlPage page = signup("tester", "abcde");
		try {
			Assertions.assertEquals("Your account", page.getTitleText());
			Assertions.assertTrue(page.getVisibleText().contains("tester"));
		} finally {
			delete(page, "abcde");
		}
	}

	@Test
	void tooLongPassword() throws java.io.IOException {
		HtmlPage page = signup("tester", "1234567890123456");
		Assertions.assertEquals("Sign up", page.getTitleText());
		Assertions.assertTrue(page.getVisibleText().contains(getMessage("2_too_long_password")));
	}

	@Test
	void almostTooLongPassword() throws java.io.IOException {
		HtmlPage page = signup("tester", "123456789012345");
		try {
			Assertions.assertEquals("Your account", page.getTitleText());
			Assertions.assertTrue(page.getVisibleText().contains("tester"));
		} finally {
			delete(page, "123456789012345");
		}
	}

	@Test
	void inconsistentPassword() throws java.io.IOException {
		HtmlPage page = signup("tester", "secret", "geheimnis");
		Assertions.assertEquals("Sign up", page.getTitleText());
		Assertions.assertTrue(page.getVisibleText().contains(getMessage("password_inconsistency")));
	}
}
