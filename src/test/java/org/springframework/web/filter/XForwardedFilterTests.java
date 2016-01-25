/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.web.filter;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.hamcrest.Matchers.*;
import static org.springframework.web.filter.XForwardedFilter.*;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.filter.XForwardedFilter;

/**
 * @author Rob Winch
 *
 */
public class XForwardedFilterTests {
	MockHttpServletRequest request;
	MockHttpServletResponse response;
	MockFilterChain chain;
	XForwardedFilter filter;

	@Before
	public void request() {
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		filter = new XForwardedFilter();
		chain = new MockFilterChain(new NoOpServlet(), filter);
	}

	@Test
	public void xforwardedHeaderWithContext() throws Exception {
		request.addHeader(XFORWARDED_HOST_HEADER, "example.com/context");

		HttpServletRequest wrappedRequest = getWrappedRequest();
		assertThat(wrappedRequest.getHeader(XFORWARDED_HOST_HEADER), nullValue());
		assertThat(wrappedRequest.getServerName(), equalTo("example.com"));
		assertThat(wrappedRequest.getContextPath(), equalTo("/context"));
	}

	@Test
	public void xforwardedHeaderNoContext() throws Exception {
		request.addHeader(XFORWARDED_HOST_HEADER, "example.com");

		HttpServletRequest wrappedRequest = getWrappedRequest();
		assertThat(wrappedRequest.getHeader(XFORWARDED_HOST_HEADER), nullValue());
		assertThat(wrappedRequest.getServerName(), equalTo("example.com"));
		assertThat(wrappedRequest.getContextPath(), equalTo(""));
	}

	@Test
	public void noXforwardedHeader() throws Exception {
		HttpServletRequest wrappedRequest = getWrappedRequest();
		assertThat(wrappedRequest.getHeader(XFORWARDED_HOST_HEADER), nullValue());
		assertThat(wrappedRequest.getServerName(), equalTo("localhost"));
		assertThat(wrappedRequest.getContextPath(), equalTo(""));
	}

	private HttpServletRequest getWrappedRequest() throws IOException, ServletException {
		filter.doFilter(request, response, chain);
		return (HttpServletRequest) chain.getRequest();
	}

	static class NoOpServlet extends HttpServlet {
		HttpServletRequest request;
	}
}
