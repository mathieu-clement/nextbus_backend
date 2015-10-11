-- Name this file schema.sql so that Spring runs it automatically

BEGIN TRANSACTION;

CREATE TABLE IF NOT EXISTS AGENCY (
  ID            BIGINT PRIMARY KEY,
  NAME          VARCHAR(255) NOT NULL,
  TIMEZONE_NAME VARCHAR(128) NOT NULL
);

CREATE TABLE IF NOT EXISTS CALENDAR_DATE (
  SERVICE_ID VARCHAR(255) NOT NULL,
  LOCAL_DATE VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS ROUTE (
  ID         VARCHAR(255) NOT NULL,
  AGENCY_ID  BIGINT       NOT NULL,
  SHORT_NAME VARCHAR(255) NOT NULL,
  LONG_NAME  VARCHAR(255) NOT NULL,
  ROUTE_TYPE VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS STOP (
  ID            VARCHAR(255) NOT NULL,
  STOP_CODE     VARCHAR(255) NULL,
  STOP_NAME     VARCHAR(255) NOT NULL,
  LATITUDE      FLOAT4       NOT NULL,
  LONGITUDE     FLOAT4       NOT NULL,
  PLATFORM_CODE VARCHAR(255) NULL
);

CREATE TABLE IF NOT EXISTS TRIP (
  ID         VARCHAR(255) NOT NULL,
  SERVICE_ID VARCHAR(255) NOT NULL,
  ROUTE_ID   VARCHAR(255) NOT NULL,
  HEAD_SIGN  VARCHAR(255) NULL,
  SHORT_NAME VARCHAR(255) NULL
);

CREATE TABLE IF NOT EXISTS STOP_TIME (
  TRIP_ID            VARCHAR(255) NOT NULL,
  DEPARTURE_DATETIME TIMESTAMP    NOT NULL,
  STOP_ID            VARCHAR(255) NOT NULL,
  STOP_SEQUENCE      INT          NULL
);

COMMIT TRANSACTION;

-- Activate the constraints only AFTER populating the DB
-- Then remember to ANALYZE

-- Typical populate command:
-- COPY AGENCY (ID, NAME, TIMEZONE_NAME) FROM '/home/mathieu/CSV/agency.csv' WITH CSV QUOTE AS '"';
-- COPY CALENDAR_DATE FROM '/home/mathieu/CSV/calendar_date.csv' WITH CSV QUOTE AS '"';
-- COPY ROUTE FROM '/home/mathieu/CSV/route.csv' WITH CSV QUOTE AS '"';
-- COPY STOP FROM '/home/mathieu/CSV/stop.csv' WITH CSV QUOTE AS '"';
-- COPY TRIP FROM '/home/mathieu/CSV/trip.csv' WITH CSV QUOTE AS '"';


BEGIN TRANSACTION;

ALTER TABLE CALENDAR_DATE ADD CONSTRAINT calendar_date_pkey PRIMARY KEY (SERVICE_ID, LOCAL_DATE);

ALTER TABLE ROUTE ADD CONSTRAINT route_pkey PRIMARY KEY (ID);
ALTER TABLE ROUTE ADD CONSTRAINT route_agency_id_fkey FOREIGN KEY (agency_id) REFERENCES agency (id);

ALTER TABLE STOP ADD CONSTRAINT stop_pkey PRIMARY KEY (ID);

ALTER TABLE TRIP ADD CONSTRAINT trip_pkey PRIMARY KEY (ID);
ALTER TABLE TRIP ADD CONSTRAINT trip_route_id_fkey FOREIGN KEY (route_id) REFERENCES route (id);

ALTER TABLE STOP_TIME ADD CONSTRAINT stop_time_pkey PRIMARY KEY (STOP_ID, DEPARTURE_DATETIME, TRIP_ID);
ALTER TABLE STOP_TIME ADD CONSTRAINT stop_time_trip_id_fkey FOREIGN KEY (trip_id) REFERENCES trip (id);
ALTER TABLE STOP_TIME ADD CONSTRAINT stop_time_stop_id_fkey FOREIGN KEY (stop_id) REFERENCES stop (id);

COMMIT TRANSACTION;