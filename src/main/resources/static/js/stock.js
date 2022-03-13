function initPage() {

}

function initFields() {
    $("#model").val(0);
    $("#partNo").val(0);
    $("#config").val(0);
    $("#qty").val("");
    $("#buyPrice").val("");
    $("#sellPrice").val("");
    $("#remark1").val("");
    $("#remark2").val("");
    $("#boxNo").val(0).focus();
    disableClearFieldButton();
}

function enableClearFieldButton() {
    $("#clearFieldsBtn").removeAttr("disabled")
}

function disableClearFieldButton() {
    $("#clearFieldsBtn").attr("disabled", "disabled")
}