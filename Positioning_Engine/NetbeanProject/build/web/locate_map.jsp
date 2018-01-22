<%@page import="ips.MyIPSDatabase"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.Random"%>
<%@page import="java.awt.Point"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.util.Collection"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<jsp:useBean id='db'
             scope='session'
             class='ips.MyIPSDatabase' />
<%
    int dev_count = 0;
    String mode = request.getParameter("display_mode");
    if (mode != null) {
        MyIPSDatabase.mode = mode;
// response.sendRedirect(request.getContextPath() + "/locate_map.jsp");
// return;
    }
    String mac = request.getParameter("device_mac");
    if (mac != null) {
        MyIPSDatabase.mac = mac;
// response.sendRedirect(request.getContextPath() + "/locate_map.jsp");
//return;
    }

    Random random = new Random();
//int[] test1 = {(random.nextInt(61) - 90), (random.nextInt(61) - 90), (random.nextInt(61) - 90), (random.nextInt(61) - 90), (random.nextInt(61) - 90), (random.nextInt(61) - 90), (random.nextInt(61) - 90), (random.nextInt(61) - 90)};

    ResultSet locatablesRS = db.getAllLocatableDevices2();
    String locatable_dev = "";
    String color = "blue";

//int[] rssArray = db.getRSSData("90:B6:86:F6:12:E3");
// Point p2 = db.KNN(rssArray);

%>
<!DOCTYPE html>

<html>
    <head>
        <link href="${pageContext.request.contextPath}/css/locate.css" type="text/css" rel="stylesheet" />
        <link href="${pageContext.request.contextPath}/css/bootstrap.css" type="text/css" rel="stylesheet" />
        <script language="JavaScript" src="${pageContext.request.contextPath}/js/locate_map.js" type="text/javascript"></script>
    </head>
    <body>

        <div class="panel panel-info">
            <div class="panel-heading">Device Info </div>
            <div class="panel-body" style="padding-top: 0; padding-bottom: 0">
                <div class="table-responsive">
                    <table class="table" id="RSStable" style="margin-bottom: 0; padding-bottom: 0; table-layout: fixed;">
                        <thead>
                            <tr>
                                <th style="text-align: center">MAC address</th><th style="text-align: center">Color</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%    System.out.println("mac::::::::::::::::::::::" + MyIPSDatabase.mac + " size: " + MyIPSDatabase.mac.length());
                                if (MyIPSDatabase.mode.equals("Single") && MyIPSDatabase.mac != "") {
                                    if(!db.isLocatable(MyIPSDatabase.mac)){
                                        System.out.println(MyIPSDatabase.mac + " not locatable");
                            %>
                                        <script>initRSSTable();</script>
                                   <%}else{
                                    dev_count = 1;
                                    System.out.println("Single");
                                    int[] rssArray = db.getRSSData2(MyIPSDatabase.mac);
                                    Point p1 = db.KNN(rssArray);
                                    System.out.println("location: " + p1.x + ", " + p1.y);
                            %>
                            <tr>
                                <td><%=MyIPSDatabase.mac%></td><td style="text-align: center; background-color: <%=color%>"></td>
                            </tr>
                        <script>
                            dispalyRSS(<%= Arrays.toString(rssArray)%>);
                            dispalyPos(<%= p1.x%>,<%= p1.y%>);
                            drawDevPos(<%=((p1.x * 0.66 - 726) / 2)%>,<%=((p1.y * 0.66 - 924) / 2)%>, "<%=color%>");
                        </script>
                        <% }} else if (MyIPSDatabase.mode.equals("Multiple")) {
                            System.out.println("Multiple");
                            while (locatablesRS.next()) {
                                locatable_dev = locatablesRS.getString("dev_mac_addr");
                                int[] rssArray1 = db.getRSSData2(locatable_dev);
                                Point p = db.KNN(rssArray1);
                                if (p != null) {
                                    dev_count++;
                                    System.out.println(locatable_dev);
                                    if (MyIPSDatabase.Locatables.containsKey(locatable_dev)) {
                                        color = MyIPSDatabase.Locatables.get(locatable_dev);

                                    } else {
                                        color = "rgb(" + random.nextInt(256) + "," + random.nextInt(256) + "," + random.nextInt(256) + ")";
                                        MyIPSDatabase.Locatables.put(locatable_dev, color);

                                    }
                        %>
                        <tr>
                            <td><%=locatable_dev%></td><td style="text-align: center; background-color: <%=color%>"></td>
                        </tr>

                        <script>
                            drawDevPos(<%=((p.x * 0.66 - 726)/ 2)%>,<%=((p.y * 0.66 - 924) / 2)%>, "<%=color%>");
                        </script>
                        <%
                                }else{
                                System.out.println("No location found");
                                }
                            }
                        } else {
                        %>
                        <tr>
                            <td style="text-align: center;">N/A</td><td style="text-align: center;">N/A</td>
                        </tr><%
                            }
                        %>
                        <script>
                            updateNumOfDevice(<%=dev_count%>);
                        </script>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </body>
</html>