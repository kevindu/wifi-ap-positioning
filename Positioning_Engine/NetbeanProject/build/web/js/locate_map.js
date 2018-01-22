function drawDevPos(x,y,color) {
    var canvas = document.getElementById('myCanvasLayer1');
    var context = canvas.getContext('2d');
    context.beginPath();
    context.arc(x, y, 10, 0, 2 * Math.PI);
    context.fillStyle = color;
    context.fill();
    context.closePath();
}

function dispalyRSS(data) {
    var myTable = document.getElementById('RSStable');
    myTable.rows[1].cells[1].innerHTML = data[0]+" dBm";
    myTable.rows[2].cells[1].innerHTML = data[1]+" dBm";
    myTable.rows[3].cells[1].innerHTML = data[2]+" dBm";
    myTable.rows[4].cells[1].innerHTML = data[3]+" dBm";
    myTable.rows[5].cells[1].innerHTML = data[4]+" dBm";
    myTable.rows[6].cells[1].innerHTML = data[5]+" dBm";
    myTable.rows[7].cells[1].innerHTML = data[6]+" dBm";
    myTable.rows[8].cells[1].innerHTML = data[7]+" dBm";
}

function dispalyPos(x,y) {
    var myPos = document.getElementById('position');
    document.getElementById("output_x2").value = x;
    document.getElementById("output_y2").value = y;
    myPos.innerHTML ="Computed Position: ("+x+", "+y+")";   
}

function updateNumOfDevice(num) {
    var p = document.getElementById('numOfDev');
    p.innerHTML ="<b>No. of devices shown on the map: </b><font size='6'>"+" "+num+"</font>" ;   
}
function initRSSTable() {
    var myTable = document.getElementById('RSStable');
    myTable.rows[1].cells[1].innerHTML = "N/A";
    myTable.rows[2].cells[1].innerHTML = "N/A";
    myTable.rows[3].cells[1].innerHTML = "N/A";
    myTable.rows[4].cells[1].innerHTML = "N/A";
    myTable.rows[5].cells[1].innerHTML = "N/A";
    myTable.rows[6].cells[1].innerHTML = "N/A";
    myTable.rows[7].cells[1].innerHTML = "N/A";
    myTable.rows[8].cells[1].innerHTML = "N/A";
    document.getElementById('position').innerHTML ="Computed Position:"; 

}