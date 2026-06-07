ALTER TABLE t_gift_family
  ADD COLUMN status VARCHAR(32) NOT NULL DEFAULT 'active' COMMENT '圈子状态：active/deleted',
  ADD INDEX idx_owner_status_id (owner_open_id, status, id);
