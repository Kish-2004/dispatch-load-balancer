package com.logistics.dispatch.util;

import org.springframework.stereotype.Component;

@Component
public class DistanceCalculator {

        private static final double EARTH_RADIUS_KM = 6371.0;

        public double calculateDistance(
                        double latitude1,
                        double longitude1,
                        double latitude2,
                        double longitude2) {

                if (latitude1 < -90 || latitude1 > 90
                                || latitude2 < -90 || latitude2 > 90) {
                        throw new IllegalArgumentException(
                                        "Latitude must be between -90 and 90");
                }

                if (longitude1 < -180 || longitude1 > 180
                                || longitude2 < -180 || longitude2 > 180) {
                        throw new IllegalArgumentException(
                                        "Longitude must be between -180 and 180");
                }
                double lat1 = Math.toRadians(latitude1);
                double lat2 = Math.toRadians(latitude2);

                double deltaLat = Math.toRadians(latitude2 - latitude1);
                double deltaLon = Math.toRadians(longitude2 - longitude1);

                double a = Math.pow(Math.sin(deltaLat / 2), 2)
                                + Math.cos(lat1) * Math.cos(lat2)
                                                * Math.pow(Math.sin(deltaLon / 2), 2);

                double c = 2 * Math.atan2(
                                Math.sqrt(a),
                                Math.sqrt(1 - Math.min(1.0, Math.max(0.0, a))));

                return EARTH_RADIUS_KM * c;
        }
}
