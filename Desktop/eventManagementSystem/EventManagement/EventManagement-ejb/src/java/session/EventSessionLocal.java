/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package session;


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
    
    public List<Event> searchEvents(String eventTitle);
    
    public Event getEvent(Long eId) throws NoResultException;
    
    public void createEvent(Event e);
    
    public void updateEvent(Event e) throws NoResultException;
    
}
