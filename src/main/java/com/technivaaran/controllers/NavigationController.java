package com.technivaaran.controllers;

import com.technivaaran.AppUrlConstants;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(AppUrlConstants.BASE_URL)
public class NavigationController {

	@GetMapping(value = "/")
	public String getIndexPage() {
		return "index";
	}

	@GetMapping(value = "/login")
	public String getLoginPage() {
		return "login";
	}

	@GetMapping(value = "/navigation")
	public String getNavigationPage() {
		return "navigation";
	}

	// ****************Branch-menu*****************//
	@GetMapping("/branchHome")
	public String getBranchPage() {
		return "masters/branch/branch";
	}

	@GetMapping("/createBranchPage")
	public String getCreateBranchPage() {
		return "masters/branch/createBranch";
	}

	@GetMapping("/updateBranchPage")
	public String getUpdateBranchPage() {
		return "masters/branch/updateBranch";
	}

	// ****************Unit-menu*****************//
	@GetMapping("/unitHome")
	public String getUnitPage() {
		return "masters/unit/unit";
	}

	@GetMapping("/createUnitPage")
	public String getCreateUnitPage() {
		return "masters/unit/createUnit";
	}

	@GetMapping("/updateUnitPage")
	public String getUpdateUnitPage() {
		return "masters/unit/updateUnit";
	}

	// ****************Item-menu*****************//
	@GetMapping("/itemHome")
	public String getItemPage() {
		return "masters/item/item";
	}

	@GetMapping("/createItemPage")
	public String getCreateItemPage() {
		return "masters/item/createItem";
	}

	@GetMapping("/updateItemPage")
	public String getUpdateItemPage() {
		return "masters/item/updateItem";
	}

	// ****************User-menu*****************//
	@GetMapping("/userHome")
	public String getUserPage() {
		return "masters/user/user";
	}

	@GetMapping("/createUserPage")
	public String getCreateUserPage() {
		return "masters/user/createUser";
	}

	@GetMapping("/updateUserPage")
	public String getUpdateUserPage() {
		return "masters/user/updateUser";
	}

	// ****************Customer-menu*****************//
	@GetMapping("/customerHome")
	public String getCustomerPage() {
		return "masters/customer/customer";
	}

	@GetMapping("/createCustomerPage")
	public String getCreateCustomerPage() {
		return "masters/customer/createCustomer";
	}

	@GetMapping("/updateCustomerPage")
	public String getUpdateCustomerPage() {
		return "masters/customer/updateCustomer";
	}

	// ****************Stock-menu*****************//
	@GetMapping("/stockHome")
	public String getStockPage() {
		return "transactions/stock/createStock";
	}

	@GetMapping("/createStockPage")
	public String getCreateStockPage() {
		return "transactions/stock/createStock";
	}

	@GetMapping("/updateStockPage")
	public String getUpdateStockPage() {
		return "transactions/stock/updateStock";
	}

	// ****************Sales-menu*****************//
	@GetMapping("/salesOrderHome")
	public String getSalesPage() {
		return "transactions/sale/saleOrderHome";
	}

	// ****************Pending-Orders-menu*****************//
	@GetMapping("/pendingHome")
	public String getPendingOrdersPage() {
		return "transactions/paymentOutstanding/paymentOutstanding";
	}
}
