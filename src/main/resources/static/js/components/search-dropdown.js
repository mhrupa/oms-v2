/**
 * Reusable Search Dropdown Component
 * ----------------------------------
 * Supports multiple dropdown instances with proper z-index stacking.
 */

const SearchDropdown = (function () {

    // Base z-index for dropdown overlays (above Bootstrap offcanvas/modal)
    let __searchDropdownZ = 20000;

    // Optional: keep track of open dropdowns so we can close others
    const __instances = new Map(); // dropdownId -> { close }

    function create(cfg) {
        const {
            inputId,
            dropdownId,
            listId,
            hiddenId,
            dataProvider,
            displayFn,
            idFn,
            closeOthers = true // default behavior
        } = cfg;

        let activeIndex = -1;

        function getViewportHeight() {
            return window.visualViewport ? window.visualViewport.height : window.innerHeight;
        }

        function clamp(n, min, max) {
            return Math.max(min, Math.min(max, n));
        }

        function open() {
            const $input = $('#' + inputId);
            const el = $input[0];
            if (!el) return;

            const rect = el.getBoundingClientRect();
            const $dropdown = $('#' + dropdownId);

            // move to body (escape offcanvas transform)
            if (!$dropdown.parent().is('body')) $('body').append($dropdown);

            // Ensure it's measurable (but not visible flicker)
            $dropdown.removeClass('d-none').css({
                position: 'fixed',
                display: 'block',
                visibility: 'hidden',    // <-- key: measure safely
                left: rect.left,
                width: rect.width,
                zIndex: 20000
            });

            // Measure dropdown natural height (after render)
            const dropdownHeight = $dropdown.outerHeight();
            const viewportH = getViewportHeight();

            const spaceBelow = viewportH - rect.bottom;
            const spaceAbove = rect.top;

            // Decide placement
            const openAbove = spaceBelow < Math.min(220, dropdownHeight) && spaceAbove > spaceBelow;

            // Calculate top and maxHeight so it never goes off-screen
            const padding = 8;
            const maxAllowed =
                openAbove
                    ? clamp(spaceAbove - padding, 120, 420)
                    : clamp(spaceBelow - padding, 120, 420);

            const top = openAbove
                ? (rect.top - padding - Math.min(dropdownHeight, maxAllowed))
                : (rect.bottom + padding);

            // Apply final styles
            $dropdown.css({
                top: Math.max(padding, top),
                //top: 100,
                maxHeight: maxAllowed + 'px',
                overflow: 'auto',
                visibility: 'visible'
            });
        }

        function close() {
            const $dropdown = $('#' + dropdownId);
            $dropdown.addClass('d-none').css({ display: 'none' });
            activeIndex = -1;
            $('#' + listId + ' .list-group-item').removeClass('active');
        }

        function render(filter = '') {
            const data = dataProvider() || [];
            const q = (filter || '').toLowerCase().trim();
            const $list = $('#' + listId);
            $list.empty();

            const filtered = data.filter(d =>
                (displayFn(d) || '').toLowerCase().includes(q)
            );

            if (!filtered.length) {
                $list.append(`<div class="list-group-item text-muted">No matches</div>`);
                return;
            }
            //console.log("filtered ", filtered);
            filtered.forEach(item => {
                const text = displayFn(item);
                $list.append(`
                  <button type="button"
                          class="list-group-item list-group-item-action"
                          data-id="${escapeHtml(idFn(item))}"
                          data-text="${escapeHtml(text)}">
                    ${escapeHtml(text)}
                  </button>
                `);
            });
        }

        // Register this instance so other dropdowns can close it
        __instances.set(dropdownId, { close });

        /* ===== Delegated bindings (safe with dynamic DOM) ===== */

        // Open (use off/on to avoid duplicate bindings if create() is called again)
        $(document)
            .off(`pointerdown.${inputId}`)
            .on(`pointerdown.${inputId}`, `#${inputId}`, function () {
                render(this.value);
                open();
            });

        // Typing
        $(document)
            .off(`input.${inputId}`)
            .on(`input.${inputId}`, `#${inputId}`, function () {
                if (inputEl.disabled || inputEl.readOnly) {
                    hideDropdown();
                    return;
                }
                render(this.value);
                open();
            });

        // Select
        $(document)
            .off(`click.${inputId}`)
            .on(`click.${inputId}`, `#${listId} .list-group-item`, function () {
                if (inputEl.disabled || inputEl.readOnly) {
                    hideDropdown();
                    return;
                }
                $('#' + inputId).val($(this).data('text'));
                if (hiddenId) $('#' + hiddenId).val($(this).data('id'));
                close();
            });

        // Outside close
        $(document)
            .off(`mousedown.${inputId}`)
            .on(`mousedown.${inputId}`, function (e) {
                const inside = $(e.target).closest(`#${inputId}, #${dropdownId}`).length > 0;
                if (!inside) close();
            });

        // Keyboard nav
        $(document)
            .off(`keydown.${inputId}`)
            .on(`keydown.${inputId}`, `#${inputId}`, function (e) {
                const $dropdown = $('#' + dropdownId);
                if ($dropdown.hasClass('d-none')) return;

                const $items = $('#' + listId + ' .list-group-item');
                if (!$items.length) return;

                if (e.key === 'Escape') return close();

                if (e.key === 'ArrowDown') {
                    e.preventDefault();
                    activeIndex = Math.min(activeIndex + 1, $items.length - 1);
                } else if (e.key === 'ArrowUp') {
                    e.preventDefault();
                    activeIndex = Math.max(activeIndex - 1, 0);
                } else if (e.key === 'Enter') {
                    e.preventDefault();
                    $items.eq(activeIndex >= 0 ? activeIndex : 0).trigger('click');
                    return;
                } else {
                    return;
                }

                $items.removeClass('active');
                const el = $items.eq(activeIndex).addClass('active')[0];
                el && el.scrollIntoView({ block: 'nearest' });
            });

        return { open, close, render };
    }

    return { create };
})();
