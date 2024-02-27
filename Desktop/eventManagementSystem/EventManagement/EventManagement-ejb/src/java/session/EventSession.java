/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package session;

import entity.Customer;
import entity.Event;
import error.NoResultException;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 *
 * @author foozh
 */
@Stateless
public class EventSession implements EventSessionLocal {

    @PersistenceContext(unitName = "EventManagement-ejbPU")
    private EntityManager em;

    public void persist(Object object) {
        em.persist(object);
    }
    
    
    @Override
    public List<Event> searchEventsByTitle(String eventTitle) {
        Query q;
        if (eventTitle != null) {
            q = em.createQuery("SELECT e FROM Event e WHERE "
                    + "LOWER(e.eventTitle) LIKE :eventTitle");
            q.setParameter("eventTitle", "%" + eventTitle.toLowerCase() + "%");
        } else {
            q = em.createQuery("SELECT e FROM Event e");
        }

        return q.getResultList();
    } //end searchEventsByTitle
    
    @Override
    public List<Event> searchEventsByOrganiserName(String name) {
        
        TypedQuery<Event> query = em.createQuery(
                "SELECT e FROM Event e WHERE LOWER(e.organiser.name) LIKE LOWER(:name)", Event.class);
        query.setParameter("name", "%" + name + "%");
        return query.getResultList();
    } //end searchEventsByOrganiserName
    
    @Override
    public List<Event> getAllEvents() {
        TypedQuery<Event> query = em.createQuery("SELECT e FROM Event e", Event.class);
        return query.getResultList();
    } //end getAllEvents
    
    @Override
    public Event getEvent(Long eId) throws NoResultException {
        Event event = em.find(Event.class, eId);

        if (event != null) {
            return event;
        } else {
            throw new NoResultException("Not found");
        } 
    } //end getEvent
    
    @Override
    public void createEvent(Event e) {
        em.persist(e);   
    } //end createEvent
    
    @Override
    public void updateEvent(Event e) throws NoResultException {
        
        Event oldE = getEvent(e.getId());
        
        oldE.setEventTitle(e.getEventTitle());
        oldE.setEventDescription(e.getEventDescription());
        oldE.setEventLocation(e.getEventLocation());
        oldE.setEventDate(e.getEventDate());
        oldE.setDeadline(e.getDeadline());
        oldE.setMaxCapacity(e.getMaxCapacity());
        oldE.setCustomerRegistered(e.getCustomerRegistered());    
    } // end updateEvent
    
    @Override
    public List<Event> getEventsByCustomerId(Long cId) {
        // Create a JPQL query to get all events organized by the user with the given ID
        Query query = em.createQuery("SELECT e FROM Event e WHERE e.organiser.id = :cId");
        query.setParameter("cId", cId);
        return query.getResultList();
    } // end getEventsByCustomerId
        
    @Override
    public List<Customer> updateMissingCustomers(Event e) {
        
        Event managedEvent = em.find(Event.class, e.getId());
        if (managedEvent == null) {
            return new ArrayList<>();
        }

        List<Customer> registeredCustomers = managedEvent.getCustomerRegistered();
        List<Customer> attendedCustomers = managedEvent.getCustomerAttended();

        List<Customer> customersMissed = new ArrayList<>(registeredCustomers);
        customersMissed.removeAll(attendedCustomers);
        
        managedEvent.setCustomerMissed(customersMissed);
        em.merge(managedEvent);
        return customersMissed;
    } //end updateMissingCustomers
    
    
    
    
    

}
