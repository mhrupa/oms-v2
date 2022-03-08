function initPage() {
    initData();
    $("#boxNo").focus();
    var table = $('#dataTable').DataTable({
        // paging: false,
        // scrollY: 420,
        // info: false
    });

    $("#addStockBtn").click(() => {
        if (validateFields()) {
            savaData();
        }
        return false;

    })
    $("#clearFieldsBtn").click(() => {
        initFields();
    })
    $('#dataTable tbody').css("cursor", "pointer").on('click', 'tr', function () {
        var data = table.row(this).data();
        $("#boxNo").val(data[0]);
        $("#model").val(data[1]);
        $("#partNo").val(data[2]);
        $("#qty").val(data[3]);
        enableClearFieldButton();
    });
}



function validateFields() {
    if ($("#boxNo").val() == "") {
        $("#boxNo").focus();
        return false;
    }

    if ($("#model").val() == "") {
        $("#model").focus();
        return false;
    }

    if ($("#partNo").val() == "") {
        $("#partNo").focus();
        return false;
    }

    if ($("#qty").val() == "") {
        $("#qty").focus();
        return false;
    }
    return true;
}

function initFields() {
    $("#boxNo").val("");
    $("#model").val("");
    $("#partNo").val("");
    $("#qty").val("");
    disableClearFieldButton();
}

function initData() {
    $.getJSON("/stock", function (data) {
        alert();
        // var items = [];
        // $.each(data, function (key, val) {
        //     items.push("<li id='" + key + "'>" + val + "</li>");
        // });

        // $("<ul/>", {
        //     "class": "my-new-list",
        //     html: items.join("")
        // }).appendTo("body");
        alert(JSON.stringify(data));
    });
}

function enableClearFieldButton() {
    $("#clearFieldsBtn").removeAttr("disabled")
}

function disableClearFieldButton() {
    $("#clearFieldsBtn").attr("disabled", "disabled")
}