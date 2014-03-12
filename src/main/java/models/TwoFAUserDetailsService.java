package models;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Service
public class TwoFAUserDetailsService implements UserDetailsService {

    private TwoFAUserRepository userRepository;

    @Autowired
    public TwoFAUserDetailsService(TwoFAUserRepository repository) {
        this.userRepository = repository;
    }
    /**
     * Returns a populated {@link UserDetails} object.
     * The username is first retrieved from the database and then mapped to
     * a {@link UserDetails} object.
     */
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            TwoFAUser domainUser = userRepository.findByUsername(username);

            boolean enabled = true;
            boolean accountNonExpired = true;
            boolean credentialsNonExpired = true;
            boolean accountNonLocked = true;

            return domainUser;
            /*
            return new User(
                    domainUser.getUsername(),
                    domainUser.getPassword(),
                    enabled,
                    accountNonExpired,
                    credentialsNonExpired,
                    accountNonLocked,
                    getAuthorities(domainUser.getRole().getAuthority()));
              */
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static Collection<? extends GrantedAuthority> getAuthorities(String role) {
        List<GrantedAuthority> authList = new ArrayList();
        authList.add(new SimpleGrantedAuthority(role));
        return authList;
    }
    
    public void save(TwoFAUser user) {
    	userRepository.save(user);
    }
/*
    private static List<GrantedAuthority> getGrantedAuthorities(List<String> roles) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }
    */

}