// package com.technivaaran.services;

// import java.time.LocalDate;
// import java.util.Arrays;
// import java.util.List;
// import java.util.Optional;
// import java.util.stream.Collectors;

// import javax.transaction.Transactional;

// import com.technivaaran.dto.OmsResponse;
// import com.technivaaran.dto.request.PaymentInRequestDto;
// import com.technivaaran.entities.CustomerEntity;
// import com.technivaaran.entities.PaymentInDetails;
// import com.technivaaran.entities.PaymentInHeader;
// import com.technivaaran.entities.SalesOrderHeader;
// import com.technivaaran.entities.User;
// import com.technivaaran.enums.OrderStatus;
// import com.technivaaran.enums.PaymentType;
// import com.technivaaran.exceptions.OMSException;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Service;
// import org.springframework.util.ObjectUtils;

// import lombok.extern.slf4j.Slf4j;

// @Slf4j
// @Service
// public class OrderAndPaymentService {

//     @Autowired
//     private PaymentInService paymentInService;

//     @Transactional
//     public void savePaymentInForOrder(PaymentInRequestDto paymentInRequestDto) throws OMSException {
//         ResponseEntity<OmsResponse> response = paymentInService.savePaymentIn(paymentInRequestDto);
//         if (response.getStatusCode() != HttpStatus.CREATED) {
//             OmsResponse omsResponse = response.getBody();
//             throw new OMSException(
//                     omsResponse != null ? omsResponse.getMessage() : "Exception occured while saving payment details.");
//         }
//     }

//     public ResponseEntity<OmsResponse> savePaymentIn(PaymentInRequestDto paymentInRequestDto) {
//         log.info("Save payment in started in order and Payment service.");
//         User user = userService.getUserById(paymentInRequestDto.getUserId());
//         if (ObjectUtils.isEmpty(user)) {
//             return new ResponseEntity<>(OmsResponse.builder().message("Invalid User found in request.")
//                     .build(), HttpStatus.BAD_REQUEST);
//         }
//         Optional<CustomerEntity> customerOp = customerService
//                 .findCustomerById(paymentInRequestDto.getCustomerId());
//         if (customerOp.isPresent()) {
//             List<Long> challanNoList = Arrays.asList(paymentInRequestDto.getChallanNos().split(","))
//                     .stream()
//                     .map(Long::parseLong).collect(Collectors.toList());
//             List<SalesOrderHeader> salesOrderHeaders = orderService.findByIdIn(challanNoList);

//             List<Long> invalidChallanList = validateChallanNo(challanNoList, salesOrderHeaders);
//             if (!invalidChallanList.isEmpty()) {
//                 return new ResponseEntity<>(OmsResponse.builder()
//                         .message("Invalid challan no received." + invalidChallanList)
//                         .build(), HttpStatus.BAD_REQUEST);
//             }

//             PaymentInHeader paymentInHeader = PaymentInHeader.builder()
//                     .paymentInDate(LocalDate.now())
//                     .amount(paymentInRequestDto.getAmount())
//                     .paymentType(paymentInRequestDto.getPaymentType())
//                     .paymentAccountName(
//                             paymentInRequestDto.getPaymentType()
//                                     .equalsIgnoreCase(PaymentType.BANK.type)
//                                             ? paymentInRequestDto.getPaymentAccount()
//                                             : "NA")
//                     .customer(customerOp.get())
//                     .user(user)
//                     .build();

//             // PaymentInDetails

//             paymentInHeader = paymentInHeaderRepository.save(paymentInHeader);

//             for (SalesOrderHeader salesOrderHeader : salesOrderHeaders) {
//                 PaymentInDetails paymentInDetails = PaymentInDetails.builder()
//                         .challanNo(salesOrderHeader.getId())
//                         .orderAmount(salesOrderHeader.getOrderAmount())
//                         .transactionDate(LocalDate.now())
//                         .paymentInHeader(paymentInHeader)
//                         .build();
//                 paymentInDetailsRepository.save(paymentInDetails);
//                 salesOrderHeader.setStatus(OrderStatus.COMPLETE.type);
//                 orderService.updateOrder(salesOrderHeader);
//             }

//             return new ResponseEntity<>(
//                     OmsResponse.builder().message("Payment record created successfully.").build(),
//                     HttpStatus.CREATED);
//         } else {
//             return new ResponseEntity<>(
//                     OmsResponse.builder().message("Invalid customer received.").build(),
//                     HttpStatus.BAD_REQUEST);
//         }
//     }
// }
