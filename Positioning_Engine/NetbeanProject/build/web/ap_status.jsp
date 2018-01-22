<%@page import="java.sql.ResultSet"%>
<%@page import="java.util.Collection"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<jsp:useBean id='db'
             scope='session'
             class='ips.MyIPSDatabase' />

<jsp:useBean id='ssh'
             scope='session'
             class='ips.SSHCommandExecutor' />

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
                                <th>MAC Address</th><th>LAN Address</th><th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                while (access_pointsRS.next()) {
                                    
                                    String host=access_pointsRS.getString("lan_addr");
                            %>
                            <tr>
                                <td><%=access_pointsRS.getString("mac_addr")%></td>
                                <td><%=host%></td>
                                <td><%=ssh.isRunning(host, "wndr4300Sniffer")%></td> 
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