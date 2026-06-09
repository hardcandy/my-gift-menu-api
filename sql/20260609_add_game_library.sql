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
