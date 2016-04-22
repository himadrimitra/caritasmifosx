package com.finflux.kyc.address.domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.kyc.address.exception.AddressNotFoundException;

@Service
public class AddressRepositoryWrapper {

    private final AddressRepository repository;

    @Autowired
    public AddressRepositoryWrapper(final AddressRepository repository) {
        this.repository = repository;
    }

    public Address findOneWithNotFoundDetection(final Long addressId) {
        final Address address = this.repository.findOne(addressId);
        if (address == null) { throw new AddressNotFoundException(addressId); }
        return address;
    }

    public void save(final Address address) {
        this.repository.save(address);
    }

    public void save(final List<Address> addresses) {
        this.repository.save(addresses);
    }
    
    public void saveAndFlush(final Address address) {
        this.repository.saveAndFlush(address);
    }

    public void delete(final Address address) {
        this.repository.delete(address);
    }
}
