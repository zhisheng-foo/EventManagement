package managedbean;

import entity.Customer;
import entity.Event;
import error.NoResultException;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import org.primefaces.model.file.UploadedFile;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.annotation.ManagedProperty;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.event.ActionEvent;
import javax.inject.Named;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.Part;

import org.primefaces.shaded.commons.io.FilenameUtils;
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
    private Part profilePhoto;
    private String email;
    private String password;
    private List<Event> eventsRegistered;
    private List<Event> eventsOrganised;
    private Long cId;
    
    private boolean showPassword = false;
    private String filename = "";
    
    @Inject
    private AuthenticationManagedBean authenticationManagedBean;
    
    public CustomerManagedBean() {
    }

    public AuthenticationManagedBean getAuthenticationManagedBean() {
        return authenticationManagedBean;
    }

    public void setAuthenticationManagedBean(AuthenticationManagedBean authenticationManagedBean) {
        this.authenticationManagedBean = authenticationManagedBean;
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

    public Part getProfilePhoto() {
        return profilePhoto;
    }

    public boolean isShowPassword() {
        return showPassword;
    }

    public void setShowPassword(boolean showPassword) {
        this.showPassword = showPassword;
    }
    
    public void setProfilePhoto(Part profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
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
    
    //this will handle the view the list of events registered
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
    
    public void togglePasswordVisibility() {
        showPassword = !showPassword;
    }

    public void setSelectedCustomer(Customer selectedCustomer) {
        this.selectedCustomer = selectedCustomer;
    }
    
    @PostConstruct
    public void init() {
        
        //loadEventsForOrganiser();
        loadSelectedCustomer();
    }
    /* 
    public void handleSearch() {
        init();
    } //end handleSearch
    */
    
    public void upload() throws IOException {
        ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

        //get the deployment path
        String UPLOAD_DIRECTORY = ctx.getRealPath("/") + "upload/";
        System.out.println("#UPLOAD_DIRECTORY : " + UPLOAD_DIRECTORY);

        //debug purposes
        setFilename(Paths.get(profilePhoto.getSubmittedFileName()).getFileName().toString());
        System.out.println("filename: " + getFilename());
        //---------------------

        //replace existing file
        Path path = Paths.get(UPLOAD_DIRECTORY + getFilename());
        InputStream bytes = profilePhoto.getInputStream();
        Files.copy(bytes, path, StandardCopyOption.REPLACE_EXISTING);
    }
    
    public boolean isPasswordStrong(String password) {
        String pattern = "^(?=.*[a-zA-Z])(?=.*[0-9]).{8,}$";
        return password.matches(pattern);
    }
    
    //this is for sign up 
    public void addCustomer(ActionEvent evt) throws IOException {

        if (name != null && name.length() > 100) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", "Name cannot be more than 100 characters"));
            return;
        }
        
        if (!isPasswordStrong(password)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", "Password is too weak. Minimum eight characters,inclusive of at least one letter and one number"));
            return;
        }
        
        List<Customer> customersWithSameName = customerSessionLocal.searchCustomers(name);
        if (!customersWithSameName.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Validation Error", "A customer with this name already exists."));
            name = "";
            contactDetails = "";
            email = "";
            password = "";
            return;
        }
        
        Customer c = new Customer();

        c.setName(name);
        c.setContactDetails(contactDetails);

        c.setProfilePhoto(null);
        c.setEmail(email);
        c.setPassword(password);
        
        //c.setProfilePhoto(profilePhoto);
        c.setEventsOrganised(new ArrayList<>());
        c.setEventsRegistered(new ArrayList<>());
        
      
        customerSessionLocal.createCustomer(c);
        
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        Flash flash = externalContext.getFlash();
        flash.setKeepMessages(true);
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Customer Added Successfully"));
        
        try {
            // Perform the redirect to the same page
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.redirect(ec.getRequestContextPath() + "/signup.xhtml");
            name = "";
            contactDetails = "";
            email = "";
            password = "";
        } catch (IOException e) {

        }
    }
 //end addCustomer
     
    //this is for view my user profile use case
    public void loadSelectedCustomer() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            cId = this.authenticationManagedBean.getUserId();
            System.out.println("Custmer Id :" + cId);
            selectedCustomer = this.customerSessionLocal.getCustomer(cId);
            name = this.selectedCustomer.getName();
            contactDetails = this.selectedCustomer.getContactDetails();
            email = this.selectedCustomer.getEmail();
            //this a little confusing but essentially is the filename
            filename = this.selectedCustomer.getProfilePhoto();
            System.out.println("Filename : " + filename);
            password = this.selectedCustomer.getPassword();
            eventsRegistered = this.selectedCustomer.getEventsRegistered();
            eventsOrganised = this.selectedCustomer.getEventsOrganised();
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to load customer"));
        }
    } //end loadSelectedCustomer
   
    //this is for edit my user profile usecase
    public void updateCustomer(ActionEvent evt) throws IOException {
        
        if (name != null && name.length() > 100) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", "Name cannot be more than 100 characters"));
            return;
        }
        
        if (!isPasswordStrong(password)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", "Password is too weak. Minimum eight characters,inclusive of at least one letter and one number"));
            return;
        }
        
        if (profilePhoto != null && profilePhoto.getSubmittedFileName() != null && !profilePhoto.getSubmittedFileName().trim().isEmpty()) {
            String submittedFileName = profilePhoto.getSubmittedFileName();
          
            // Use regex to check if the file extension is jpg, jpeg, or png
            if (!submittedFileName.matches("([^\\s]+(\\.(?i)(jpg|jpeg|png))$)")) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", "File is not in jpg/jpeg/png format"));
                return;
            }
            this.upload();
        } else {
            
            System.out.println("No new photo uploaded, keeping the existing one.");
        }
        
        List<Customer> customersWithSameName = customerSessionLocal.searchCustomers(name);
        boolean nameExists = customersWithSameName.stream()
                .anyMatch(customer -> !customer.getId().equals(selectedCustomer.getId()) && customer.getName().equalsIgnoreCase(name));

        if (nameExists) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Validation Error", "A customer with this name already exists."));
            name = selectedCustomer.getName();
            return;
        }
        FacesContext context = FacesContext.getCurrentInstance();
        selectedCustomer.setName(name);
        selectedCustomer.setContactDetails(contactDetails);
        selectedCustomer.setEmail(email);
        selectedCustomer.setProfilePhoto(filename);
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
    /*
    //handles list all events organised by customer usecase
    public void loadEventsForOrganiser() {
        
        if (selectedCustomer != null) {
            eventsOrganised = selectedCustomer.getEventsOrganised();
        } else {
           eventsOrganised = new ArrayList<>();
        }
    } //end loadEventsForOrganiser
    
    //This handles register for an event use case - add the constraints later
    public void registerEvent(Event e) throws NoResultException {
        customerSessionLocal.addEventRegistered(cId, e); 
        eventsRegistered = customerSessionLocal.getEventsRegisteredByCustomerId(cId);      
    }
    
    //This handles unregister for an event use case - add the constraints later
    public void unregisterEvent(Event e) throws NoResultException {
        customerSessionLocal.removeEventFromEventsRegistered(cId, e);
        eventsRegistered = customerSessionLocal.getEventsRegisteredByCustomerId(cId);
    } */
    public void validateNameLength() {
        if (name != null && name.length() > 100) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Exceed 100 characters", "The name must not exceed 100 characters."));
        }
    }

}
