ALTER TABLE t_gift_werewolf_game
  ADD COLUMN player_count INT NOT NULL DEFAULT 6 AFTER players_text,
  ADD COLUMN config_text TEXT AFTER player_count;
