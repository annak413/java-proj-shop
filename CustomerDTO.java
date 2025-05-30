package com.onlineshops.yourprojectname.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    // Password should NOT be in DTOs for GET requests.
    // For POST/PUT, you might have a separate DTO or include it,
    // but ensure it's hashed before saving.
}