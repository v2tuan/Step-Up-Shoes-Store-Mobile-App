package com.stepup.model.location;

import com.stepup.model.Address;

public class AddressRequest {
    private Address address;
    private boolean setDefault;

    public AddressRequest(Address address, boolean setDefault) {
        this.address = address;
        this.setDefault = setDefault;
    }
}