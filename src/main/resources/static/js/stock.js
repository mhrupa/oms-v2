function initPage() {
    
}

const savaData = () => {
    var requestData = {
        "boxNo": $("#boxNo").val(),
        "partNo": $("#partNo").val(),
        "model": $("#model").val(),
        "qty": $("#qty").val()
    };

    $.post("/stock", JSON.stringify(requestData), function (res) {
        $("#omsToast").html(res.message);
        $('.toast').removeClass('bg-danger').addClass('bg-success').toast('show');
    }).fail(function (res) {
        $("#omsToast").html(JSON.parse(res.responseText).message);
        $('.toast').addClass('bg-danger').toast('show');
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

function enableClearFieldButton() {
    $("#clearFieldsBtn").removeAttr("disabled")
}

function disableClearFieldButton() {
    $("#clearFieldsBtn").attr("disabled", "disabled")
}