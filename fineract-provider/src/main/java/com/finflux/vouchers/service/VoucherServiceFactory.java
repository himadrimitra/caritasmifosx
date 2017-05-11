package com.finflux.vouchers.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VoucherServiceFactory {

    final Map<String, VoucherService> services = new HashMap<>();

    @Autowired
    public VoucherServiceFactory(Set<VoucherService> services) {
        for (VoucherService service : services) {
            this.services.put(service.getKey(), service);
        }
    }

    public VoucherService findVoucherService(final String voucherType) {
        VoucherService service = null;
        if (voucherType != null) {
            service = this.services.get(voucherType.toLowerCase());
        }
        return service;
    }
}
