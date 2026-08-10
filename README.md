# Kingdom Quest Application

## Overview

Kingdom Quest is a Spring Boot web application that allows users to create heroes, complete quests, forge items, manage their inventory, and participate in time-limited kingdom events.

Players can choose a hero class, complete class-specific quests, earn XP and gold, and craft equipment using a forging system. Administrators can manage users, quests, and kingdom events.

The project demonstrates the use of Spring MVC, Thymeleaf, Spring Data JPA, Spring Security, validation, REST communication, caching integration, scheduled functionality, and layered architecture.

Events Microservice:
https://github.com/vi-10/events-svc

---

## Architecture

The application follows a layered architecture:

Controllers — handle HTTP requests and return Thymeleaf views
Services — contain business logic
Repositories — provide database access through Spring Data JPA
DTOs — transfer data between application layers
Mappers — convert entities to DTOs
Security — handles authentication and role-based authorization

The Events functionality is separated into an independent REST microservice.

### Events Microservice

The main application communicates with the Events microservice through REST endpoints.

The microservice is responsible for:

Creating events
Editing events
Deleting events
Retrieving the active event
Automatically managing event status
Caching event data

The main application communicates with the microservice through a REST client.

---

## Technology Stack

### Main Application

- Java 17
- Spring Boot 3.4.0
- Spring MVC
- Spring Security
- Spring Data JPA
- Thymeleaf
- MySQL
- Maven
- HTML5
- CSS3
- REST client
- BCrypt

---

## Domain Entities

### User
Represents an application user.

**Properties:**
- UUID id
- username
- password
- email
- profilePicture
- role (PLAYER / ADMIN)
- server (EUROPE / ASIA_PACIFIC / AFRICA / NORTH_AMERICA / SOUTH_AMERICA)
- isActive
- hero (One-to-One relationship)

---

### Hero
Represents the player's in-game character.

**Properties:**
- UUID id
- roleplayName
- heroClass (WARRIOR / MAGE / ROGUE / HEALER)
- level
- xp
- gold
- user (One-to-One relationship)
- items (One-to-Many relationship)

---

### Quest
Represents a playable mission in the game.

**Properties:**
- UUID id
- title
- description
- questType (COMBAT / MAGIC / STEALTH / SUPPORT)
- requiredLevel
- rewardXp
- rewardGold

---

### Item
Represents forgeable equipment.

**Properties:**
- UUID id
- name
- heroClass
- requiredGold
- rarity

---

### HeroItem
Represents the relationship between heroes and owned items.

**Properties:**
- UUID id
- hero
- item

---

## Functionalities

### Registration
Users can create an account with a selected hero class and server. A default profile picture is assigned automatically based on hero class.

---

### Authentication

Users authenticate through Spring Security.

The authenticated user's information is accessed through Spring Security's AuthenticationUserDetails principal.

Controllers can obtain the currently authenticated user using:

Inactive users are prevented from accessing the application.

---

### Authorization

Role-based authorization is used throughout the application.

Administrative functionality is protected so that only users with the appropriate administrator role can access it.

---

### Dashboard
Displays user and hero information including level, XP, gold, and roleplay details.

---

### Quest System

Players can:

- View available quests
- Complete quests
- Earn XP
- Earn gold
- Progress through levels

Quest completion is restricted according to:

- Hero class
- Required level

Active kingdom events can provide additional XP and gold bonuses for matching quest types.

---

### Forging System

Players can:

- View available items
- Forge items using gold
- Receive forged items in their inventory

Items are restricted to compatible hero classes.

A player must also have enough gold to forge an item.

---

### Inventory System
- Displays all items owned by a hero
- Shows item name and rarity
- Allows users to drop items from their inventory

---

### Active event

Players can retrieve an anctive event and view its information.

---

### Admin Panel

#### Quest Management
Admins can:
- Create quests
- Edit existing quests
- Delete quests

Includes form-based UI with dropdown selection for editing/deleting quests.

---

#### User Management
Admins can:
- View all users
- Change user roles
- Activate / deactivate accounts

---

#### Event Management
Administrators can manage kingdom events through the main application's admin interface.

Admins can:
Create events
Edit events
Delete events

Event management is handled through the separate Events microservice.

The main application provides the administrative UI and delegates event operations to the microservice.

---

## Error Handling

The application uses custom exceptions and centralized exception handling to manage errors consistently.

Handled cases include missing resources, duplicate data, invalid input, unauthorized actions, and other business rule violations.

---

## Validation

All forms include server-side validation.

Examples include:

Username length constraints
Password strength validation
Required fields for registration
Quest validation (level, type, rewards)
Item validation (cost, class restrictions)
Event validation (title, description, quest type, rewards, start/end times)

Validation errors are displayed in the UI using Thymeleaf.

The Events REST microservice also validates incoming event creation and editing requests.

---

## Pages

- Kingdom Quest (Index)
- Login
- Register
- Dashboard
- Edit Profile
- Available Quests
- Quest Result
- Forge Items
- Inventory

### Admin pages
- Users
- Quest Administration
- Create Quest
- Edit Quest
- Delete Quest
- Active event
- Event Administration
- Create Event
- Edit Event
- Delete Event

---

## Relationships

- User → Hero (One-to-One)
- Hero → HeroItem (One-to-Many)
- HeroItem → Item (Many-to-One)

---

## Testing

The application contains multiple levels of automated tests:

Unit tests
Integration tests
API/controller tests

The tests cover the main services, controllers, validation, authentication, authorization, and REST communication.

---

## Default Admin Account

- Username: admin
- Password: admin123

---

## Database Setup

- Configure your MySQL credentials in application.properties before running the app
- The Events microservice also requires its own database configuration.

---

## Running the Application

Both applications must be running for the complete application functionality to work.

- Start the Events microservice.
- Start the main Kingdom Quest application.
- Access the Kingdom Quest web application.

---

## Future Improvements

- Quest history tracking
- Item rarity effects (stats system)
- Battle system (PvE encounters)
- Marketplace between players
- Equipment system (equip/unequip items)
- XP scaling formula improvements
- Filtering and search for items/quests
- Email notifications for admin actions
- Additional event types and effects
