<%@page import="java.sql.ResultSet"%>
<%@page import="java.util.Collection"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
    <head>
        <title>MyIPS</title>
        <link href="${pageContext.request.contextPath}/css/bootstrap.css" type="text/css" rel="stylesheet" />
        <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
        <script language="JavaScript" src="${pageContext.request.contextPath}/js/ap.js" type="text/javascript"></script>
        <script language="JavaScript" src="${pageContext.request.contextPath}/js/jquery-1.11.3.min.js" type="text/javascript"></script>
        <script language="JavaScript" src="${pageContext.request.contextPath}/js/jquery-migrate-1.2.1.min.js" type="text/javascript"></script>
   
        <script>$(document).ready(function () {
                    $("#refresh").click(function () {
                        $("#aplist").load("ap_list.jsp");
                    });
                });
        </script>
        
    </head>
    <body>
        <!--header-->
        <div>
            <jsp:include page="header.jsp" flush="true"/>
        </div>
        <div class="container">
            <div class="page-header" style=" position: relative; margin-bottom:0 ">
                <h1>Access Points</h1>
                <a id="refresh" style=" position: absolute; top: 25%; right:0px;" href="#" class="btn btn-info btn-sm">
                    <span class="glyphicon glyphicon-refresh"></span> Refresh
                </a>
            </div>
            <div id="aplist"> <jsp:include page="ap_list.jsp"/></div>    
        </div>
        <!--footer-->
        <div>
            <jsp:include page="footer.jsp" flush="true"/>
        </div>
    </body>
</html>