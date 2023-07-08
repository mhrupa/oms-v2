package com.technivaaran.services;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.technivaaran.dto.OmsResponse;
import com.technivaaran.dto.request.OrderRequestDto;
import com.technivaaran.dto.request.PaymentInRequestDto;
import com.technivaaran.entities.CustomerEntity;
import com.technivaaran.entities.PaymentInDetails;
import com.technivaaran.entities.PaymentInHeader;
import com.technivaaran.entities.SalesOrderDetails;
import com.technivaaran.entities.SalesOrderHeader;
import com.technivaaran.entities.User;
import com.technivaaran.enums.OrderStatus;
import com.technivaaran.enums.PaymentType;
import com.technivaaran.repositories.PaymentInDetailsRepository;
import com.technivaaran.repositories.PaymentInHeaderRepository;
import com.technivaaran.repositories.SalesOderDetailRepository;
import com.technivaaran.repositories.SalesOrderHeaderRepository;
import com.technivaaran.utils.DateUtils;
import com.technivaaran.utils.ResponseUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentInService {

    @Autowired
    private PaymentInHeaderRepository paymentInHeaderRepository;

    @Autowired
    private PaymentInDetailsRepository paymentInDetailsRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserService userService;

    @Autowired
    private SalesOrderHeaderRepository salesOrderHeaderRepository;

    @Autowired
    private SalesOderDetailRepository salesOderDetailRepository;

    @Transactional
    public ResponseEntity<OmsResponse> savePaymentIn(PaymentInRequestDto paymentInRequestDto) {
        log.info("In Save payment in started.");
        User user = userService.getUserById(paymentInRequestDto.getUserId());
        if (ObjectUtils.isEmpty(user)) {
            return ResponseUtil.createResponseEntity("Invalid User found in request.",
                    null, HttpStatus.BAD_REQUEST);
        }
        Optional<CustomerEntity> customerOp = customerService
                .findCustomerById(paymentInRequestDto.getCustomerId());
        if (customerOp.isEmpty()) {
            return ResponseUtil.createResponseEntity("Invalid customer received.",
                    null, HttpStatus.BAD_REQUEST);
        }
        List<Long> challanNoList = Arrays.asList(paymentInRequestDto.getChallanNos().split(","))
                .stream().map(Long::parseLong).collect(Collectors.toList());
        List<SalesOrderHeader> salesOrderHeaders = salesOrderHeaderRepository.findByChallanNoIn(challanNoList);

        List<Long> invalidChallanList = validateChallanNo(challanNoList, salesOrderHeaders);
        if (!invalidChallanList.isEmpty()) {
            return ResponseUtil.createResponseEntity("Invalid challan no received." + invalidChallanList,
                    null, HttpStatus.BAD_REQUEST);
        }

        PaymentInHeader paymentInHeader = PaymentInHeader.builder()
                .paymentInDate(DateUtils.getLocalDateFromDDMMYYYYString(paymentInRequestDto.getPaymentDate()))
                // .amount(paymentInRequestDto.getAmount())
                .amount(paymentInRequestDto.getUpdatedAmount())
                .paymentType(paymentInRequestDto.getPaymentType())
                .paymentAccountName(
                        paymentInRequestDto.getPaymentType()
                                .equalsIgnoreCase(PaymentType.BANK.type)
                                        ? paymentInRequestDto.getPaymentAccount()
                                        : "NA")
                .customer(customerOp.get())
                .user(user)
                .build();
        paymentInHeader = paymentInHeaderRepository.save(paymentInHeader);

        for (SalesOrderHeader salesOrderHeader : salesOrderHeaders) {
            PaymentInDetails paymentInDetails = PaymentInDetails.builder()
                    .challanNo(salesOrderHeader.getChallanNo())
                    .orderAmount(salesOrderHeader.getOrderAmount())
                    .transactionDate(LocalDate.now())
                    .paymentInHeader(paymentInHeader)
                    .build();
            paymentInDetailsRepository.save(paymentInDetails);
            if (paymentInRequestDto.getPaymentType().equalsIgnoreCase(PaymentType.PENDING.type)
                    || paymentInRequestDto.getPaymentType().equalsIgnoreCase(PaymentType.VPP.type)) {
                salesOrderHeader.setStatus(OrderStatus.PENDING.type);
            } else {
                salesOrderHeader.setStatus(OrderStatus.COMPLETE.type);
            }
            salesOrderHeader.setPaymentType(paymentInRequestDto.getPaymentType());

            if (paymentInRequestDto.getAmount() != paymentInRequestDto.getUpdatedAmount()) {
                salesOrderHeader.setOrderAmount(paymentInRequestDto.getUpdatedAmount());
                salesOrderHeader
                        .setSellPrice((float) paymentInRequestDto.getUpdatedAmount() / salesOrderHeader.getQuantity());

                List<SalesOrderDetails> salesOrderDetailsList = salesOderDetailRepository
                        .findBySalesOrderHeader(salesOrderHeader);
                for (SalesOrderDetails salesOrderDetails : salesOrderDetailsList) {
                    salesOrderDetails.setSellRate(
                            (float) paymentInRequestDto.getUpdatedAmount() / salesOrderHeader.getQuantity());
                    salesOderDetailRepository.save(salesOrderDetails);
                }
            }
            if (paymentInRequestDto.getPaymentType()
                    .equalsIgnoreCase(PaymentType.BANK.type)) {
                salesOrderHeader.setRemark(paymentInRequestDto.getPaymentAccount());
            }
            salesOrderHeaderRepository.save(salesOrderHeader);
        }

        return new ResponseEntity<>(
                OmsResponse.builder().message("Payment record created successfully.").build(),
                HttpStatus.CREATED);
    }

    private List<Long> validateChallanNo(List<Long> challanNoList, List<SalesOrderHeader> salesOrderHeaders) {
        return challanNoList.stream().filter(challanNo -> salesOrderHeaders.stream()
                .noneMatch(salesOrderHeader -> salesOrderHeader.getChallanNo().equals(challanNo)))
                .collect(Collectors.toList());
    }

    public void updatePaymentAmountByChallanNoToZero(Long challanNo) {
           Optional<PaymentInDetails> paymentIndetailsOp = paymentInDetailsRepository.findByChallanNo(challanNo);
           if(paymentIndetailsOp.isPresent()) {
                PaymentInDetails paymentInDetails = paymentIndetailsOp.get();
                paymentInDetails.setOrderAmount(0);
                paymentInDetails.getPaymentInHeader().setAmount(0);
                paymentInDetailsRepository.save(paymentInDetails);
           } 
    }

    public Optional<PaymentInDetails> findByChallanNo(Long challanNo) {
         return paymentInDetailsRepository.findByChallanNo(challanNo);
    }
    
    public Optional<PaymentInDetails> findFirstByChallanNoOrderByIdDesc(Long challanNo) {
        return paymentInDetailsRepository.findFirstByChallanNoOrderByIdDesc(challanNo);
   }
    
    public PaymentInHeader updatePaymentInHeaderPaymentType(PaymentInHeader paymentInHeader, OrderRequestDto orderRequestDto) {
        paymentInHeader.setPaymentType(orderRequestDto.getPaymentType());
        return paymentInHeaderRepository.save(paymentInHeader);
    }
    
    public PaymentInHeader updatePaymentInHeaderAmount(PaymentInHeader paymentInHeader, OrderRequestDto orderRequestDto) {
        paymentInHeader.setAmount(orderRequestDto.getOrderAmount());
        return paymentInHeaderRepository.save(paymentInHeader);
    }
    
    public PaymentInDetails updatePaymentInDetailsOrderAmount(PaymentInDetails paymentInDetails, OrderRequestDto orderRequestDto) {
        paymentInDetails.setOrderAmount(orderRequestDto.getOrderAmount());
        return paymentInDetailsRepository.save(paymentInDetails);
    }
    
    public void deletePaymentInDetailsById(PaymentInDetails paymentInDetails) {
        paymentInDetailsRepository.delete(paymentInDetails);
    }
    
    public void deletePaymentInHeaderById(PaymentInHeader paymentInHeader) {
        paymentInHeaderRepository.delete(paymentInHeader);
    }

}
