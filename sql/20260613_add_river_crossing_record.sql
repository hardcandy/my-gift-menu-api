CREATE TABLE IF NOT EXISTS t_gift_river_crossing_record (
  id INT PRIMARY KEY AUTO_INCREMENT,
  family_id INT NOT NULL,
  player_open_id VARCHAR(128) NOT NULL,
  player_name VARCHAR(128) NOT NULL,
  step_count INT NOT NULL,
  fail_count INT NOT NULL DEFAULT 0,
  hint_count INT NOT NULL DEFAULT 0,
  duration_ms INT NOT NULL DEFAULT 0,
  star_count INT NOT NULL DEFAULT 1,
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  create_time DATETIME NOT NULL,
  modify_time DATETIME NOT NULL,
  INDEX idx_family_rank (family_id, status, star_count, step_count, duration_ms),
  INDEX idx_player_time (player_open_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
