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
public interface CustomerSessionLocal {
    
    public List<Customer> searchCustomers(String name);
    
    public Customer getCustomer(Long cId) throws NoResultException;
    
    public void createCustomer(Customer c);
    
    public void updateCustomer(Customer c) throws NoResultException;
    
    public void addEventRegistered(Long eId, Event e) throws NoResultException;
    
    public void addEventOrganised(Long cId, Event e) throws NoResultException;
    
    public void removeEventFromEventsRegistered(Long cId, Event e) throws NoResultException;
    
    public void removeEventFromEventOrganised(Long cId, Event e) throws NoResultException;
   
    public void deleteCustomer(Long cId) throws NoResultException;
}
