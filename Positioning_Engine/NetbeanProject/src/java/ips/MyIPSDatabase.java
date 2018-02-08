package ips;

/**
 *
 * @author Jiuzhou
 */
import java.awt.Point;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MyIPSDatabase {

    public static String mac;
     public static String mode;
    public static HashMap<String, String> Locatables = new HashMap<String, String>();
    Connection con = null;
    static MyIPSDatabase singleton=null;

    public static void main(String[] args) throws Exception {


        MyIPSDatabase db = new MyIPSDatabase();


    }

    public MyIPSDatabase() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("loaded class");
            con = DriverManager.getConnection("jdbc:mysql://xxx:3306/myips", "xxx", "xxx");
            System.out.println("created con");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Exception in MyIPSDatabase(): " + e);
        }

    }

     public static MyIPSDatabase getSingleton() {
        if (singleton == null) {
            singleton = new MyIPSDatabase();
        }
        return singleton;
    }

    public ResultSet getAllAPs() {
        try {
            Statement s = con.createStatement();
            String query = "SELECT model, mac_addr, lan_addr, ST_X(position) AS x, ST_Y(position) as y,lan_id from access_points";
            ResultSet rs = s.executeQuery(query);


            return rs;
        } catch (Exception e) {
            System.out.println("Exception in getAllAPs(): " + e);
            return null;
        }
    }
    public ResultSet getAllFingerprints() {
        try {
            Statement s = con.createStatement();
            String query = "Select id,ST_X(position) AS x, ST_Y(position) as y,ap2_1_rss,ap2_2_rss,ap2_3_rss,ap2_4_rss,ap2_5_rss,ap2_6_rss,ap2_7_rss,ap2_8_rss from fingerprints";
            ResultSet rs = s.executeQuery(query);


            return rs;
        } catch (Exception e) {
            System.out.println("Exception in getAllFingerprints(): " + e);
            return null;
        }
    }



    public ResultSet getAllLocatableDevices2() {
        try {
            Statement s = con.createStatement();
            String query = "SELECT dev_mac_addr,MIN(time) AS last_detected, TIMESTAMPDIFF(SECOND,MIN(time),NOW()) AS elapsed_time FROM "
                    + "(SELECT detected_devices.dev_mac_addr,time FROM "
                    + "detected_devices,(SELECT dev_mac_addr, COUNT(*) AS count FROM detected_devices GROUP BY dev_mac_addr HAVING COUNT(*)>=5) AS locatables "
                    + "WHERE detected_devices.dev_mac_addr=locatables.dev_mac_addr)as newTable where TIMESTAMPDIFF(SECOND,time,NOW())<=10 group by dev_mac_addr ";
            //where TIMESTAMPDIFF(SECOND,time,NOW())<=30
            ResultSet rs = s.executeQuery(query);
            return rs;
        } catch (Exception e) {
            System.out.println("Exception in  getAllLocatableDevices2(): " + e);
            return null;
        }
    }

    public boolean isLocatable(String MAC) {
        try {
            Statement s = con.createStatement();
            String query = "SELECT dev_mac_addr,MIN(time) AS last_detected, TIMESTAMPDIFF(SECOND,MIN(time),NOW()) AS elapsed_time FROM "
                    + "(SELECT detected_devices.dev_mac_addr,time FROM "
                    + "detected_devices,(SELECT dev_mac_addr, COUNT(*) AS count FROM detected_devices group by dev_mac_addr HAVING COUNT(*)>=5) AS locatables "
                    + "WHERE detected_devices.dev_mac_addr=locatables.dev_mac_addr)as newTable where TIMESTAMPDIFF(SECOND,time,NOW())<=10 AND dev_mac_addr='"+MAC+"' group by dev_mac_addr, time";
            ResultSet rs = s.executeQuery(query);
            rs.last();
            return rs.getRow()!=0;
        } catch (Exception e) {
            System.out.println("Exception in  isLocatable(): " + e);
            return false;
        }
    }


    public ResultSet getAllFromTable(String tbl_name) {
        try {
            Statement s = con.createStatement();
            String query = "Select * from " + tbl_name;
            ResultSet rs = s.executeQuery(query);
            return rs;
        } catch (Exception e) {
            System.out.println("Exception in  getAllFromTable(): " + e);
            return null;
        }
    }

     public ResultSet getAllWiFiDevices() {
        try {
            Statement s = con.createStatement();
            String query = "Select * from detected_devices";
            ResultSet rs = s.executeQuery(query);
            return rs;
        } catch (Exception e) {
            System.out.println("Exception in  getAllWiFiDevices(): " + e);
            return null;
        }
    }

    public int getNumofWiFiDevices() {
        try {
            Statement s = con.createStatement();
            String query = "Select COUNT(DISTINCT dev_mac_addr) from detected_devices";
            ResultSet rs = s.executeQuery(query);
            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }
            return count;
        } catch (Exception e) {
            System.out.println("Exception in   getNumofWiFiDevices(): " + e);
            return 0;
        }
    }

    public double getEuclideanDistance(int[] fingerprint, int[] measuredRSS) {
        double dist = 0.0;
        for (int i = 0; i < fingerprint.length; i++) {
            dist += Math.pow(fingerprint[i] - measuredRSS[i], 2);
        }
        return Math.sqrt(dist);
    }

    // get the RSS of the mobile device through device MAC address
       public int[] getRSSData2(String mac_addr) {
        int[] rss_data = {-100, -100, -100, -100, -100, -100, -100, -100};
        try {
            Statement s = con.createStatement();

            String query = "SELECT dev_mac_addr,ap_mac_addr,signal_strength, lan_id FROM detected_devices right join access_points on ap_mac_addr=mac_addr WHERE dev_mac_addr='" + mac_addr + "' ORDER by lan_id ASC";
            ResultSet rs = s.executeQuery(query);

            while (rs.next()) {
                if (rs.getInt("lan_id") == 1) {
                    rss_data[0] = rs.getInt("signal_strength");
                    System.out.println("lan 1: " + rss_data[0]);
                    continue;
                }
                if (rs.getInt("lan_id") == 2) {
                    rss_data[1] = rs.getInt("signal_strength");
                    System.out.println("lan 2: " + rss_data[1]);
                    continue;
                }
                if (rs.getInt("lan_id") == 3) {
                    rss_data[2] = rs.getInt("signal_strength");
                    System.out.println("lan 3: " + rss_data[2]);
                    continue;
                }
                if (rs.getInt("lan_id") == 4) {
                    rss_data[3] = rs.getInt("signal_strength");
                    System.out.println("lan 4: " + rss_data[3]);
                    continue;
                }
                if (rs.getInt("lan_id") == 5) {
                    rss_data[4] = rs.getInt("signal_strength");
                    System.out.println("lan 5: " + rss_data[4]);
                    continue;
                }
                if (rs.getInt("lan_id") == 6) {
                    rss_data[5] = rs.getInt("signal_strength");
                    System.out.println("lan 6: " + rss_data[5]);
                    continue;
                }
                if (rs.getInt("lan_id") == 7) {
                    rss_data[6] = rs.getInt("signal_strength");
                    System.out.println("lan 7: " + rss_data[6]);
                    continue;
                }
                if (rs.getInt("lan_id") == 8) {
                    rss_data[7] = rs.getInt("signal_strength");
                    System.out.println("lan 8: " + rss_data[7]);
                }
            }

            return rss_data;

        } catch (Exception e) {
            System.out.println("Exception in  getRSSData2(): " + e);
            return null;
        }

    }


    public Point KNN(int[] measuredRSS) {
        double distance;
        int[] fingerprint;
        List<Result> resultList = new ArrayList<Result>();
        try {
            Statement s = con.createStatement();
            String query = "Select ST_X(position) AS x, ST_Y(position) as y,ap2_1_rss,ap2_2_rss,ap2_3_rss,ap2_4_rss,ap2_5_rss,ap2_6_rss,ap2_7_rss,ap2_8_rss from fingerprints";
            ResultSet rs = s.executeQuery(query);

            while (rs.next()) {

                int rss1 = rs.getInt("ap2_1_rss");
                int rss2 = rs.getInt("ap2_2_rss");
                int rss3 = rs.getInt("ap2_3_rss");
                int rss4 = rs.getInt("ap2_4_rss");
                int rss5 = rs.getInt("ap2_5_rss");
                int rss6 = rs.getInt("ap2_6_rss");
                int rss7 = rs.getInt("ap2_7_rss");
                int rss8 = rs.getInt("ap2_8_rss");

                int x = rs.getInt("x");
                int y = rs.getInt("y");
                fingerprint = new int[]{rss1, rss2, rss3, rss4, rss5, rss6,rss7, rss8};
                distance = getEuclideanDistance(fingerprint, measuredRSS);
                //System.out.println("Dist:"+distance);
                resultList.add(new Result(new Point(x, y), distance));
            }


            Collections.sort(resultList, new ResultComparator());

            System.out.println("dist: "+ resultList.get(0).euclidean_dist);

            if (resultList.get(0).euclidean_dist <= 200) {//30
                return resultList.get(0).position;
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println("Exception in  getKNN(): " + e);
            return null;
        }
    }

    public int getNumofRowsInTable(String tbl_name) {
        try {
            Statement s = con.createStatement();
            String query = "Select COUNT(*) from " + tbl_name;
            ResultSet rs = s.executeQuery(query);
            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }
            return count;
        } catch (Exception e) {
            System.out.println("Exception in   getNumofRecordsFromTable(): " + e);
            return 0;
        }
    }

    public void setAPPosition(String dev_mac_addr, String x, String y) throws SQLException {

        PreparedStatement pre_statement = null;

        String query = "UPDATE access_points SET position=POINT(?,?) WHERE mac_addr=?";

        try {

            pre_statement = con.prepareStatement(query);

            try {
                int xpos = Integer.parseInt(x);
                int ypos = Integer.parseInt(y);
                pre_statement.setInt(1, xpos);
                pre_statement.setInt(2, ypos);

            } catch (NumberFormatException e) {

                pre_statement.setString(1, null);
                pre_statement.setString(2, null);
            }
            pre_statement.setString(3, dev_mac_addr);

            pre_statement.executeUpdate();

        } catch (Exception e) {
            System.out.println("Exception in setAPPosition(): " + e);

        } finally {

            if (pre_statement != null) {
                pre_statement.close();
            }

        }
    }

    public void saveData(int x1, int y1, int x2, int y2) throws SQLException {
        PreparedStatement pre_statement = null;

        String query = "INSERT INTO data_essex(actual_position, estimated_position) "
                + "VALUES(POINT(?,?),POINT(?,?))";

        try {
            pre_statement = con.prepareStatement(query);
            try {
                pre_statement.setInt(1, x1);
                pre_statement.setInt(2, y1);
                pre_statement.setInt(3, x2);
                pre_statement.setInt(4, y2);
            } catch (NumberFormatException e) {
                pre_statement.setString(1, null);
                pre_statement.setString(2, null);
            }
            pre_statement.executeUpdate();
        } catch (Exception e) {
            System.out.println("Exception in saveEvaluationData(): " + e);

        } finally {
            if (pre_statement != null) {
                pre_statement.close();
            }

        }
    }
}
