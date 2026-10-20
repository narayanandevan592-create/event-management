-- =========================================================================
-- Academic OOP Project: Sample Data for Demonstration & Viva
-- =========================================================================

USE event_management_db;

-- Clear previous test data in proper order (child tables first)
DELETE FROM registrations;
DELETE FROM sessions;
DELETE FROM participants;
DELETE FROM events;

-- 1. Insert Sample Events
INSERT INTO events (event_id, event_name, description, event_date, venue, organizer_name) VALUES
('EVT-101', 'Annual Tech Symposium 2026', 'A premier gathering of AI researchers, developers, and tech leaders.', '2026-11-15', 'Main Auditorium, Block A', 'Prof. Alan Turing'),
('EVT-102', 'Java & OOP Bootcamp', 'Intensive hands-on workshop covering OOP design patterns and JDBC architecture.', '2026-11-20', 'Lab 3, CS Department', 'Dr. James Gosling'),
('EVT-103', 'National Hackathon Finale', '48-hour competitive innovation hackathon for engineering teams.', '2026-12-05', 'Innovation Center', 'Tech Club Committee');

-- 2. Insert Sample Participants
INSERT INTO participants (participant_id, full_name, email, phone) VALUES
('PAR-201', 'Aarav Sharma', 'aarav.sharma@example.edu', '9876543210'),
('PAR-202', 'Diya Patel', 'diya.patel@example.edu', '9876543211'),
('PAR-203', 'Rohan Gupta', 'rohan.gupta@example.edu', '9876543212'),
('PAR-204', 'Ananya Iyer', 'ananya.iyer@example.edu', '9876543213');

-- 3. Insert Sample Sessions (Composition: tied to Event EVT-101 and EVT-102)
INSERT INTO sessions (session_id, event_id, session_title, session_date, start_time, end_time, venue) VALUES
('SES-301', 'EVT-101', 'Opening Keynote: Future of AI & Computing', '2026-11-15', '09:30:00', '11:00:00', 'Main Stage'),
('SES-302', 'EVT-101', 'Panel Discussion: Modern Software Architectures', '2026-11-15', '11:30:00', '13:00:00', 'Hall B'),
('SES-303', 'EVT-101', 'Networking & Tech Exhibits', '2026-11-15', '14:00:00', '16:00:00', 'Exhibition Foyer'),
('SES-304', 'EVT-102', 'Session 1: Deep Dive into OOP Principles', '2026-11-20', '10:00:00', '12:30:00', 'Lab 3'),
('SES-305', 'EVT-102', 'Session 2: Building JDBC Desktop Applications', '2026-11-20', '13:30:00', '16:00:00', 'Lab 3');

-- 4. Insert Sample Registrations (Association: Participant <-> Event)
INSERT INTO registrations (registration_id, event_id, participant_id, registration_date, status) VALUES
('REG-401', 'EVT-101', 'PAR-201', '2026-10-10', 'Confirmed'),
('REG-402', 'EVT-101', 'PAR-202', '2026-10-11', 'Confirmed'),
('REG-403', 'EVT-102', 'PAR-201', '2026-10-12', 'Confirmed'),
('REG-404', 'EVT-102', 'PAR-203', '2026-10-12', 'Confirmed'),
('REG-405', 'EVT-103', 'PAR-204', '2026-10-14', 'Confirmed');
