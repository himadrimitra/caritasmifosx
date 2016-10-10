
package com.finflux.risk.creditbureau.provider.highmark.xsd.ack;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.finflux.risk.creditbureau.provider.highmark.xsd.ack package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.finflux.risk.creditbureau.provider.highmark.xsd.ack
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link REPORTFILE }
     * 
     */
    public REPORTFILE createREPORTFILE() {
        return new REPORTFILE();
    }

    /**
     * Create an instance of {@link INQUIRY }
     * 
     */
    public INQUIRY createINQUIRY() {
        return new INQUIRY();
    }

    /**
     * Create an instance of {@link REPORTFILE.INQUIRYSTATUS }
     * 
     */
    public REPORTFILE.INQUIRYSTATUS createREPORTFILEINQUIRYSTATUS() {
        return new REPORTFILE.INQUIRYSTATUS();
    }

    /**
     * Create an instance of {@link INQUIRY.ERRORS }
     * 
     */
    public INQUIRY.ERRORS createINQUIRYERRORS() {
        return new INQUIRY.ERRORS();
    }

    /**
     * Create an instance of {@link ERROR }
     * 
     */
    public ERROR createERROR() {
        return new ERROR();
    }

}
