package com.technivaaran.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NavigationController {

	@GetMapping(value = "/")
	public String getIndexPage() {
		return "index";
	}

	@GetMapping(value = "/login")
	public String getLoginPage() {
		return "login";
//		return "desktop";
	}

	@GetMapping(value = "/navigation")
	public String getNavigationPage() {
		return "navigation";
	}

	// ****************Category-menu*****************//
	@GetMapping("/categoryHome")
	public String getCategoryPage() {
		return "masters/category/category";
	}

	@GetMapping("/createCategoryPage")
	public String getCreateCategoryPage() {
		return "masters/category/createCategory";
	}

	@GetMapping("/updateCategoryPage")
	public String getUpdateCategoryPage() {
		return "masters/category/updateCategory";
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
}
