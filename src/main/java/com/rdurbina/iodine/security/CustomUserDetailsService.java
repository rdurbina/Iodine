package com.rdurbina.iodine.security;

import com.rdurbina.iodine.model.User;
import com.rdurbina.iodine.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        User user = this.userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found")
        );
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                /* TODO: Define the Role entity and return the roles dynamically,
                 *  This here was temporarily written like this for the sake of completion.
                 */
                List.of(new SimpleGrantedAuthority("ROLE_EXAMPLE"))
        );
    }
}
