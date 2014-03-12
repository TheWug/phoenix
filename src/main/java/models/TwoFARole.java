package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;


@Entity(name = "authorities")
public class TwoFARole implements Serializable {

    @Id
    @OneToOne
    private TwoFAUser username;
    private String authority;

    public TwoFAUser getUsername() {
        return username;
    }

    public void setUsername(TwoFAUser username) {
        this.username = username;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}

