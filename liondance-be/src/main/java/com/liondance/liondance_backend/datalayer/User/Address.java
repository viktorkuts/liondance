package com.liondance.liondance_backend.datalayer.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private String streetAddress;
    private String city;
    private String state;
    private String zip;
}