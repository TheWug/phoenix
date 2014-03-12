package hello;


import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

public class CustomAuthenticationDetails extends WebAuthenticationDetails {

    private boolean twoFARequired = false;
    private boolean twoFAValid = false;
    private String sharedSecret = null;

    public CustomAuthenticationDetails(HttpServletRequest request) {super(request);}

    public boolean isTwoFARequired() {
        return twoFARequired;
    }

    public void setTwoFARequired(boolean twoFARequired) {
        this.twoFARequired = twoFARequired;
    }

    public boolean isTwoFAValid() {
        return twoFAValid;
    }

    public void setTwoFAValid(boolean twoFAValid) {
        this.twoFAValid = twoFAValid;
    }
    
    public String getSharedSecret() {
    	return sharedSecret;
    }
    
    public void setSharedSecret(String sharedSecret) {
    	this.sharedSecret = sharedSecret;
    }
}
