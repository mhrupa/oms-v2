/* globals feather */

(function () {
  'use strict';
  function replaceIcons() {
    if (typeof feather !== 'undefined') {
      feather.replace({ 'aria-hidden': 'true' });
    }
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', replaceIcons);
  } else {
    replaceIcons();
  }
})();
