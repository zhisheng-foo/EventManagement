# Event Management System
Name : Foo Zhi Sheng

Student : A0252699L

The Event Management System (EMS) is a comprehensive software solution designed to streamline the planning, organization, and execution of events. 

## Tech Stack Used
- Java
- Jakarta EE

## Database Details and Instructions for deploying project 

### Database Configuration:
- **Database Name:** eventDB
- **Username:** administrator
- **Password:** password

### Instructions for Deploying the Project:

1. **Clone the Repository:** Clone the project repository from GitHub.

2. **Ensure GlassFish 5.1.0 is Installed:** Make sure that GlassFish 5.1.0 application server is installed and configured on your system.

3. **Connect to eventDB:**
   - Ensure that the `eventDB` database is set up and accessible.
   - Configure the database connection settings in the project configuration files, such as `persistence.xml` or `glassfish-resources.xml`, to connect to the `eventDB` database. 
   
4. **Run the Event Management Module:**
   - Deploy the Event Management Module to GlassFish 5.1.0:
   
## Core Features

1. **User Management:**
   - **Sign up:** Users can create a new account to access the system.
   - **Login:** Registered users can log in to their accounts securely.
   - **Logout:** Users can log out of their accounts to end their session.
   - **View My User Profile:** Users can view their profile information, including name, contact details, profile photo, and email.
   - **Edit My User Profile:** Users can update their profile information as needed.

2. **Event Listing Management (for Event Organizers):**
   - **Add an Event:** Event organizers can create new events, providing details such as title, date, location, description, and registration deadline.
   - **Delete an Event:** Event organizers can remove events from the system.
   - **List All Events Created by User:** Event organizers can view a list of events they have created.
   - **View Event Details:** Event organizers can see the details of each event, including attendee lists and registration status.
   - **Mark User as Present:** Event organizers can mark users as present for an event.
   - **Unmark User as Present:** Event organizers can remove a user's attendance status for an event.

3. **Event Registration (for Event Attendees):**
   - **Search for Events:** Users can search for events based on various criteria.
   - **Register for an Event:** Users can sign up to attend an event before the registration deadline.
   - **Unregister for an Event:** Users can cancel their registration for an event before it starts.
   - **View List of Registered Events:** Users can see a list of events they have registered for.

## Bonus Features
- Enhanced Validation Constraints: Implement extensive validation constraints to ensure data integrity and prevent errors throughout the system. 
- Password Masking: Enhance security by implementing password masking functionality to conceal user passwords while typing.

##Image Preview 

![My Photo](https://drive.google.com/file/d/1yp26sXWPCY-NdDo1xFO4AnOGXYYu4eOK/view?usp=drive_link)

