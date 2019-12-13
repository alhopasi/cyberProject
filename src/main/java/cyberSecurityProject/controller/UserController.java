package cyberSecurityProject.controller;

import cyberSecurityProject.domain.Account;
import cyberSecurityProject.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;

@org.springframework.stereotype.Controller
public class UserController {

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping(value = {"/login"})
    public String login() {
        System.out.println("Login page loaded");
        return "login";
    }

    @PostMapping(value = {"/login/new"})
    public String newAccount(@RequestParam String newUsername, @RequestParam String newPassword) {
        System.out.println("Creating new");
        if (accountRepository.findByUsername(newUsername) != null) {
            System.out.println("Error, username " + newUsername + " exists");
            return "redirect:/login";
        }

        Account account = new Account();
        account.setUsername(newUsername);
        account.setPassword(encoder.encode(newPassword));
        accountRepository.save(account);
        return "redirect:/login";
    }

    @PostMapping(value = {"/remove"})
    public String removeAccount(@RequestParam String id) {
        Account account = getAccount(id);
        accountRepository.delete(account);
        return "redirect:/";
    }

    @GetMapping(value = {"/account/{id}"})
    public String getAccountInfo(Model model, @PathVariable String id) {
        Account account = getAccount(id);
        model.addAttribute(account);
        return "account";
    }



    @PostMapping(value = {"/account/{id}/edit"})
    public String editAccountInfo(Model model, @PathVariable String id, @RequestParam String newUsername, @RequestParam String newCreditcard, @RequestParam String newPassword, @RequestParam String password) {
        final String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountRepository.findByUsername(currentUserName);
        if (account.getId() != Long.valueOf(id)) {
            return "editError";
        }
        if (!encoder.matches(password, account.getPassword())) {
            return "editError";
        }
        if (account.getUsername() != newUsername) {
            account.setUsername(newUsername);
        }
        if (account.getCreditcard() != newCreditcard) {
            account.setCreditcard(newCreditcard);
        }
        if (!newPassword.isEmpty()) {
            account.setPassword(encoder.encode(newPassword));
        }
        accountRepository.save(account);
        changeCurrentUser(account.getUsername(), account.getPassword());
        return "redirect:/account/" + id;
    }

    private Account getAccount(String id) {
        long userId = Long.valueOf(id);
        return accountRepository.findById(userId).get();
    }

    private void changeCurrentUser(String username, String password) {
        Collection<SimpleGrantedAuthority> nowAuthorities =(Collection<SimpleGrantedAuthority>)SecurityContextHolder
                .getContext().getAuthentication().getAuthorities();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, password, nowAuthorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
