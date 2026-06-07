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
