package com.taskService.interceptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.auth.AuthenticationException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import redis.clients.jedis.Jedis;

public class RequestProcessingInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	RedisTemplate<String, Object> template;

	private static final Logger logger = Logger
			.getLogger(RequestProcessingInterceptor.class);

	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws AuthenticationException {
		long startTime = System.currentTimeMillis();

		if (!request.getRequestURI().equals("/api/taskapp/user/**")) {

			String auth_key = request.getHeader("auth_key");

			Jedis jedis = new Jedis("localhost", 6379);
			jedis.connect();

			if (auth_key != null) {
				System.out.println(jedis.exists(auth_key));
				Map<String, String> jedismap = jedis.hgetAll(auth_key);

				// System.out.println("jedismap ::"+jedismap.keySet());
				if (!jedis.exists(auth_key)) {
					throw new AuthenticationException("Bad credentials");
				}
			}

		}

		/*
		 * logger.info("preHandle Request URL::" +
		 * request.getRequestURL().toString() + ":: Start Time=" +
		 * System.currentTimeMillis()); logger.info(request);
		 * 
		 * request.setAttribute("startTime", startTime);
		 * 
		 * Enumeration<String> parameters = request.getParameterNames(); while
		 * (parameters.hasMoreElements()) { String currentParameter =
		 * parameters.nextElement(); logger.info("Request parameter::" +
		 * currentParameter + " :: " + request.getParameter(currentParameter));
		 * }
		 * 
		 * Enumeration<String> headers = request.getHeaderNames(); while
		 * (headers.hasMoreElements()) { String currentHeader =
		 * headers.nextElement(); logger.info("Request Header::" + currentHeader
		 * + " :: " + request.getHeader(currentHeader)); }
		 */
		// if returned false, we need to make sure 'response' is sent
		return true;
	}

}
