package com.technivaaran.services;

import java.time.LocalDateTime;
import java.util.Optional;

import com.technivaaran.dto.OmsResponse;
import com.technivaaran.dto.request.OrderRequestDto;
import com.technivaaran.entities.CustomerEntity;
import com.technivaaran.entities.SalesOrderDetails;
import com.technivaaran.entities.SalesOrderHeader;
import com.technivaaran.entities.StockDetails;
import com.technivaaran.entities.StockHeader;
import com.technivaaran.entities.User;
import com.technivaaran.enums.OrderStatus;
import com.technivaaran.enums.StockType;
import com.technivaaran.repositories.SalesOderDetailRepository;
import com.technivaaran.repositories.SalesOrderHeaderRepository;
import com.technivaaran.utils.DateUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

    public ResponseEntity<OmsResponse> createSalesOrder(OrderRequestDto orderRequestDto) {
        log.info("Inside create sales oreder service");
        Optional<CustomerEntity> customerOp = customerService.findCustomerById(orderRequestDto.getCustomerId());
        if (customerOp.isPresent()) {
            Optional<StockHeader> stockHeaderOp = stockService
                    .getStockHeaderByStockHeaderId(orderRequestDto.getStockHeaderId());
            if (stockHeaderOp.isPresent()) {
                Optional<StockDetails> stockDetailsOp = stockService
                        .findStockDetailsById(orderRequestDto.getStockDetailId());
                User user = userService.getUserById(orderRequestDto.getUserId());
                if (stockDetailsOp.isPresent()) {
                    SalesOrderHeader salesOrderHeader = SalesOrderHeader.builder()
                            .orderDate(DateUtils.getLocalDateFromDDMMYYYYString(orderRequestDto.getOrderDate()))
                            .sellPrice(stockDetailsOp.get().getSellPrice())
                            .quantity(orderRequestDto.getQuantity())
                            .courierCharges(orderRequestDto.getCourierCharges())
                            .paymentType(orderRequestDto.getPaymentType())
                            .remark(orderRequestDto.getRemark())
                            .orderAmount(orderRequestDto.getQuantity() * stockDetailsOp.get().getSellPrice()
                                    + orderRequestDto.getCourierCharges())
                            .status(OrderStatus.PENDING.toString())
                            .stockDetails(stockDetailsOp.get())
                            .customer(customerOp.get())
                            .stockHeader(stockHeaderOp.get())
                            .user(user)
                            .build();

                    salesOrderHeaderRepository.save(salesOrderHeader);

                    SalesOrderDetails salesOrderDetails = SalesOrderDetails.builder()
                            .orderQty(orderRequestDto.getQuantity())
                            .sellRate(stockDetailsOp.get().getSellPrice())
                            .status(OrderStatus.PENDING.type)
                            .transactionDateTime(LocalDateTime.now())
                            .salesOrderHeader(salesOrderHeader)
                            .user(user)
                            .build();

                    salesOderDetailRepository.save(salesOrderDetails);

                    stockService.updateStockHeaderAndStockDetais(stockHeaderOp.get(),
                            StockType.OUT.type, orderRequestDto.getQuantity(), stockDetailsOp.get().getSellPrice(),
                            stockDetailsOp.get().getBuyPrice(), user);

                    return new ResponseEntity<>(OmsResponse.builder().message("Sales order created successfully.")
                            .data(orderRequestDto).build(), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(OmsResponse.builder().message("invalid stock detail data received.")
                            .data(orderRequestDto).build(), HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>(OmsResponse.builder().message("invalid stock data received.")
                        .data(orderRequestDto).build(), HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(OmsResponse.builder().message("invalid customer data received.")
                    .data(orderRequestDto).build(), HttpStatus.BAD_REQUEST);
        }

    }
}
