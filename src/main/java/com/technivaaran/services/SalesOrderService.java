package com.technivaaran.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.technivaaran.dto.OmsResponse;
import com.technivaaran.dto.request.OrderRequestDto;
import com.technivaaran.dto.request.PaymentInRequestDto;
import com.technivaaran.dto.response.SalesOrderResponseDto;
import com.technivaaran.dto.response.StockResponseDto;
import com.technivaaran.dto.response.reports.AccountPaymentDataDto;
import com.technivaaran.entities.CustomerEntity;
import com.technivaaran.entities.PaymentAccountsEntity;
import com.technivaaran.entities.PaymentInDetails;
import com.technivaaran.entities.SalesOrderDetails;
import com.technivaaran.entities.SalesOrderHeader;
import com.technivaaran.entities.StockDetails;
import com.technivaaran.entities.StockHeader;
import com.technivaaran.entities.User;
import com.technivaaran.enums.OrderStatus;
import com.technivaaran.enums.PaymentType;
import com.technivaaran.enums.StockTransactionType;
import com.technivaaran.enums.StockType;
import com.technivaaran.exceptions.OMSException;
import com.technivaaran.repositories.SalesOderDetailRepository;
import com.technivaaran.repositories.SalesOrderHeaderRepository;
import com.technivaaran.utils.CurrencyUtil;
import com.technivaaran.utils.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SalesOrderService {

        @Autowired
        private CustomerService customerService;

        @Autowired
        private StockService stockService;

        @Autowired
        private UserService userService;

        @Autowired
        private SalesOrderHeaderRepository salesOrderHeaderRepository;

        @Autowired
        private SalesOderDetailRepository salesOderDetailRepository;

        @Autowired
        private PaymentInService paymentInService;

        @Autowired
        private ChallanNoService challanNoService;

        @Autowired
        private PaymentAccountsService paymentAccountsService;

        public ResponseEntity<OmsResponse> createSalesOrder(OrderRequestDto orderRequestDto) {
                log.info("Inside create sales oreder service");
                String customerName = "";
                String customerLocation = "";
                try {
                        customerName = orderRequestDto.getCustomer().split("\\(")[0];
                        customerLocation = orderRequestDto.getCustomer().split("\\(")[1].split("\\)")[0];
                } catch (Exception e) {
                        log.error("invalid customer entered");
                        return new ResponseEntity<>(
                                        OmsResponse.builder().message("invalid customer entered.").build(),
                                        HttpStatus.BAD_REQUEST);
                }

                Optional<CustomerEntity> customerOp = customerService.findCustomerByNameAndLocation(customerName,
                                customerLocation);
                if (customerOp.isPresent()) {
                        Optional<StockHeader> stockHeaderOp = stockService
                                        .getStockHeaderByStockHeaderId(orderRequestDto.getStockHeaderId());
                        if (stockHeaderOp.isPresent()) {
                                return validateStockDetailsAndcreateOrder(orderRequestDto,
                                                stockHeaderOp.get(), customerOp.get());
                        } else {
                                return new ResponseEntity<>(
                                                OmsResponse.builder().message("invalid stock data received.").build(),
                                                HttpStatus.BAD_REQUEST);
                        }
                } else {
                        return new ResponseEntity<>(OmsResponse.builder().message("invalid customer data received.")
                                        .data(orderRequestDto).build(), HttpStatus.BAD_REQUEST);
                }

        }

        public ResponseEntity<OmsResponse> returnSalesOrder(Long challanNo) {
                log.info("Inside return sales oreder service");
                Optional<SalesOrderHeader> salesOrderOp = salesOrderHeaderRepository.findByChallanNo(challanNo);
                if (salesOrderOp.isPresent()) {
                        SalesOrderHeader salesOrderHeader = salesOrderOp.get();
                        salesOrderHeader.setOrderAmount(0);
                        salesOrderHeader.setStatus(OrderStatus.RETURNED.type);
                        salesOrderHeader.setPaymentType(PaymentType.RETURNED.type);
                        salesOrderHeaderRepository.save(salesOrderHeader);

                        paymentInService.updatePaymentAmountByChallanNoToZero(challanNo);

                        return new ResponseEntity<>(
                                        OmsResponse.builder().message("Order returned successfully.")
                                                        .data(salesOrderHeader).build(),
                                        HttpStatus.OK);
                } else {
                        return new ResponseEntity<>(OmsResponse.builder().message("invalid challan no received.")
                                        .data(challanNo).build(), HttpStatus.BAD_REQUEST);
                }
        }

        public ResponseEntity<OmsResponse> updateSalesOrder(OrderRequestDto orderRequestDto) {
                log.info("Inside updateSalesOrder sales oreder service");
                Optional<SalesOrderHeader> salesOrderOp = salesOrderHeaderRepository
                                .findByChallanNo(orderRequestDto.getChallanNo());
                if (salesOrderOp.isPresent()) {
                        String customerName = "";
                        String customerLocation = "";
                        try {
                                customerName = orderRequestDto.getCustomer().split("\\(")[0];
                                customerLocation = orderRequestDto.getCustomer().split("\\(")[1].split("\\)")[0];
                        } catch (Exception e) {
                                log.error("invalid customer entered");
                                return new ResponseEntity<>(
                                                OmsResponse.builder().message("invalid customer entered.").build(),
                                                HttpStatus.BAD_REQUEST);
                        }
                        Optional<CustomerEntity> customerOp = customerService.findCustomerByNameAndLocation(
                                        customerName, customerLocation);
                        if (customerOp.isPresent()) {
                                SalesOrderHeader salesOrderHeader = salesOrderOp.get();
                                salesOrderHeader.setOrderDate(
                                                 DateUtils.getLocalDateFromDDMMYYYYString(
                                                                orderRequestDto.getOrderDate()));
                                salesOrderHeader.setOrderAmount(orderRequestDto.getOrderAmount());
                                salesOrderHeader.setCustomer(customerOp.get());
                                salesOrderHeader = salesOrderHeaderRepository.save(salesOrderHeader);

                                List<SalesOrderDetails> salesOrderDetailsList = salesOderDetailRepository
                                                .findBySalesOrderHeader(salesOrderHeader);

                                for (SalesOrderDetails salesOrderDetails : salesOrderDetailsList) {
                                        salesOrderDetails.setSellRate((float) salesOrderHeader.getOrderAmount()
                                                        / salesOrderHeader.getQuantity());

                                        salesOderDetailRepository.save(salesOrderDetails);
                                }

                                // SalesOrderDetails
                                // paymentInService.updatePaymentAmountByChallanNoToZero(challanNo);

                                return new ResponseEntity<>(OmsResponse.builder().message("Order updated successfully.")
                                                .data(salesOrderHeader).build(), HttpStatus.OK);
                        } else {
                                return new ResponseEntity<>(
                                                OmsResponse.builder().message("invalid customer data received.")
                                                                .data(orderRequestDto).build(),
                                                HttpStatus.BAD_REQUEST);
                        }
                } else {
                        return new ResponseEntity<>(OmsResponse.builder().message("invalid challan no received.")
                                        .data(orderRequestDto.getChallanNo()).build(), HttpStatus.BAD_REQUEST);
                }
        }

        @Transactional
        private ResponseEntity<OmsResponse> validateStockDetailsAndcreateOrder(OrderRequestDto orderRequestDto,
                        StockHeader stockHeader, CustomerEntity customer) {

                Optional<StockDetails> stockDetailsOp = stockService
                                .findLatestStockDetailsByStockHeader(orderRequestDto.getStockHeaderId());
                User user = userService.getUserById(orderRequestDto.getUserId());
                if (stockDetailsOp.isEmpty()) {
                        return new ResponseEntity<>(
                                        OmsResponse.builder().message("Invalid stock detail data received.")
                                                        .data(orderRequestDto).build(),
                                        HttpStatus.BAD_REQUEST);
                }
                if ((stockHeader.getClosingQty()
                                - orderRequestDto.getQuantity()) < 0) {
                        return new ResponseEntity<>(
                                        OmsResponse.builder().message("Available stock is less than order quantity.")
                                                        .data(orderRequestDto).build(),
                                        HttpStatus.BAD_REQUEST);
                }
                SalesOrderHeader salesOrderHeader = SalesOrderHeader.builder()
                                .orderDate(DateUtils.getLocalDateFromDDMMYYYYString(
                                                orderRequestDto.getOrderDate()))
                                .sellPrice(orderRequestDto.getSellPrice())
                                .quantity(orderRequestDto.getQuantity())
                                .courierCharges(orderRequestDto.getCourierCharges())
                                .paymentType(orderRequestDto.getPaymentType())
                                .remark(orderRequestDto.getRemark())
                                .orderAmount(orderRequestDto.getQuantity()
                                                * orderRequestDto.getSellPrice()
                                                + orderRequestDto.getCourierCharges())
                                .stockDetails(stockDetailsOp.get())
                                .challanNo(challanNoService.getMaxChallanNo())
                                .customer(customer)
                                .stockHeader(stockHeader)
                                .user(user)
                                .build();
                if (orderRequestDto.getPaymentType().equalsIgnoreCase(PaymentType.PENDING.type)
                                || orderRequestDto.getPaymentType().equalsIgnoreCase(PaymentType.VPP.type)) {
                        salesOrderHeader.setStatus(OrderStatus.PENDING.type);
                } else {
                        salesOrderHeader.setStatus(OrderStatus.COMPLETE.type);
                }
                salesOrderHeader = salesOrderHeaderRepository.save(salesOrderHeader);

                SalesOrderDetails salesOrderDetails = SalesOrderDetails.builder()
                                .orderQty(orderRequestDto.getQuantity())
                                .sellRate(orderRequestDto.getSellPrice())
                                .status(OrderStatus.PENDING.type)
                                .transactionDateTime(LocalDateTime.now())
                                .salesOrderHeader(salesOrderHeader)
                                .user(user)
                                .build();
                salesOderDetailRepository.save(salesOrderDetails);
                ResponseEntity<OmsResponse> response;
                if (!(orderRequestDto.getPaymentType().equalsIgnoreCase(PaymentType.PENDING.type)
                                || orderRequestDto.getPaymentType().equalsIgnoreCase(PaymentType.VPP.type))) {
                        PaymentInRequestDto paymentInRequestDto = PaymentInRequestDto.builder()
                                        .challanNos(salesOrderHeader.getChallanNo() + "")
                                        .paymentDate(orderRequestDto.getOrderDate())
                                        .customerId(customer.getId())
                                        .paymentType(orderRequestDto.getPaymentType())
                                        .paymentAccount(orderRequestDto.getRemark())
                                        .amount(salesOrderHeader.getOrderAmount())
                                        .updatedAmount(orderRequestDto.getQuantity() *
                                                        orderRequestDto.getSellPrice()
                                                        + orderRequestDto.getCourierCharges())
                                        .userId(user.getId())
                                        .build();
                        response = paymentInService.savePaymentIn(paymentInRequestDto);
                        if (response.getStatusCode() != HttpStatus.CREATED) {
                                OmsResponse omsResponse = response.getBody();
                                throw new OMSException(
                                                omsResponse != null ? omsResponse.getMessage()
                                                                : "Exception occured while saving payment details.");
                        }
                }

                response = stockService.updateStockHeaderAndStockDetais(stockHeader,
                                StockType.OUT.type, orderRequestDto.getQuantity(), orderRequestDto.getSellPrice(),
                                stockDetailsOp.get().getBuyPrice(), user, StockTransactionType.NORMAL, "");
                OmsResponse omsResponse = response.getBody();
                if (!ObjectUtils.isEmpty(omsResponse)) {
                        StockResponseDto stockResponseDto = (StockResponseDto) omsResponse.getData();
                        stockResponseDto.setChallanNo(Long.toString(salesOrderHeader.getChallanNo()));
                        stockResponseDto.setOrderDate(salesOrderHeader.getOrderDate() + "");
                        stockResponseDto.setCustomerName(customer.getCustomerName());
                        stockResponseDto.setCustomerEmail(customer.getEmail());
                        stockResponseDto.setCustomerLocation(customer.getLocation());
                        stockResponseDto.setTotalAmount(Double.toString(salesOrderHeader.getOrderAmount()));
                        stockResponseDto.setTotalAmountString(CurrencyUtil
                                        .convertToIndianCurrency(Double.toString(salesOrderHeader.getOrderAmount())));
                        return new ResponseEntity<>(OmsResponse.builder()
                                        .message("Sales order created successfully.")
                                        .data(stockResponseDto).build(), HttpStatus.OK);
                } else {
                        return new ResponseEntity<>(
                                        OmsResponse.builder().message("Invalid order request received.")
                                                        .data(orderRequestDto).build(),
                                        HttpStatus.BAD_REQUEST);
                }
        }

        public ResponseEntity<OmsResponse> getPendingOrders() {
                List<SalesOrderHeader> orderHeaderList = salesOrderHeaderRepository
                                .findByStatus(OrderStatus.PENDING.toString());
                List<SalesOrderResponseDto> orderResponseDtos = new ArrayList<>();
                orderHeaderList.forEach(orderHeader -> {
                        SalesOrderResponseDto salesOrderResponseDto = SalesOrderResponseDto.builder()
                                        .challanNo(orderHeader.getChallanNo())
                                        .orderDate(DateUtils.convertDateToddmmyyyy(orderHeader.getOrderDate()))
                                        .customerName(orderHeader.getCustomer().getCustomerName() + "("
                                                        + orderHeader.getCustomer().getLocation() + ")")
                                        .part(orderHeader.getStockHeader().getPartEntity().getPartNo())
                                        .model(orderHeader.getStockHeader().getItemMaster().getItemName())
                                        .config(orderHeader.getStockHeader().getConfigDetailsEntity()
                                                        .getConfiguration())
                                        .details(orderHeader.getStockHeader().getDetails())
                                        .qty(orderHeader.getQuantity())
                                        .sellPrice(orderHeader.getSellPrice())
                                        .courierCharges(orderHeader.getCourierCharges())
                                        .orderAmount(orderHeader.getOrderAmount())
                                        .paymentType(orderHeader.getPaymentType())
                                        .salesOrderId(orderHeader.getId())
                                        .stockHeaderId(orderHeader.getStockHeader().getId())
                                        .customerId(orderHeader.getCustomer().getId())
                                        .build();
                        orderResponseDtos.add(salesOrderResponseDto);
                });
                return new ResponseEntity<>(
                                OmsResponse.builder().message("Order data received.")
                                                .data(orderResponseDtos).build(),
                                HttpStatus.OK);
        }

        public ResponseEntity<OmsResponse> getAllOrders(String fromDate, String toDate) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                List<SalesOrderHeader> orderHeaderList = salesOrderHeaderRepository
                                .findByOrderDateBetween(LocalDate.parse(fromDate, formatter),
                                                LocalDate.parse(toDate, formatter));
                List<SalesOrderResponseDto> orderResponseDtos = new ArrayList<>();
                orderHeaderList.forEach(orderHeader -> {
                        SalesOrderResponseDto salesOrderResponseDto = SalesOrderResponseDto.builder()
                                        .challanNo(orderHeader.getChallanNo())
                                        .orderDate(DateUtils.convertDateToddmmyyyy(orderHeader.getOrderDate()))
                                        .customerName(orderHeader.getCustomer().getCustomerName())
                                        .customerLocation(orderHeader.getCustomer().getLocation())
                                        .part(orderHeader.getStockHeader().getPartEntity().getPartNo())
                                        .partId(orderHeader.getStockHeader().getPartEntity().getId())
                                        .model(orderHeader.getStockHeader().getItemMaster().getItemName())
                                        .config(orderHeader.getStockHeader().getConfigDetailsEntity()
                                                        .getConfiguration())
                                        .configId(orderHeader.getStockHeader().getConfigDetailsEntity().getId())
                                        .details(orderHeader.getStockHeader().getDetails())
                                        .qty(orderHeader.getQuantity())
                                        .sellPrice(orderHeader.getSellPrice())
                                        .courierCharges(orderHeader.getCourierCharges())
                                        .orderAmount(orderHeader.getOrderAmount())
                                        .salesOrderId(orderHeader.getId())
                                        .stockHeaderId(orderHeader.getStockHeader().getId())
                                        .customerId(orderHeader.getCustomer().getId())
                                        .paymentType(orderHeader.getPaymentType())
                                        .build();

                        if (orderHeader.getPaymentType().equalsIgnoreCase(PaymentType.BANK.type)
                                        || orderHeader.getPaymentType().equalsIgnoreCase(PaymentType.PAYTM.type)) {
                                Optional<PaymentAccountsEntity> paymentAccountEntityOp = paymentAccountsService
                                                .findById(Long.parseLong(orderHeader.getRemark()));
                                if (paymentAccountEntityOp.isPresent()) {
                                        PaymentAccountsEntity accountsEntity = paymentAccountEntityOp.get();
                                        salesOrderResponseDto.setPaymentAccName(accountsEntity.getAccountName());
                                }
                        }
                        Optional<PaymentInDetails> paymentInDetailsOp = paymentInService
                                        .findByChallanNo(orderHeader.getChallanNo());
                        if (paymentInDetailsOp.isPresent()) {

                                salesOrderResponseDto.setPaymentDate(DateUtils.convertDateToddmmyyyy(
                                                paymentInDetailsOp.get().getPaymentInHeader().getPaymentInDate()));
                        }

                        orderResponseDtos.add(salesOrderResponseDto);
                });
                return new ResponseEntity<>(
                                OmsResponse.builder().message("Order data received.")
                                                .data(orderResponseDtos).build(),
                                HttpStatus.OK);
        }

        public List<SalesOrderHeader> findByIdIn(List<Long> challanNoList) {
                return salesOrderHeaderRepository.findByIdIn(challanNoList);
        }

        public SalesOrderHeader updateOrder(SalesOrderHeader salesOrderHeader) {
                return salesOrderHeaderRepository.save(salesOrderHeader);
        }

        public ResponseEntity<OmsResponse> getSalesOrderDataForPrinting(Long challanNo) {
                return null;
        }

        public ResponseEntity<OmsResponse> getAccountPaymentData(Long account, int month, int year) {
                List<Object[]> dataList = salesOrderHeaderRepository.getAccountPaymentData(account, month, year);
                List<AccountPaymentDataDto> accountPaymentDataDtos = new ArrayList<>();

                dataList.forEach(data -> {
                        AccountPaymentDataDto accountPaymentDataDto = AccountPaymentDataDto.builder()
                                        .challanNo(data[0].toString())
                                        .orderDate(DateUtils.convertDateToddmmyyyy(LocalDate.parse(data[1].toString())))
                                        .paymentDate(DateUtils
                                                        .convertDateToddmmyyyy(LocalDate.parse(data[2].toString())))
                                        .customerName(data[3].toString())
                                        .itemName(data[4].toString())
                                        .orderAmount(data[5].toString())
                                        .build();
                        accountPaymentDataDtos.add(accountPaymentDataDto);
                });

                return new ResponseEntity<>(
                                OmsResponse.builder().message("Order data received.")
                                                .data(accountPaymentDataDtos).build(),
                                HttpStatus.OK);
        }
}
