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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Rob Winch
 *
 */
public class XForwardedFilter extends OncePerRequestFilter implements Ordered {
	public static final String XFORWARDED_HOST_HEADER = "X-Forwarded-Host";
	public static final String XFORWARDED_PROTO_HEADER = "X-Forwarded-Proto";

	private int order = Ordered.HIGHEST_PRECEDENCE;

	/* (non-Javadoc)
	 * @see org.springframework.web.filter.OncePerRequestFilter#doFilterInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		filterChain.doFilter(new XForwardedRequestWrapper(request), response);
	}


	/**
	 * @author Rob Winch
	 *
	 */
	private static class XForwardedRequestWrapper extends HttpServletRequestWrapper {

		private final UriComponents components;
		private final String contextPath;
		private final String host;

		public XForwardedRequestWrapper(HttpServletRequest request) {
			super(request);
			this.components = UriComponentsBuilder
				.fromHttpRequest(new ServletServerHttpRequest(request))
				.build();

			String host = this.components.getHost();
			int contextPathIndexStart = host.indexOf("/");
			if(contextPathIndexStart > -1) {
				this.contextPath = host.substring(contextPathIndexStart, host.length());
				this.host = host.substring(0, contextPathIndexStart);
			} else {
				this.contextPath = request.getContextPath();
				this.host = host;
			}
		}

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletRequestWrapper#getHeader(java.lang.String)
		 */
		@Override
		public String getHeader(String name) {
			if(XFORWARDED_HOST_HEADER.equals(name)) {
				return null;
			}
			if(XFORWARDED_PROTO_HEADER.equals(name)) {
				return null;
			}
			return super.getHeader(name);
		}

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletRequestWrapper#getHeaderNames()
		 */
		@Override
		public Enumeration<String> getHeaderNames() {
			return super.getHeaderNames();
		}

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletRequestWrapper#getHeaders(java.lang.String)
		 */
		@Override
		public Enumeration<String> getHeaders(String name) {
			if(XFORWARDED_HOST_HEADER.equals(name)) {
				return Collections.enumeration(Collections.emptyList());
			}
			if(XFORWARDED_PROTO_HEADER.equals(name)) {
				return Collections.enumeration(Collections.emptyList());
			}
			return super.getHeaders(name);
		}

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletRequestWrapper#getRequestURI()
		 */
		@Override
		public String getRequestURI() {
			// TODO Auto-generated method stub
			return super.getRequestURI();
		}

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletRequestWrapper#getRequestURL()
		 */
		@Override
		public StringBuffer getRequestURL() {
			// TODO Auto-generated method stub
			return super.getRequestURL();
		}

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletRequestWrapper#getServletPath()
		 */
		@Override
		public String getServletPath() {
			// TODO Auto-generated method stub
			return super.getServletPath();
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequestWrapper#getProtocol()
		 */
		@Override
		public String getProtocol() {
			return components.getScheme();
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequestWrapper#getRemoteAddr()
		 */
		@Override
		public String getRemoteAddr() {
			// TODO Auto-generated method stub
			return super.getRemoteAddr();
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequestWrapper#getRemoteHost()
		 */
		@Override
		public String getRemoteHost() {
			// TODO Auto-generated method stub
			return super.getRemoteHost();
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequestWrapper#getRemotePort()
		 */
		@Override
		public int getRemotePort() {
			// TODO Auto-generated method stub
			return super.getRemotePort();
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequestWrapper#getScheme()
		 */
		@Override
		public String getScheme() {
			return components.getScheme();
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequestWrapper#getServerName()
		 */
		@Override
		public String getServerName() {
			return host;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequestWrapper#getServerPort()
		 */
		@Override
		public int getServerPort() {
			int port = components.getPort();
			if(port > 0) {
				return port;
			}
			try {
				return new URL(getScheme(), getRemoteHost(), "/").getDefaultPort();
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequestWrapper#isSecure()
		 */
		@Override
		public boolean isSecure() {
			return "https".equals(getScheme());
		}

		public String getContextPath() {
			return contextPath;
		}



//		@Override
//		public String getServletPath() {
//			String servletPath = super.getServletPath();
//			int start = servletPath.indexOf(contextPath);
//			if (start < 0) {
//				return servletPath;
//			}
//			int end = start + contextPath.length();
//			return servletPath.substring(end);
//		}
//
//		@Override
//		public String getContextPath() {
//			if
//			return contextPath;
//		}
	}


	/* (non-Javadoc)
	 * @see org.springframework.core.Ordered#getOrder()
	 */
	@Override
	public int getOrder() {
		// TODO Auto-generated method stub
		return 0;
	}
}
