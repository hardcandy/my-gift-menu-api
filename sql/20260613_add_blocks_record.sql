CREATE TABLE IF NOT EXISTS t_gift_blocks_record (
  id INT PRIMARY KEY AUTO_INCREMENT,
  family_id INT NOT NULL,
  player_open_id VARCHAR(128) NOT NULL,
  player_name VARCHAR(128) NOT NULL,
  score INT NOT NULL DEFAULT 0,
  line_count INT NOT NULL DEFAULT 0,
  level INT NOT NULL DEFAULT 1,
  duration_ms INT NOT NULL DEFAULT 0,
  mode VARCHAR(32) NOT NULL DEFAULT 'classic',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  create_time DATETIME NOT NULL,
  modify_time DATETIME NOT NULL,
  INDEX idx_family_rank (family_id, status, score, line_count, duration_ms),
  INDEX idx_player_time (player_open_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
