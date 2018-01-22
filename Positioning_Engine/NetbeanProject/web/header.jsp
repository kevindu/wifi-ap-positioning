<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">   
        <link href="${pageContext.request.contextPath}/css/header.css" type="text/css" rel="stylesheet" />
        <link href="${pageContext.request.contextPath}/css/bootstrap.css" type="text/css" rel="stylesheet" />
    </head>
    <body>

        <nav class="navbar navbar-inverse">
            <div class="container-fluid">
                <div class="container">
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#myNavbar">
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>                        
                    </button>
                    <a class="navbar-brand" style="color:white" href="${pageContext.request.contextPath}/index.jsp">MyIPS</a>
                </div>
                <div class="collapse navbar-collapse" id="myNavbar">
                    <ul class="nav navbar-nav">
                        <li class="active"><a href="${pageContext.request.contextPath}/index.jsp">Home</a></li>
                        <li><a href="${pageContext.request.contextPath}/ap.jsp">View All Access Points</a></li>
                        <li><a href="${pageContext.request.contextPath}/wifi.jsp">Detected WiFi devices</a></li>
                        <li><a href="${pageContext.request.contextPath}/locate.jsp">Locate nearby WiFi devices</a></li>
                    </ul>

                </div>
            </div>
                    </div>
        </nav>

    </body>
</html>
