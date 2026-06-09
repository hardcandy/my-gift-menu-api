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
