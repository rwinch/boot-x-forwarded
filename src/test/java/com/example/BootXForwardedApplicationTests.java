package com.example;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.XForwardedFilter;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = BootXForwardedApplication.class)
@WebAppConfiguration
public class BootXForwardedApplicationTests {
	@Autowired
	WebApplicationContext wac;
	@Autowired
	XForwardedFilter xForwardedFilter;

	MockMvc mockMvc;

	@Before
	public void setup() {
		MockHttpServletRequestBuilder defaultRequest = get("/")
				.header(XForwardedFilter.XFORWARDED_HOST_HEADER, "example.com/context")
				.header(XForwardedFilter.XFORWARDED_PROTO_HEADER, "https");
		mockMvc = MockMvcBuilders
				.webAppContextSetup(wac)
				.addFilters(xForwardedFilter)
				.apply(springSecurity())
				.defaultRequest(defaultRequest)
				.build();
	}

	@Test
	public void redirectProperly() throws Exception {
		mockMvc.perform(get("/"))
			.andExpect(redirectedUrl("https://example.com/context/login"));
	}

}
