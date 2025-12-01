var appUser = new Object();
var company = new Object();
var customerUpdateData = new Object();
var searchString = "";
var showBuyPrice = false;
var items = new Array();
function numberOnly(e) {
  if (e.which != 8 && e.which != 0 && (e.which < 48 || e.which > 57)) {
    return false;
  }
}
function numberOnlyWithNegativeValues(e) {
  if (e.which != 8 && e.which != 0 && e.which != 45 && (e.which < 48 || e.which > 57)) {
    return false;
  }
}
function formatDateAsDDMMYYYY(date) {
  var month = "" + (date.getMonth() + 1),
    day = "" + date.getDate(),
    year = date.getFullYear();

  if (month.length < 2) month = "0" + month;
  if (day.length < 2) day = "0" + day;

  return [day, month, year].join("/");
}
