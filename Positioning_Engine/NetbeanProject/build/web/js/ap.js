$(document).ready(function () {
    $("#refresh").click(function () {
        $("#aplist").load("ap_list.jsp");
    });
});