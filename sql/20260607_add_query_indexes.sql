ALTER TABLE t_gift_family
  ADD INDEX idx_owner_open_id_id (owner_open_id, id);

ALTER TABLE t_gift_child
  ADD INDEX idx_family_id_id (family_id, id);

ALTER TABLE t_gift_request
  ADD INDEX idx_family_id_id (family_id, id),
  ADD INDEX idx_family_status_id (family_id, status, id),
  ADD INDEX idx_family_claimed_id (family_id, claimed_by_open_id, id);

ALTER TABLE t_gift_family_member
  ADD INDEX idx_member_open_id_id (member_open_id, id),
  ADD INDEX idx_family_id_id (family_id, id);

ALTER TABLE t_gift_circle_join_request
  ADD INDEX idx_family_status_id (family_id, status, id),
  ADD INDEX idx_family_applicant_status (family_id, applicant_open_id, status);

ALTER TABLE t_gift_proposal
  ADD INDEX idx_family_id_id (family_id, id),
  ADD INDEX idx_family_sender_id (family_id, sender_open_id, id),
  ADD INDEX idx_family_receiver_id (family_id, receiver_open_id, id);
