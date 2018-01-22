<%@page import="java.sql.ResultSet"%>
<%@page import="java.util.Collection"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<jsp:useBean id='db'
             scope='session'
             class='ips.MyIPSDatabase' />

<%

    int numberOfAPs = db.getNumofRowsInTable("access_points");
    int numberOfWiFidevices = db.getNumofWiFiDevices();

    ResultSet statisticsRS = db.getAllFromTable("statistics");
    int numberOfPackets = 0;
    while (statisticsRS.next()) {
        numberOfPackets += statisticsRS.getInt("frequency");
    }
    ResultSet locatableRS = db.getAllLocatableDevices2();
    locatableRS.last();
    int numberofLocatables = locatableRS.getRow();


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

        <div class="table-responsive">

            <div class="page-header">
                <h1>System Overview</h1>     
            </div>

            <table class="table table-striped">
                <thead>
                <tbody>
                    <tr style="border-top-style: none;">
                        <td style="border-top-style: none;">Number of APs currently used:</td>
                        <td style="border-top-style: none;"><%=numberOfAPs%></td>
                    </tr>
                    <tr>
                        <td>Number of detected Wi-Fi devices:</td>
                        <td><%=numberOfWiFidevices%></td>
                    </tr>
                    <tr>
                        <td>Number of Probe Requests Sniffed:</td>
                        <td><%=numberOfPackets%></td>
                    </tr>
                    <tr>
                        <td>Number of locatable Wi-Fi devices :</td>
                        <td><%=numberofLocatables%></td>
                    </tr>
                </tbody>
            </table>
        </div>  
        <!--footer-->
        <div>
            <jsp:include page="footer.jsp" flush="true"/>
        </div>
    </body>
</html>