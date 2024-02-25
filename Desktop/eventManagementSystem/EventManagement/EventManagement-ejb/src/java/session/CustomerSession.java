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

/**
 *
 * @author foozh
 */
@Stateless
public class CustomerSession implements CustomerSessionLocal {

    @PersistenceContext(unitName = "EventManagement-ejbPU")
    private EntityManager em;

    public void persist(Object object) {
        em.persist(object);
    }
    
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
    } // end updateCustomer
    
    @Override
    public void addEventRegistered(Long cId, Event e) throws NoResultException {
        Customer attendee = getCustomer(cId);
        
        if (attendee == null) {
            throw new NoResultException("Customer (Attendee)not found for ID: " + cId);
        }
        
        //this persistence is redundant but I am just going to leave it here just in case for the time being
        em.persist(e);
        
        attendee.getEventsRegistered().add(e);
        e.getAttendees().add(attendee);
        
        em.merge(attendee);
        em.merge(e);
       
    } // end addEventRegistered
    
    @Override
    public void addEventOrganised(Long cId, Event e ) throws NoResultException {
        Customer organiser = getCustomer(cId);
        
         if (organiser == null) {
            throw new NoResultException("Customer (Organiser) not found for ID: " + cId);
        }
         
        //this persistence is redundant but I am just going to leave it here just in case for the time being
        em.persist(e);
        
        organiser.getEventsOrganised().add(e);
        e.setOrganiser(organiser);
        
        em.merge(organiser);
        em.merge(e);
    } //end addEventOrganised
    
    //Note that the event is not yet removed from the database
    @Override
    public void removeEventFromEventsRegistered(Long cId, Event e) throws NoResultException {
        Customer attendee = getCustomer(cId);

        if (attendee == null) {
            throw new NoResultException("Customer (Attendee) not found for ID: " + cId);
        }

        if (!attendee.getEventsRegistered().contains(e)) {
            throw new IllegalArgumentException("Event not registered by the customer.");
        }

        attendee.getEventsRegistered().remove(e);

        e.getAttendees().remove(attendee);

        em.merge(attendee);
        em.merge(e);
    } // end removeEventFromEventsRegistered
    
    //this works as  a deleteEvent method 
    @Override
    public void removeEventFromEventOrganised(Long cId, Event e) throws NoResultException {
        Customer organiser = getCustomer(cId);

        if (organiser == null) {
            throw new NoResultException("Customer (Organiser) not found for ID: " + cId);
        }

        if (!organiser.getEventsOrganised().contains(e)) {
            throw new IllegalArgumentException("Event not organised by the customer.");
        }
        
        organiser.getEventsOrganised().remove(e);

        e.getAttendees().clear(); 

        em.remove(em.contains(e) ? e : em.merge(e));
        
        em.merge(organiser);
    } // end removeEventFromEventOrganised
    
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
            e.getAttendees().remove(customer);
            return e;
        }).forEachOrdered(e -> {
            em.merge(e);
        });

        em.remove(customer);
    } // end deleteCustomer


    
    

}
