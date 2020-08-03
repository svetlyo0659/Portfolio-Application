
-----------------------------------------------------------------------

CREATE TABLE INDIVIDUALS (
idiv_id VARCHAR2(150),
name VARCHAR2(50) NOT NULL,
type VARCHAR2(50) NOT NULL,
LEI VARCHAR2(20)
);

ALTER TABLE INDIVIDUALS
ADD  LEI VARCHAR2(20);



-- Private Entity or Legal Entity
INSERT INTO individuals(name,type) VALUES('Ivan Petrov','Private Entity');
INSERT INTO individuals(name,type) VALUES('Petar Petrov','Legal Entity');

SELECT * FROM INDIVIDUALS
WHERE  name LIKE '%%'
AND TYPE LIKE '%%'
ORDER BY name;

SELECT EXISTS(SELECT * FROM INDIVIDUALS WHERE NAME = Ivan);

--DROP TABLE INDIVIDUALS;
--DROP TRIGGER INDIVIDUALS_TRG;
--DROP SEQUENCE INDIVIDUALS_SEQ;

--------------------------------------------------------------------------------------------

CREATE TABLE TBL_STATEMENT(
st_id VARCHAR2(150),
st_dt TIMESTAMP NOT NULL,
PRIMARY KEY (st_id)
);

--DROP TABLE TBL_STATEMENT;

SELECT * FROM TBL_STATEMENT;

INSERT INTO TBL_STATEMENT (st_dt) VALUES(SYSDATE);

COMMIT;

--------------------------------------------------------------------------------------------
--doc_type := INVOICE OR CREDIT NOTE

CREATE TABLE PORTFOLIO(
doc_id VARCHAR2(150) NOT NULL,
doc_type VARCHAR2(50) NOT NULL,
amnt NUMBER(16,6) NOT NULL,
cr VARCHAR2(150) NOT NULL,
db VARCHAR2(150) NOT NULL,
st_id VARCHAR2(150) NOT NULL,
FOREIGN KEY (cr) REFERENCES INDIVIDUALS (idiv_id),
FOREIGN KEY (db) REFERENCES INDIVIDUALS (idiv_id),
FOREIGN KEY (st_id) REFERENCES TBL_STATEMENT (st_id)
);

--DROP TABLE PORTFOLIO;

commit;

SELECT * FROM TBL_STATEMENT;
SELECT * FROM PORTFOLIO;

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


CREATE OR REPLACE FUNCTION identifier_exist(identifier_num IN INDIVIDUALS.lei%TYPE) RETURN INTEGER
AS
i_count INTEGER ;
BEGIN
    SELECT COUNT(*) INTO i_count
                    FROM INDIVIDUALS
                    WHERE lei = identifier_num;
        IF i_count > 0 THEN
            RETURN 1;
        ELSE
            RETURN 0;
        END IF;
END;

DROP FUNCTION identifier_exist;

COMMIT;


DROP FUNCTION indiv_exists;

DECLARE
res INTEGER;
BEGIN
    res := indiv_exists('Ivan Ivanov');

    IF res = 0 THEN
    DBMS_OUTPUT.PUT_LINE('FASLE');
    ELSE
    DBMS_OUTPUT.PUT_LINE('TRUE');
    END IF;
END;


DECLARE
res INTEGER;
BEGIN
    res := identifier_exist('8711075594');

    IF res = 0 THEN
    DBMS_OUTPUT.PUT_LINE('FALSE');
    ELSE
    DBMS_OUTPUT.PUT_LINE('TRUE');
    END IF;
END;

SET SERVEROUTPUT ON;

SELECT COUNT(*) as COUNT
FROM INDIVIDUALS
WHERE name = 'Ivan Ivanov';


BEGIN

    DBMS_OUTPUT.PUT_LINE('22');
END;

COMMIT;

DELETE  FROM INDIVIDUALS WHERE lei = 'Legal Entity';

SELECT * FROM INDIVIDUALS
WHERE NAME LIKE '%%'
AND TYPE LIKE '%%'
ORDER BY name;

SELECT TRIM('"' FROM '"name"') as trimed FROM DUAL;


SELECT * FROM PORTFOLIO;
DELETE FROM PORTFOLIO WHERE DOC_TYPE = 'INVOICE';
SELECT * FROM TBL_STATEMENT;
SELECT * FROM INDIVIDUALS;

--DROP TRIGGER PORTFOLIO_TRG;
--DROP TRIGGER PORTFOLIO_TRG1;
--DROP SEQUENCE PORTFOLIO_SEQ1;
--DROP SEQUENCE TBL_STATEMENT_SEQ;
--DROP TRIGGER TBL_STATEMENT_TRG;
--DROP SEQUENCE SEQ1;

COMMIT;

INSERT INTO PORTFOLIO (doc_type,amnt,cr,db) VALUES('INVOICE',500,'222','666');


SELECT * FROM PORTFOLIO p
INNER JOIN TBL_STATEMENT s
ON p.ST_ID = s.ST_ID
WHERE cr = 161 OR db = 161
AND DOC_TYPE = 'INVOICE'
ORDER BY ST_DT DESC;

SELECT * FROM PORTFOLIO;



-------- GET CURRENT BALANCE FROM DATE TO DATE

CREATE PROCEDURE get_balance IS
indiv_balance NUMBER(16,6);
BEGIN
indiv_balance := SELECT SUM(amnt) FROM PORTFOLIO;
    DBMS_OUTPUT.PUT_LINE(indiv_balance);
END;

DROP PROCEDURE get_balance;



SELECT SUM(amnt)
    FROM PORTFOLIO p
    INNER JOIN TBL_STATEMENT s
    ON p.st_id  = s.st_id
    WHERE cr = '161' OR db = '161'
   -- AND st_dt > SYSDATE ;


SET SERVEROUTPUT ON;


DECLARE
res INTEGER;
BEGIN
    res := get_balance('161', SYSDATE);

    DBMS_OUTPUT.PUT_LINE(res);
END;

----- START BALANCE
SELECT * FROM PORTFOLIO
WHERE cr = '161' -- OR db = '161'
AND ROWNUM <=1;

--out_balance out portfolio.amnt%TYPE
--in_st_date IN tbl_statement.st_dt%TYPE


CREATE OR REPLACE PROCEDURE GET_CURRENT_BALANCE( in_indiv_id IN individuals.idiv_id%TYPE)
AS
    CURSOR c1 IS
    SELECT
        amnt,cr,db
    FROM
        PORTFOLIO p
    INNER JOIN TBL_STATEMENT s
    ON p.ST_ID = s.ST_ID
    WHERE CR = in_indiv_id OR DB = in_indiv_id;

    TYPE arr IS TABLE OF c1%rowtype INDEX BY PLS_INTEGER;

    arr1 arr;
    start_balance NUMBER(16, 6);
    doc_type VARCHAR2(150);
    amount NUMBER(16,6);
BEGIN
    -- start balance declaring
    SELECT amnt INTO start_balance FROM PORTFOLIO
    WHERE cr = '161' -- OR db = '161'
    AND ROWNUM <=1;

    FOR i IN 1..arr1.COUNT
    LOOP
        dbms_output.put_line('hello');
    END LOOP;
END;


EXECUTE GET_CURRENT_BALANCE('161');


DROP PROCEDURE GET_CURRENT_BALANCE;

SET SERVEROUTPUT ON;

SELECT * FROM PORTFOLIO;

SELECT amnt +
        CASE doc_type
        WHEN 'CREDIT NOTE' THEN - amnt
        ELSE amnt END AS balance
        FROM PORTFOLIO
        WHERE cr = '161' OR db = '161';

        commit;

        SELECT SUM(amnt)
        FROM PORTFOLIO
        WHERE cr = '161' OR db = '161';


                SELECT start_balance +
        CASE doc_type
        WHEN 'CREDIT NOTE' THEN - amnt
        ELSE amnt END INTO indiv_balance
        FROM PORTFOLIO
        WHERE cr = indiv_id OR db = indiv_id;


------- GET CURRENT BALANCE

    @param indiv_id IN INDIVIDUALS.lei%TYPE
    @param start_date IN TBL_STATEMENT.st_dt%TYPE
    @return
    */

--, start_date IN TBL_STATEMENT.st_dt%TYPE


CREATE OR REPLACE FUNCTION get_balance(indiv_id IN INDIVIDUALS.idiv_id%TYPE, in_time IN TBL_STATEMENT.st_dt%TYPE) RETURN NUMBER
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
    credit_note_sum := 0;

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

DROP FUNCTION get_balance;

commit;

DECLARE
res NUMBER;
BEGIN
    res := get_balance('162','2020-07-13 15:42:12.000000000');
    DBMS_OUTPUT.PUT_LINE(res);
END;

    SELECT
        doc_type,amnt,st_dt
    FROM
        PORTFOLIO p
    INNER JOIN TBL_STATEMENT s
    ON p.ST_ID = s.ST_ID
    WHERE CR = '1' OR DB = '1';

    SELECT * FROM PORTFOLIO;

    SELECT * FROM INDIVIDUALS;

COMMIT;

SET SERVEROUTPUT ON;

---- TEST THE FUNCTION
DECLARE
indiv_balance NUMBER(16,6);
invoice_sum NUMBER(16,6);
credit_note_sum NUMBER (16,6);
in_date TIMESTAMP;

CURSOR c1 IS
    SELECT
        doc_type,amnt,st_dt
    FROM
        PORTFOLIO p
    INNER JOIN TBL_STATEMENT s
    ON p.ST_ID = s.ST_ID
    WHERE CR = '162';
    --AND st_dt <= TO_TIMESTAMP('2020-07-13 15:42:12.000000000','YYYY-MM-DD HH24:MI:SS.FF');

CURSOR c2 IS
    SELECT
        doc_type,amnt,st_dt
    FROM
        PORTFOLIO p
    INNER JOIN TBL_STATEMENT s
    ON p.ST_ID = s.ST_ID
    WHERE DB = '162';
    --AND st_dt <= TO_TIMESTAMP('2020-07-13 15:42:12.000000000','YYYY-MM-DD HH24:MI:SS.FF');


BEGIN

    indiv_balance :=0;
    invoice_sum :=0;
    credit_note_sum := 0;

    FOR obj IN c1
        LOOP


        invoice_sum := invoice_sum + obj.amnt;

     dbms_output.put_line(invoice_sum);
    END LOOP;


    FOR obj IN c2
        LOOP


        credit_note_sum := credit_note_sum + obj.amnt;

     dbms_output.put_line(credit_note_sum);
    END LOOP;

    indiv_balance := invoice_sum-credit_note_sum;
    dbms_output.put_line(indiv_balance);
END;

SELECT * FROM PORTFOLIO;
WHERE
SELECT * FROM INDIVIDUALS;

   SELECT ---big fuckin problem
        doc_type,amnt,st_dt
    FROM
        PORTFOLIO p
    INNER JOIN TBL_STATEMENT s
    ON p.ST_ID = s.ST_ID
    WHERE CR = '162';


    ------ addding date to the query
        SELECT
        doc_type,amnt,st_dt
    FROM
        PORTFOLIO p
    INNER JOIN TBL_STATEMENT s
    ON p.ST_ID = s.ST_ID
    WHERE CR = '1' OR DB = '1'
    AND st_dt <= SYSDATE;  --- SEE SYSDATE

commit;


---------------------------------- PORTFOLIO SELECTS

page_num := 1;
row_num := 2;

SELECT doc_type, amnt,
(SELECT name FROM INDIVIDUALS i
INNER JOIN PORTFOLIO p
ON i.idiv_id = p.cr) AS credited ,
(SELECT name FROM INDIVIDUALS
INNER JOIN PORTFOLIO
ON idiv_id = db) AS debited
,idiv_id, type, lei, st_dt  FROM PORTFOLIO p
INNER JOIN INDIVIDUALS i
ON p.db = i.idiv_id --OR p.db = i.idiv_id
INNER JOIN TBL_STATEMENT s
ON p.st_id = s.st_id
WHERE doc_type LIKE '%%'
AND name LIKE  '%%'
AND LEI LIKE '%%'
ORDER BY  ST_DT;
--OFFSET (1 - 1)* 2 ROWS
--FETCH NEXT 2 ROWS ONLY;

---------------------------------------------------------------

DECLARE
indiv_name INDIVIDUALS.name%TYPE;

CURSOR cursor1 IS SELECT name FROM INDIVIDUALS i
                INNER JOIN PORTFOLIO p
                ON i.idiv_id = p.cr OR idiv_id = db
                INNER JOIN TBL_STATEMENT s
                ON s.st_id = p.st_id
                WHERE idiv_id = '161'
                AND s.st_id = '81';

BEGIN

OPEN cursor1;

FETCH cursor1 INTO indiv_name;
dbms_output.put_line(indiv_name);
CLOSE cursor1;

END;

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

DECLARE
name VARCHAR2(50);
BEGIN
name := get_name('161','81');
dbms_output.put_line(name);
END;

DROP FUNCTION get_name;
---------------------------------------------------------------

SELECT name FROM INDIVIDUALS i
INNER JOIN PORTFOLIO p
ON i.idiv_id = p.cr OR idiv_id = db
INNER JOIN TBL_STATEMENT s
ON s.st_id = p.st_id
WHERE idiv_id = '161'
AND s.st_dt = TO_TIMESTAMP('2020-07-12 22:27:43.000000000','YYYY-MM-DD HH24:MI:SS.FF');
--FETCH NEXT 1 ROWS ONLY;

--OFFSET (page_num - 1)* row_num ROWS
--FETCH NEXT row_num ROWS ONLY;


SELECT * FROM PORTFOLIO;

-----------------------------------------------------------


COMMIT;

SELECT * FROM INDIVIDUALS;

    SELECT
    doc_type, amnt, name, cr, db, idiv_id, st_dt
    FROM
    PORTFOLIO p
    INNER JOIN TBL_STATEMENT s
    ON p.st_id = s.st_id
    INNER JOIN INDIVIDUALS i
    ON i.idiv_id = p.cr  OR i.idiv_id = p.db;
   --WHERE CR = '161';
    --AND st_dt < TO_TIMESTAMP('2020-07-15 11:29:49.000000000','YYYY-MM-DD HH24:MI:SS.FF');



    SELECT SUM(amnt) - SUM(SELECT amnt FROM PORTFOLIO WHERE cr = '161') FROM PORTFOLIO;

SELECT SUM(amnt)FROM PORTFOLIO p
INNER JOIN TBL_STATEMENT s
ON s.st_id = p.st_id
WHERE cr ='161' AND st_dt
BETWEEN TO_TIMESTAMP('2020-07-15 11:29:49.000000000','YYYY-MM-DD HH24:MI:SS.FF')
AND TO_TIMESTAMP('2020-07-15 11:29:49.000000000','YYYY-MM-DD HH24:MI:SS.FF');
--WHERE cr = '161' ;

SELECT
((SELECT SUM(amnt)
FROM PORTFOLIO
WHERE cr ='161'
A) -
(SELECT SUM(amnt)
FROM PORTFOLIO
WHERE db ='161'))AS TOTAL
FROM DUAL;


------ CAN BE USED IN get current balance fronm date to date
SELECT
((SELECT SUM(amnt)FROM PORTFOLIO p
INNER JOIN TBL_STATEMENT s
ON s.st_id = p.st_id
WHERE cr ='162' AND st_dt
BETWEEN TO_TIMESTAMP(TO_CHAR('2020-07-10 11:29:49.000000000'),'YYYY-MM-DD HH24:MI:SS.FF')
AND TO_TIMESTAMP(TO_CHAR('2020-07-17 11:29:49.000000000'),'YYYY-MM-DD HH24:MI:SS.FF')) -
(SELECT SUM(amnt)FROM PORTFOLIO p
INNER JOIN TBL_STATEMENT s
ON s.st_id = p.st_id
WHERE db ='162' AND st_dt
BETWEEN TO_TIMESTAMP(TO_CHAR('2020-07-10 11:29:49.000000000'),'YYYY-MM-DD HH24:MI:SS.FF')
AND TO_TIMESTAMP(TO_CHAR('2020-07-17 11:29:49.000000000'),'YYYY-MM-DD HH24:MI:SS.FF')))AS TOTAL
FROM DUAL;


---- TOTAL OF ALL PERIOD
SELECT
((SELECT SUM(amnt)FROM PORTFOLIO p
INNER JOIN TBL_STATEMENT s
ON s.st_id = p.st_id
WHERE cr ='162' AND st_dt
BETWEEN TO_TIMESTAMP('2020-07-10 11:29:49.00','YYYY-MM-DD HH24:MI:SS.FF')
AND TO_TIMESTAMP('2020-07-17 11:29:49.00','YYYY-MM-DD HH24:MI:SS.FF')))AS TOTAL
FROM DUAL;

--- insert for testing
INSERT INTO PORTFOLIO (doc_type,amnt,cr,db) VALUES('INVOICE',104500,162,null);



------ TRIGGER FOR DELETETING  INDIVIDUAL AND REMOVING THE DATA FROM PORTFOLIO

CREATE OR REPLACE TRIGGER trg_delete_from_Portfolio

BEFORE DELETE
    ON INDIVIDUALS
    FOR EACH ROW

DECLARE

CURSOR cur_amnt IS
                SELECT amnt,cr,db
                FROM PORTFOLIO;
                --WHERE db = :OLD.idiv_id ;

     --cur_row cur_amnt%ROWTYPE;

    st_id_seq VARCHAR2(150);
BEGIN
    --OPEN cur_amnt;



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


DELETE FROM INDIVIDUALS WHERE idiv_id = '2';

SELECT * FROM INDIVIDUALS;

DROP TRIGGER trg_delete_from_Portfolio;

SELECT * FROM PORTFOLIO;
SELECT * FROM INDIVIDUALS;


SELECT * FROM TBL_STATEMENT;
COMMIT;

SELECT SUM(amnt)
FROM PORTFOLIO
WHERE cr = '1';

                SELECT amnt,cr,db
                FROM PORTFOLIO p
                --INNER JOIN INDIVIDUALS i
                --ON i.idiv_id = p.db
                WHERE '2' = p.db;


DELETE FROM PORTFOLIO WHERE DOC_ID = '62';


COMMIT;
SAVEPOINT;
-------------------------------------------------
DELETE FROM INDIVIDUALS WHERE idiv_id = '2';
