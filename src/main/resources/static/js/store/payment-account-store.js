// /js/customerAccount-store.js
window.PaymentAccountStore = (function () {
    let _paymentAccounts = null;
    let _loadingPromise = null;

    function getCached() {
        return _paymentAccounts;
    }

    function load(forceRefresh = false) {
        if (!forceRefresh && _paymentAccounts) {
            return Promise.resolve(_paymentAccounts);
        }

        if (_loadingPromise) return _loadingPromise;

        _loadingPromise = $.ajax({
            url: "/paymentAccounts",   // becomes /api/paymentAccounts due to ajaxSetup
            method: "GET"
        })
            .then(function (data) {
                _paymentAccounts = Array.isArray(data) ? data : (data?.data ?? []);
                $(document).trigger("paymentAccounts:loaded", [_paymentAccounts]);
                return _paymentAccounts;
            })
            .always(function () {
                _loadingPromise = null;
            });

        return _loadingPromise;
    }

    function set(paymentAccounts) {
        _paymentAccounts = paymentAccounts || [];
        $(document).trigger("paymentAccounts:loaded", [_paymentAccounts]);
    }

    function invalidate() {
        _paymentAccounts = null;
        _loadingPromise = null;
    }

    return { load, getCached, set, invalidate };
})();
