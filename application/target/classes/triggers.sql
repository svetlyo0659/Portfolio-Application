------ TRIGGER FOR INSERT IN  INDIVIDUAL
create or replace TRIGGER INDIVIDUALS_TRG
BEFORE INSERT ON INDIVIDUALS
FOR EACH ROW
BEGIN
  <<COLUMN_SEQUENCES>>
  BEGIN
    IF INSERTING AND :NEW.IDIV_ID IS NULL THEN
      SELECT INDIVIDUALS_SEQ.NEXTVAL INTO :NEW.IDIV_ID FROM SYS.DUAL;
    END IF;
  END COLUMN_SEQUENCES;
END;

-----------------------------------------------------------------------------
------ TRIGGER FOR INSERT IN  PORTFOLIO

create or replace TRIGGER PORTFOLIO_ID_TRIGGER
BEFORE INSERT ON PORTFOLIO
FOR EACH ROW
BEGIN
  <<COLUMN_SEQUENCES>>
  BEGIN
    IF INSERTING AND :NEW.DOC_ID IS NULL THEN
      SELECT PORTFOLIO_ID_SEQ.NEXTVAL INTO :NEW.DOC_ID FROM SYS.DUAL;
    END IF;
  END COLUMN_SEQUENCES;
END;


---------------------------------------------------------------------------------
--- TRIGGER FOR INSEERT IN PORFOLIO AND IN TBL_STATEMENT

create or replace TRIGGER PORTFOLIO_ST_ID_TRG
BEFORE INSERT ON PORTFOLIO
FOR EACH ROW
BEGIN
  <<COLUMN_SEQUENCES>>
  BEGIN
    IF INSERTING AND :NEW.ST_ID IS NULL THEN
      SELECT PORTFOLIO_ST_ID_SEQ.NEXTVAL INTO :NEW.ST_ID FROM SYS.DUAL;
      INSERT INTO TBL_STATEMENT(ST_ID,ST_DT) VALUES(:NEW.ST_ID, SYSDATE);
    END IF;
  END COLUMN_SEQUENCES;
END;


------ TRIGGER FOR DELETING  INDIVIDUAL AND REMOVING THE DATA FROM PORTFOLIO

CREATE OR REPLACE TRIGGER trg_delete_from_Portfolio

BEFORE DELETE
    ON INDIVIDUALS
    FOR EACH ROW

DECLARE

CURSOR cur_amnt IS
                SELECT amnt,cr,db
                FROM PORTFOLIO;

st_id_seq VARCHAR2(150);
BEGIN


    --- before the delete this loop takes the amnt from the portfolio and inserts the amnt and the cr Id into
    --- PORTFOLIO so That the balance of the credited Individual does not change.

        FOR obj IN cur_amnt LOOP
        st_id_seq := PORTFOLIO_ST_ID_SEQ.NEXTVAL;
        IF obj.db = :OLD.idiv_id THEN
        INSERT INTO TBL_STATEMENT VALUES(st_id_seq, SYSDATE);
        INSERT INTO PORTFOLIO VALUES(PORTFOLIO_ID_SEQ.NEXTVAL, 'INVOICE', obj.amnt, obj.cr, null, st_id_seq);
        END IF;
        END LOOP;

-- DELETE FROM TABLE PORTFOLIO THE cr and the cd ID  where idiv_id = cr OR db
        DELETE FROM PORTFOLIO WHERE cr = :OLD.idiv_id OR db = :OLD.idiv_id;

END;


---------------------------------------------------------------------------------------------------

CREATE OR REPLACE FUNCTION indiv_exists(indiv_name IN INDIVIDUALS.name%TYPE) RETURN INTEGER
AS
i_count INTEGER ;
BEGIN
    SELECT COUNT(*) INTO i_count
                    FROM INDIVIDUALS
                    WHERE name = indiv_name;
        IF i_count > 0 THEN
            RETURN 1;
        ELSE
            RETURN 0;
        END IF;
END;

---------------------------------------------------------------------------------------------------
-- function to return name or credited or debited

    @param individual_id IN INDIVIDUALS.idiv_id%TYPE
    @param in_time IN TBL_STATEMENT.st_dt%TYPE
    @return
    */

CREATE OR REPLACE FUNCTION get_name(individual_id IN INDIVIDUALS.idiv_id%TYPE, in_time IN TBL_STATEMENT.st_dt%TYPE)
RETURN VARCHAR2
AS

CURSOR cursor1 IS SELECT name FROM INDIVIDUALS i
                INNER JOIN PORTFOLIO p
                ON i.idiv_id = p.cr OR idiv_id = db
                INNER JOIN TBL_STATEMENT s
                ON s.st_id = p.st_id
                WHERE idiv_id = individual_id
                AND s.st_dt = TO_TIMESTAMP(TO_CHAR(in_time),'YYYY-MM-DD HH24:MI:SS.FF');

out_name VARCHAR2(50);

BEGIN

out_name := '';
OPEN cursor1;

FETCH cursor1 INTO out_name;

CLOSE cursor1;

RETURN out_name;
END;



-----------------------------FUNCTION RETURSN THE CURRENT BALANCE


create or replace FUNCTION get_balance(indiv_id IN INDIVIDUALS.idiv_id%TYPE, in_time IN TBL_STATEMENT.st_dt%TYPE)
RETURN NUMBER
AS

indiv_balance NUMBER(16,6);
invoice_sum NUMBER(16,6);
credit_note_sum NUMBER (16,6);


CURSOR c1 IS
    SELECT
        doc_type,amnt,st_dt
    FROM
        PORTFOLIO p
    INNER JOIN TBL_STATEMENT s
    ON p.ST_ID = s.ST_ID
    WHERE CR = indiv_id
    AND st_dt <= TO_TIMESTAMP(TO_CHAR(in_time),'YYYY-MM-DD HH24:MI:SS.FF');

CURSOR c2 IS
    SELECT
        doc_type,amnt,st_dt
    FROM
        PORTFOLIO p
    INNER JOIN TBL_STATEMENT s
    ON p.ST_ID = s.ST_ID
    WHERE DB = indiv_id
    AND st_dt <= TO_TIMESTAMP(TO_CHAR(in_time),'YYYY-MM-DD HH24:MI:SS.FF');

BEGIN

    indiv_balance :=0;
    invoice_sum :=0;
    credit_note_sum :=0;

    FOR obj IN c1
        LOOP
        invoice_sum := invoice_sum + obj.amnt;
        END LOOP;

    FOR obj IN c2
        LOOP
        credit_note_sum := credit_note_sum + obj.amnt;
        END LOOP;

    indiv_balance := invoice_sum-credit_note_sum;

    RETURN indiv_balance;
END;


-------------------------------------------------------------