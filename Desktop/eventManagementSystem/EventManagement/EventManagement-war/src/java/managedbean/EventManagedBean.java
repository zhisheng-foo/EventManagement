/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package managedbean;

import entity.Customer;
import entity.Event;
import error.NoResultException;
import java.io.IOException;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import session.CustomerSessionLocal;
import session.EventSessionLocal;

/**
 *
 * @author foozh
 */
@Named(value = "eventManagedBean")
@ViewScoped
public class EventManagedBean implements Serializable {
    
    @EJB
    EventSessionLocal eventSessionLocal;
    
    @EJB
    CustomerSessionLocal customerSessionLocal;
    
    private String eventTitle;
    private Date eventDate;
    private String eventLocation;
    private String eventDescription;
    private Date deadline;
    private List<Customer> registeredCustomers;
    private List<Customer> attendedCustomers;
    private List<Customer> missedCustomers;
    
    private long organiserId;
    private Event selectedEvent;
    
    private String searchString; 
    private String searchType = "TITLE";
    private String validationMessage;
    
    private List<Event> events;
    
    private Long eId;
    
    @Inject
    private CustomerManagedBean customerManagedBean;
    
    public EventManagedBean() {
    }

    public EventSessionLocal getEventSessionLocal() {
        return eventSessionLocal;
    }

    public void setEventSessionLocal(EventSessionLocal eventSessionLocal) {
        this.eventSessionLocal = eventSessionLocal;
    }

    public CustomerManagedBean getCustomerManagedBean() {
        return customerManagedBean;
    }

    public void setCustomerManagedBean(CustomerManagedBean customerManagedBean) {
        this.customerManagedBean = customerManagedBean;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public List<Customer> getRegisteredCustomers() {
        return registeredCustomers;
    }

    public void setRegisteredCustomers(List<Customer> registeredCustomers) {
        this.registeredCustomers = registeredCustomers;
    }

    public long getOrganiserId() {
        return organiserId;
    }

    public void setOrganiserId(long organiserId) {
        this.organiserId = organiserId;
    }

    public CustomerSessionLocal getCustomerSessionLocal() {
        return customerSessionLocal;
    }

    public void setCustomerSessionLocal(CustomerSessionLocal customerSessionLocal) {
        this.customerSessionLocal = customerSessionLocal;
    }

    public Long geteId() {
        return eId;
    }

    public void seteId(Long eId) {
        this.eId = eId;
    }
    
    public Event getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(Event selectedEvent) {
        this.selectedEvent = selectedEvent;
    }

    public List<Customer> getAttendedCustomers() {
        return attendedCustomers;
    }

    public void setAttendedCustomers(List<Customer> attendedCustomers) {
        this.attendedCustomers = attendedCustomers;
    }

    public List<Customer> getMissedCustomers() {
        return missedCustomers;
    }

    public void setMissedCustomers(List<Customer> missedCustomers) {
        this.missedCustomers = missedCustomers;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public String getValidationMessage() {
        return validationMessage;
    }

    public void setValidationMessage(String validationMessage) {
        this.validationMessage = validationMessage;
    }
    
    public Date getToday() {
        return new Date(); // Returns today's date
    }
      
    @PostConstruct
    public void init() {
        String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
        Long customerId = this.customerManagedBean.getSelectedCustomer().getId();
        if (viewId != null && viewId.endsWith("/eventlistingmanagement.xhtml")) {
            
            this.events = eventSessionLocal.findAllEventsOrganisedByUserId(customerId);
            
        } else if (viewId != null && viewId.endsWith("/eventregistration.xhtml")) {
            if (searchString == null || searchString.equals("")) {
                
                this.events = eventSessionLocal.getAllEventsExcludingOrganizer(customerId);
            } else {
            
                switch (searchType) {
                    case "TITLE":
                        events = eventSessionLocal.searchEventsByTitleExcludingOrganizer(searchString , customerId);
                        break;
                    case "ORGANISER":
                        events = eventSessionLocal.searchEventsByOrganiserNameExcludingOrganiser(searchString, customerId);
                        break;
                    default:
                        events = new ArrayList<>(); 
                        break;
                } 
            } 
        
        }
    }
        
    

    public void handleSearch() {
        init();
    } //end handleSearch
    
    //handles add an event usecase - set the constraints later when you implement it
    public void addEvent(ActionEvent evt) throws NoResultException {
        if (eventTitle != null && eventTitle.length() > 100) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", "Name cannot be more than 100 characters"));
            return;
        }
        
        Event e = new Event();
        
        e.setEventTitle(eventTitle);
        e.setEventDate(eventDate);
        e.setEventLocation(eventLocation);
        e.setEventDescription(eventDescription);
        e.setDeadline(deadline);
        
        long cId = this.customerManagedBean.getcId();
        this.setOrganiserId(cId);
        e.setOrganiser(this.customerSessionLocal.getCustomer(organiserId));
        e.getOrganiser().getEventsOrganised().add(e);
        this.customerManagedBean.getSelectedCustomer().getEventsOrganised().add(e);
        eventSessionLocal.createEvent(e);
        
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        Flash flash = externalContext.getFlash();
        flash.setKeepMessages(true);
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Event Added Successfully"));
        
        try {
            // Perform the redirect to the same page
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.redirect(ec.getRequestContextPath() + "/secret/createEvent.xhtml");
            eventTitle = "";
            eventDate = null;
            eventLocation = "";
            eventDescription = "";
            deadline = null;
        } catch (IOException ex) {

        }
    } //end addEvent
    
    
    //handles deleteEvent use case
    public void deleteEvent() {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String eIdStr = params.get("eId");
        
        if (eIdStr == null || eIdStr.isEmpty()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Event ID is missing"));
            return;
        }

        eId = Long.parseLong(eIdStr);
        Event eventToDelete;
        
        try {
            // Retrieve the event from the database
            eventToDelete = eventSessionLocal.getEvent(eId);
            customerSessionLocal.removeEventFromEventOrganised(customerManagedBean.getSelectedCustomer().getId(), eventToDelete);
            init();
            context.addMessage(null, new FacesMessage("Success", "Successfully deleted event"));
        } catch (NoResultException e) {
            
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to delete event"));
            return;
        }
    }
        
    // handles view details of an event usecase 
    public void loadSelectedEvent() {
       
        eventTitle = this.selectedEvent.getEventTitle();
        eventDate = this.selectedEvent.getEventDate();
        eventLocation = this.selectedEvent.getEventLocation();
        eventDescription = this.selectedEvent.getEventDescription();
        attendedCustomers = this.selectedEvent.getCustomerAttended();
        missedCustomers = this.selectedEvent.getCustomerMissed();
        registeredCustomers = this.selectedEvent.getCustomerRegistered();
        organiserId = this.selectedEvent.getOrganiser().getId();
    }
    
    public void markAsPresent(Long cId) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            
            Customer customer = customerSessionLocal.getCustomer(cId);
            if(selectedEvent.getCustomerAttended().contains(customer)) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Customer already marked as present"));
                return;
            }
             
            if(selectedEvent.getCustomerMissed().contains(customer)) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Customer is already marked as not present , revert back first"));
                return;
            }
             
            if(!selectedEvent.getCustomerAttended().contains(customer)) {
                
                attendedCustomers = eventSessionLocal.updateAttendingCustomers(selectedEvent ,customer);
                this.getEventSessionLocal().updateEvent(selectedEvent);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Customer is marked as present"));
            }  
           
        } catch (Exception e) {
            
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Selected Customer is not marked as present"));
          
        }
    }
    
    public void revertPresent(Long cId) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            
            Customer customer = customerSessionLocal.getCustomer(cId);
            if(selectedEvent.getCustomerAttended().contains(customer)) {
                attendedCustomers = eventSessionLocal.removeAttendingCustomers(selectedEvent, customer);
                this.getEventSessionLocal().updateEvent(selectedEvent); 
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Revert successful"));
            }
        } catch (Exception e) {
            
            System.out.print("Selected Customer is not marked as present");
          
        }
    }
    
    public void markAsMissing(Long cId) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            
            Customer customer = customerSessionLocal.getCustomer(cId);
            if(selectedEvent.getCustomerMissed().contains(customer)) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Customer already marked as not present"));
            }
            
            
            if (selectedEvent.getCustomerAttended().contains(customer)) {

                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Customer is already marked as present , revert back first"));
                return;
            }
            if (!selectedEvent.getCustomerMissed().contains(customer)) {
                missedCustomers = eventSessionLocal.updateMissingCustomers(selectedEvent, customer);
                this.getEventSessionLocal().updateEvent(selectedEvent);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Customer is marked as missing"));     
            }
        } catch (NoResultException e) {

            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Customer is not marked as missing"));
        }
    }
    
    public void revertMissing(Long cId) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {

            Customer customer = customerSessionLocal.getCustomer(cId);
            if (selectedEvent.getCustomerMissed().contains(customer)) {
                attendedCustomers = eventSessionLocal.removeMissingCustomers(selectedEvent, customer);
                this.getEventSessionLocal().updateEvent(selectedEvent);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Revert successful"));
            }
        } catch (Exception e) {

            System.out.print("Selected Customer is not marked as present");

        }
    }  
}
