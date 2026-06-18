package com.springboot.security;

import com.springboot.model.User;
import com.springboot.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class CustomUserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByName(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("User %s does not exist!", username));
        }
        return new UserRepositoryUserDetails(user);
    }

    private final static class UserRepositoryUserDetails extends User implements UserDetails {

        private static final long serialVersionUID = 1L;
        private User user;

        private UserRepositoryUserDetails(User user) {
            this.user = user;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return AuthorityUtils.createAuthorityList("ADMIN", "ROLE_USER");
        }

        @Override
        public String getUsername() {
            return this.user.getName();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;//not for production just to show concept
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;//not for production just to show concept
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;//not for production just to show concept
        }

        @Override
        public boolean isEnabled() {
            return true;//not for production just to show concept
        }

        @Override
        public String getPassword() {
            return this.user.getPassword();//not for production just to show concept
        }
    }
}
