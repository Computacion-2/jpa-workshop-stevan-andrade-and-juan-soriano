package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor 
@Builder 
public class RoleResponseDTO {
    
    private Long id;
    private String name;

}
