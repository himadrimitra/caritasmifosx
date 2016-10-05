package org.apache.fineract.infrastructure.configuration.data;

public class HighmarkCredentialsData {

    private final String PRODUCTTYP;
    private final String PRODUCTVER;
    private final String REQMBR;
    private final String SUBMBRID;
    private final String REQVOLTYP;
    private final String TESTFLG;
    private final String USERID;
    private final String PWD;
    private final String AUTHFLG;
    private final String AUTHTITLE;
    private final String RESFRMT;
    private final String MEMBERPREOVERRIDE;
    private final String RESFRMTEMBD;
    private final String LOSNAME;
    private final String URL;
    private final String CREDTRPTID;
    private final String CREDTREQTYP;
    private final String CREDTINQPURPSTYP;
    private final String CREDTINQPURPSTYPDESC;
    private final String CREDITINQUIRYSTAGE;
    private final String CREDTRPTTRNDTTM;
    private final String ORDEROFREQUEST;
    private final String HIGHMARKQUERY;

    public HighmarkCredentialsData(String pRODUCTTYP, String pRODUCTVER, String rEQMBR, String sUBMBRID, String rEQVOLTYP, String tESTFLG,
            String uSERID, String pWD, String aUTHFLG, String aUTHTITLE, String rESFRMT, String mEMBERPREOVERRIDE, String rESFRMTEMBD,
            String lOSNAME, String uRL, String cREDTRPTID, String cREDTREQTYP, String cREDTINQPURPSTYP, String cREDTINQPURPSTYPDESC,
            String cREDITINQUIRYSTAGE, String cREDTRPTTRNDTTM, String oRDEROFREQUEST, String hIGHMARKQUERY) {
        this.PRODUCTTYP = pRODUCTTYP;
        this.PRODUCTVER = pRODUCTVER;
        this.REQMBR = rEQMBR;
        this.SUBMBRID = sUBMBRID;
        this.REQVOLTYP = rEQVOLTYP;
        this.TESTFLG = tESTFLG;
        this.USERID = uSERID;
        this.PWD = pWD;
        this.AUTHFLG = aUTHFLG;
        this.AUTHTITLE = aUTHTITLE;
        this.RESFRMT = rESFRMT;
        this.MEMBERPREOVERRIDE = mEMBERPREOVERRIDE;
        this.RESFRMTEMBD = rESFRMTEMBD;
        this.LOSNAME = lOSNAME;
        this.URL = uRL;
        this.CREDTRPTID = cREDTRPTID;
        this.CREDTREQTYP = cREDTREQTYP;
        this.CREDTINQPURPSTYP = cREDTINQPURPSTYP;
        this.CREDTINQPURPSTYPDESC = cREDTINQPURPSTYPDESC;
        this.CREDITINQUIRYSTAGE = cREDITINQUIRYSTAGE;
        this.CREDTRPTTRNDTTM = cREDTRPTTRNDTTM;
        this.ORDEROFREQUEST = oRDEROFREQUEST;
        this.HIGHMARKQUERY = hIGHMARKQUERY;
    }

    public String getPRODUCTTYP() {
        return PRODUCTTYP;
    }

    public String getPRODUCTVER() {
        return PRODUCTVER;
    }

    public String getREQMBR() {
        return REQMBR;
    }

    public String getSUBMBRID() {
        return SUBMBRID;
    }

    public String getREQVOLTYP() {
        return REQVOLTYP;
    }

    public String getTESTFLG() {
        return TESTFLG;
    }

    public String getUSERID() {
        return USERID;
    }

    public String getPWD() {
        return PWD;
    }

    public String getAUTHFLG() {
        return AUTHFLG;
    }

    public String getAUTHTITLE() {
        return AUTHTITLE;
    }

    public String getRESFRMT() {
        return RESFRMT;
    }

    public String getMEMBERPREOVERRIDE() {
        return MEMBERPREOVERRIDE;
    }

    public String getRESFRMTEMBD() {
        return RESFRMTEMBD;
    }

    public String getLOSNAME() {
        return LOSNAME;
    }

    public String getURL() {
        return this.URL;
    }

    public String getCREDTRPTID() {
        return this.CREDTRPTID;
    }

    public String getCREDTREQTYP() {
        return this.CREDTREQTYP;
    }

    public String getCREDTINQPURPSTYP() {
        return this.CREDTINQPURPSTYP;
    }

    public String getCREDTINQPURPSTYPDESC() {
        return this.CREDTINQPURPSTYPDESC;
    }

    public String getCREDITINQUIRYSTAGE() {
        return this.CREDITINQUIRYSTAGE;
    }

    public String getCREDTRPTTRNDTTM() {
        return this.CREDTRPTTRNDTTM;
    }

    public String getORDEROFREQUEST() {
        return this.ORDEROFREQUEST;
    }

    public String getHIGHMARKQUERY() {
        return this.HIGHMARKQUERY;
    }

}
