ALTER TABLE t_gift_blokus_game
  ADD COLUMN turn_time_seconds INT NOT NULL DEFAULT 60 AFTER turn_no,
  ADD COLUMN turn_started_at DATETIME AFTER started_at;
