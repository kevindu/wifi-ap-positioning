<%@page import="java.sql.ResultSet"%>
<%@page import="java.util.Collection"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<jsp:useBean id='db'
             scope='session'
             class='ips.MyIPSDatabase' />


<%
    ResultSet access_pointsRS = db.getAllAPs();
    String x = request.getParameter("Xcoordinate");
    String y = request.getParameter("Ycoordinate");
    String mac = request.getParameter("MAC");

    if (x != null && y != null && mac != null) {
        System.out.println("x:" + x);
        System.out.println("y:" + y);
        System.out.println("mac:" + mac);

        db.setAPPosition(mac, x, y);
        response.sendRedirect(request.getContextPath() + "/ap.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <title>MyIPS</title>
        <link href="${pageContext.request.contextPath}/css/bootstrap.css" type="text/css" rel="stylesheet" />
        <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">

    </head>
    <body>
        <%java.text.DateFormat df = new java.text.SimpleDateFormat("HH:mm:ss ");%>
        <div style="text-align: right" ><b>Last updated: </b><%= df.format(new java.util.Date())%></div>
        <div class="jumbotron">

            <div class = "table-responsive">
                <div class="pre-scrollable"  style="overflow-y:auto;">
                    <table class = "table">                 
                        <thead>
                            <tr>
                                <th>Model</th><th>MAC Address</th><th>LAN Address</th><th>Position(x,y)</th><th></th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                while (access_pointsRS.next()) {
                            %>
                            <tr>
                                <td> <%= access_pointsRS.getString("model")%> </td>
                                <td><%=access_pointsRS.getString("mac_addr")%></td>
                                <td><%=access_pointsRS.getString("lan_addr")%></td>
                                <td>(<%=access_pointsRS.getString("x")%>,<%=access_pointsRS.getString("y")%>)</td> 
                                <td><form class="form-inline" role="form" action="ap_list.jsp" method="post">
                                        <input type="hidden" name="MAC" value="<%= access_pointsRS.getString("mac_addr")%>">
                                        <b>New:</b>
                                        
                                        
                                        <div class="input-group input-group-sm">
                                            <span class="input-group-addon" id="xpos" style="padding-top:0px; padding-bottom:0px; height: 20px; line-height: 20px;">X</span>
                                            <input type="number" class="form-control input-sm" aria-describedby="xpos" name="Xcoordinate" style="padding-top:0px; padding-bottom:0px; height: 25px; line-height: 25px;width: 58px;" min="0" max="999">
                                        </div>
                                        
                                        <div class="input-group input-group-sm">
                                            <span class="input-group-addon" id="ypos" style="padding-top:0px; padding-bottom:0px; height: 20px; line-height: 20px;">Y</span>
                                            <input type="number" class="form-control" aria-describedby="ypos" name="Ycoordinate" style="padding-top:0px; padding-bottom:0px; height: 25px; line-height: 25px;width: 58px;" min="0" max="999">
                                        </div>

                                        <button  type="submit" class="btn btn-primary btn-sm"  style="padding-top:0px; padding-bottom:0px; height: 25px; line-height: 25px;" >Update</button>

                                    </form></td>
                            </tr>
                            <%
                                }
                            %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </body>
</html>