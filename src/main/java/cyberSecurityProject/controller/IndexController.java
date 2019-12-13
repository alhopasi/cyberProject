package cyberSecurityProject.controller;

import cyberSecurityProject.domain.Account;
import cyberSecurityProject.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@org.springframework.stereotype.Controller
public class IndexController {

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping(value = {"/*"})
    public String redirect() {
        return "redirect:/";
    }

    @GetMapping(value = {"/"})
    public String index(Model model) {
        final String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountRepository.findByUsername(currentUserName);
        model.addAttribute("userAccount", account);

        List<Account> accounts;
        accounts = accountRepository.findAll();

        model.addAttribute("accounts", accounts);
        System.out.println("Index page loaded");
        return "index";
    }


}
