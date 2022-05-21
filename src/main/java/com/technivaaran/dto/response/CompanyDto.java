package com.technivaaran.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyDto {
    private String companyName;
    private String contact1;
    private String contact2;
    private String contact3;
    private String contact4;

}
