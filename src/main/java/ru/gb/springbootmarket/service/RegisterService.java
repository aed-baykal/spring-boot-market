package ru.gb.springbootmarket.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gb.springbootmarket.model.Customer;
import ru.gb.springbootmarket.model.MarketUser;
import ru.gb.springbootmarket.model.RegistrationToken;
import ru.gb.springbootmarket.repository.AuthorityRepository;
import ru.gb.springbootmarket.repository.RegistrationTokenRepository;
import ru.gb.springbootmarket.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static ru.gb.springbootmarket.enums.EmailType.USER_REGISTRATION;

@Service
public class RegisterService {

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

    @Transactional
    public void signUp(String username, String password, String email, String adress) {
        boolean userExist = userRepository.findByLogin(username).isPresent();
        if (userExist) {
            throw new IllegalStateException("Пользователь уже существует");
        }
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setAddress(adress);
        MarketUser user = new MarketUser();
        user.setCustomer(customer);
        user.setLogin(username);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setEnabled(false);
        user.setAuthorities(Set.of(authorityRepository.findByName("ROLE_USER")));
        userRepository.save(user);

        String tokenUid = UUID.randomUUID().toString();
        registrationTokenRepository.save(new RegistrationToken(tokenUid, LocalDateTime.now().plusMinutes(15), user));

        emailService.sendMail(USER_REGISTRATION, Map.of("token", tokenUid), List.of(email));
    }

    @Transactional
    public boolean confirmRegistration(String token) {
        Optional<MarketUser> user = registrationTokenRepository.findUserByToken(LocalDateTime.now(), token);
        if (user.isEmpty()) {
            return false;
        }
        user.ifPresent(u -> u.setEnabled(true));
        return true;
    }

    public void resendingToken(RegistrationToken registrationToken) {
        MarketUser marketUser = registrationToken.getMarketUser();
        registrationTokenRepository.delete(registrationToken);
        String tokenUid = UUID.randomUUID().toString();
        registrationTokenRepository.save(new RegistrationToken(tokenUid, LocalDateTime.now().plusMinutes(15), marketUser));

        emailService.sendMail(USER_REGISTRATION, Map.of("token", tokenUid), List.of(marketUser.getCustomer().getEmail()));
    }

    public RegistrationToken findRegistrationTokenByToken(String token) {
        return registrationTokenRepository.findRegistrationTokenByToken(token);
    }
}
