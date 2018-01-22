/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xuan.location;

import java.util.Comparator;

/**
 *
 * @author Jiuzhou
 */
public class ResultComparator implements Comparator<Result> {

    @Override
    public int compare(Result o1, Result o2) {
       return o1.euclidean_dist < o2.euclidean_dist ? -1 : o1.euclidean_dist == o2.euclidean_dist ? 0 : 1;
    }

}
