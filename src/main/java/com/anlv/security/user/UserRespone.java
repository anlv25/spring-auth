package com.anlv.security.user;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRespone {
    @JsonProperty("full_name")
    private String fullName;
    private String image;
    private String email;
    private String role;
}
