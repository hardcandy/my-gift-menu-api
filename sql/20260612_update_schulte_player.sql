ALTER TABLE t_gift_schulte_record
  MODIFY child_id INT NULL,
  ADD COLUMN player_type VARCHAR(32) NOT NULL DEFAULT 'child' AFTER child_open_id,
  ADD COLUMN player_open_id VARCHAR(128) AFTER player_type,
  ADD COLUMN player_name VARCHAR(128) AFTER player_open_id,
  ADD INDEX idx_family_player_time (family_id, player_type, player_open_id, start_time);

UPDATE t_gift_schulte_record
SET player_type = 'child',
    player_open_id = child_open_id,
    player_name = child_name
WHERE player_name IS NULL;
