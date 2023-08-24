package com.technivaaran.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.technivaaran.AppUrlConstants;
import com.technivaaran.services.ChallanNoService;

@RestController
@RequestMapping(AppUrlConstants.BASE_URL)
public class ChallanNoController {

    @Autowired
    private ChallanNoService challanNoService;

    @GetMapping("/challan-no")
    public long getChallanNo() {
        return challanNoService.getNextChallanNo();
    }
}
