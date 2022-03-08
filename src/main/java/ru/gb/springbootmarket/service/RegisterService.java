package ru.gb.springbootmarket.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gb.springbootmarket.model.Customer;
import ru.gb.springbootmarket.model.RegistrationToken;
import ru.gb.springbootmarket.repository.AuthorityRepository;
import ru.gb.springbootmarket.repository.RegistrationTokenRepository;
import ru.gb.springbootmarket.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RegisterService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthorityRepository authorityRepository;
    private final RegistrationTokenRepository registrationTokenRepository;
    private final EmailService emailService;

    public RegisterService(UserRepository userRepository,
                           BCryptPasswordEncoder bCryptPasswordEncoder,
                           AuthorityRepository authorityRepository,
                           RegistrationTokenRepository registrationTokenRepository,
                           EmailService emailService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.authorityRepository = authorityRepository;
        this.registrationTokenRepository = registrationTokenRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByLogin(username)
                .map(user -> new User(
                                user.getCustomer().getEmail(),
                                user.getPassword(),
                                user.getEnabled(), true, true, true,
                                user.getAuthorities().stream().map(authority ->
                                        new SimpleGrantedAuthority(authority.getName())).collect(Collectors.toSet())
                        )
                ).orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }

    @Transactional
    public void sighUp(String username, String password, String email, String adress) {
        boolean userExist = userRepository.findByLogin(username).isPresent();
        if (userExist) {
            throw new IllegalStateException("Пользователь уже существует");
        }
        var customer = new Customer();
        customer.setEmail(email);
        customer.setAddress(adress);
        var user = new ru.gb.springbootmarket.model.User();
        user.setCustomer(customer);
        user.setLogin(username);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setEnabled(false);
        user.setAuthorities(Set.of(authorityRepository.findByName("ROLE_USER")));
        userRepository.save(user);

        String tokenUid = UUID.randomUUID().toString();
        registrationTokenRepository.save(new RegistrationToken(tokenUid, LocalDateTime.now().plusMinutes(15), user));

        emailService.sendVarificationLink(user.getCustomer().getEmail(), tokenUid);
    }

    @Transactional
    public boolean confirmRegistration(String token) {
        var user = registrationTokenRepository.findUserByToken(LocalDateTime.now(), token);
        if (user.isEmpty()) {
            return false;
        }
        user.ifPresent(u -> u.setEnabled(true));
        return true;
    }

    public void resendingToken(RegistrationToken registrationToken) {
        ru.gb.springbootmarket.model.User user = registrationToken.getUser();
        registrationTokenRepository.delete(registrationToken);
        String tokenUid = UUID.randomUUID().toString();
        registrationTokenRepository.save(new RegistrationToken(tokenUid, LocalDateTime.now().plusMinutes(15), user));

        emailService.sendVarificationLink(user.getCustomer().getEmail(), tokenUid);
    }
}
