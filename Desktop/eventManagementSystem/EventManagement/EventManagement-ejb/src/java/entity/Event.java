/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

/**
 *
 * @author foozh
 */
@Entity
public class Event implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    @Size(min = 1, max = 100)
    private String eventTitle;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date eventDate;
    
    @Column(nullable = false, length = 100)
    @Size(min = 1, max = 100)
    private String eventLocation;
    
    private String eventDescription;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date deadline;
    
    @ManyToMany
    @JoinTable(
            name = "event_customer_attended",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "customer_id")
    )
    private List<Customer> customerAttended;
    
    @ManyToMany
    @JoinTable(
            name = "event_customer_missed",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "customer_id")
    )
    private List<Customer> customerMissed;
    
    @ManyToMany(mappedBy = "eventsRegistered",cascade={CascadeType.MERGE},fetch = FetchType.EAGER)
    private List<Customer> customerRegistered;
    
    @ManyToOne(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    private Customer organiser;
    
    public Long getId() {
        return id;
    }

    public Event() {
        customerMissed = new ArrayList<>();
        customerAttended = new ArrayList<>();
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<Customer> getCustomerRegistered() {
        return customerRegistered;
    }

    public void setCustomerRegistered(List<Customer> customerRegistered) {
        this.customerRegistered = customerRegistered;
    }

    public Customer getOrganiser() {
        return organiser;
    }

    public void setOrganiser(Customer organiser) {
        this.organiser = organiser;
    }


    public List<Customer> getCustomerAttended() {
        return customerAttended;
    }

    public void setCustomerAttended(List<Customer> customerAttended) {
        this.customerAttended = customerAttended;
    }

    public List<Customer> getCustomerMissed() {
        return customerMissed;
    }

    public void setCustomerMissed(List<Customer> customerMissed) {
        this.customerMissed = customerMissed;
    }
   
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Event)) {
            return false;
        }
        Event other = (Event) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Event[ id=" + id + " ]";
    }
    
}
