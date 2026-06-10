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
  INDEX idx_owner_open_id (owner_open_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
