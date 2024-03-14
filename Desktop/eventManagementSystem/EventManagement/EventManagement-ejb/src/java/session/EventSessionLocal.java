/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package session;


import entity.Customer;
import entity.Event;
import error.NoResultException;
import java.util.List;
import javax.ejb.Local;


/**
 *
 * @author foozh
 */
@Local
public interface EventSessionLocal {
    
    public List<Event> searchEventsByOrganiserNameExcludingOrganiser(String name, Long excludeOrganiserId);
    
    public List<Event> getAllEventsExcludingOrganizer(Long organiserId);
    
    public List<Event> searchEventsByTitleExcludingOrganizer(String eventTitle, Long organiserId);
    
    public Event getEvent(Long eId) throws NoResultException;
    
    public List<Customer> updateAttendingCustomers(Event e , Customer c);
    
    public List<Customer> removeAttendingCustomers(Event e, Customer c);
    
    public List<Customer> updateMissingCustomers(Event e, Customer c);
    
    public List<Customer> removeMissingCustomers(Event e, Customer c);
    
    public void createEvent(Event e);
    
    public void updateEvent(Event e) throws NoResultException;
    
    public List<Event> getEventsByCustomerId(Long cId);
    
    public List<Event> findAllEventsOrganisedByUserId(Long userId);
    
}
