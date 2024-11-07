package com.devland.assignment.finalproject.applicationuser;

import com.devland.assignment.finalproject.applicationuser.exception.EmailAlreadyRegisteredException;
import com.devland.assignment.finalproject.applicationuser.exception.UsernameAlreadyExistException;
import com.devland.assignment.finalproject.applicationuser.model.ApplicationUser;
import com.devland.assignment.finalproject.authentication.model.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApplicationUserService implements UserDetailsService {
    private final ApplicationUserRepository applicationUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserDetails loadUserByUsername(String username) {
        ApplicationUser applicationUser = this.applicationUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username: " + username + " not found."));

        return UserPrincipal.build(applicationUser);
    }

    public ApplicationUser findByUsername(String username) {
        return this.applicationUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username: " + username + " not found."));
    }

    public ApplicationUser create(ApplicationUser newUser) {
        Optional<ApplicationUser> existingUser = this.applicationUserRepository.findByUsername(newUser.getUsername());

        if (existingUser.isPresent()) {
            throw new UsernameAlreadyExistException("Username " + newUser.getUsername() + " is already exist");
        }

        existingUser = this.applicationUserRepository.findByEmail(newUser.getEmail());

        if (existingUser.isPresent()) {
            throw new EmailAlreadyRegisteredException(newUser.getEmail() + " is already registered to a different account");
        }

        newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));

        return this.applicationUserRepository.save(newUser);
    }

    public ApplicationUser save(ApplicationUser existingUser) {
        return this.applicationUserRepository.save(existingUser);
    }
}