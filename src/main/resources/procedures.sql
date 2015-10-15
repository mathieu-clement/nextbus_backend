-- all distances in meters
-- latitude and longitude in degrees as usual

CREATE OR REPLACE FUNCTION CLOSEST_STOPS(userLat FLOAT8, userLon FLOAT8, maxDist INTEGER) -- distance in meters
  RETURNS TABLE(id VARCHAR(255), stop_code VARCHAR(255), stop_name VARCHAR(255),
  latitude REAL, longitude REAL, platform_code VARCHAR(255), distance INT) AS $$
DECLARE
  earthRadius   REAL := 6371000;
  userLatRad    REAL;
  cosUserLatRad REAL;
  userLonRad    REAL;
  -- Bounding box
  minLat        REAL;
  minLon        REAL;
  maxLat        REAL;
  maxLon        REAL;
BEGIN
  userLonRad := radians(userLon);
  userLatRad := radians(userLat);
  cosUserLatRad := cos(userLatRad);

  -- Bounding box
  minLat := userLat - degrees(maxDist / earthRadius);
  maxLat := userLat + degrees(maxDist / earthRadius);
  minLon := userLon - degrees(maxDist / earthRadius / cos(cosUserLatRad));
  maxLon := userLon + degrees(maxDist / earthRadius / cos(cosUserLatRad));

  RETURN QUERY
  SELECT
    s.id            AS id,
    s.stop_code     AS stop_code,
    s.stop_name     AS stop_name,
    s.latitude      AS latitude,
    s.longitude     AS longitude,
    s.platform_code AS platform_code,
    -- Haversine formula
    CAST((earthRadius * 2 * atan2(sqrt(sin(radians(s.latitude - userLat) / 2) * sin(radians(s.latitude - userLat) / 2) +
                                       cosUserLatRad * cos(radians(s.latitude)) *
                                       sin(radians(s.longitude - userLon) / 2) *
                                       sin(radians(s.longitude - userLon) / 2)), sqrt(1 - (
      sin(radians(s.latitude - userLat) / 2) * sin(radians(s.latitude - userLat) / 2) +
      cosUserLatRad * cos(radians(s.latitude)) * sin(radians(s.longitude - userLon) / 2) *
      sin(radians(s.longitude - userLon) / 2))))) AS INT)
                    AS distance
  FROM stop s
  WHERE s.latitude > minLat AND s.latitude < maxLat
        AND s.longitude > minLon AND s.longitude < maxLon
        AND CAST((earthRadius * 2 * atan2(sqrt(sin(radians(s.latitude - userLat) / 2) *
                                               sin(radians(s.latitude - userLat) / 2) +
                                               cosUserLatRad * cos(radians(s.latitude)) *
                                               sin(radians(s.longitude - userLon) / 2) *
                                               sin(radians(s.longitude - userLon) / 2)), sqrt(1 - (
    sin(radians(s.latitude - userLat) / 2) * sin(radians(s.latitude - userLat) / 2) +
    cosUserLatRad * cos(radians(s.latitude)) * sin(radians(s.longitude - userLon) / 2) *
    sin(radians(s.longitude - userLon) / 2))))) AS INT)
            < maxDist

  ORDER BY distance;

END; $$
LANGUAGE PLPGSQL;


-- Typical query: SELECT * FROM NEXT_BUSES('8504881', now() + interval '8 hours');

CREATE OR REPLACE FUNCTION NEXT_BUSES(stopId VARCHAR(255), maxTimestamp TIMESTAMP WITH TIME ZONE)
  RETURNS TABLE(next_departure TIMESTAMP, agency_timezone VARCHAR(255), trip_short_name VARCHAR(255), trip_head_sign VARCHAR(255)) AS $$
DECLARE
  stop_name_from_provided_stop_id VARCHAR(255);
BEGIN

  RETURN QUERY
  SELECT
    MIN(s.departure_datetime) AS next_departure,
    a.timezone_name           AS agency_timezone,
    t.short_name              AS trip_short_name,
    t.head_sign               AS trip_head_sign
  FROM stop_time s
    JOIN trip t ON s.trip_id = t.id
    JOIN route r ON t.route_id = r.id
    JOIN agency a ON r.agency_id = a.id
  WHERE stop_id = stopId
        AND departure_datetime > NOW() AND departure_datetime < maxTimestamp
        AND t.head_sign NOT IN (SELECT stop_name
                                FROM stop
                                WHERE id = stopId)
  GROUP BY t.short_name, t.head_sign, a.timezone_name
  ORDER BY CAST(t.short_name AS INT), head_sign;

  EXCEPTION
  WHEN invalid_text_representation
    THEN

      RETURN QUERY
      SELECT
        MIN(s.departure_datetime) AS next_departure,
        a.timezone_name           AS agency_timezone,
        t.short_name              AS trip_short_name,
        t.head_sign               AS trip_head_sign
      FROM stop_time s
        JOIN trip t ON s.trip_id = t.id
        JOIN route r ON t.route_id = r.id
        JOIN agency a ON r.agency_id = a.id
      WHERE stop_id = stopId
            AND departure_datetime > NOW() AND departure_datetime < maxTimestamp
            AND t.head_sign NOT IN (SELECT stop_name
                                    FROM stop
                                    WHERE id = stopId)
      GROUP BY t.short_name, t.head_sign, a.timezone_name
      ORDER BY t.short_name, head_sign, departure_datetime;

END; $$
LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION NEXT_BUSES_ALL(stopId VARCHAR(255), maxTimestamp TIMESTAMP WITH TIME ZONE)
  RETURNS TABLE(next_departure TIMESTAMP, agency_timezone VARCHAR(255), trip_short_name VARCHAR(255), trip_head_sign VARCHAR(255)) AS $$
DECLARE
  stop_name_from_provided_stop_id VARCHAR(255);
BEGIN

  RETURN QUERY
  SELECT
    s.departure_datetime AS departure_time,
    a.timezone_name      AS agency_timezone,
    t.short_name         AS trip_short_name,
    t.head_sign          AS trip_head_sign
  FROM stop_time s
    JOIN trip t ON s.trip_id = t.id
    JOIN route r ON t.route_id = r.id
    JOIN agency a ON r.agency_id = a.id
  WHERE stop_id = stopId
        AND departure_datetime > NOW() AND departure_datetime < maxTimestamp
        AND t.head_sign NOT IN (SELECT stop_name
                                FROM stop
                                WHERE id = stopId)
  ORDER BY CAST(t.short_name AS INT), head_sign, departure_datetime;

  EXCEPTION
  WHEN INVALID_TEXT_REPRESENTATION
    THEN

      RETURN QUERY
      SELECT
        s.departure_datetime AS departure_time,
        a.timezone_name      AS agency_timezone,
        t.short_name         AS trip_short_name,
        t.head_sign          AS trip_head_sign
      FROM stop_time s
        JOIN trip t ON s.trip_id = t.id
        JOIN route r ON t.route_id = r.id
        JOIN agency a ON r.agency_id = a.id
      WHERE stop_id = stopId
            AND departure_datetime > NOW() AND departure_datetime < maxTimestamp
            AND t.head_sign NOT IN (SELECT stop_name
                                    FROM stop
                                    WHERE id = stopId)

      ORDER BY short_name, head_sign, departure_datetime;

END; $$
LANGUAGE PLPGSQL;