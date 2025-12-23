// Keep a single modal instance
let createCustomerModal;

// Expose globally (required)
window.openCreateCustomerModal = function (prefill = {}) {
    if (!createCustomerModal) {
        createCustomerModal = new bootstrap.Modal(
            document.getElementById('createCustomerModal'),
            {
                backdrop: 'static',
                keyboard: true
            }
        );
    }

    // reset form
    const $form = $('#createCustomerForm');
    $form[0].reset();
    $form.removeClass('was-validated');
    $('#ccErrorBox').addClass('d-none').text('');

    // prefills
    if (prefill.customerName) $('#ccCustomerName').val(prefill.customerName);
    if (prefill.location) $('#ccLocation').val(prefill.location);
    if (prefill.email) $('#ccEmail').val(prefill.email);
    if (prefill.contact) $('#ccContact').val(prefill.contact);

    createCustomerModal.show();
};

/* ================================
   Delegated modal event handlers
   ================================ */

// Focus first field when modal opens
$(document).on('shown.bs.modal', '#createCustomerModal', function () {
    $('#ccCustomerName').trigger('focus');
});

// Allow digits only for contact
$(document).on('input', '#ccContact', function () {
    this.value = this.value.replace(/[^\d]/g, '');
});

// Form submit
$(document).on('submit', '#createCustomerForm', async function (e) {
    e.preventDefault();

    const form = this;
    const $form = $(form);
    $('#ccErrorBox').addClass('d-none').text('');

    if (!form.checkValidity()) {
        $form.addClass('was-validated');
        return;
    }


    const payload = {
        customerName: $('#ccCustomerName').val().trim(),
        location: $('#ccLocation').val().trim(),
        email: $('#ccEmail').val().trim(),
        contact: $('#ccContact').val().trim(),
        userId: appUser.userId
    };

    try {
        $('#ccBtnCreate').prop('disabled', true);
        $('#ccSpinner').removeClass('d-none');

        const resp = await fetch('/api/customers', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (!resp.ok) {
            throw new Error(await resp.text() || 'Failed to create customer');
        }

        const created = await resp.json();
        const custData = created.data;
        $('#customer')
            .val(`${custData.customerName}(${custData.location || ''})`)
            .trigger('input'); // keeps dropdown logic happy

        $('#customerId').val(custData.id);

        createCustomerModal.hide();
    } catch (err) {
        $('#ccErrorBox').removeClass('d-none').text(err.message);
    } finally {
        $('#ccBtnCreate').prop('disabled', false);
        $('#ccSpinner').addClass('d-none');
    }
});
