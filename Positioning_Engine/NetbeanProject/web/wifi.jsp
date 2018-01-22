<%@page import="java.sql.ResultSet"%>
<%@page import="java.util.Collection"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<jsp:useBean id='db'
             scope='session'
             class='ips.MyIPSDatabase' />

<%
    ResultSet detected_devicesRS = db.getAllWiFiDevices();
    ResultSet locatablesRS = db.getAllLocatableDevices2();
%>
<!DOCTYPE html>
<html>
    <head>
        <title>MyIPS</title>
         <link href="${pageContext.request.contextPath}/css/index.css" type="text/css" rel="stylesheet" />
        <link href="${pageContext.request.contextPath}/css/bootstrap.css" type="text/css" rel="stylesheet" />
    </head>
    <body>
        <!--header-->
        <div>
            <jsp:include page="header.jsp" flush="true"/>
        </div>

        <div class="container">
            <div class="page-header">
                <h1>Detected Wi-Fi devices</h1>
            </div>
            <div class="jumbotron">
                <div class = "table-responsive">
                    <div class="pre-scrollable" style="max-height:180px; overflow-y:auto;">
                        <table class = "table">
                            <thead>
                                <tr>
                                    <th>Device MAC address</th><th>AP MAC Address</th><th>Signal strength (dBm)</th><th>Timestamp</th>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                    while (detected_devicesRS.next()) {
                                %>
                                <tr>
                                    <td> <%= detected_devicesRS.getString("dev_mac_addr")%> </td>
                                    <td align="right"><%=detected_devicesRS.getString("ap_mac_addr")%></td>
                                    <td align="right"><%=detected_devicesRS.getInt("signal_strength")%></td>
                                    <td align="right"><%=detected_devicesRS.getString("time")%></td>
                                </tr>
                                <%
                                    }
                                %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <div class="page-header">
                <h1>Locatable Wi-Fi devices</h1>
            </div>
            <div class="jumbotron">
                <div class = "table-responsive">
                    <div class="pre-scrollable" style="max-height:150px; overflow-y:auto;">
                        <table class = "table">
                            <thead>
                                <tr>
                                    <th>Device MAC address</th><th>Last Detected</th><th>Elapsed Time (Sec)</th>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                    while (locatablesRS.next()) {
                                %>
                                <tr>
                                    <td> <%= locatablesRS.getString("dev_mac_addr")%> </td>

                                    <td align="right"><%=locatablesRS.getString("last_detected")%></td>
                                    <td align="right"><%=locatablesRS.getString("elapsed_time")%></td>

                                </tr>
                                <%
                                    }
                                %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
        <!--footer-->
        <div>
            <jsp:include page="footer.jsp" flush="true"/>
        </div>
    </body>
</html>