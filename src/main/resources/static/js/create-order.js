let createOrderInitialized = false;

function escapeHtml(str) {
    if (!str) return '';
    return String(str)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

function initCreateOrderOnce() {
    if (createOrderInitialized) return;

    // Datepicker init once
    $('#orderDate')
        .datepicker({
            format: 'dd/mm/yyyy',
            autoclose: true,
            todayHighlight: true,
            startDate: new Date(),
            orientation: "bottom auto"
        })
        .on("changeDate", function () {
            $("#customer").focus();
        });

    // Close button – hide drawer + dropdowns
    $('#btnCancelCreateOrder')
        .off('click')
        .on('click', function () {
            // close any open search dropdowns
            $('#customerDropdown, #paymentAccountDropdown')
                .addClass('d-none')
                .hide();

            if (window.stockDrawer) stockDrawer.hide();
        });

    // Add Customer button
    $(document)
        .off('click', '#btnAddCustomer')
        .on('click', '#btnAddCustomer', function () {
            const prefill = { customerName: ($('#customer').val() || '').trim() };

            // If offcanvas is open, close it first then open modal
            const $drawer = $('#stockDrawer'); // <-- change if your offcanvas id differs
            const isDrawerOpen = $drawer.hasClass('show');

            if (isDrawerOpen && window.stockDrawer) {
                // open modal after drawer is fully closed
                $drawer.one('hidden.bs.offcanvas', function () {
                    window.openCreateCustomerModal(prefill);
                });
                window.stockDrawer.hide();
                return;
            }

            // no offcanvas open, open modal directly
            window.openCreateCustomerModal(prefill);
        });

    // Add Apyemnt Account button
    $(document)
        .off('click', '#btnAddPaymentAccount')
        .on('click', '#btnAddPaymentAccount', function () {
            const prefill = { paymentAcount: ($('#paymentAcount').val() || '').trim() };

            // If offcanvas is open, close it first then open modal
            const $drawer = $('#stockDrawer'); // <-- change if your offcanvas id differs
            const isDrawerOpen = $drawer.hasClass('show');

            if (isDrawerOpen && window.stockDrawer) {
                // open modal after drawer is fully closed
                $drawer.one('hidden.bs.offcanvas', function () {
                    window.openCreatePaymentAccountModal(prefill);
                });
                window.stockDrawer.hide();
                return;
            }

            // no offcanvas open, open modal directly
            window.openCreatePaymentAccountModal(prefill);
        });

    // Create Order submit validation (Bootstrap)
    $(document)
        .off('click', '#formCreateOrder button[type="submit"], #btnCreateOrder')
        .on('click', '#formCreateOrder button[type="submit"], #btnCreateOrder', function (e) {
            const form = $('#formCreateOrder')[0];
            if (!form) return;

            // run validation
            form.classList.add('was-validated');

            if (!form.checkValidity()) {
                e.preventDefault();
                e.stopPropagation();

                // focus first invalid field
                const firstInvalid = form.querySelector(':invalid');
                if (firstInvalid) firstInvalid.focus();
                return;
            }

            // If valid: manually trigger submit (or call your Ajax create order)
            // form.submit();  // if you want normal submit
            $('#formCreateOrder').trigger('submit'); // if you have ajax submit elsewhere
        });

    // Block dropdown triggers when disabled (payment account)
    $(document).on('focus input click keydown', '#paymentAcount', function (e) {
        if (this.disabled) {
            e.preventDefault();
            e.stopImmediatePropagation();
            $('#paymentAccountDropdown').addClass('d-none').hide();
            this.blur();
            return false;
        }
    });

    $('#paymentType')
        .off('change')
        .on('change', function () {
            togglePaymentAccount(this.value);
        });


    createOrderInitialized = true;
}

function initCreateOrderFields() {
    $("#challanNo").val("");
    $("#customer").val("");
    $("#customerId").val("");
    $("#paymentAcount").val("");
    $("#paymentAccountId").val("");

    $("#itemQuantity").val("0");
    $("#courierCharges").val("0");
    $("#paymentType").val("0");

    $("#itemAmount").val("0");
    $("#orderAmount").val("0");
    $("#orderRemark").val("0");

    // reset new sell rate too (optional)
    $("#newItemSellRate").val("");

    // reset bootstrap validation state
    const form = $('#formCreateOrder')[0];
    if (form) form.classList.remove('was-validated');
    $('#formCreateOrder .is-valid, #formCreateOrder .is-invalid')
        .removeClass('is-valid is-invalid');
}


function openCreateOrder(selectedRowData) {
    initCreateOrderOnce();

    // clear fields first
    initCreateOrderFields();
    // default state (disabled)
    togglePaymentAccount($('#paymentType').val());

    // set date
    $('#orderDate').datepicker('update', formatDateAsDDMMYYYY(new Date()));

    // set selected row details
    $('#itemDetailsForOrder').val(
        `${selectedRowData.locationName} | ${selectedRowData.modelName} | ${selectedRowData.partNo} | ` +
        `${selectedRowData.configuration} | ${selectedRowData.details} | ${selectedRowData.vendorName}`
    );
    $('#itemSellRate').val(selectedRowData.sellPrice);
    $('#newItemSellRate').val(selectedRowData.sellPrice);

    // challan number
    fetchAndSetLatestChallanNo();

    CustomerStore.load(false).then(() => {
        SearchDropdown.create({
            inputId: 'customer',
            dropdownId: 'customerDropdown',
            listId: 'customerList',
            hiddenId: 'customerId',
            dataProvider: () => CustomerStore.getCached(),
            displayFn: c => `${c.customerName}(${c.location})`,
            idFn: c => c.id
        });
    });

    PaymentAccountStore.load(false).then(() => {
        SearchDropdown.create({
            inputId: 'paymentAcount',
            dropdownId: 'paymentAccountDropdown',
            listId: 'paymentAccountList',
            hiddenId: 'paymentAccountId',
            dataProvider: () => PaymentAccountStore.getCached(),
            displayFn: a => a.accountName,
            idFn: a => a.id
        });
    });
}

function fetchAndSetLatestChallanNo() {
    $('#challanNo').val('Loading...');

    $.ajax({ url: '/challan-no', method: 'GET' })
        .done(function (challanNo) {
            $('#challanNo').val(challanNo);
        })
        .fail(function () {
            $('#challanNo').val('');
            alert('Failed to fetch challan no');
        });
}

$(document).on('hidden.bs.modal', '#createCustomerModal', function () {
    // Re-open the offcanvas if you want the user to continue the order flow
    if (window.stockDrawer) {
        window.stockDrawer.show();
    }

    // Put focus back
    setTimeout(() => $('#customer').trigger('focus'), 150);
});

$(document).on('hidden.bs.modal', '#createPaymentAccountModal', function () {
    // Re-open the offcanvas if you want the user to continue the order flow
    if (window.stockDrawer) {
        window.stockDrawer.show();
    }

    // Put focus back
    setTimeout(() => $('#paymentAcount').trigger('focus'), 150);
});

function togglePaymentAccount(paymentType) {
    const enable = (paymentType === 'paytm' || paymentType === 'bank');

    const $input = $('#paymentAcount');
    const $hidden = $('#paymentAccountId');
    const $btn = $('#btnAddPaymentAccount');

    if (enable) {
        $input.prop('disabled', false);
        $btn.prop('disabled', false);
    } else {
        $input.prop('disabled', true).val('').trigger('blur');
        $hidden.val('');
        $btn.prop('disabled', true);
        $('#paymentAccountDropdown').addClass('d-none').hide();
    }
}


