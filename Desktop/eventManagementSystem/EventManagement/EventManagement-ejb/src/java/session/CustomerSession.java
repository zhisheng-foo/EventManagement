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
public class CustomerSession implements CustomerSessionLocal {

    @PersistenceContext(unitName = "EventManagement-ejbPU")
    private EntityManager em;

    //you can use this method to check whether a customer exist too
    @Override
    public List<Customer> searchCustomers(String name) {
        Query q;
        if (name != null) {
            q = em.createQuery("SELECT c FROM Customer c WHERE "
                    + "LOWER(c.name) LIKE :name");
            q.setParameter("name", "%" + name.toLowerCase() + "%");
        } else {
            q = em.createQuery("SELECT c FROM Customer c");
        }
        
        return q.getResultList();
    } //end searchCustomers
    
    @Override
    public Customer validateLogin(String username, String password) throws NoResultException {
                       
        TypedQuery<Customer> query = em.createQuery("SELECT c FROM Customer c WHERE c.name = :username AND c.password = :password", Customer.class);
        query.setParameter("username", username);
        query.setParameter("password", password);
        try {
            return (Customer) query.getSingleResult();
        } catch (Exception ex) {
            return null;
        }
        
    }

    @Override
    public Customer getCustomer(Long cId) throws NoResultException {
        Customer cust = em.find(Customer.class, cId);

        if (cust != null) {
            return cust;
        } else {
            throw new NoResultException("Not found");
        }
    } //end getCustomer
    
    @Override
    public void createCustomer(Customer c) {
        em.persist(c);
    } //end createCustomer
    
    @Override
    public void updateCustomer(Customer c) throws NoResultException {
        Customer oldC = getCustomer(c.getId());
        
         oldC.setName(c.getName());
         oldC.setContactDetails(c.getContactDetails());
         oldC.setProfilePhoto(c.getProfilePhoto());
         oldC.setEmail(c.getEmail());
         oldC.setPassword(c.getPassword());
         oldC.setEventsOrganised(c.getEventsOrganised());
         oldC.setEventsRegistered(c.getEventsRegistered());
         
         em.merge(oldC);
    } // end updateCustomer
    
    @Override
    public void addEventRegistered(Long cId, Event e) throws NoResultException {
        Customer attendee = getCustomer(cId);
        Event eventToAdd = em.find(Event.class, e.getId());
        
        if (attendee == null) {
            throw new NoResultException("Customer (Attendee)not found for ID: " + cId);
        }
        
        eventToAdd.getCustomerRegistered().add(attendee);
        attendee.getEventsRegistered().add(eventToAdd);
        
        
        em.merge(attendee);
        em.merge(eventToAdd);
    } // end addEventRegistered
    
    @Override
    public void addEventOrganised(Long cId, Event e ) throws NoResultException {
        Customer organiser = getCustomer(cId);
        Event managedEvent = em.find(Event.class, e.getId());
        
         if (organiser == null) {
            throw new NoResultException("Customer (Organiser) not found for ID: " + cId);
        }
         
        organiser.getEventsOrganised().add(managedEvent);
        //e.setOrganiser(organiser);
        
        em.merge(organiser);
        em.merge(managedEvent);
    } //end addEventOrganised
    
    //Note that the event is not yet removed from the database
    @Override
    public void removeEventFromEventsRegistered(Long cId, Event e) throws NoResultException {
        Customer attendee = getCustomer(cId); 
        Event eventToRemove = em.find(Event.class, e.getId());

        if (attendee == null) {
            throw new NoResultException("Customer (Attendee) not found for ID: " + cId);
        }

        if (!attendee.getEventsRegistered().contains(eventToRemove)) {
            throw new IllegalArgumentException("Event not registered by the customer.");
        }
        
        attendee.getEventsRegistered().remove(eventToRemove);
        eventToRemove.getCustomerRegistered().remove(attendee);
        
        em.merge(eventToRemove);
        em.merge(attendee);
        
    } // end removeEventFromEventsRegistered
    
    //this works as  a deleteEvent method 
    @Override
    public void removeEventFromEventOrganised(Long cId, Event e) throws NoResultException {
        
        Customer organiser = em.find(Customer.class, cId);
        Event managedEvent = em.find(Event.class, e.getId());

        if (organiser == null) {
            throw new NoResultException("Customer (Organiser) not found for ID: " + cId);
        }

        if (managedEvent == null) {
            throw new NoResultException("Event not found for ID: " + e.getId());
        }
 
        if (!organiser.getId().equals(managedEvent.getOrganiser().getId())) {
            throw new IllegalArgumentException("Event not organised by the customer.");
        }

        organiser.getEventsOrganised().remove(managedEvent);
        
        // Remove the event from each customer's list of events registered
        for (Customer customer : managedEvent.getCustomerRegistered()) {
            if(customer.getEventsRegistered().contains(managedEvent)) {
            
                customer.getEventsRegistered().remove(managedEvent);
                em.merge(customer);
            }
            
        }
        managedEvent.getCustomerRegistered().clear();
        

        em.remove(managedEvent);
        em.merge(organiser);
    }

 // end removeEventFromEventOrganised
    
    @Override
    public void deleteCustomer(Long cId) throws NoResultException {
        Customer customer = em.find(Customer.class, cId);

        if (customer == null) {
            throw new NoResultException("Customer not found for ID: " + cId);
        }
        
        new ArrayList<>(customer.getEventsOrganised()).forEach(e -> {    
            em.remove(e);
        });

        new ArrayList<>(customer.getEventsRegistered()).stream().map(e -> {
            e.getCustomerRegistered().remove(customer);
            return e;
        }).forEachOrdered(e -> {
            em.merge(e);
        });

        em.remove(customer);
    } // end deleteCustomer

    
    @Override
    public List<Event> getEventsRegisteredByCustomerId(Long cId){
        TypedQuery<Event> query = em.createQuery(
            "SELECT e FROM Event e JOIN e.customerRegistered c WHERE c.id = :cId", Event.class);
        query.setParameter("cId", cId);
        return query.getResultList();
    
    }
}
