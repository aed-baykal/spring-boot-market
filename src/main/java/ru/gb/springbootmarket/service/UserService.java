package ru.gb.springbootmarket.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gb.springbootmarket.model.MarketUser;
import ru.gb.springbootmarket.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByLogin(username)
                .map(user -> new User(
                                user.getLogin(),
                                user.getPassword(),
                                user.getAuthorities().stream().map(authority -> new SimpleGrantedAuthority(authority.getName())).collect(Collectors.toSet())
                        )
                ).orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }

    public MarketUser findUserByUserName(String userName) {
        return userRepository.findByLogin(userName).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<MarketUser> getActiveManagers() {
        return userRepository.findAllFetchAuthority().stream()
                .filter(MarketUser::getEnabled)
                .filter(user -> user.getAuthorities().stream().anyMatch(authority -> authority.getName().equals("ROLE_MANAGER")))
                .collect(Collectors.toList());
    }

}
