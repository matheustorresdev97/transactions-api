package com.matheustorres.transactions.services;

import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class SessionService {

    private static final String SESSION_COOKIE_NAME = "sessionId";
    private static final int COOKIE_MAX_AGE = 7 * 24 * 60 * 60;

    public UUID getOrCreateSessionId(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (SESSION_COOKIE_NAME.equals(cookie.getName())) {
                    try {
                        return UUID.fromString(cookie.getValue());
                    } catch (IllegalArgumentException e) {
                        break;
                    }
                }
            }
        }

        UUID sessionId = UUID.randomUUID();
        Cookie sessionCookie = new Cookie(SESSION_COOKIE_NAME, sessionId.toString());
        sessionCookie.setHttpOnly(true);
        sessionCookie.setSecure(false); // true em produção com HTTPS
        sessionCookie.setPath("/");
        sessionCookie.setMaxAge(COOKIE_MAX_AGE);
        response.addCookie(sessionCookie);

        return sessionId;
    }
}
