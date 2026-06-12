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
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  create_time DATETIME NOT NULL,
  modify_time DATETIME NOT NULL,
  INDEX idx_owner_open_id (owner_open_id),
  INDEX idx_owner_open_id_id (owner_open_id, id),
  INDEX idx_owner_status_id (owner_open_id, status, id)
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
  INDEX idx_family_id_id (family_id, id),
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
  INDEX idx_family_id_id (family_id, id),
  INDEX idx_family_status_id (family_id, status, id),
  INDEX idx_family_claimed_id (family_id, claimed_by_open_id, id),
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
  INDEX idx_member_open_id (member_open_id),
  INDEX idx_member_open_id_id (member_open_id, id),
  INDEX idx_family_id_id (family_id, id)
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
  INDEX idx_family_status_id (family_id, status, id),
  INDEX idx_family_applicant_status (family_id, applicant_open_id, status),
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
  INDEX idx_family_id_id (family_id, id),
  INDEX idx_family_sender_id (family_id, sender_open_id, id),
  INDEX idx_family_receiver_id (family_id, receiver_open_id, id),
  INDEX idx_sender_open_id (sender_open_id),
  INDEX idx_receiver_open_id (receiver_open_id),
  INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_gift_app_feedback (
  id INT PRIMARY KEY AUTO_INCREMENT,
  open_id VARCHAR(128),
  nick_name VARCHAR(64),
  feedback_type VARCHAR(32) NOT NULL DEFAULT 'suggestion',
  content VARCHAR(2048) NOT NULL,
  contact VARCHAR(128),
  page_path VARCHAR(256),
  status VARCHAR(32) NOT NULL DEFAULT 'new',
  create_time DATETIME NOT NULL,
  modify_time DATETIME NOT NULL,
  INDEX idx_status_id (status, id),
  INDEX idx_open_id_id (open_id, id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_gift_request_message (
  id INT PRIMARY KEY AUTO_INCREMENT,
  gift_request_id INT NOT NULL,
  open_id VARCHAR(128) NOT NULL,
  nick_name VARCHAR(64),
  content VARCHAR(512) NOT NULL,
  create_time DATETIME NOT NULL,
  INDEX idx_gift_request_id_id (gift_request_id, id),
  INDEX idx_open_id_id (open_id, id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_gift_game (
  id INT PRIMARY KEY AUTO_INCREMENT,
  family_id INT NOT NULL,
  owner_open_id VARCHAR(128) NOT NULL,
  image_file_id VARCHAR(512),
  name VARCHAR(128) NOT NULL,
  location VARCHAR(128),
  duration_minutes INT NOT NULL,
  last_played_by_open_ids VARCHAR(1024),
  last_played_by_names VARCHAR(512),
  last_played_at DATETIME,
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  create_time DATETIME NOT NULL,
  modify_time DATETIME NOT NULL,
  INDEX idx_family_status_id (family_id, status, id),
  INDEX idx_family_last_played (family_id, last_played_at),
  INDEX idx_owner_open_id (owner_open_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_gift_game_play_log (
  id INT PRIMARY KEY AUTO_INCREMENT,
  game_id INT NOT NULL,
  family_id INT NOT NULL,
  operator_open_id VARCHAR(128) NOT NULL,
  player_open_ids VARCHAR(1024) NOT NULL,
  player_names VARCHAR(512) NOT NULL,
  source VARCHAR(32) NOT NULL DEFAULT 'manual',
  played_at DATETIME NOT NULL,
  create_time DATETIME NOT NULL,
  INDEX idx_game_played_at (game_id, played_at),
  INDEX idx_family_played_at (family_id, played_at),
  INDEX idx_operator_open_id (operator_open_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_gift_restaurant (
  id INT PRIMARY KEY AUTO_INCREMENT,
  family_id INT NOT NULL,
  owner_open_id VARCHAR(128) NOT NULL,
  image_file_id VARCHAR(512),
  name VARCHAR(128) NOT NULL,
  location VARCHAR(256),
  average_cost DECIMAL(10,2) NOT NULL DEFAULT 0,
  distance_km DECIMAL(10,2) NOT NULL DEFAULT 0,
  recommended_dishes VARCHAR(1024),
  cuisine_type VARCHAR(64),
  tags VARCHAR(512),
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  average_score DECIMAL(4,1),
  last_ate_at DATETIME,
  create_time DATETIME NOT NULL,
  modify_time DATETIME NOT NULL,
  INDEX idx_family_status_id (family_id, status, id),
  INDEX idx_family_score (family_id, average_score),
  INDEX idx_family_distance (family_id, distance_km),
  INDEX idx_owner_open_id (owner_open_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_gift_restaurant_visit (
  id INT PRIMARY KEY AUTO_INCREMENT,
  restaurant_id INT NOT NULL,
  family_id INT NOT NULL,
  operator_open_id VARCHAR(128) NOT NULL,
  member_open_ids VARCHAR(1024) NOT NULL,
  member_names VARCHAR(512) NOT NULL,
  dishes VARCHAR(1024),
  note VARCHAR(512),
  ate_at DATETIME NOT NULL,
  create_time DATETIME NOT NULL,
  INDEX idx_restaurant_ate_at (restaurant_id, ate_at),
  INDEX idx_family_ate_at (family_id, ate_at),
  INDEX idx_operator_open_id (operator_open_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_gift_restaurant_score (
  id INT PRIMARY KEY AUTO_INCREMENT,
  visit_id INT NOT NULL,
  restaurant_id INT NOT NULL,
  family_id INT NOT NULL,
  scorer_open_id VARCHAR(128) NOT NULL,
  scorer_name VARCHAR(64),
  score DECIMAL(4,1) NOT NULL,
  note VARCHAR(512),
  create_time DATETIME NOT NULL,
  INDEX idx_restaurant_id (restaurant_id),
  INDEX idx_visit_id (visit_id),
  INDEX idx_family_scorer (family_id, scorer_open_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_gift_study_item (
  id INT PRIMARY KEY AUTO_INCREMENT,
  family_id INT NOT NULL,
  owner_open_id VARCHAR(128) NOT NULL,
  name VARCHAR(128) NOT NULL,
  subject_scope VARCHAR(128) NOT NULL DEFAULT '全部',
  grade_scope VARCHAR(128) NOT NULL DEFAULT '全部',
  score_type VARCHAR(32) NOT NULL DEFAULT 'text',
  field_config TEXT,
  correction_enabled TINYINT NOT NULL DEFAULT 1,
  sort_order INT NOT NULL DEFAULT 100,
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  create_time DATETIME NOT NULL,
  modify_time DATETIME NOT NULL,
  INDEX idx_family_status_sort (family_id, status, sort_order),
  UNIQUE KEY uk_family_grade_subject_name (family_id, grade_scope, subject_scope, name, status),
  INDEX idx_owner_open_id (owner_open_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_gift_study_subject (
  id INT PRIMARY KEY AUTO_INCREMENT,
  family_id INT NOT NULL,
  owner_open_id VARCHAR(128) NOT NULL,
  name VARCHAR(64) NOT NULL,
  grade_scope VARCHAR(128) NOT NULL DEFAULT '全部',
  sort_order INT NOT NULL DEFAULT 100,
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  create_time DATETIME NOT NULL,
  modify_time DATETIME NOT NULL,
  INDEX idx_family_status_sort (family_id, status, sort_order),
  UNIQUE KEY uk_family_grade_name (family_id, grade_scope, name, status),
  INDEX idx_owner_open_id (owner_open_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_gift_study_record (
  id INT PRIMARY KEY AUTO_INCREMENT,
  family_id INT NOT NULL,
  child_id INT NOT NULL,
  child_name VARCHAR(64) NOT NULL,
  grade VARCHAR(32) NOT NULL,
  subject VARCHAR(64) NOT NULL,
  item_id INT NOT NULL,
  item_name VARCHAR(128) NOT NULL,
  content_title VARCHAR(128),
  record_date DATETIME NOT NULL,
  score_type VARCHAR(32) NOT NULL DEFAULT 'text',
  score_value VARCHAR(128),
  has_error TINYINT NOT NULL DEFAULT 0,
  error_count INT NOT NULL DEFAULT 0,
  corrected TINYINT NOT NULL DEFAULT 1,
  correction_mark VARCHAR(8) NOT NULL DEFAULT '★',
  note VARCHAR(1024),
  attachment_file_id VARCHAR(512),
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by_open_id VARCHAR(128) NOT NULL,
  create_time DATETIME NOT NULL,
  modify_time DATETIME NOT NULL,
  INDEX idx_family_status_date (family_id, status, record_date),
  INDEX idx_family_correction (family_id, correction_mark, status),
  INDEX idx_child_subject_grade (child_id, subject, grade),
  INDEX idx_item_id (item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_gift_schulte_record (
  id INT PRIMARY KEY AUTO_INCREMENT,
  family_id INT NOT NULL,
  child_id INT,
  child_name VARCHAR(128) NOT NULL,
  child_open_id VARCHAR(128),
  player_type VARCHAR(32) NOT NULL DEFAULT 'child',
  player_open_id VARCHAR(128),
  player_name VARCHAR(128),
  operator_open_id VARCHAR(128) NOT NULL,
  game_name VARCHAR(64) NOT NULL DEFAULT '舒尔特方格',
  game_mode VARCHAR(64) NOT NULL DEFAULT '数字顺序',
  difficulty VARCHAR(16) NOT NULL,
  grid_size INT NOT NULL,
  total_numbers INT NOT NULL,
  start_time DATETIME NOT NULL,
  end_time DATETIME NOT NULL,
  duration_ms INT NOT NULL,
  pause_duration_ms INT NOT NULL DEFAULT 0,
  completed TINYINT NOT NULL DEFAULT 1,
  completed_count INT NOT NULL,
  error_count INT NOT NULL DEFAULT 0,
  average_interval_ms INT NOT NULL DEFAULT 0,
  rating VARCHAR(32) NOT NULL,
  note VARCHAR(512),
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  create_time DATETIME NOT NULL,
  modify_time DATETIME NOT NULL,
  INDEX idx_family_child_time (family_id, child_id, start_time),
  INDEX idx_family_player_time (family_id, player_type, player_open_id, start_time),
  INDEX idx_family_difficulty_time (family_id, difficulty, start_time),
  INDEX idx_operator_open_id (operator_open_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_gift_gomoku_game (
  id INT PRIMARY KEY AUTO_INCREMENT,
  family_id INT NOT NULL,
  room_code VARCHAR(16) NOT NULL,
  black_open_id VARCHAR(128) NOT NULL,
  black_name VARCHAR(128) NOT NULL,
  white_open_id VARCHAR(128),
  white_name VARCHAR(128),
  current_turn VARCHAR(16) NOT NULL DEFAULT 'black',
  board_text VARCHAR(255) NOT NULL,
  move_count INT NOT NULL DEFAULT 0,
  last_move_index INT,
  winner_open_id VARCHAR(128),
  winner_name VARCHAR(128),
  status VARCHAR(32) NOT NULL DEFAULT 'waiting',
  create_time DATETIME NOT NULL,
  modify_time DATETIME NOT NULL,
  INDEX idx_room_code (room_code),
  INDEX idx_family_status_time (family_id, status, modify_time),
  INDEX idx_black_open_id (black_open_id),
  INDEX idx_white_open_id (white_open_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_gift_word_pack (
  id INT PRIMARY KEY AUTO_INCREMENT,
  family_id INT NOT NULL,
  owner_open_id VARCHAR(128) NOT NULL,
  child_id INT,
  name VARCHAR(128) NOT NULL,
  grade VARCHAR(32),
  semester VARCHAR(32),
  source VARCHAR(64),
  note VARCHAR(512),
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  create_time DATETIME NOT NULL,
  modify_time DATETIME NOT NULL,
  INDEX idx_family_child_status (family_id, child_id, status),
  INDEX idx_family_child_name_status (family_id, child_id, name, status),
  INDEX idx_owner_open_id (owner_open_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_gift_word_item (
  id INT PRIMARY KEY AUTO_INCREMENT,
  family_id INT NOT NULL,
  pack_id INT NOT NULL,
  word_text VARCHAR(64) NOT NULL,
  pinyin VARCHAR(128),
  hint VARCHAR(512),
  phrases VARCHAR(512),
  mistake_tip VARCHAR(512),
  unit_name VARCHAR(64),
  need_read TINYINT NOT NULL DEFAULT 1,
  need_write TINYINT NOT NULL DEFAULT 1,
  important TINYINT NOT NULL DEFAULT 0,
  source VARCHAR(64),
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  create_time DATETIME NOT NULL,
  modify_time DATETIME NOT NULL,
  INDEX idx_pack_status (pack_id, status),
  INDEX idx_family_word (family_id, word_text)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_gift_word_progress (
  id INT PRIMARY KEY AUTO_INCREMENT,
  family_id INT NOT NULL,
  child_id INT NOT NULL,
  word_id INT NOT NULL,
  recognize_status VARCHAR(32) NOT NULL DEFAULT 'new',
  read_status VARCHAR(32) NOT NULL DEFAULT 'new',
  use_status VARCHAR(32) NOT NULL DEFAULT 'new',
  write_status VARCHAR(32) NOT NULL DEFAULT 'new',
  recognize_correct_streak INT NOT NULL DEFAULT 0,
  read_correct_streak INT NOT NULL DEFAULT 0,
  use_correct_streak INT NOT NULL DEFAULT 0,
  write_correct_streak INT NOT NULL DEFAULT 0,
  recognize_wrong_count INT NOT NULL DEFAULT 0,
  read_wrong_count INT NOT NULL DEFAULT 0,
  use_wrong_count INT NOT NULL DEFAULT 0,
  write_wrong_count INT NOT NULL DEFAULT 0,
  last_practiced_at DATETIME,
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  create_time DATETIME NOT NULL,
  modify_time DATETIME NOT NULL,
  UNIQUE KEY uk_child_word (child_id, word_id),
  INDEX idx_family_child (family_id, child_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_gift_word_play_record (
  id INT PRIMARY KEY AUTO_INCREMENT,
  family_id INT NOT NULL,
  child_id INT NOT NULL,
  child_name VARCHAR(128) NOT NULL,
  pack_id INT,
  pack_name VARCHAR(128),
  operator_open_id VARCHAR(128) NOT NULL,
  mode VARCHAR(32) NOT NULL DEFAULT 'comprehensive',
  total_count INT NOT NULL DEFAULT 0,
  correct_count INT NOT NULL DEFAULT 0,
  wrong_count INT NOT NULL DEFAULT 0,
  write_pending_count INT NOT NULL DEFAULT 0,
  summary_json TEXT,
  note VARCHAR(512),
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  played_at DATETIME NOT NULL,
  create_time DATETIME NOT NULL,
  modify_time DATETIME NOT NULL,
  INDEX idx_family_child_time (family_id, child_id, played_at),
  INDEX idx_pack_time (pack_id, played_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
