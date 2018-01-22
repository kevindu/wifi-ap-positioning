<%@page import="ips.MyIPSDatabase"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.util.Collection"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<jsp:useBean id='db'
             scope='session'
             class='ips.MyIPSDatabase' />
<%
    MyIPSDatabase.mac = "";
    MyIPSDatabase.mode = "Single";
    MyIPSDatabase.Locatables.clear();

    ResultSet FP_RS = db.getAllFingerprints();

    int x = 0;
    int y = 0;

    ResultSet locatablesRS = db.getAllLocatableDevices2();
    locatablesRS.last();
    int numOfLocatables = locatablesRS.getRow();
    locatablesRS.beforeFirst();
    String locatable_dev = "";

    String out_x1 = request.getParameter("output_x1");
    String out_y1 = request.getParameter("output_y1");
    String out_x2 = request.getParameter("output_x2");
    String out_y2 = request.getParameter("output_y2");

    if (out_x1 != null && out_y1 != null && out_x2 != null && out_y2 != null) {
        System.out.println("x1:" + out_x1);
        System.out.println("y1:" + out_y1);
        System.out.println("x2:" + out_x2);
        System.out.println("y2:" + out_y2);

        db.saveData(Integer.parseInt(out_x1), Integer.parseInt(out_y1), Integer.parseInt(out_x2), Integer.parseInt(out_y2));

        response.sendRedirect(request.getContextPath() + "/locate.jsp");
        return;
    }

%>
<!DOCTYPE html>

<html>
    <head>
        <title>MyIPS</title>   
        <link href="${pageContext.request.contextPath}/css/bootstrap.css" type="text/css" rel="stylesheet" />
        <link href="${pageContext.request.contextPath}/css/bootstrap-select.css" type="text/css" rel="stylesheet" />
        <link href="${pageContext.request.contextPath}/css/bootstrap-switch.css" type="text/css" rel="stylesheet" />

        <script language="JavaScript" src="${pageContext.request.contextPath}/js/locate.js" type="text/javascript"></script>
        <script language="JavaScript" src="${pageContext.request.contextPath}/js/jquery-1.11.3.min.js" type="text/javascript"></script>     
        <script language="JavaScript" src="${pageContext.request.contextPath}/js/bootstrap.js" type="text/javascript"></script>
        <script language="JavaScript" src="${pageContext.request.contextPath}/js/bootstrap-select.js" type="text/javascript"></script>
        <script language="JavaScript" src="${pageContext.request.contextPath}/js/bootstrap-switch.js" type="text/javascript"></script>
        <script>
            $(function (argument) {
                $('[type="checkbox"]').bootstrapSwitch();
            });
            $(document).ready(function () {
                $('#mode-select').on('hidden.bs.select', function (e) {
                    var mode = document.getElementById("mode-select").value;
                    var mac = document.getElementById("wifidevice-select").value;
                    if (mode === "Multiple") {
                        $("#single-mode").css("visibility", "hidden");
                        $('#wifidevice-select').selectpicker('val', '');
                        mac = "";
                        initRSSTable();
                        var str = $('input[name="my-checkbox"]').bootstrapSwitch('state');
                        if (str === false) {
                            canvas = document.getElementById("myCanvasLayer1");
                            ctx = canvas.getContext("2d");
                            ctx.clearRect(0, 0, canvas.width, canvas.height);
                        }
                        $('#map').load('locate_map.jsp');
                    } else {
                        $("#single-mode").css("visibility", "visible");
                        var str = $('input[name="my-checkbox"]').bootstrapSwitch('state');
                        if (str === false) {
                            canvas = document.getElementById("myCanvasLayer1");
                            ctx = canvas.getContext("2d");
                            ctx.clearRect(0, 0, canvas.width, canvas.height);
                        }
                        $('#map').load('locate_map.jsp');
                    }
                    $.post("locate_map.jsp",
                            {
                                display_mode: mode,
                                device_mac: mac
                            });
                });
                $('#wifidevice-select').on('hidden.bs.select', function (e) {
                    var value1 = document.getElementById("wifidevice-select").value;

                    $.post("locate_map.jsp",
                            {
                                device_mac: value1
                            });
                });



            });
        </script>

        <script type="text/javascript">

            document.addEventListener("DOMContentLoaded", init, false);

            function init()
            {
                var canvas = document.getElementById("myCanvasLayer2");
                canvas.addEventListener("mouseup", getPosition, false);
                canvas.addEventListener("touchstart", getPosition1, false);
            }

            function getPosition(event)
            {
                var x, y;
                canoffset = $(myCanvasLayer1).offset();
                x = event.clientX + document.body.scrollLeft + document.documentElement.scrollLeft - Math.floor(canoffset.left);
                y = event.clientY + document.body.scrollTop + document.documentElement.scrollTop - Math.floor(canoffset.top) + 1;
                var coordinates = document.getElementById('coordinates');
                drawX(x, y);
                x = (x * 2 + 726)/0.66;
                y = (y * 2 + 924)/0.66;

                x = Math.round(x);
                y = Math.round(y);
                document.getElementById("output_x1").value = x;
                document.getElementById("output_y1").value = y;
                coordinates.innerHTML = "<b>Position:</b> (" + x + ", " + y + ")";

            }

            function getPosition1(e)
            {
                var x, y;

                x = e.pageX - this.offsetLeft;
                y = e.pageY - this.offsetTop;
                var coordinates = document.getElementById('coordinates');
                drawX(x, y);
                x = x * 1.5 + 1095;
                y = y * 1.5 + 1418;
                coordinates.innerHTML = "<b>Position1:</b> (" + x + ", " + y + ")";

            }


        </script>
    </head>
    <body>
        <!--header-->
        <div>
            <jsp:include page="header.jsp" flush="true"/>
        </div>
        <div class="container">
            <div class="page-header">

                <h1>Locate Nearby Wi-Fi Devices</h1>    


            </div>
            <div class="row" style="height: 900px;">       
                <div class="col-xs-8">   
                    <h3 style="margin-top:0px;">Map</h3>   
                    <div style="position: relative;">
                        <img id="background" class="img-responsive" src="${pageContext.request.contextPath}/images/f5map.png" alt="Lab1" style="width:363px;height:637px;">

                        <canvas id="myCanvas" class="img-responsive" width="363" height="637" style="position: absolute; left: 0; top: 0; z-index: 0;"></canvas>

                        <script>
                            drawMap();
                        </script>
                        <%while (FP_RS.next()) {
                                x = FP_RS.getInt("x");
                                y = FP_RS.getInt("y");
                        %>
                        <!--DRAW-->
                        <%
                            }
                        %>
                        <canvas id="myCanvasLayer1" class="img-responsive" width="363" height="637" style="position: absolute; left: 0; top: 0; z-index: 1;"></canvas>
                        <canvas id="myCanvasLayer2" class="img-responsive" width="363" height="637" style="position: absolute; left: 0; top: 0; z-index: 2;"></canvas>
                        <!--<div id="map"> LOCATE_MAP</div>--> 
                    </div>             
                </div>   
                <div class="col-xs-4">
                    <!--                    <h3 style="margin-top:0px;">Locatable Wi-Fi devices</h3> -->             
                    <h3 style="margin-top:0px;">Display Mode: <select class="selectpicker" data-width="55%" name="display_mode" id="mode-select">
                            <option selected="selected">Multiple</option>
                            <option >Single</option>

                        </select>
                    </h3>

                    <div id="single-mode" style="visibility:hidden;">
                        <div class="row">                        
                            <%java.text.DateFormat df = new java.text.SimpleDateFormat("HH:mm:ss ");%>
                            <div class="col-sm-12"  style="text-align: right" ><b>Last updated: </b><%= df.format(new java.util.Date())%><button id="refresh"  type="button" class="btn btn-success btn-xs" onclick="updateDev();">Update</button></div>

                        </div>
                        <hr style="padding: 0;margin-bottom: 0; margin-top: 10px">
                        <p></p>       
                        <div class="input-group">
                            <span class="input-group-addon"><b>Device</b></span>
                            <select id="wifidevice-select" class="selectpicker"  data-size="5" data-width="100%" name="choose" data-live-search="true" title="Please select a WiFi device ...">
                                <%while (locatablesRS.next()) {
                                        locatable_dev = locatablesRS.getString("dev_mac_addr");
                                %>  
                                <option value= <%=locatable_dev%>><%=locatable_dev%></option>
                                <%
                                    }
                                %>  
                            </select>
                        </div>
                        <p></p>
                        <hr style="padding: 0;margin: 0">
                        <p></p>
                        <div class="panel panel-info" >
                            <div class="panel-heading">Live RSS Measurements </div>
                            <div class="panel-body" style="padding-top: 0; padding-bottom: 0">
                                <div class="table-responsive">   
                                    <table class="table" id="RSStable" style="margin-bottom: 0; padding-bottom: 0;">
                                        <thead>
                                            <tr>
                                                <th style="text-align: center">Access Point</th><th style="text-align: center">Received Signal Strength</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr>
                                                <td>192.168.2.1</td><td style="text-align: center">N/A</td>
                                            </tr>
                                            <tr>
                                                <td>192.168.2.2</td><td style="text-align: center">N/A</td>
                                            </tr>
                                            <tr>
                                                <td>192.168.2.3</td><td style="text-align: center">N/A</td>
                                            </tr>
                                            <tr>
                                                <td>192.168.2.4</td><td style="text-align: center">N/A</td>
                                            </tr>
                                            <tr>
                                                <td>192.168.2.5</td><td style="text-align: center">N/A</td>
                                            </tr>
                                            <tr>
                                                <td>192.168.2.6</td><td style="text-align: center">N/A</td>
                                            </tr>
                                            <tr>
                                                <td>192.168.2.7</td><td style="text-align: center">N/A</td>
                                            </tr>
                                            <tr>
                                                <td>192.168.2.8</td><td style="text-align: center">N/A</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>

                            <div class="panel-footer" id="position">Computed Position:</div>             
                        </div>
                        <div>
                            <b>Trajectory Tracking: </b><input type="checkbox" data-state="false" name="my-checkbox">
                        </div>
                        <p  style="display: inline;" id="coordinates"><b>Position:</b></p>

                        <form class="form-inline" role="form" action="locate.jsp" method="post">
                            <input id="output_x1"type="hidden" name="output_x1" value=0>
                            <input id="output_y1"type="hidden" name="output_y1" value=0>
                            <input id="output_x2"type="hidden" name="output_x2" value=0>
                            <input id="output_y2"type="hidden" name="output_y2" value=0>

                            <button  style="display: inline;" id="save_data" type="submit" class="btn btn-primary btn-sm">Save</button>
                        </form>
                    </div>

                    <hr style="padding: 0;margin-bottom: 10px; margin-top: 10px">

                    <div class="row">
                        <div class="col-sm-12">
                            <div class="well well-sm"><p id="numOfDev"></p></div>
                        </div>
                    </div>
                    <div id="count"></div>

                </div>
            </div>
            <div class="row">  
                <div id="map"> <jsp:include page="locate_map.jsp"/></div> 
            </div>
        </div>

        <!--footer-->
        <div>
            <jsp:include page="footer.jsp" flush="true"/>
        </div>
    </body>
</html>