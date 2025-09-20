
<img width="960" height="418" alt="TRAIN BOOKING PIC" src="https://github.com/user-attachments/assets/3c7ffcd8-3cd8-4b32-be70-fab427c4de67" />
# 🚆 Train Booking System

## 📖 Introduction
The **Train Booking System** is a web-based application designed to modernize the way passengers book train tickets. Traditionally, train tickets in Nigeria (and many parts of Africa) are purchased manually at railway stations, which is inconvenient, time-consuming, and prone to errors.  

This project provides a **digital solution** by offering an online platform where passengers can:
- Create accounts and manage profiles  
- Search for available trains and schedules  
- Book tickets seamlessly  
- Receive confirmation notifications  

Built with **Spring Boot** and **MySQL**, and deployed on **Render** with secure database hosting via **Aiven**, this system demonstrates the integration of backend technologies, database management, and cloud deployment for solving real-world problems.

---

## 🛑 Problem Statement
In Nigeria, passengers often face difficulties when trying to travel by train:
- **Manual Booking Process:** Passengers must physically go to the station to purchase tickets.  
- **Time Wastage:** Queues and long waiting times make the experience stressful.  
- **Limited Accessibility:** Only those physically present at the station can book a seat.  
- **Inefficient Management:** Railway staff manually handle reservations, making it prone to errors, double-booking, or loss of records.  

This traditional system is outdated and unable to meet the demands of a modern, digital society.

---

## 💡 Motivation
The idea for this project came from **personal experience**: during a trip from **Ibadan to Lagos**, I discovered that the booking process was still largely manual. This motivated me to challenge myself to build a **realistic train booking platform** that could solve the pain points I encountered.  

I wanted to build not just a school project but a **practical application** that mirrors real-world scenarios. The project also allowed me to practice:
- **Backend development** with Spring Boot  
- **Database design** for relational systems  
- **Authentication and security** using JWT  
- **Cloud deployment** (Render + Aiven)  

---

## ✅ Proposed Solution
The Train Booking System addresses the identified problems by:
- Providing a **secure online registration and login** system.  
- Allowing users to **browse available trains and schedules** remotely.  
- Enabling **online reservations** without needing to be physically present.  
- Sending **email confirmations** for account activation and bookings.  
- Empowering admins with tools to **manage trains, schedules, and users**.  

This ensures convenience, accessibility, and efficiency in the train ticketing process.

---

## 🛠️ Tech Stack
The project is built with a robust and scalable stack:

### **Backend**
- **Spring Boot** – Core framework for building the REST API  
- **Spring Security + JWT** – Authentication and authorization  
- **Hibernate/JPA** – Object Relational Mapping (ORM) for database operations  

### **Database**
- **MySQL** – Relational database for storing users, trains, bookings, and payments  
- **Aiven (Managed MySQL)** – Secure cloud-hosted database with SSL support  

### **Deployment**
- **Render** – Cloud hosting platform for deploying the Spring Boot application  
- **Docker** (optional, if containerized)  

### **Build & Tools**
- **Maven** – Dependency management and build tool  
- **Java 17** – Programming language  
- **Lombok** – Reduces boilerplate code (getters, setters, constructors)  

### **Other Integrations**
- **Email Service** – For sending activation and booking confirmation emails  
- **Validation APIs** – To enforce strong password, email, and ID rules 
 

---

