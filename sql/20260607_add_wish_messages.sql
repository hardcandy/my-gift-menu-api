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
