package com.example.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor 
@Builder 
public class RoleRequestDTO {
    
    String name;
    List<Long> permissionIds;

}
