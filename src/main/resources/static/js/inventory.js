var tableRows = "";
var totalPages = 0;
var totalElements = 0;
const rowsPerPage = 10;
var currentPage = 1;
var searchString = "";
let currentStock = null;
let createOrderLoaded = false;

// Function to render inventory table
function renderTable() {
    $.ajax({
        url: `/v2/stock/search?page=${currentPage}&size=${rowsPerPage}&query=${encodeURIComponent(searchString)}`,
        method: 'GET',
        success: function (data) {
            tableRows = data.content;
            totalPages = data.totalPages;
            totalElements = data.totalElements;
            const tableBody = $('#tableBody');
            tableBody.empty(); // Clear existing data

            // Populate table with new data
            tableRows.forEach(item => addInventoryTableRow(item, tableBody));

            renderPaginationControls();
            renderPaginationDetails();

        },
        error: function (error) {
            console.error('Error fetching inventory data:', error);
        }
    });
}

function addInventoryTableRow(item, tableBody) {
    const row = document.createElement('tr');
    // item id (hidden)
    const td = document.createElement('td');
    td.style.display = 'none';
    td.textContent = `${item.id}`;
    row.appendChild(td);

    // location name
    const td1 = document.createElement('td');
    td1.textContent = `${item.locationName}`;
    row.appendChild(td1);

    // model name
    const td2 = document.createElement('td');
    td2.classList.add('ellipsis');
    td2.title = item.modelName;
    td2.textContent = `${item.modelName}`;
    row.appendChild(td2);

    // remark
    const td3 = document.createElement('td');
    td3.classList.add('ellipsis');
    td3.textContent = `${item.remark}`;
    row.appendChild(td3);

    // part no
    const td4 = document.createElement('td');
    td4.textContent = `${item.partNo}`;
    row.appendChild(td4);

    // configuration
    const td5 = document.createElement('td');
    td5.textContent = `${item.configuration}`;
    row.appendChild(td5);

    // details
    const td6 = document.createElement('td');
    td6.textContent = `${item.details}`;
    row.appendChild(td6);

    // quantity
    const td7 = document.createElement('td');
    td7.classList.add('text-right');
    td7.textContent = `${item.qty}`;
    row.appendChild(td7);

    // sell price
    const td8 = document.createElement('td');
    td8.classList.add('text-right');
    td8.textContent = `${item.sellPrice}`;
    row.appendChild(td8);

    // vendor name
    const td9 = document.createElement('td');
    td9.textContent = `${item.vendorName}`;
    row.appendChild(td9);

    row.style.cursor = 'pointer';
    // row.onclick = function () { showRowPopup(item); };
    // row.onclick = function () {
    //     //currentStock = item;
    //     //renderOverview(item);
    //     //showDrawerState('overview');
    //     //stockDrawer.show();
    // };
    row.oncontextmenu = function (e) {
        showContextMenu(e, item);
    }
    tableBody.append(row);
}

function showStockDrawer() {
    $('#stockContextMenu').hide();
    showDrawerState('overview');
    stockDrawer.show();
}

function hideStockDrawer() {
    stockDrawer.hide();
}

function centerOffcanvas(widthPx) {
    const half = widthPx / 2;
    $('#stockDrawer').css({
        width: widthPx + 'px',
        left: '50%',
        right: 'auto',
        marginLeft: '-' + half + 'px'
    });
}

function resetOffcanvasFullWidth() {
    $('#stockDrawer').css({
        width: '100%',
        left: 0,
        right: 0,
        marginLeft: 0
    });
}


function showDrawerState(state) {
    // hide all
    $('#drawerStateOverview, #drawerStateUpdateStock, #drawerStateCreateOrder').addClass('d-none');
    const $drawer = $('#stockDrawer');
    $drawer.css('height', '');
    resetOffcanvasFullWidth();
    if (state === 'overview') {
        $('#drawerStateOverview').removeClass('d-none');
        $('#stockDrawerLabel').text('Stock');
    } else if (state === 'updateStock') {
        $('#drawerStateUpdateStock').removeClass('d-none');
        $('#stockDrawerLabel').text('Update Stock');
    } else if (state === 'createOrder') {
        $('#drawerStateCreateOrder').removeClass('d-none');
        $('#stockDrawerLabel').text('Create Order');
        $drawer.css('height', '85vh');
        centerOffcanvas(980);
    }
}

// Hide menu when clicking elsewhere
function hideContextMenu() {
    const $menu = $('#stockContextMenu');
    $menu.removeClass('show');
    setTimeout(() => $menu.hide(), 120);
}

function showContextMenu(e, item) {
    e.preventDefault();
    currentStock = item;
    const $menu = $('#stockContextMenu');
    //$menu.addClass('show');
    $menu.css({
        display: 'block',
        left: e.pageX + 'px',
        top: e.pageY + 'px'
    });
    setTimeout(() => $menu.addClass('show'), 10);
}

// Function to setup pagination controls
function setupPagination(totalPages, currentPage, size) {
    const paginationControls = $('#pagination-controls');
    paginationControls.empty(); // Clear existing controls
    for (let i = 0; i < totalPages; i++) {
        const pageButton = `<button class="page-btn" data-page="${i}">${i + 1}</button>`;
        paginationControls.append(pageButton);
    }

    // Highlight current page
    $(`button[data-page=${currentPage}]`).addClass('active');

    // Page button click event
    $('.page-btn').click(function () {
        const selectedPage = $(this).data('page');
        loadInventoryData(selectedPage, size);
    });
}

function renderPaginationControls() {
    const controls = document.getElementById('paginationControls');
    controls.innerHTML = '';
    //const totalPages = Math.ceil(tableRows.length / rowsPerPage);
    // First page button
    const firstBtn = document.createElement('button');
    firstBtn.textContent = '«';
    firstBtn.className = 'pagination-btn';
    firstBtn.disabled = currentPage === 1;
    firstBtn.title = 'First page';
    firstBtn.onclick = () => { currentPage = 1; renderTable(); };
    controls.appendChild(firstBtn);
    // Previous page button
    const prevBtn = document.createElement('button');
    prevBtn.textContent = '‹';
    prevBtn.className = 'pagination-btn';
    prevBtn.disabled = currentPage === 1;
    prevBtn.title = 'Previous page';
    prevBtn.onclick = () => { currentPage--; renderTable(); };
    controls.appendChild(prevBtn);
    var pageCount = 0;
    // Page numbers
    for (let i = 1; i <= totalPages; i++) {
        const pageBtn = document.createElement('button');
        pageBtn.textContent = i;
        pageBtn.className = 'pagination-btn' + (i === currentPage ? ' active' : '');
        pageBtn.disabled = i === currentPage;
        pageBtn.onclick = () => { currentPage = i; renderTable(); };
        controls.appendChild(pageBtn);
        pageCount++;
        if (pageCount >= 5) {
            break; // Limit to 5 page buttons
        }
    }
    // Next page button
    const nextBtn = document.createElement('button');
    nextBtn.textContent = '›';
    nextBtn.className = 'pagination-btn';
    nextBtn.disabled = currentPage === totalPages;
    nextBtn.title = 'Next page';
    nextBtn.onclick = () => { currentPage++; renderTable(); };
    controls.appendChild(nextBtn);
    // Last page button
    const lastBtn = document.createElement('button');
    lastBtn.textContent = '»';
    lastBtn.className = 'pagination-btn';
    lastBtn.disabled = currentPage === totalPages;
    lastBtn.title = 'Last page';
    lastBtn.onclick = () => { currentPage = totalPages; renderTable(); };
    controls.appendChild(lastBtn);
}
function renderPaginationDetails() {
    const details = document.getElementById('paginationDetails');
    const start = (currentPage - 1) * rowsPerPage + 1;
    const end = Math.min(currentPage * rowsPerPage, tableRows.length);
    details.innerHTML = `Showing <strong>${start}–${end}</strong> out of <strong>${totalElements}</strong> items`;
}
function showAllRows(e) {
    e.preventDefault();
    currentPage = 1;
    window.rowsPerPageBackup = rowsPerPage;
    window.rowsPerPage = tableRows.length;
    renderTable();
    // Hide show all link after click
    document.querySelector('.pagination-showall').style.display = 'none';
}

function createOrder() {
    if (!currentStock) return;

    const payload = {
        stockHeaderId: currentStock.id,
        customerName: $('#ordCustomer').val(),
        qty: $('#ordQty').val(),
        sellPrice: $('#ordSellPrice').val(),
        notes: $('#ordNotes').val()
        // add other fields needed by your order DTO
    };

    $.ajax({
        url: '/api/v2/orders',  // adjust to your actual create-order URL
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(payload),
        success: function (res) {
            // TODO: show toast, maybe navigate to order page
            stockDrawer.hide();
        }
    });
}

function updateStock() {
    if (!currentStock) return;

    const payload = {
        id: currentStock.id,
        qty: $('#updQty').val(),
        sellPrice: $('#updSellPrice').val(),
        buyPrice: $('#updBuyPrice').val(),
        remarkText: $('#updRemark').val()
        // add other fields you expect in StockRequestDto
    };

    $.ajax({
        url: '/api/v2/stock/' + currentStock.id,
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(payload),
        success: function (res) {
            // TODO: show success toast, update row in table without full reload
            location.reload();
        }
    });
}

function deleteStock() {
    if (!confirm('Delete this stock row?')) return;

    $.ajax({
        url: '/api/v2/stock/delRow/' + currentStock.id,
        method: 'POST',
        success: function (res) {
            // TODO: show toast from your existing OmsResponse
            location.reload(); // simplest; or remove row from table
        }
    });
}
