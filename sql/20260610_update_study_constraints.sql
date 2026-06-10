ALTER TABLE t_gift_study_item
  ADD COLUMN score_type VARCHAR(32) NOT NULL DEFAULT 'text';

DELETE s1 FROM t_gift_study_subject s1
INNER JOIN t_gift_study_subject s2
  ON s1.family_id = s2.family_id
  AND s1.grade_scope = s2.grade_scope
  AND s1.name = s2.name
  AND s1.status = s2.status
  AND s1.id < s2.id
  AND s1.status <> 'deleted';

DELETE i1 FROM t_gift_study_item i1
INNER JOIN t_gift_study_item i2
  ON i1.family_id = i2.family_id
  AND i1.grade_scope = i2.grade_scope
  AND i1.subject_scope = i2.subject_scope
  AND i1.name = i2.name
  AND i1.status = i2.status
  AND i1.id < i2.id
  AND i1.status <> 'deleted';

ALTER TABLE t_gift_study_subject
  ADD UNIQUE KEY uk_family_grade_name (family_id, grade_scope, name, status);

ALTER TABLE t_gift_study_item
  ADD UNIQUE KEY uk_family_grade_subject_name (family_id, grade_scope, subject_scope, name, status);
