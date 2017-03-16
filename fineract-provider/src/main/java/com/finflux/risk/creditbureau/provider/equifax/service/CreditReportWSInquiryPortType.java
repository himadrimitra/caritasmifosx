package com.finflux.risk.creditbureau.provider.equifax.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

import com.finflux.risk.creditbureau.provider.equifax.xsd.InquiryRequestType;
import com.finflux.risk.creditbureau.provider.equifax.xsd.InquiryResponseType;
import com.finflux.risk.creditbureau.provider.equifax.xsd.ObjectFactory;


@WebService(name = "CreditReportWSInquiryPortType", targetNamespace = "http://services.equifax.com/eport/servicedefs/1.0")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    ObjectFactory.class
})
public interface CreditReportWSInquiryPortType {


    /**
     * 
     * @param input
     * @return
     *     returns com.finflux.risk.creditbureau.provider.equifax.xsd.InquiryResponseType
     */
    @WebMethod(action = "http://services.equifax.com/CreditReportWS/CreditReportWSInquiry/1.0")
    @WebResult(name = "InquiryResponse", targetNamespace = "http://services.equifax.com/eport/ws/schemas/1.0", partName = "Output")
    public InquiryResponseType getConsumerCreditReport(
        @WebParam(name = "InquiryRequest", targetNamespace = "http://services.equifax.com/eport/ws/schemas/1.0", partName = "Input")
        InquiryRequestType input);

}
