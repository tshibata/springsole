package com.x7th.sole;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;

import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

@SpringBootTest
public class IntervalEntryTests extends AbstractHtmlTests {

	@BeforeEach
	void setup() throws InterruptedException {
		properties.setOpenEntryInterval(3);
		Thread.sleep(5000);
	}

	@Test
	void busySignup() throws InterruptedException, java.io.IOException {
		HtmlPage page = signup("tester", "secret");
		try {
			Assertions.assertEquals("Your account", page.getTitleText());
			Assertions.assertTrue(page.getVisibleText().contains("tester"));
			signout(page);
			try {
				page = signup("tester2", "secret");
				Assertions.fail("FailingHttpStatusCodeException must be thrown");
			} catch (FailingHttpStatusCodeException ex) {
				Assertions.assertEquals(403, ex.getStatusCode());
			}
		} finally {
			page = signin("tester", "secret");
			delete(page, "secret");
		}
	}

	@Test
	void easySignup() throws InterruptedException, java.io.IOException {
		HtmlPage page = signup("tester", "secret");
		try {
			Assertions.assertEquals("Your account", page.getTitleText());
			Assertions.assertTrue(page.getVisibleText().contains("tester"));
			signout(page);
			Thread.sleep(5000);
			page = signup("tester2", "secret");
			try {
				Assertions.assertEquals("Your account", page.getTitleText());
				Assertions.assertTrue(page.getVisibleText().contains("tester2"));
			} finally {
				delete(page, "secret");
			}
		} finally {
			page = signin("tester", "secret");
			delete(page, "secret");
		}
	}
}
