CREATE DATABASE IF NOT EXISTS responselink_db;

USE responselink_db;


-- 1. INCIDENTS TABLE

CREATE TABLE IF NOT EXISTS incidents (

                                         id BIGINT AUTO_INCREMENT PRIMARY KEY,

                                         title VARCHAR(255) NOT NULL,

    description TEXT,

    severity ENUM(
                     'LOW',
                     'MEDIUM',
                     'HIGH',
                     'CRITICAL'
                 ) NOT NULL,

    incident_type VARCHAR(100) NOT NULL,

    reporter VARCHAR(255) NOT NULL,

    latitude DECIMAL(10,7) NOT NULL,

    longitude DECIMAL(10,7) NOT NULL,

    status ENUM(
                   'REPORTED',
                   'ACKNOWLEDGED',
                   'RESPONDING',
                   'RESOLVED'
               ) NOT NULL DEFAULT 'REPORTED',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT chk_latitude
    CHECK (latitude BETWEEN -90 AND 90),

    CONSTRAINT chk_longitude
    CHECK (longitude BETWEEN -180 AND 180),

    INDEX idx_incident_status(status),

    INDEX idx_incident_severity(severity),

    INDEX idx_incident_created(created_at)

    );


-- 2. RESPONDERS TABLE

CREATE TABLE IF NOT EXISTS responders (

                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,

                                          name VARCHAR(150) NOT NULL,

    email VARCHAR(255) NOT NULL UNIQUE,

    team VARCHAR(100) NOT NULL,

    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP

    );


-- 3. INCIDENT_RESPONDERS TABLE

CREATE TABLE IF NOT EXISTS incident_responders (

                                                   incident_id BIGINT NOT NULL,

                                                   responder_id BIGINT NOT NULL,

                                                   assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                                   PRIMARY KEY (incident_id, responder_id),

    CONSTRAINT fk_ir_incident
    FOREIGN KEY (incident_id)
    REFERENCES incidents(id)
    ON DELETE CASCADE,

    CONSTRAINT fk_ir_responder
    FOREIGN KEY (responder_id)
    REFERENCES responders(id),

    INDEX idx_responder_id(responder_id)

    );


-- 4. INCIDENT_STATUS_HISTORY TABLE

CREATE TABLE IF NOT EXISTS incident_status_history (

                                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,

                                                       incident_id BIGINT NOT NULL,

                                                       old_status VARCHAR(30),

    new_status VARCHAR(30) NOT NULL,

    actor_reference VARCHAR(255) NOT NULL,

    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_history_incident
    FOREIGN KEY (incident_id)
    REFERENCES incidents(id)
    ON DELETE CASCADE,

    INDEX idx_history_incident(incident_id),

    INDEX idx_history_changed(changed_at)

    );