package com.x7th.sole;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.html.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ClosedEntryTests extends AbstractHtmlTests {

	@BeforeEach
	void setup() {
		properties.setOpenEntryInterval(- 1);
	}

	@Test
	void CanNotFindSignUpPage() throws java.io.IOException {
		HtmlPage page;
		page = webClient.getPage("http://localhost:8080/accounts");
		try {
			page.getAnchorByText(getMessage("sign_up"));
			Assertions.fail("ElementNotFoundException must be thrown");
		} catch (ElementNotFoundException ex) {
		}
	}

	@Test
	void CanNotSignUp() throws java.io.IOException {
		HtmlPage page;
		try {
			page = webClient.getPage("http://localhost:8080/signup");
			Assertions.fail("FailingHttpStatusCodeException must be thrown");
		} catch (FailingHttpStatusCodeException ex) {
			Assertions.assertEquals(403, ex.getStatusCode());
		}
	}
}
