package com.av.investment_tracker.user.service;

import com.av.investment_tracker.security.UserPrincipal;
import com.av.investment_tracker.user.model.User;
import com.av.investment_tracker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@NullMarked
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                            new UsernameNotFoundException("User not found with email: "+email)
                        );
        return new UserPrincipal(user);
    }
}
