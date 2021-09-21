package com.svinogr.flier.model;

import lombok.Data;

/**
 * @author SVINOGR
 * version 0.0.1
 *
 * Class helper to define coord araund for coord
 *
 */
@Data
public class CoordHelper {
    private Coord coord;
    private static final double METRE= 0.001;

    /**
     * Constructor
     * @param coord {@link Coord}
     * @see CoordHelper
     */
    public CoordHelper(Coord coord) {
        this.coord = coord;
    }

    public double getNordUpPoint(){
        if (coord.getLat() < 0) {
            return coord.getLat() + METRE;
        } else {
            return coord.getLat() + METRE;
        }
    }

    public double getSouthDownPoint(){
        if (coord.getLat() < 0) {
            return coord.getLat() - METRE;
        } else {
            return coord.getLat() - METRE;
        }
    }

    public double getWestLeftPoint(){
        if (coord.getLng() < 0) {
            return coord.getLng() - METRE;
        } else {
            return coord.getLng() - METRE;
        }
    }
    public double getEastRightPoint(){
        if (coord.getLng() < 0) {
            return coord.getLng() + METRE;
        } else {
            return coord.getLng() + METRE;
        }
    }

    @Deprecated
    public int getSquareCoord() {
        if (coord.getLat() > 0 && coord.getLng() < 0) return 1;
        if (coord.getLat() > 0 && coord.getLng() > 0) return 2;
        if (coord.getLat() < 0 && coord.getLng() < 0) return 3;
        if (coord.getLat() < 0 && coord.getLng() > 0) return 4;
        return 0;
    }
}
