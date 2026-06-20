package com.av.investment_tracker.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    private SecurityUtils(){}

    public static Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }
        if (!(authentication.getPrincipal() instanceof UserPrincipal userPrincipal)) {
            throw new RuntimeException("Invalid authentication principal");
        }
        return userPrincipal.getUser().getId();
    }

}
