package com.finflux.risk.creditbureau.configuration.data;

public class CreditBureauData {

    private final long creditBureauId;

    private final String creditBureauName;

    private final String creditBureauCountry;

    private final String creditBureauProduct;
    
    private final String creditBureauSummary;

//    private final long implementationkey;

    private final boolean is_active;

    private CreditBureauData(final long creditBureauId, final String creditBureauName, final String creditBureauCountry, final String creditBureauProduct,
                             final String creditBureauSummary, final boolean is_active) {
        this.creditBureauId = creditBureauId;
        this.creditBureauName = creditBureauName;
        this.creditBureauCountry = creditBureauCountry;
        this.creditBureauProduct = creditBureauProduct;
        this.creditBureauSummary = creditBureauSummary;
//        this.implementationkey = implementationkey;
        this.is_active = is_active;
    }

    public static CreditBureauData instance(final long creditbureau_id, final String creditbureau_name, final String country,
            final String product_name, final String cbSummary, final boolean is_active) {
        return new CreditBureauData(creditbureau_id, creditbureau_name, country, product_name,cbSummary,
                is_active);
    }

    
    
    public String getCreditBureauSummary() {
        return this.creditBureauSummary;
    }

    public long getCreditBureauId() {
        return this.creditBureauId;
    }

    
    public String getCreditBureauName() {
        return this.creditBureauName;
    }

    
    public String getCreditBureauCountry() {
        return this.creditBureauCountry;
    }

    
    public String getCreditBureauProduct() {
        return this.creditBureauProduct;
    }

    
    public boolean is_active() {
        return is_active;
    }
}
