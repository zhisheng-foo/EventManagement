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
    private List<Customer> attendees;
    private Customer organiser;
    private List<Event> eventsByOrganiser;
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

    public List<Customer> getAttendees() {
        return attendees;
    }

    public void setAttendees(List<Customer> attendees) {
        this.attendees = attendees;
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

    public List<Event> getEventsByOrganiser() {
        return eventsByOrganiser;
    }

    public void setEventsByOrganiser(List<Event> eventsByOrganiser) {
        this.eventsByOrganiser = eventsByOrganiser;
    }
   
    @PostConstruct
    public void init() {

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
        e.setAttendees(attendees);
        
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
    
    //handles list all events organised by customer usecase
    public void loadEventsForOrganiser() {
        if (organiser != null) {
            eventsByOrganiser = eventSessionLocal.getEventsByCustomerId(organiser.getId());
        } else {
            // Handle the case where there is no authenticated user
            eventsByOrganiser = new ArrayList<>();
        }
    }   
}
