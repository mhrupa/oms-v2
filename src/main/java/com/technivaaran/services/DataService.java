package com.technivaaran.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.technivaaran.dto.OmsResponse;
import com.technivaaran.repositories.PaymentInDetailsRepository;
import com.technivaaran.repositories.PaymentInHeaderRepository;
import com.technivaaran.repositories.SalesOderDetailRepository;
import com.technivaaran.repositories.SalesOrderHeaderRepository;
import com.technivaaran.utils.DateUtils;

@Service
public class DataService {

    @Autowired
    private PaymentInDetailsRepository paymentInDetailsRepository;

    @Autowired
    private PaymentInHeaderRepository paymentInHeaderRepository;

    @Autowired
    private SalesOderDetailRepository salesOderDetailRepository;

    @Autowired
    private SalesOrderHeaderRepository salesOrderHeaderRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseEntity<OmsResponse> cleanUpData(String tillDate) {
        try {
            paymentInDetailsRepository
                    .deleteLessThanTransactionDate(DateUtils.convertStringDateToyyyymmdd(tillDate));

            paymentInHeaderRepository
                    .deleteLessThanEqualToPaymentInDate(DateUtils.convertStringDateToyyyymmdd(tillDate));

            salesOderDetailRepository
                    .deleteLessThanEqualToTransactionDateTime(DateUtils.convertStringDateToyyyymmdd(tillDate));

            salesOrderHeaderRepository
                    .deleteLessThanEqualToOrderDate(DateUtils.convertStringDateToyyyymmdd(tillDate));

            return new ResponseEntity<>(OmsResponse.builder().message("Data cleaned up successfully!").build(),
                    HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(
                    OmsResponse.builder().message("Exception occured while cleaning up data " + ex.getMessage())
                            .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
