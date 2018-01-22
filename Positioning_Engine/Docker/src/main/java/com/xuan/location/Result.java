/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xuan.location;

import java.awt.Point;

/**
 *
 * @author Jiuzhou
 */
public class Result {

    Point position;
    double euclidean_dist;

    public Result(Point position, double euclidean_distance) {
        this.position = position;
        this.euclidean_dist = euclidean_distance;
    }
}
