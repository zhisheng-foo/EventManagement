package managedbean;

import entity.Customer;
import error.NoResultException;
import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.annotation.ManagedProperty;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import session.CustomerSessionLocal;


@Named(value = "authenticationManagedBean")
@SessionScoped
public class AuthenticationManagedBean implements Serializable {

    private String username = "";
    private String password = "";
    @EJB
    CustomerSessionLocal customerSessionLocal;
    private long userId = -1;
    
    @Inject
    private CustomerManagedBean customerManagedBean;
    
    
    
    public AuthenticationManagedBean() {
    }
    
    @PostConstruct
    public void init() {
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
    
    //login use case - login does provide the UI for wrong username / password 
    public String login() throws IOException, NoResultException {
        FacesContext context = FacesContext.getCurrentInstance();
        

        // Check for username
        if (username == null || username.trim().isEmpty()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Username Required", "Please enter a username."));
        }

        // Check for password
        if (password == null || password.trim().isEmpty()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Password Required", "Please enter a password."));    
        }
       
        Customer customer = customerSessionLocal.validateLogin(username, password);
        
        username = "";
        password = "";

        if (customer != null) {
            userId = customer.getId();
            context.getExternalContext().redirect("./secret/mainpage.xhtml");
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Login Error", "Password or username is wrong"));
            userId = -1;
        }
        return "index.xhtml"; 
    }// end login
    
    //logout use case
    public String logout() {
        username = "";
        password = "";
        userId = -1;

        return "/index.xhtml?faces-redirect=true";
    } //end logout
    
    //direct to sign up page
    public String goToSignUp() {
        username = "";
        password = "";
        return "signup?faces-redirect=true";
    }
    
    public String goToLogin() {
        username = "";
        password = "";
        return "index?faces-redirect=true";
    }
    
    public String goToEventListing() {
        return "eventlistingmanagement?faces-redirect=true";
    }
}
