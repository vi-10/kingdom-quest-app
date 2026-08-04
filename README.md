# Kingdom Quest Application

## Overview

Kingdom Quest is a Spring Boot web application that allows users to create heroes, complete quests, forge items, and progress through a fantasy RPG-style system. Players can choose a hero class, complete class-specific quests, earn XP and gold, and craft equipment using a forging system.

The application is developed for the Spring Fundamentals Exam and demonstrates the use of Spring MVC, Thymeleaf, Spring Data JPA, validation, session-based authentication, role-based authorization, and layered architecture.

---

## Technology Stack

- Java 17
- Spring Boot 3.4.0
- Spring MVC
- Spring Data JPA
- Thymeleaf
- MySQL
- Maven
- HTML5
- CSS3

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

### Login / Logout
Users can log in using username and password. Session-based authentication is used.

---

### Dashboard
Displays user and hero information including level, XP, gold, and roleplay details.

---

### Quest System
- View available quests
- Complete quests based on hero class and level restrictions
- Gain XP and gold rewards
- Automatic level progression based on XP

---

### Forging System
- View available items
- Forge items using gold
- Items are added to hero inventory
- Class restrictions apply

---

### Inventory System
- Displays all items owned by a hero
- Shows item name and rarity

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

## Security

- Session-based authentication using HttpSession
- Role-based authorization (ADMIN / PLAYER)
- Admin endpoints protected via interceptor
- Inactive users are automatically blocked
- Passwords stored using BCrypt hashing

---

## Validation

All forms include server-side validation:

- Username length constraints
- Password strength validation
- Required fields for registration
- Quest validation (level, type, rewards)
- Item validation (cost, class restrictions)

Validation errors are displayed in the UI using Thymeleaf.

---

## Pages

- Kingdom Quest (Index)
- Login
- Register
- Dashboard
- Available Quests
- Quest Result
- Forge Items
- Inventory
- Users
- Quest Administration
  - Create Quest
  - Edit Quest
  - Delete Quest

---

## Relationships

- User → Hero (One-to-One)
- Hero → HeroItem (One-to-Many)
- HeroItem → Item (Many-to-One)

---

## Default Admin Account

- Username: admin
- Password: admin123

---

Database Setup

- Configure your MySQL credentials in application.properties before running the app

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
