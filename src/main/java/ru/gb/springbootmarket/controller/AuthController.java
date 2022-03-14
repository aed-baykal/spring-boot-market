package ru.gb.springbootmarket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.gb.springbootmarket.model.RegistrationToken;
import ru.gb.springbootmarket.service.RegisterService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class AuthController {

    private final RegisterService registerService;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);


    public AuthController(RegisterService registerService) {
        this.registerService = registerService;
    }

    public boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    @GetMapping("/login")
    public String loginForm() {
        return "registration/login";
    }

    @GetMapping("/register")
    public String registerForm() {
        return "registration/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String password2,
                           @RequestParam String email,
                           @RequestParam String adress,
                           Model model) {
        if (!validate(email)) {
            model.addAttribute("illegalStateException", new IllegalStateException("Не указана электронная почта"));
            return "registration/register";
        }
        if (password.equals(password2)) {
            try {
                registerService.signUp(username, password, email, adress);
                return "registration/register-confirm";
            } catch (IllegalStateException e) {
                model.addAttribute("illegalStateException", e);
                return "registration/register";
            }
        } else {
            model.addAttribute("illegalStateException", new IllegalStateException("Пароли не совпадают"));
            return "registration/register";
        }

    }

    @GetMapping("/register/confirm")
    public String registerConfirm(@RequestParam String token) {
        if (registerService.confirmRegistration(token)) {
            return "registration/register-complete";
        } else {
            RegistrationToken registrationToken = registerService.findRegistrationTokenByToken(token);
            if (registrationToken == null) return "redirect:/";
            registerService.resendingToken(registrationToken);
            return "redirect:/back_to_register";
        }
    }

    @GetMapping("/back_to_register")
    public String backToRegisterForm() {
        return "registration/back_to_register";
    }

}
