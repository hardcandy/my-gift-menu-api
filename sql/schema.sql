CREATE DATABASE IF NOT EXISTS my_gift_menu DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE my_gift_menu;

CREATE TABLE IF NOT EXISTS t_gift_user (
  id INT PRIMARY KEY AUTO_INCREMENT,
  open_id VARCHAR(128) NOT NULL UNIQUE,
  session_key VARCHAR(128),
  union_id VARCHAR(128),
  nick_name VARCHAR(64),
  avatar_url VARCHAR(512),
  role VARCHAR(32) NOT NULL DEFAULT 'parent',
  create_time DATETIME NOT NULL,
  modify_time DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_gift_family (
  id INT PRIMARY KEY AUTO_INCREMENT,
  family_name VARCHAR(64) NOT NULL,
  circle_type VARCHAR(32) NOT NULL DEFAULT 'family',
  owner_open_id VARCHAR(128) NOT NULL,
  owner_role VARCHAR(32) NOT NULL DEFAULT 'parent',
  create_time DATETIME NOT NULL,
  modify_time DATETIME NOT NULL,
  INDEX idx_owner_open_id (owner_open_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_gift_child (
  id INT PRIMARY KEY AUTO_INCREMENT,
  family_id INT NOT NULL,
  child_open_id VARCHAR(128),
  child_name VARCHAR(64) NOT NULL,
  birthday VARCHAR(32),
  guardian_open_id VARCHAR(128) NOT NULL,
  create_time DATETIME NOT NULL,
  modify_time DATETIME NOT NULL,
  INDEX idx_family_id (family_id),
  INDEX idx_child_open_id (child_open_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_gift_request (
  id INT PRIMARY KEY AUTO_INCREMENT,
  family_id INT NOT NULL,
  child_id INT,
  receiver_open_id VARCHAR(128),
  receiver_name VARCHAR(64),
  target_open_id VARCHAR(128),
  target_name VARCHAR(64),
  title VARCHAR(128) NOT NULL,
  reason VARCHAR(1024),
  scene_type VARCHAR(64),
  expected_date VARCHAR(32),
  budget VARCHAR(64),
  product_link VARCHAR(1024),
  status VARCHAR(32) NOT NULL,
  created_by_open_id VARCHAR(128) NOT NULL,
  reviewer_open_id VARCHAR(128),
  claimed_by_open_id VARCHAR(128),
  claimed_by_name VARCHAR(64),
  claim_note VARCHAR(512),
  reviewed_at DATETIME,
  claimed_at DATETIME,
  confirmed_at DATETIME,
  completed_at DATETIME,
  thank_you_sent_at DATETIME,
  create_time DATETIME NOT NULL,
  modify_time DATETIME NOT NULL,
  INDEX idx_family_id (family_id),
  INDEX idx_child_id (child_id),
  INDEX idx_status (status),
  INDEX idx_claimed_by_open_id (claimed_by_open_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_gift_audit_log (
  id INT PRIMARY KEY AUTO_INCREMENT,
  gift_request_id INT NOT NULL,
  operator_open_id VARCHAR(128) NOT NULL,
  action VARCHAR(64) NOT NULL,
  comment VARCHAR(512),
  create_time DATETIME NOT NULL,
  INDEX idx_gift_request_id (gift_request_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS t_gift_family_member (
  id INT PRIMARY KEY AUTO_INCREMENT,
  family_id INT NOT NULL,
  member_open_id VARCHAR(128) NOT NULL,
  member_role VARCHAR(32) NOT NULL DEFAULT 'relative',
  create_time DATETIME NOT NULL,
  modify_time DATETIME NOT NULL,
  UNIQUE KEY uk_family_member (family_id, member_open_id),
  INDEX idx_member_open_id (member_open_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_gift_feedback (
  id INT PRIMARY KEY AUTO_INCREMENT,
  gift_request_id INT NOT NULL,
  feedback_open_id VARCHAR(128) NOT NULL,
  rating VARCHAR(32) NOT NULL,
  message VARCHAR(512),
  preference VARCHAR(64),
  parent_note VARCHAR(512),
  create_time DATETIME NOT NULL,
  modify_time DATETIME NOT NULL,
  UNIQUE KEY uk_gift_feedback (gift_request_id),
  INDEX idx_feedback_open_id (feedback_open_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_gift_circle_invite (
  id INT PRIMARY KEY AUTO_INCREMENT,
  family_id INT NOT NULL,
  invite_code VARCHAR(64) NOT NULL UNIQUE,
  created_by_open_id VARCHAR(128) NOT NULL,
  expire_time DATETIME NOT NULL,
  create_time DATETIME NOT NULL,
  modify_time DATETIME NOT NULL,
  INDEX idx_family_id (family_id),
  INDEX idx_expire_time (expire_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_gift_circle_join_request (
  id INT PRIMARY KEY AUTO_INCREMENT,
  family_id INT NOT NULL,
  invite_code VARCHAR(64) NOT NULL,
  applicant_open_id VARCHAR(128) NOT NULL,
  applicant_nick_name VARCHAR(64),
  requested_role VARCHAR(32) NOT NULL DEFAULT 'relative',
  status VARCHAR(32) NOT NULL DEFAULT 'pending',
  approve_open_id VARCHAR(128),
  approve_time DATETIME,
  create_time DATETIME NOT NULL,
  modify_time DATETIME NOT NULL,
  INDEX idx_family_status (family_id, status),
  INDEX idx_applicant_open_id (applicant_open_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_gift_user_subscription (
  id INT PRIMARY KEY AUTO_INCREMENT,
  open_id VARCHAR(128) NOT NULL,
  template_id VARCHAR(128) NOT NULL,
  scene VARCHAR(64) NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'accept',
  create_time DATETIME NOT NULL,
  modify_time DATETIME NOT NULL,
  UNIQUE KEY uk_user_template_scene (open_id, template_id, scene),
  INDEX idx_open_id (open_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_gift_proposal (
  id INT PRIMARY KEY AUTO_INCREMENT,
  family_id INT NOT NULL,
  sender_open_id VARCHAR(128) NOT NULL,
  sender_name VARCHAR(64),
  receiver_open_id VARCHAR(128),
  receiver_name VARCHAR(64),
  title VARCHAR(128) NOT NULL,
  reason VARCHAR(1024),
  scene_type VARCHAR(64),
  budget VARCHAR(64),
  product_link VARCHAR(1024),
  gift_options TEXT,
  selected_options TEXT,
  status VARCHAR(32) NOT NULL,
  confirm_open_id VARCHAR(128),
  confirm_note VARCHAR(512),
  confirmed_at DATETIME,
  create_time DATETIME NOT NULL,
  modify_time DATETIME NOT NULL,
  INDEX idx_family_id (family_id),
  INDEX idx_sender_open_id (sender_open_id),
  INDEX idx_receiver_open_id (receiver_open_id),
  INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
