package managedbean;

import entity.Customer;
import error.NoResultException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.ejb.EJB;
import session.CustomerSessionLocal;

@Named(value = "authenticationManagedBean")
@SessionScoped
public class AuthenticationManagedBean implements Serializable {

    private String username = null;
    private String password = null;
    @EJB
    CustomerSessionLocal customerSessionLocal;
    private long userId = -1;

    public AuthenticationManagedBean() {
    }

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

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
    
    public String login() {
        try {
            // Use the customerSessionLocal bean to check the credentials
            Customer customer = customerSessionLocal.validateLogin(username, password);

            if (customer != null) {
                // Login successful

                // Store the logged-in user's ID
                userId = customer.getId(); 

                // Redirect to the secret page
                return "/secret/secret.xhtml?faces-redirect=true";
            } else {
                // Login failed - customer is null (not found or invalid credentials)
                username = null;
                password = null;
                userId = -1;
                return "login.xhtml";
            }
        } catch (NoResultException e) {
            // Handle the case where no customer is found
            username = null;
            password = null;
            userId = -1;
            return "login.xhtml";
        }
    } // end login

    public String logout() {
        username = null;
        password = null;
        userId = -1;

        return "/login.xhtml?faces-redirect=true";
    } //end logout
}
