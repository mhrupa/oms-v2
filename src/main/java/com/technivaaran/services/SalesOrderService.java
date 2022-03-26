package com.technivaaran.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.technivaaran.dto.OmsResponse;
import com.technivaaran.dto.request.OrderRequestDto;
import com.technivaaran.dto.response.SalesOrderResponseDto;
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
						OmsResponse.builder().message("invalid stock data received.")
								.data(orderRequestDto).build(),
						HttpStatus.BAD_REQUEST);
			}
		} else {
			return new ResponseEntity<>(OmsResponse.builder().message("invalid customer data received.")
					.data(orderRequestDto).build(), HttpStatus.BAD_REQUEST);
		}

	}

	private ResponseEntity<OmsResponse> validateStockDetailsAndcreateOrder(OrderRequestDto orderRequestDto,
			StockHeader stockHeader, CustomerEntity customer) {

		Optional<StockDetails> stockDetailsOp = stockService
				.findStockDetailsById(orderRequestDto.getStockDetailId());
		User user = userService.getUserById(orderRequestDto.getUserId());
		if (stockDetailsOp.isPresent()) {
			if ((stockHeader.getClosingQty()
					- orderRequestDto.getQuantity()) >= 0) {
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
						.status(orderRequestDto.getPaymentType())
						.stockDetails(stockDetailsOp.get())
						.customer(customer)
						.stockHeader(stockHeader)
						.user(user)
						.build();

				salesOrderHeaderRepository.save(salesOrderHeader);

				SalesOrderDetails salesOrderDetails = SalesOrderDetails.builder()
						.orderQty(orderRequestDto.getQuantity())
						.sellRate(orderRequestDto.getSellPrice())
						.status(OrderStatus.PENDING.type)
						.transactionDateTime(LocalDateTime.now())
						.salesOrderHeader(salesOrderHeader)
						.user(user)
						.build();

				salesOderDetailRepository.save(salesOrderDetails);
				ResponseEntity<OmsResponse> response = stockService.updateStockHeaderAndStockDetais(stockHeader,
						StockType.OUT.type,
						orderRequestDto.getQuantity(), orderRequestDto.getSellPrice(),
						stockDetailsOp.get().getBuyPrice(), user);
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
			} else {
				return new ResponseEntity<>(
						OmsResponse.builder().message("Available stock is less than order quantity.")
								.data(orderRequestDto).build(),
						HttpStatus.BAD_REQUEST);
			}
		} else {
			return new ResponseEntity<>(
					OmsResponse.builder().message("invalid stock detail data received.")
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
}
