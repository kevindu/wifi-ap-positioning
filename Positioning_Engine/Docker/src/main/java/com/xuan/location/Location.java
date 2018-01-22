package com.xuan.location;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.awt.Point;
import java.util.*;
import com.google.gson.Gson;

/**
 * exposed at "localiser" path
 */
@Path("location")
public class Location {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getMH(@QueryParam("mac") String mac) {
        MyIPSDatabase db = new MyIPSDatabase();
        int[] rssArray = db.getRSSData2(mac);
        System.out.println("rssArray "+Arrays.toString(rssArray));
        Point p1 = db.KNN(rssArray);
        db.destroy();
        Gson gson = new Gson();
        String json = gson.toJson(p1);
        return json;
    }
}
