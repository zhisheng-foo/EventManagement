/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author foozh
 */
@Entity
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    @NotNull
    @Size(min = 1, max = 100)
    private String name;
    
    @Column(length = 300)
    @Size(min = 1, max = 300)
    private String contactDetails;
    
  
    private String profilePhoto;
    
    @Column(length = 200)
    @Size(min = 1, max = 200)
    private String email;
    
    @Column(nullable = false, length = 100)
    @Size(min = 1, max = 100)
    private String password;
    
    @ManyToMany(cascade={CascadeType.MERGE},fetch = FetchType.EAGER)
    private List<Event> eventsRegistered;
    
    @OneToMany(mappedBy = "organiser", cascade={CascadeType.MERGE},fetch = FetchType.EAGER)
    private List<Event> eventsOrganised;

    public Customer() {
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(String contactDetails) {
        this.contactDetails = contactDetails;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public List<Event> getEventsRegistered() {
        return eventsRegistered;
    }

    public void setEventsRegistered(List<Event> eventsRegistered) {
        this.eventsRegistered = eventsRegistered;
    }
    
    public List<Event> getEventsOrganised() {
        return eventsOrganised;
    }

    public void setEventsOrganised(List<Event> eventsOrganised) {
        this.eventsOrganised = eventsOrganised;
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
        if (!(object instanceof Customer)) {
            return false;
        }
        Customer other = (Customer) object;
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
