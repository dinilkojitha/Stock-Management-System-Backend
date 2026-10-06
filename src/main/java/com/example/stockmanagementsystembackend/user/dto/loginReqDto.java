package com.example.stockmanagementsystembackend.user.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Setter
public class loginReqDto {
    private String email;
    private String password;
}
