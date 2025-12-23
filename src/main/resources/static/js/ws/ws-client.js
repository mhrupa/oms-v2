// /js/ws-client.js
let customerRefreshTimer = null;
let paymentAccountRefreshTimer = null;

window.OmsWs = (function () {
  let client = null;
  let connected = false;

  function connect() {
    if (client && connected) return;

    client = new StompJs.Client({
      webSocketFactory: () => new SockJS('/ws'),

      // called on connect
      onConnect: () => {
        connected = true;
        console.log("WS connected");

        client.subscribe('/topic/customer', onCustomerEvent);
        client.subscribe('/topic/customerAccount', onCustomerAccountEvent);
        client.subscribe('/topic/vendor', onVendorEvent);
        client.subscribe('/topic/payment', onPaymentEvent);
        client.subscribe('/topic/notifications', onNotificationEvent);
      },

      // called on disconnect / broker error
      onStompError: (frame) => {
        console.error("Broker error:", frame.headers['message'], frame.body);
      },

      onWebSocketClose: () => {
        connected = false;
        console.log("WS disconnected");
      },

      // AUTO reconnect (ms)
      reconnectDelay: 3000,

      // IMPORTANT: debug must be a function, not null
      debug: function () {
        // keep silent in prod
        // console.log(str);
      }
    });

    client.activate();
  }

  function safeParse(msg) {
    try { return JSON.parse(msg.body); } catch (e) { return null; }
  }

  function scheduleCustomerRefresh() {
    clearTimeout(customerRefreshTimer);
    customerRefreshTimer = setTimeout(() => {
      CustomerStore.load(true);
    }, 300); // 300–800ms is fine
  }

  function onCustomerEvent(message) {
    //console.log("received customer event", message);
    const evt = safeParse(message);
    if (evt?.type === 'MASTERDATA_CHANGED' && evt?.entity === 'CUSTOMER') {
      scheduleCustomerRefresh();
    }
  }

  function onCustomerAccountEvent(message) {
    CustomerAccountStore.invalidate();

    if ($('#customerAccountData').length) {
      CustomerAccountStore.load(true).then(function () {
        renderCustomerAccountsDatalist();
      });
    }
  }

  function onVendorEvent(msg) {
    const evt = safeParse(msg);
    if (window.VendorStore) VendorStore.invalidate();
    // refresh UI if vendor dropdown present (same pattern)
  }

  function schedulePaymentAccountRefresh() {
    clearTimeout(paymentAccountRefreshTimer);
    paymentAccountRefreshTimer = setTimeout(() => {
      PaymentAccountStore.load(true);
    }, 300); // 300–800ms is fine
  }

  function onPaymentEvent(message) {
    const evt = safeParse(message);
    if (evt?.type === 'MASTERDATA_CHANGED' && evt?.entity === 'PAYMENT_ACCOUNT') {
      if (window.PaymentAccountStore) PaymentAccountStore.invalidate();
      schedulePaymentAccountRefresh();
    }
  }

  function onNotificationEvent(msg) {
    const evt = safeParse(msg);
    // show toast / badge
    // Example: reuse your toast
    if (evt && evt.type === "NOTIFICATION_NEW") {
      // Put your own text; if you want, fetch details by id via REST
      if (typeof showToast === "function") {
        showToast("New notification received");
      }
    }
  }

  return { connect };
})();

