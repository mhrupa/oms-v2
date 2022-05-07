package com.technivaaran.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import com.technivaaran.dto.OmsResponse;
import com.technivaaran.dto.request.OrderRequestDto;
import com.technivaaran.dto.request.PaymentInRequestDto;
import com.technivaaran.dto.response.SalesOrderResponseDto;
import com.technivaaran.entities.CustomerEntity;
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
import com.technivaaran.utils.DateUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

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

    public ResponseEntity<OmsResponse> createSalesOrder(OrderRequestDto orderRequestDto) {
        log.info("Inside create sales oreder service");
        Optional<CustomerEntity> customerOp = customerService.findCustomerById(orderRequestDto.getCustomerId());
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
                .status(orderRequestDto.getPaymentType().equalsIgnoreCase(PaymentType.PENDING.type)
                        ? OrderStatus.PENDING.type
                        : OrderStatus.COMPLETE.type)
                .stockDetails(stockDetailsOp.get())
                .challanNo(challanNoService.getMaxChallanNo())
                .customer(customer)
                .stockHeader(stockHeader)
                .user(user)
                .build();
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
        if (!orderRequestDto.getPaymentType().equalsIgnoreCase(PaymentType.PENDING.type)) {
            PaymentInRequestDto paymentInRequestDto = PaymentInRequestDto.builder()
                    .challanNos(salesOrderHeader.getChallanNo()+"")
                    .paymentDate(orderRequestDto.getOrderDate())
                    .customerId(customer.getId())
                    .paymentType(orderRequestDto.getPaymentType())
                    .paymentAccount(orderRequestDto.getRemark())
                    .amount(salesOrderHeader.getOrderAmount())
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
            return new ResponseEntity<>(OmsResponse.builder()
                    .message("Sales order created successfully.")
                    .data(omsResponse.getData()).build(), HttpStatus.OK);
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
                    .challanNo(orderHeader.getId())
                    .orderDate(orderHeader.getOrderDate())
                    .customerName(orderHeader.getCustomer().getCustomerName())
                    .part(orderHeader.getStockHeader().getPartEntity().getPartNo())
                    .model(orderHeader.getStockHeader().getItemMaster().getItemName())
                    .config(orderHeader.getStockHeader().getConfigDetailsEntity().getConfiguration())
                    .details(orderHeader.getStockHeader().getDetails())
                    .qty(orderHeader.getQuantity())
                    .sellPrice(orderHeader.getSellPrice())
                    .courierCharges(orderHeader.getCourierCharges())
                    .orderAmount(orderHeader.getOrderAmount())
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
                .findByOrderDateBetween(LocalDate.parse(fromDate, formatter), LocalDate.parse(toDate, formatter));
        List<SalesOrderResponseDto> orderResponseDtos = new ArrayList<>();
        orderHeaderList.forEach(orderHeader -> {
            SalesOrderResponseDto salesOrderResponseDto = SalesOrderResponseDto.builder()
                    .challanNo(orderHeader.getId())
                    .orderDate(orderHeader.getOrderDate())
                    .customerName(orderHeader.getCustomer().getCustomerName())
                    .part(orderHeader.getStockHeader().getPartEntity().getPartNo())
                    .model(orderHeader.getStockHeader().getItemMaster().getItemName())
                    .config(orderHeader.getStockHeader().getConfigDetailsEntity().getConfiguration())
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
}
