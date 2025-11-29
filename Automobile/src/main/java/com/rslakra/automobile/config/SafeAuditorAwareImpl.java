package com.rslakra.automobile.config;

import com.rslakra.appsuite.spring.context.AuditorAwareImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Safe implementation of AuditorAware that handles null authentication gracefully.
 *
 * @author Rohtash Lakra
 */
public class SafeAuditorAwareImpl extends AuditorAwareImpl {

    /**
     * Returns the current auditor (logged-in user) or "system" if not authenticated.
     *
     * @return
     */
    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() 
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.of("system");
        }
        return super.getCurrentAuditor();
    }
}

