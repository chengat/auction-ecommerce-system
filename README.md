# Auction E-commerce System

This repository contains the source code for an Auction E-commerce System built with a **Java backend** and **HTML/CSS/JavaScript frontend**. The system is designed for extensibility, security, and modularity, leveraging a three-tier architecture for two types of auction: Dutch Auction and Forward Auction. Also contains the containerized Docker Images.

---

## Major Design Decisions

- **Three-Tier Architecture:**  
  - **Presentation Layer:** HTML/CSS/JavaScript frontend for user interaction.
  - **Middle Layer:** Heavily modularized Java controllers and service modules for business logic.
  - **Data Layer:** Multiple databases accessed via a JDBC-based DatabaseController module.

- **Modular Middle Layer:**  
  Each module satisfies a different use case, exposing specific interfaces for controlled operations.

- **Database Connectivity and Security:**  
  - The DatabaseController connects to multiple databases via JDBC.
  - Pre-prepared database operations are exposed; direct queries from other modules are prohibited.
  - Business data (items, auctions, bids, payments) is stored separately from user credentials and session tokens for added security.

- **Authentication & Session Management:**  
  - Users are provisioned with random tokens upon login, representing their session.
  - Tokens enable tracking of simultaneous sessions and are associated with item bids, restricting duplicate transactions.
  - Security proxies and authentication interfaces provide modular, layered security.

- **Interfaces & Internal APIs:**  
  - Internal APIs are separated from client-facing APIs; only internal modules bypass token checks.
  - All client interfaces (except login) are protected by security proxies that enforce session validity.

## Technology Used:

Java, JDBC, MySQL, WebSockets, Servlets, HTML, CSS, RMI, Jersey REST APIs, Maven, Tomcat, Docker
---
