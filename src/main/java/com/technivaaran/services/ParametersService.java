package com.technivaaran.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.technivaaran.dto.response.CompanyDto;
import com.technivaaran.entities.Parameters;
import com.technivaaran.repositories.ParameterRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ParametersService {
    
    @Autowired
    private ParameterRepository parameterRepository;

    public CompanyDto getAllParameters() {
        log.info("In get All parameters service method.");
        List<Parameters> parameterList = parameterRepository.findAll();
        Map<String, String> company = new HashMap<>();
        parameterList.forEach(parameter -> {
            company.put(parameter.getParamCode(), parameter.getParamValue());
        });

        CompanyDto companyDto = CompanyDto.builder()
        .companyName(company.get("COMP"))
        .contact1(company.get("CON1"))
        .contact2(company.get("CON2"))
        .contact3(company.get("CON3"))
        .contact4(company.get("CON4"))
        .build();

        return companyDto;
    }
}
