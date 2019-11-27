package uk.gov.companieshouse.items.orders.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An instance of this represents the item options for a certificate item.
 */
public class CertificateItemOptions {

    @JsonProperty("additional_information")
    private String additionalInformation;

    // TODO Give these options human readable names
    @JsonProperty("cert_acc")
    private boolean isCertAcc;

    @JsonProperty("cert_arts")
    private boolean isCertArts;

    @JsonProperty("cert_cobj")
    private boolean isCertCobj;

    @JsonProperty("cert_dir")
    private boolean isCertDir;

    @JsonProperty("cert_dissliq")
    private boolean isCertDissLiq;

    @JsonProperty("cert_domicil")
    private boolean isCertDomicil;

    @JsonProperty("cert_extra_i")
    private boolean isCertExtraI;

    @JsonProperty("cert_inc")
    private boolean isCertInc;

    @JsonProperty("cert_inc_con")
    private boolean isCertIncCon;

    @JsonProperty("cert_inc_con_last")
    private boolean isCertIncConLast;

    @JsonProperty("cert_isscap")
    private boolean isCertIssCap;

    @JsonProperty("cert_mem")
    private boolean isCertmem;

    @JsonProperty("cert_mortdoc")
    private boolean isCertMortDoc;

    @JsonProperty("cert_nomc")
    private boolean isCertNomc;

    @JsonProperty("cert_nonexis")
    private boolean isCertNonExis;

    @JsonProperty("cert_only")
    private boolean isCertOnly;

    @JsonProperty("cert_other_c")
    private boolean isCertOtherC;

    @JsonProperty("cert_ret")
    private boolean isCertRet;

    @JsonProperty("cert_roc")
    private boolean isCertRoc;

    @JsonProperty("cert_sec")
    private boolean isCertsec;

    @JsonProperty("cert_shar")
    private boolean isCertShar;

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public boolean isCertAcc() {
        return isCertAcc;
    }

    public void setCertAcc(boolean certAcc) {
        isCertAcc = certAcc;
    }

    public boolean isCertArts() {
        return isCertArts;
    }

    public void setCertArts(boolean certArts) {
        isCertArts = certArts;
    }

    public boolean isCertCobj() {
        return isCertCobj;
    }

    public void setCertCobj(boolean certCobj) {
        isCertCobj = certCobj;
    }

    public boolean isCertDir() {
        return isCertDir;
    }

    public void setCertDir(boolean certDir) {
        isCertDir = certDir;
    }

    public boolean isCertDissLiq() {
        return isCertDissLiq;
    }

    public void setCertDissLiq(boolean certDissLiq) {
        isCertDissLiq = certDissLiq;
    }

    public boolean isCertDomicil() {
        return isCertDomicil;
    }

    public void setCertDomicil(boolean certDomicil) {
        isCertDomicil = certDomicil;
    }

    public boolean isCertExtraI() {
        return isCertExtraI;
    }

    public void setCertExtraI(boolean certExtraI) {
        isCertExtraI = certExtraI;
    }

    public boolean isCertInc() {
        return isCertInc;
    }

    public void setCertInc(boolean certInc) {
        isCertInc = certInc;
    }

    public boolean isCertIncCon() {
        return isCertIncCon;
    }

    public void setCertIncCon(boolean certIncCon) {
        isCertIncCon = certIncCon;
    }

    public boolean isCertIncConLast() {
        return isCertIncConLast;
    }

    public void setCertIncConLast(boolean certIncConLast) {
        isCertIncConLast = certIncConLast;
    }

    public boolean isCertIssCap() {
        return isCertIssCap;
    }

    public void setCertIssCap(boolean certIssCap) {
        isCertIssCap = certIssCap;
    }

    public boolean isCertmem() {
        return isCertmem;
    }

    public void setCertmem(boolean certmem) {
        isCertmem = certmem;
    }

    public boolean isCertMortDoc() {
        return isCertMortDoc;
    }

    public void setCertMortDoc(boolean certMortDoc) {
        isCertMortDoc = certMortDoc;
    }

    public boolean isCertNomc() {
        return isCertNomc;
    }

    public void setCertNomc(boolean certNomc) {
        isCertNomc = certNomc;
    }

    public boolean isCertNonExis() {
        return isCertNonExis;
    }

    public void setCertNonExis(boolean certNonExis) {
        isCertNonExis = certNonExis;
    }

    public boolean isCertOnly() {
        return isCertOnly;
    }

    public void setCertOnly(boolean certOnly) {
        isCertOnly = certOnly;
    }

    public boolean isCertOtherC() {
        return isCertOtherC;
    }

    public void setCertOtherC(boolean certOtherC) {
        isCertOtherC = certOtherC;
    }

    public boolean isCertRet() {
        return isCertRet;
    }

    public void setCertRet(boolean certRet) {
        isCertRet = certRet;
    }

    public boolean isCertRoc() {
        return isCertRoc;
    }

    public void setCertRoc(boolean certRoc) {
        isCertRoc = certRoc;
    }

    public boolean isCertsec() {
        return isCertsec;
    }

    public void setCertsec(boolean certsec) {
        isCertsec = certsec;
    }

    public boolean isCertShar() {
        return isCertShar;
    }

    public void setCertShar(boolean certShar) {
        isCertShar = certShar;
    }

    @Override
    public String toString() {
        return "CertificateItemOptions{" +
                "additionalInformation='" + additionalInformation + '\'' +
                ", isCertAcc=" + isCertAcc +
                ", isCertArts=" + isCertArts +
                ", isCertCobj=" + isCertCobj +
                ", isCertDir=" + isCertDir +
                ", isCertDissLiq=" + isCertDissLiq +
                ", isCertDomicil=" + isCertDomicil +
                ", isCertExtraI=" + isCertExtraI +
                ", isCertInc=" + isCertInc +
                ", isCertIncCon=" + isCertIncCon +
                ", isCertIncConLast=" + isCertIncConLast +
                ", isCertIssCap=" + isCertIssCap +
                ", isCertmem=" + isCertmem +
                ", isCertMortDoc=" + isCertMortDoc +
                ", isCertNomc=" + isCertNomc +
                ", isCertNonExis=" + isCertNonExis +
                ", isCertOnly=" + isCertOnly +
                ", isCertOtherC=" + isCertOtherC +
                ", isCertRet=" + isCertRet +
                ", isCertRoc=" + isCertRoc +
                ", isCertsec=" + isCertsec +
                ", isCertShar=" + isCertShar +
                '}';
    }
}
