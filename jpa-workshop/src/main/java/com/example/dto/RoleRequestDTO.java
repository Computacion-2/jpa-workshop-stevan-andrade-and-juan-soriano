package com.example.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor 
@Builder 
public class RoleRequestDTO {
    
    String name;
    Set<Long> permissionIds;

}
