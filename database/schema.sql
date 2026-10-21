-- =========================================================================
-- Eventora — Campus Event Management & Registration Application
-- Database Schema (MySQL 8.x)
-- =========================================================================
-- This script creates the database and relational tables with foreign keys
-- to demonstrate Referential Integrity, One-to-Many, and Many-to-Many relationships.
-- =========================================================================

CREATE DATABASE IF NOT EXISTS event_management_db;
USE event_management_db;

-- -------------------------------------------------------------------------
-- Table 1: events
-- Represents the core Event entity.
-- Primary Key: event_id (Uniquely identifies each event).
-- registration_fee: 0.0 indicates a Free event; > 0.0 indicates a Paid event.
-- -------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS events (
    event_id VARCHAR(50) PRIMARY KEY,
    event_name VARCHAR(150) NOT NULL,
    description TEXT,
    event_date DATE NOT NULL,
    venue VARCHAR(150) NOT NULL,
    organizer_name VARCHAR(100) NOT NULL,
    registration_fee DOUBLE DEFAULT 0.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- -------------------------------------------------------------------------
-- Table 2: participants (Students)
-- Represents Student participants who sign up, log in, and register for events.
-- Primary Key: participant_id
-- Unique Constraint: email is UNIQUE to avoid duplicate user accounts.
-- -------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS participants (
    participant_id VARCHAR(50) PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(30),
    password VARCHAR(100) NOT NULL DEFAULT 'student123',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- -------------------------------------------------------------------------
-- Table 3: admins (Administrators)
-- Pre-created administrator account for managing events, sessions, and payments.
-- Default account: username: admin / password: admin123
-- -------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS admins (
    admin_id VARCHAR(50) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Pre-seed default Administrator account
INSERT INTO admins (admin_id, username, password, full_name, email)
SELECT 'ADM-001', 'admin', 'admin123', 'System Administrator', 'admin@eventora.com'
WHERE NOT EXISTS (SELECT 1 FROM admins WHERE username = 'admin');

-- -------------------------------------------------------------------------
-- Table 4: sessions
-- Demonstrates COMPOSITION and a ONE-TO-MANY Relationship:
-- An Event contains many Sessions.
-- Foreign Key: event_id references events(event_id).
-- ON DELETE CASCADE ensures if an event is deleted, its sessions are removed.
-- -------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sessions (
    session_id VARCHAR(50) PRIMARY KEY,
    event_id VARCHAR(50) NOT NULL,
    session_title VARCHAR(150) NOT NULL,
    session_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    venue VARCHAR(150) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_session_event FOREIGN KEY (event_id) 
        REFERENCES events(event_id) ON DELETE CASCADE
);

-- -------------------------------------------------------------------------
-- Table 5: registrations
-- Demonstrates ASSOCIATION and a MANY-TO-MANY Relationship:
-- Connects Participants with Events.
-- Unique Key (event_id, participant_id): Prevents duplicate registrations.
-- Supports Offline Payments at Venue Desk:
--   fee_amount: fee charged for the event
--   payment_mode: 'FREE' or 'OFFLINE'
--   payment_status: 'CONFIRMED', 'PENDING_APPROVAL', or 'REJECTED'
--   payment_notes: receipt or desk counter reference
-- -------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS registrations (
    registration_id VARCHAR(50) PRIMARY KEY,
    event_id VARCHAR(50) NOT NULL,
    participant_id VARCHAR(50) NOT NULL,
    registration_date DATE NOT NULL,
    status VARCHAR(30) DEFAULT 'Confirmed',
    fee_amount DOUBLE DEFAULT 0.0,
    payment_mode VARCHAR(50) DEFAULT 'OFFLINE',
    payment_status VARCHAR(30) DEFAULT 'CONFIRMED',
    payment_notes VARCHAR(255) DEFAULT '',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reg_event FOREIGN KEY (event_id) 
        REFERENCES events(event_id) ON DELETE CASCADE,
    CONSTRAINT fk_reg_participant FOREIGN KEY (participant_id) 
        REFERENCES participants(participant_id) ON DELETE CASCADE,
    CONSTRAINT uk_event_participant UNIQUE KEY (event_id, participant_id)
);

-- Indexes to optimize search queries by date and relationship lookups
CREATE INDEX idx_events_date ON events(event_date);
CREATE INDEX idx_sessions_event ON sessions(event_id);
CREATE INDEX idx_reg_event ON registrations(event_id);
CREATE INDEX idx_reg_participant ON registrations(participant_id);
CREATE INDEX idx_reg_pay_status ON registrations(payment_status);
