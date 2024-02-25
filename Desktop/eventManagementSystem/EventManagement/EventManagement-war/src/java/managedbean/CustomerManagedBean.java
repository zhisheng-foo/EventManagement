/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package managedbean;

import entity.Customer;
import entity.Event;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;

import javax.faces.view.ViewScoped;
import session.CustomerSessionLocal;

/**
 *
 * @author foozh
 */
@Named(value = "customerManagedBean")
@ViewScoped
public class CustomerManagedBean implements Serializable {

    @EJB
    CustomerSessionLocal customerSessionLocal;
    private String name;
    private String contactDetails;
    private Customer selectedCustomer;
    private String profilePhoto;
    private String email;
    private String password;
    private List<Event> eventsRegistered;
    private List<Event> eventsOrganised;
    private Long cId;
    
    public CustomerManagedBean() {
    }
    
    public Long getcId() {
        return cId;
    }

    public void setcId(Long cId) {
        this.cId = cId;
    }

    public CustomerSessionLocal getCustomerSessionLocal() {
        return customerSessionLocal;
    }

    public void setCustomerSessionLocal(CustomerSessionLocal customerSessionLocal) {
        this.customerSessionLocal = customerSessionLocal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(String contactDetails) {
        this.contactDetails = contactDetails;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Event> getEventsRegistered() {
        return eventsRegistered;
    }

    public void setEventsRegistered(List<Event> eventsRegistered) {
        this.eventsRegistered = eventsRegistered;
    }

    public List<Event> getEventsOrganised() {
        return eventsOrganised;
    }

    public void setEventsOrganised(List<Event> eventsOrganised) {
        this.eventsOrganised = eventsOrganised;
    }

    public Customer getSelectedCustomer() {
        return selectedCustomer;
    }

    public void setSelectedCustomer(Customer selectedCustomer) {
        this.selectedCustomer = selectedCustomer;
    }
    
    @PostConstruct
    public void init() {
    
    }
    
    public void handleSearch() {
        init();
    } //end handleSearch
    
    //this is for sign up - you might need to act more constraints for this when you implement
    public void addCustomer(ActionEvent evt) {
        Customer c = new Customer();
        
        c.setName(name);
        c.setContactDetails(contactDetails);
        c.setProfilePhoto(profilePhoto);
        c.setEmail(email);
        c.setPassword(password);
        c.setEventsOrganised(eventsOrganised);
        c.setEventsRegistered(eventsRegistered);
        
        customerSessionLocal.createCustomer(c); 
    } //end addCustomer
      
    //this is for view my user profile use case
    public void loadSelectedCustomer() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            this.selectedCustomer
                    = customerSessionLocal.getCustomer(cId);
            name = this.selectedCustomer.getName();
            contactDetails = this.selectedCustomer.getContactDetails();
            email = this.selectedCustomer.getEmail();
            password = this.selectedCustomer.getPassword();
            eventsRegistered = this.selectedCustomer.getEventsRegistered();
            eventsOrganised = this.selectedCustomer.getEventsOrganised();
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to load customer"));
        }
    } //end loadSelectedCustomer
    
    //this is for edit my user profile usecase
    public void updateCustomer(ActionEvent evt) {
        FacesContext context = FacesContext.getCurrentInstance();
        selectedCustomer.setName(name);
        selectedCustomer.setContactDetails(contactDetails);
        selectedCustomer.setProfilePhoto(profilePhoto);
        selectedCustomer.setEmail(email);
        selectedCustomer.setPassword(password);
        selectedCustomer.setEventsOrganised(eventsOrganised);
        selectedCustomer.setEventsRegistered(eventsRegistered);
        
        try {
            customerSessionLocal.updateCustomer(selectedCustomer);
        } catch (Exception e) {
            //show with an error icon
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to update customer"));
            return;
        }
        //need to make sure reinitialize the customers collection
        init();
        context.addMessage(null, new FacesMessage("Success",
                "Successfully updated customer"));
    } //end updateCustomer
    
    
}
