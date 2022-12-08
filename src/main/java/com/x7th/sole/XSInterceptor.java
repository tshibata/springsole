package com.x7th.sole;

import java.net.URL;
import java.net.MalformedURLException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class XSInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ForbiddenException {
		if (! request.getMethod().equals("GET")) {
			try {
				URL src = new URL(request.getHeader("Referer"));
				URL dst = new URL(request.getRequestURL().toString());
				if (! src.getHost().equals(dst.getHost())) {
					throw new ForbiddenException();
				}
			} catch (MalformedURLException ex) {
				throw new ForbiddenException();
			}
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
	}
}
