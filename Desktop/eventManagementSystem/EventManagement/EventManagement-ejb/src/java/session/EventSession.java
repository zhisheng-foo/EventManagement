/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package session;

import entity.Event;
import error.NoResultException;
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
public class EventSession implements EventSessionLocal {

    @PersistenceContext(unitName = "EventManagement-ejbPU")
    private EntityManager em;

    public void persist(Object object) {
        em.persist(object);
    }
    
    @Override
    public List<Event> searchEvents(String eventTitle) {
        Query q;
        if (eventTitle != null) {
            q = em.createQuery("SELECT e FROM Event e WHERE "
                    + "LOWER(e.eventTitle) LIKE :eventTitle");
            q.setParameter("eventTitle", "%" + eventTitle.toLowerCase() + "%");
        } else {
            q = em.createQuery("SELECT e FROM Event e");
        }

        return q.getResultList();
    } //end searchEvents
    
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
        oldE.setAttendees(e.getAttendees());    
    }
    
    
    

}
