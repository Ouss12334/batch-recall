use recall 

select count(*)
-- select BIN_TO_UUID(pdt_uniq_id)
from product

delete from product


-- if cut job suddently and no auto increment id
UPDATE BATCH_JOB_EXECUTION bje SET bje.STATUS = 'FAILED', bje.END_TIME = CURRENT_TIMESTAMP() WHERE bje.END_TIME IS NULL;
UPDATE BATCH_STEP_EXECUTION bse SET bse.STATUS = 'FAILED', bse.END_TIME = CURRENT_TIMESTAMP() WHERE bse.END_TIME IS NULL;

