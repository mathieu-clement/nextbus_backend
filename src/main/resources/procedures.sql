-- all distances in meters
-- latitude and longitude in degrees as usual

CREATE OR REPLACE FUNCTION CLOSEST_STOPS(userLat REAL, userLon REAL, maxDist INTEGER) -- distance in meters
  RETURNS TABLE(id VARCHAR(255), stop_code VARCHAR(255), stop_name VARCHAR(255),
  latitude REAL, longitude REAL, platform_code VARCHAR(255), distance REAL) AS $$
DECLARE
  earthRadius   REAL := 6371000;
  maxDistReal   REAL;
  userLatRad    REAL;
  cosUserLatRad REAL;
  userLonRad    REAL;
  -- Bounding box
  minLat        REAL;
  minLon        REAL;
  maxLat        REAL;
  maxLon        REAL;
BEGIN
  maxDistReal := CAST(maxDist AS REAL);
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
    s.id,
    s.stop_code,
    s.stop_name,
    s.latitude,
    s.longitude,
    s.platform_code,
    -- Haversine formula
    CAST((earthRadius * 2 * atan2(sqrt(sin(radians(s.latitude - userLat) / 2) * sin(radians(s.latitude - userLat) / 2) +
                                       cosUserLatRad * cos(radians(s.latitude)) *
                                       sin(radians(s.longitude - userLon) / 2) *
                                       sin(radians(s.longitude - userLon) / 2)), sqrt(1 - (
      sin(radians(s.latitude - userLat) / 2) * sin(radians(s.latitude - userLat) / 2) +
      cosUserLatRad * cos(radians(s.latitude)) * sin(radians(s.longitude - userLon) / 2) *
      sin(radians(s.longitude - userLon) / 2))))) AS REAL)
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
    sin(radians(s.longitude - userLon) / 2))))) AS REAL)
            < maxDistReal

  ORDER BY distance;

END; $$
LANGUAGE PLPGSQL;