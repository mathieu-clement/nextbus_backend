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
    s.id AS id,
    s.stop_code AS stop_code,
    s.stop_name AS stop_name,
    s.latitude AS latitude,
    s.longitude AS longitude,
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