package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data 
@AllArgsConstructor 
@Builder 
public class UserResponseDTO {
    
    private Long id;
    private String name;
    private String email;
    private RoleResponseDTO role;

}
