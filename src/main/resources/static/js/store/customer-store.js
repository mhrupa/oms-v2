// /js/customer-store.js
window.CustomerStore = (function () {
    let _customers = null;
    let _loadingPromise = null;

    function getCached() {
        return _customers;
    }

    function load(forceRefresh = false) {
        if (!forceRefresh && _customers) {
            return Promise.resolve(_customers);
        }

        if (_loadingPromise) return _loadingPromise;

        _loadingPromise = $.ajax({
            url: "/customers",   // becomes /api/customers due to ajaxSetup
            method: "GET"
        })
            .then(function (data) {
                _customers = Array.isArray(data) ? data : (data?.data ?? []);
                $(document).trigger("customers:loaded", [_customers]);
                return _customers;
            })
            .always(function () {
                _loadingPromise = null;
            });

        return _loadingPromise;
    }

    function set(customers) {
        _customers = customers || [];
        $(document).trigger("customers:loaded", [_customers]);
    }

    function add(customer) {
        if (!customer) return;

        if (!_customers) _customers = [];

        const id = customer.id ?? customer.customerId;
        const idx = id != null ? _customers.findIndex(c => (c.id ?? c.customerId) == id) : -1;

        if (idx >= 0) {
            _customers[idx] = { ..._customers[idx], ...customer };
        } else {
            _customers.push(customer);
        }

        $(document).trigger("customers:loaded", [_customers]);
    }

    function refresh() {
        return load(true);
    }

    function invalidate() {
        _customers = null;
        _loadingPromise = null;
    }

    return { load, refresh, getCached, set, add, invalidate };
})();
