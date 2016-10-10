package com.finflux.risk.creditbureau.configuration.data;

public class CreditBureauProductData {

    private final long credit_bureau_product_id;

    private final String cb_product_name;

    private final long cb_master_id;

    private CreditBureauProductData(final long credit_bureau_product_id, final String cb_product_name, final long cb_master_id) {
        this.credit_bureau_product_id = credit_bureau_product_id;
        this.cb_product_name = cb_product_name;
        this.cb_master_id = cb_master_id;
    }

    public static CreditBureauProductData instance(final long credit_bureau_product_id, final String cb_product_name,
            final long cb_master_id) {
        return new CreditBureauProductData(credit_bureau_product_id, cb_product_name, cb_master_id);
    }

    public long getCredit_bureau_product_id() {
        return this.credit_bureau_product_id;
    }

    public String getCb_product_name() {
        return this.cb_product_name;
    }

    public long getCb_master_id() {
        return this.cb_master_id;
    }

}
