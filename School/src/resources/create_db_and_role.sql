--CREATE ROLE rector WITH
  LOGIN
  NOSUPERUSER
  INHERIT
  CREATEDB
  NOCREATEROLE
  NOREPLICATION
  ENCRYPTED PASSWORD 'SCRAM-SHA-256$4096:BXVOsQ4PApMG+I57NQH/iw==$fmFDiJWdR0dYxJy72Ce2Bz/2o4r9vhJJtSX+PjN+KeM=:UC6AIbolMstaWwi/ApdIFJpbWZvod96gcUDD5+JJAqY=';

  
-- DROP DATABASE university;
CREATE DATABASE university
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'Russian_Russia.1251'
    LC_CTYPE = 'Russian_Russia.1251'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;
GRANT ALL ON DATABASE university TO postgres;
GRANT TEMPORARY, CONNECT ON DATABASE university TO PUBLIC;
GRANT TEMPORARY ON DATABASE university TO rector;

-- DROP SCHEMA university ;
CREATE SCHEMA university AUTHORIZATION postgres;
GRANT ALL ON SCHEMA university TO postgres;
GRANT ALL ON SCHEMA university TO rector;
ALTER DEFAULT PRIVILEGES IN SCHEMA university GRANT ALL ON TABLES TO rector;
ALTER DEFAULT PRIVILEGES IN SCHEMA university GRANT ALL ON SEQUENCES TO rector;