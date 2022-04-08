package com.technivaaran.controllers;

import java.util.List;
import java.util.Map;

import com.technivaaran.AppUrlConstants;
import com.technivaaran.dto.OmsResponse;
import com.technivaaran.entities.RemarkEntity;
import com.technivaaran.services.RemarkService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(AppUrlConstants.BASE_URL)
public class RemarkController {
    @Autowired
    private RemarkService remarkService;

    @PostMapping("/remarks")
    public ResponseEntity<OmsResponse> saveRemark(@RequestBody Map<String, String> requestMap) {
        log.info("creating remark started");

        return remarkService.createRemark(requestMap.get("remark"));
    }

    @GetMapping("/remarks")
    public List<RemarkEntity> getAllRemarkss() {
        return remarkService.findAllRemarks();
    }
}
