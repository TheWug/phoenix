package models;

import javax.persistence.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Entity(name="wolf")
@Table(name = "users")
public class NotTwoFAUser implements UserDetails {


    @Id
    private String username;
    private String password;
    private String sharedSecret;
    private boolean enabled;
    private boolean twoFactorEnabled;

    @OneToOne(mappedBy="username", cascade = {CascadeType.ALL})
    private TwoFARole authority;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSharedSecret() {
        return sharedSecret;
    }

    public void setSharedSecret(String sharedSecret) {
        this.sharedSecret = sharedSecret;
    }
    
    public boolean isTwoFactorEnabled() {
        return twoFactorEnabled;
    }

    public void setTwoFactorEnabled(boolean twoFactorEnabled) {
        this.twoFactorEnabled = twoFactorEnabled;
    }

    public TwoFARole getRole() {
        return authority;
    }

    public void setRole(TwoFARole role) {
        this.authority = role;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() { return enabled; }

    //public boolean getAuthorities() {
    //    return authority.getAuthority()
    //
    //
    // }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return TwoFAUserDetailsService.getAuthorities(authority.getAuthority());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}