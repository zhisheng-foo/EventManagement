/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package session;

import entity.Customer;
import entity.Event;
import error.NoResultException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    public List<Event> searchEventsByTitleExcludingOrganizer(String eventTitle, Long organiserId) {
        Query q;
        if (eventTitle != null) {
            q = em.createQuery("SELECT e FROM Event e WHERE "
                    + "LOWER(e.eventTitle) LIKE :eventTitle AND e.organiser.id <> :organiserId");
            q.setParameter("eventTitle", "%" + eventTitle.toLowerCase() + "%");
            q.setParameter("organiserId", organiserId);
        } else {
            
            q = em.createQuery("SELECT e FROM Event e WHERE e.organiser.id <> :organiserId");
            q.setParameter("organiserId", organiserId);
        }

        return q.getResultList();
    } //end searchEventsByTitleExcludingOrganizer

    
    @Override
    public List<Event> searchEventsByOrganiserNameExcludingOrganiser(String name, Long excludeOrganiserId) {
        TypedQuery<Event> query = em.createQuery(
                "SELECT e FROM Event e WHERE LOWER(e.organiser.name) LIKE LOWER(:name) AND e.organiser.id <> :excludeOrganiserId", Event.class);
        query.setParameter("name", "%" + name + "%");
        query.setParameter("excludeOrganiserId", excludeOrganiserId);
        return query.getResultList();
    } //end searchEventsByOrganiserName
   
    @Override
    public List<Event> findAllEventsOrganisedByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        Query query = em.createQuery("SELECT e FROM Event e WHERE e.organiser.id = :id");
        query.setParameter("id", userId);

        return query.getResultList();
    }

    
    @Override
    public List<Event> getAllEventsExcludingOrganizer(Long organiserId) {
        TypedQuery<Event> query = em.createQuery("SELECT e FROM Event e WHERE e.organiser.id <> :organiserId", Event.class);
        query.setParameter("organiserId", organiserId);
        return query.getResultList();
    } //end getAllEventsExcludingOrganizer

    
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
        updateEventCustomers(oldE.getCustomerRegistered(), e.getCustomerRegistered()); // Use the actual Customer list
        updateEventCustomers(oldE.getCustomerMissed(), e.getCustomerMissed());
        updateEventCustomers(oldE.getCustomerAttended(), e.getCustomerAttended());

        em.merge(oldE);
    } // end updateEvent
    
    private void updateEventCustomers(List<Customer> oldCustomers, List<Customer> newCustomers) {
        oldCustomers.clear();
        newCustomers.stream().map(newCustomer -> em.find(Customer.class, newCustomer.getId())).forEachOrdered(managedCustomer -> {
            if (managedCustomer != null) {
                oldCustomers.add(managedCustomer);
            } else {
                // Handle the case where the customer might not be found in the database
                // This might involve logging, throwing an exception, or deciding to ignore
            }
        });
    }
    
    @Override
    public List<Event> getEventsByCustomerId(Long cId) {
        // Create a JPQL query to get all events organized by the user with the given ID
        Query query = em.createQuery("SELECT e FROM Event e WHERE e.organiser.id = :cId");
        query.setParameter("cId", cId);
        return query.getResultList();
    } // end getEventsByCustomerId
        
    
    @Override
    public List<Customer> updateAttendingCustomers(Event e , Customer c) {
        Event managedEvent = em.find(Event.class, e.getId());
        List<Customer> attendedCustomers = managedEvent.getCustomerAttended();
        
        attendedCustomers.add(c);
        e.getCustomerAttended().add(c);
        return managedEvent.getCustomerAttended();
    }
    
    @Override
    public List<Customer> updateMissingCustomers(Event e, Customer c) {
        Event managedEvent = em.find(Event.class, e.getId());
        List<Customer> missingCustomers = managedEvent.getCustomerMissed();

        missingCustomers.add(c);
        e.getCustomerMissed().add(c);
        return managedEvent.getCustomerMissed();
    }
    
    
    
    
    
    
    

}
