function drawMap() {
    var canvas = document.getElementById('myCanvas');
    var context = canvas.getContext('2d');
    var map = document.getElementById('background');
    
    context.drawImage(map, 0, 0);
}

function drawAPPos(x, y,lan_id) {
    var canvas = document.getElementById('myCanvas');
    var context = canvas.getContext('2d');
    var text = lan_id;
    
    context.beginPath();
    context.arc(x, y, 10, 0, 2 * Math.PI);
    context.fillStyle = 'black';
    context.fill();
    context.closePath();

    context.fill();
    context.fillStyle = "white"; // font color to write the text with
    var font = "bold " + 10 + "px serif";
    context.font = font;
// Move it down by half the text height and left by half the text width
    var width = context.measureText(text).width;
    var height = context.measureText("w").width; // this is a GUESS of height
    context.fillText(text, x - (width / 2), y + (height / 2));
}

function drawX(x, y) {
    var canvas = document.getElementById('myCanvasLayer2');
    var ctx = canvas.getContext("2d");
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    
    ctx.beginPath();
    
    ctx.strokeStyle="#FF0000";
    ctx.moveTo(x - 10, y );
    ctx.lineTo(x + 10, y );
    ctx.stroke();

    ctx.moveTo(x , y - 10);
    ctx.lineTo(x , y + 10);
    ctx.stroke();
}

var i = 0;
var time=11;
var auto_refresh = setInterval(
        function ()
        {   time--;
            var count = document.getElementById('count');
            count.innerHTML =" <div class='alert alert-warning'><strong>The map will be refreshed in <font size='6'>"+time+"</font> seconds.</strong></div>";
            
            if (time===0){
            var str = $('input[name="my-checkbox"]').bootstrapSwitch('state');
            if (str === false) {
                canvas = document.getElementById("myCanvasLayer1");
                ctx = canvas.getContext("2d");
                ctx.clearRect(0, 0, canvas.width, canvas.height);
                //drawMap();
            }
            time=11;
            $('#map').load('locate_map.jsp');
        }
        
            
        }, 1000);
        
function updateDev(){
    location.reload(true);
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