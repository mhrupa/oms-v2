// Keep a single modal instance
let createPaymentAccountModal;

// Expose globally (required)
window.openCreatePaymentAccountModal = function (prefill = {}) {
    if (!createPaymentAccountModal) {
        createPaymentAccountModal = new bootstrap.Modal(
            document.getElementById('createPaymentAccountModal'),
            {
                backdrop: 'static',
                keyboard: true
            }
        );
    }

    // reset form
    const $form = $('#createPaymentAccountForm');
    $form[0].reset();
    $form.removeClass('was-validated');
    $('#ccErrorBox').addClass('d-none').text('');

    // prefills
    if (prefill.customerName) $('#ccAccountName').val(prefill.customerName);

    createPaymentAccountModal.show();
};

/* ================================
   Delegated modal event handlers
   ================================ */

// Focus first field when modal opens
$(document).on('shown.bs.modal', '#createPaymentAccountModal', function () {
    $('#ccAccountName').trigger('focus');
});

// Form submit
$(document).on('submit', '#createPaymentAccountForm', async function (e) {
    e.preventDefault();

    const form = this;
    const $form = $(form);
    $('#ccErrorBox').addClass('d-none').text('');

    if (!form.checkValidity()) {
        $form.addClass('was-validated');
        return;
    }

    const payload = {
        paymentAccountName: $('#ccAccountName').val().trim(),
        userId: appUser.userId
    };

    try {
        $('#ccBtnCreate').prop('disabled', true);
        $('#ccSpinner').removeClass('d-none');

        const resp = await fetch('/api/paymentAccounts', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (!resp.ok) {
            throw new Error(await resp.text() || 'Failed to Payment Account');
        }

        const created = await resp.json();
        const resData = created.data;
        $('#paymentAcount')
            .val(`${resData.accountName}`)
            .trigger('input'); // keeps dropdown logic happy

        $('#paymentAccountId').val(resData.id);

        createPaymentAccountModal.hide();
    } catch (err) {
        $('#ccErrorBox').removeClass('d-none').text(err.message);
    } finally {
        $('#ccBtnCreate').prop('disabled', false);
        $('#ccSpinner').addClass('d-none');
    }
});
