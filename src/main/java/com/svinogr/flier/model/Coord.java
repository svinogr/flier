package com.svinogr.flier.model;

import lombok.Data;

/**
 *  @author SVINOGR
 * version 0.0.1
 *
 * Class point of coordinate
 */
@Data
public class Coord {
    private double lng;
    private double lat;

    public Coord(double lng, double lat) {
        this.lng = lng;
        this.lat = lat;
    }
}
