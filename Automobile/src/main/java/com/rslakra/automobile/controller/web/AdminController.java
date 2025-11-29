package com.rslakra.automobile.controller.web;

import com.rslakra.automobile.domain.entities.AutoUser;
import com.rslakra.automobile.service.AuthService;
import com.rslakra.automobile.service.security.context.ContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Admin Controller - Hidden admin functionality.
 *
 * @author Rohtash Lakra
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);

    private final AuthService authService;

    /**
     * @param authService
     */
    @Autowired
    public AdminController(AuthService authService) {
        LOGGER.debug("AdminController({})", authService);
        this.authService = authService;
    }

    /**
     * Shows the hidden admin registration page.
     * Access via: /admin/register
     *
     * @return
     */
    @GetMapping("/register")
    public String adminRegisterPage() {
        LOGGER.debug("adminRegisterPage()");
        return "admin-register";
    }

    /**
     * Registers a new admin user.
     *
     * @param autoUser
     * @return
     */
    @PostMapping("/register")
    public String adminRegister(@ModelAttribute AutoUser autoUser) {
        LOGGER.debug("+adminRegister({})", autoUser);
        // Force role to ADMIN
        autoUser.setRole("ADMIN");
        autoUser.setStatus("ACTIVE");
        autoUser = authService.register(autoUser);
        Authentication authentication = ContextUtils.INSTANCE.getAuthentication();
        LOGGER.debug("-adminRegister() authentication: {}, redirect:/", authentication);
        return "redirect:/login?admin=true";
    }

}

