// create-order.js (UPDATED)
// Fixes:
// 1) Works with async fragment load (/inventory/create-order) + offcanvas state switching
// 2) Ensures initial stock values (particulars, rate, qty etc.) are set on FIRST load
// 3) Removes incorrect .val('cash') usage and avoids resetting values after you set them
// 4) Ensures payment account enable/disable runs correctly for default payment type
// 5) Keeps your waitForCreateOrderDomReady + idempotent handler binding

function escapeHtml(str) {
    if (!str) return '';
    return String(str)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&quot;")
        .replaceAll("'", "&#039;");
}

function getOffcanvasEl() {
    return document.getElementById('stockDrawer');
}

function getStockDrawerInstance() {
    const el = getOffcanvasEl();
    if (!el) return null;
    return bootstrap.Offcanvas.getOrCreateInstance(el);
}

function isDisabled(inputId) {
    const el = document.getElementById(inputId);
    return !el || el.disabled || el.readOnly;
}

/**
 * Wait until Create Order form exists AND has real layout size.
 * IMPORTANT: scope to #stockDrawer to avoid picking up any accidental templates elsewhere.
 */
function waitForCreateOrderDomReady(timeoutMs = 1500) {
    const start = performance.now();

    return new Promise((resolve, reject) => {
        (function poll() {
            const $form = $('#stockDrawer #formCreateOrder');
            const formEl = $form[0];

            if (formEl) {
                const rect = formEl.getBoundingClientRect();
                const visible = rect.width > 0 && rect.height > 0;
                const isVisible = $form.is(':visible');

                if (visible && isVisible) return resolve(true);
            }

            if (performance.now() - start > timeoutMs) {
                return reject(new Error('Create Order DOM not ready/visible within timeout'));
            }

            requestAnimationFrame(poll);
        })();
    });
}

/** One-time delegated event wiring (safe even if DOM is replaced) */
function bindCreateOrderHandlers() {
    // Cancel
    $(document)
        .off('click.createOrder', '#btnCancelCreateOrder')
        .on('click.createOrder', '#btnCancelCreateOrder', function () {
            $('#customerDropdown, #paymentAccountDropdown').addClass('d-none').hide();
            const inst = getStockDrawerInstance();
            if (inst) inst.hide();
        });

    // Add Customer
    $(document)
        .off('click.createOrder', '#btnAddCustomer')
        .on('click.createOrder', '#btnAddCustomer', function () {
            const prefill = { customerName: ($('#customer').val() || '').trim() };

            const inst = getStockDrawerInstance();
            const $drawer = $('#stockDrawer');
            const isDrawerOpen = $drawer.hasClass('show');

            if (isDrawerOpen && inst) {
                $drawer.one('hidden.bs.offcanvas.createOrder', function () {
                    window.openCreateCustomerModal(prefill);
                });
                inst.hide();
                return;
            }
            window.openCreateCustomerModal(prefill);
        });

    // Add Payment Account
    $(document)
        .off('click.createOrder', '#btnAddPaymentAccount')
        .on('click.createOrder', '#btnAddPaymentAccount', function () {
            const prefill = { paymentAcount: ($('#paymentAcount').val() || '').trim() };

            const inst = getStockDrawerInstance();
            const $drawer = $('#stockDrawer');
            const isDrawerOpen = $drawer.hasClass('show');

            if (isDrawerOpen && inst) {
                $drawer.one('hidden.bs.offcanvas.createOrder', function () {
                    window.openCreatePaymentAccountModal(prefill);
                });
                inst.hide();
                return;
            }
            window.openCreatePaymentAccountModal(prefill);
        });

    // Submit validation
    $(document)
        .off('click.createOrder', '#formCreateOrder button[type="submit"], #btnCreateOrder')
        .on('click.createOrder', '#formCreateOrder button[type="submit"], #btnCreateOrder', function (e) {
            const form = $('#formCreateOrder')[0];
            if (!form) return;

            form.classList.add('was-validated');

            if (!form.checkValidity()) {
                e.preventDefault();
                e.stopPropagation();
                const firstInvalid = form.querySelector(':invalid');
                if (firstInvalid) firstInvalid.focus();
                return;
            }

            $('#formCreateOrder').trigger('submit');
        });

    // Disable guard for payment account input
    $(document)
        .off('focus.createOrder input.createOrder click.createOrder keydown.createOrder', '#paymentAcount')
        .on('focus.createOrder input.createOrder click.createOrder keydown.createOrder', '#paymentAcount', function (e) {
            if (this.disabled || this.readOnly) {
                e.preventDefault();
                e.stopImmediatePropagation();
                $('#paymentAccountDropdown').addClass('d-none').hide();
                this.blur();
                return false;
            }
        });

    // Payment type change
    $(document)
        .off('change.createOrder', '#paymentType')
        .on('change.createOrder', '#paymentType', function () {
            togglePaymentAccount(this.value);
        });

    // Cleanup when drawer hides
    $(document)
        .off('hidden.bs.offcanvas.createOrder', '#stockDrawer')
        .on('hidden.bs.offcanvas.createOrder', '#stockDrawer', function () {
            $('#customerDropdown, #paymentAccountDropdown').addClass('d-none').hide();
        });

    // Reopen after modals close
    $(document)
        .off('hidden.bs.modal.createOrder', '#createCustomerModal')
        .on('hidden.bs.modal.createOrder', '#createCustomerModal', function () {
            const inst = getStockDrawerInstance();
            if (inst) inst.show();
            setTimeout(() => $('#customer').trigger('focus'), 150);
        });

    $(document)
        .off('hidden.bs.modal.createOrder', '#createPaymentAccountModal')
        .on('hidden.bs.modal.createOrder', '#createPaymentAccountModal', function () {
            const inst = getStockDrawerInstance();
            if (inst) inst.show();
            setTimeout(() => $('#paymentAcount').trigger('focus'), 150);
        });
}

function initCreateOrderFields() {
    // Clear basic fields
    $("#challanNo").val("");
    $("#customer").val("");
    $("#customerId").val("");
    $("#paymentAcount").val("");
    $("#paymentAccountId").val("");

    // Defaults
    $("#itemQuantity").val("1");         // ✅ better default than 0
    $("#courierCharges").val("0");
    $("#paymentType").val("cash");       // ✅ default payment type

    $("#itemAmount").val("0");
    $("#orderAmount").val("0");
    $("#orderRemark").val("");           // ✅ do not default to "0"

    $("#newItemSellRate").val("");

    const form = $('#formCreateOrder')[0];
    if (form) form.classList.remove('was-validated');
    $('#formCreateOrder .is-valid, #formCreateOrder .is-invalid')
        .removeClass('is-valid is-invalid');
}

/** Must run only when form is visible */
function initCreateOrderUI() {
    // Datepicker
    try { $('#orderDate').datepicker('destroy'); } catch (e) { /* ignore */ }

    $('#orderDate')
        .datepicker({
            format: 'dd/mm/yyyy',
            autoclose: true,
            todayHighlight: true,
            startDate: new Date(),
            orientation: "bottom auto"
        })
        .off('changeDate.createOrder')
        .on('changeDate.createOrder', function () {
            $("#customer").focus();
        });

    // set today
    $('#orderDate').datepicker('update', formatDateAsDDMMYYYY(new Date()));

    // Dropdowns (search-dropdown.js uses off/on per inputId, so safe to call)
    SearchDropdown.create({
        inputId: 'customer',
        dropdownId: 'customerDropdown',
        listId: 'customerList',
        hiddenId: 'customerId',
        dataProvider: () => CustomerStore.getCached(),
        displayFn: c => `${c.customerName}(${c.location})`,
        idFn: c => c.id
    });

    SearchDropdown.create({
        inputId: 'paymentAcount',
        dropdownId: 'paymentAccountDropdown',
        listId: 'paymentAccountList',
        hiddenId: 'paymentAccountId',
        dataProvider: () => PaymentAccountStore.getCached(),
        displayFn: a => a.accountName,
        idFn: a => a.id
    });

    if (isDisabled('paymentAcount')) {
        $('#paymentAccountDropdown').addClass('d-none').hide();
    }
}

/**
 * Populate fields from selected stock row.
 * IMPORTANT: call this only after fragment is loaded + form visible.
 */
function applySelectedRowDefaults(selectedRowData) {
    if (!selectedRowData) return;

    // particulars
    $('#itemDetailsForOrder').val(
        `${selectedRowData.locationName} | ${selectedRowData.modelName} | ${selectedRowData.partNo} | ` +
        `${selectedRowData.configuration} | ${selectedRowData.details} | ${selectedRowData.vendorName}`
    );

    // sell rate
    $('#itemSellRate').val(selectedRowData.sellPrice);
    $('#newItemSellRate').val(selectedRowData.sellPrice);

    // qty default = 1 (only if empty/0)
    const curQty = ($('#itemQuantity').val() || '').trim();
    if (!curQty || curQty === '0') $('#itemQuantity').val('1');
}

function openCreateOrder(selectedRowData) {
    // Always bind (safe / idempotent)
    bindCreateOrderHandlers();

    // Ensure state
    if (typeof window.showDrawerState === 'function') {
        window.showDrawerState('createOrder');
    }

    // Load stores (can happen while drawer is opening)
    const p1 = CustomerStore.load(false);
    const p2 = PaymentAccountStore.load(false);

    // Show offcanvas (if not already)
    const inst = getStockDrawerInstance();
    if (inst) inst.show();

    // After drawer is shown, wait until Create Order DOM is visible, then init UI and set values
    $('#stockDrawer')
        .off('shown.bs.offcanvas.createOrderUI')
        .one('shown.bs.offcanvas.createOrderUI', function () {
            Promise.all([p1, p2]).finally(function () {
                waitForCreateOrderDomReady(2000)
                    .then(() => {
                        // 1) reset fields AFTER fragment is ready (prevents first-load "missing values")
                        initCreateOrderFields();

                        // 2) init UI widgets
                        initCreateOrderUI();

                        // 3) apply selected row defaults
                        applySelectedRowDefaults(selectedRowData);

                        // 4) challan number
                        fetchAndSetLatestChallanNo();

                        // 5) enable/disable payment account based on default payment type
                        togglePaymentAccount($('#paymentType').val());

                        // focus
                        setTimeout(() => $('#customer').trigger('focus'), 50);
                    })
                    .catch((err) => {
                        console.warn('Create order first-show issue:', err);

                        // last resort show
                        $('#stockDrawer #formCreateOrder').removeClass('d-none').show();

                        // try once more next frame
                        requestAnimationFrame(() => {
                            initCreateOrderFields();
                            initCreateOrderUI();
                            applySelectedRowDefaults(selectedRowData);
                            fetchAndSetLatestChallanNo();
                            togglePaymentAccount($('#paymentType').val());
                        });
                    });
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
