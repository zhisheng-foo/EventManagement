/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package managedbean;

import entity.Customer;
import entity.Event;
import error.NoResultException;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
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
    CustomerSessionLocal customerSessonLocal;
    
    
    private String eventTitle;
    private Date eventDate;
    private String eventLocation;
    private String eventDescription;
    private Date deadline;
    private Long maxCapacity;
    private List<Customer> registeredCustomer;
    private List<Customer> attendedCustomers;
    private List<Customer> missedCustomers;
    private Customer organiser;
    private Event selectedEvent;
    
    private String searchString; // The search string entered by the user
    private String searchType;
    
    private List<Event> events;
    
    private Long eId;
    
    
    public EventManagedBean() {
    }

    public EventSessionLocal getEventSessionLocal() {
        return eventSessionLocal;
    }

    public void setEventSessionLocal(EventSessionLocal eventSessionLocal) {
        this.eventSessionLocal = eventSessionLocal;
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

    public Long getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Long maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public List<Customer> getRegisteredCustomer() {
        return registeredCustomer;
    }

    public void setRegisteredCustomer(List<Customer> registeredCustomer) {
        this.registeredCustomer = registeredCustomer;
    }

    public Customer getOrganiser() {
        return organiser;
    }

    public void setOrganiser(Customer organiser) {
        this.organiser = organiser;
    }

    public CustomerSessionLocal getCustomerSessonLocal() {
        return customerSessonLocal;
    }

    public void setCustomerSessonLocal(CustomerSessionLocal customerSessonLocal) {
        this.customerSessonLocal = customerSessonLocal;
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
    
    //Search for an event via title name or organiser name
    @PostConstruct
    public void init() {
        if (searchString == null || searchString.isEmpty()) {
            events = eventSessionLocal.getAllEvents(); // Method to fetch all events if no search criteria
        } else {
            switch (searchType) {
                case "TITLE":
                    events = eventSessionLocal.searchEventsByTitle(searchString);
                    break;
                case "ORGANISER":
                    events = eventSessionLocal.searchEventsByOrganiserName(searchString);
                    break;
                default:
                    events = new ArrayList<>(); 
                    break;
            }
        }
        
    }

    public void handleSearch() {
        init();
    } //end handleSearch
    
    //handles add an event usecase - set the constraints later when you implement it
    public void addEvent(ActionEvent evt) throws NoResultException {
        Event e = new Event();
        
        e.setEventTitle(eventTitle);
        e.setEventDate(eventDate);
        e.setEventLocation(eventLocation);
        e.setEventDescription(eventDescription);
        e.setDeadline(deadline);
        e.setMaxCapacity(maxCapacity);
        e.setCustomerRegistered(registeredCustomer);
        
        //you dun have to setOrganiser as it is handled in addEventOrganised
        
        customerSessonLocal.addEventOrganised(organiser.getId(),e); 
        eventSessionLocal.createEvent(e);
      
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
            customerSessonLocal.removeEventFromEventOrganised(eId, eventToDelete);
            context.addMessage(null, new FacesMessage("Success", "Successfully deleted event"));
        } catch (Exception e) {
            
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to delete event"));
            return;
        }
        init();
    }
    
 
    // handles view details of an event usecase
    public void loadSelectedEvent() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            this.selectedEvent
                    = eventSessionLocal.getEvent(eId);
            eventTitle = this.selectedEvent.getEventTitle();
            eventDate = this.selectedEvent.getEventDate();
            eventLocation = this.selectedEvent.getEventLocation();
            eventDescription = this.selectedEvent.getEventDescription();
            maxCapacity = this.selectedEvent.getMaxCapacity();
            attendedCustomers = this.selectedEvent.getCustomerAttended();
            missedCustomers = this.eventSessionLocal.updateMissingCustomers(this.selectedEvent);
            organiser = this.selectedEvent.getOrganiser();
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to load event"));
        }
    }
    
    //Mark a user as present or unmark user as present for an event
    //this handles attendedCustomer, do not confuse with registeredCustomers
    public void toggleCustomerAttendance(Long customerId, Long eventId) {
        try {
            Event event = eventSessionLocal.getEvent(eventId);
            Customer customer = customerSessonLocal.getCustomer(customerId);

            if (event != null && customer != null) {
                
                if (event.getCustomerAttended().contains(customer)) {
                    event.getCustomerAttended().remove(customer);
                } else {
                    event.getCustomerAttended().add(customer);
                }
                eventSessionLocal.updateEvent(event);

                // Recalculate missed customers since the attendance list changed
                missedCustomers = eventSessionLocal.updateMissingCustomers(event);
                attendedCustomers = event.getCustomerAttended();

                selectedEvent = event;

            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Event or Customer not found"));
            }
        } catch (Exception e) {
            // Handle any exceptions
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to toggle attendance status"));
        }
    }
    
    //bonus feature - edit the event itself but with some constraints , some stuff you cannot edit
    public void updateEvent(ActionEvent evt) {
        FacesContext context = FacesContext.getCurrentInstance();
        selectedEvent.setEventTitle(eventTitle);
        selectedEvent.setEventDescription(eventDescription);
        
        //cannot see below the the number of people registered
        selectedEvent.setMaxCapacity(maxCapacity);
        selectedEvent.setCustomerRegistered(registeredCustomer);
        selectedEvent.setCustomerAttended(attendedCustomers);
        selectedEvent.setCustomerMissed(missedCustomers);
     
        try {
            eventSessionLocal.updateEvent(selectedEvent);
        } catch (Exception e) {
            //show with an error icon
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to update Event"));
            return;
        }
        //need to make sure reinitialize the customers collection
        init();
        context.addMessage(null, new FacesMessage("Success",
                "Successfully updated Event"));
    } //end updateCustomer 
    

    
    
    
}
