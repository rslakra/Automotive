package com.rslakra.automobile.controller.web;

import com.rslakra.automobile.domain.repositories.ServiceTypeRepository;
import com.rslakra.automobile.domain.repositories.UserRepository;
import com.rslakra.automobile.service.security.context.ContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    private final UserRepository userRepository;
    private final ServiceTypeRepository serviceTypeRepository;

    /**
     * @param userRepository
     * @param serviceTypeRepository
     */
    @Autowired
    public HomeController(UserRepository userRepository, ServiceTypeRepository serviceTypeRepository) {
        LOGGER.debug("HomeController({}, {})", userRepository, serviceTypeRepository);
        this.userRepository = userRepository;
        this.serviceTypeRepository = serviceTypeRepository;
    }

    /**
     * @return
     */
    @GetMapping
    public String homePage() {
        return "home";
    }

    /**
     * Displays the register page.
     *
     * @return
     */
    @GetMapping({"/register", "/register/"})
    public String registerPage() {
        return "register";
    }

    /**
     * @return
     */
    @GetMapping({"/login", "/login/"})
    public String loginPage() {
        return "login";
    }

    /**
     * @param model
     * @return
     */
    @GetMapping("/index")
    public String loginPageRedirect(Model model) {
//        return "redirect:/index";
        ContextUtils.getDomain().ifPresent(domain -> {
            model.addAttribute("domain", domain);
        });

//        return "redirect:/login";
        return "redirect:/";
    }

    /**
     * @param model
     * @return
     */
    @RequestMapping("/auth/index")
    public String indexPage(Model model) {
        ContextUtils.getDomain().ifPresent(domain -> {
            model.addAttribute("domain", domain);
        });

        return "home";
    }

}
