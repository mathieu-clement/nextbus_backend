BEGIN TRANSACTION;

CREATE TABLE IF NOT EXISTS AGENCY (
  ID            BIGINT PRIMARY KEY,
  NAME          VARCHAR(255) NOT NULL,
  TIMEZONE_NAME VARCHAR(128) NOT NULL
);

CREATE TABLE IF NOT EXISTS CALENDAR_DATE (
  SERVICE_ID VARCHAR(255) NOT NULL,
  LOCAL_DATE DATE         NOT NULL,
  PRIMARY KEY (SERVICE_ID, LOCAL_DATE)
);

CREATE TABLE IF NOT EXISTS ROUTE (
  ID           VARCHAR(255) PRIMARY KEY,
  PK_AGENCY_ID BIGINT REFERENCES AGENCY (ID) NOT NULL,
  SHORT_NAME   VARCHAR(255)                  NOT NULL,
  LONG_NAME    VARCHAR(255)                  NOT NULL,
  ROUTE_TYPE   SMALLINT                      NOT NULL
);

CREATE TABLE IF NOT EXISTS STOP (
  ID            VARCHAR(255) PRIMARY KEY,
  STOP_CODE     VARCHAR(255) NOT NULL,
  STOP_NAME     VARCHAR(255) NOT NULL,
  LATITUDE      FLOAT4       NOT NULL,
  LONGITUDE     FLOAT4       NOT NULL,
  PLATFORM_CODE VARCHAR(255) NULL
);

CREATE TABLE IF NOT EXISTS TRIP (
  ID         VARCHAR(255) PRIMARY KEY,
  SERVICE_ID VARCHAR(255)                       NOT NULL,
  ROUTE_ID   VARCHAR(255) REFERENCES ROUTE (ID) NOT NULL,
  HEAD_SIGN  VARCHAR(255)                       NULL,
  SHORT_NAME VARCHAR(255)                       NULL
);

CREATE TABLE IF NOT EXISTS STOP_TIME (
  TRIP_ID            VARCHAR(255) REFERENCES TRIP (ID) NOT NULL,
  DEPARTURE_DATETIME TIMESTAMP                         NOT NULL,
  STOP_ID            VARCHAR(255) REFERENCES STOP (ID) NOT NULL,
  STOP_SEQUENCE      INT                               NULL,
  PRIMARY KEY (STOP_ID, DEPARTURE_DATETIME, TRIP_ID)
);

COMMIT TRANSACTION;