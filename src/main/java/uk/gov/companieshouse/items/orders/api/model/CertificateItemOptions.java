package uk.gov.companieshouse.items.orders.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An instance of this represents the item options for a certificate item.
 */
public class CertificateItemOptions {

    @JsonProperty("additional_information")
    private String additionalInformation;

    // TODO Give these options human readable names
    private boolean isCertAcc;

    private boolean isCertArts;

    private boolean isCertCobj;

    private boolean isCertDir;

    private boolean isCertDissLiq;

    private boolean isCertDomicil;

    private boolean isCertExtraI;

    private boolean isCertInc;

    private boolean isCertIncCon;

    private boolean isCertIncConLast;

    private boolean isCertIssCap;

    private boolean isCertMem;

    private boolean isCertMortDoc;

    private boolean isCertNomc;

    private boolean isCertNonExis;

    private boolean isCertOnly;

    private boolean isCertOtherC;

    private boolean isCertRet;

    private boolean isCertRoc;

    private boolean isCertSec;

    private boolean isCertShar;

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    @JsonProperty("cert_acc")
    public boolean isCertAcc() {
        return isCertAcc;
    }

    public void setCertAcc(boolean certAcc) {
        isCertAcc = certAcc;
    }

    @JsonProperty("cert_arts")
    public boolean isCertArts() {
        return isCertArts;
    }

    public void setCertArts(boolean certArts) {
        isCertArts = certArts;
    }

    @JsonProperty("cert_cobj")
    public boolean isCertCobj() {
        return isCertCobj;
    }

    public void setCertCobj(boolean certCobj) {
        isCertCobj = certCobj;
    }

    @JsonProperty("cert_dir")
    public boolean isCertDir() {
        return isCertDir;
    }

    public void setCertDir(boolean certDir) {
        isCertDir = certDir;
    }

    @JsonProperty("cert_dissliq")
    public boolean isCertDissLiq() {
        return isCertDissLiq;
    }

    public void setCertDissLiq(boolean certDissLiq) {
        isCertDissLiq = certDissLiq;
    }

    @JsonProperty("cert_domicil")
    public boolean isCertDomicil() {
        return isCertDomicil;
    }

    public void setCertDomicil(boolean certDomicil) {
        isCertDomicil = certDomicil;
    }

    @JsonProperty("cert_extra_i")
    public boolean isCertExtraI() {
        return isCertExtraI;
    }

    public void setCertExtraI(boolean certExtraI) {
        isCertExtraI = certExtraI;
    }

    @JsonProperty("cert_inc")
    public boolean isCertInc() {
        return isCertInc;
    }

    public void setCertInc(boolean certInc) {
        isCertInc = certInc;
    }

    @JsonProperty("cert_inc_con")
    public boolean isCertIncCon() {
        return isCertIncCon;
    }

    public void setCertIncCon(boolean certIncCon) {
        isCertIncCon = certIncCon;
    }

    @JsonProperty("cert_inc_con_last")
    public boolean isCertIncConLast() {
        return isCertIncConLast;
    }

    public void setCertIncConLast(boolean certIncConLast) {
        isCertIncConLast = certIncConLast;
    }

    @JsonProperty("cert_isscap")
    public boolean isCertIssCap() {
        return isCertIssCap;
    }

    public void setCertIssCap(boolean certIssCap) {
        isCertIssCap = certIssCap;
    }

    @JsonProperty("cert_mem")
    public boolean isCertMem() {
        return isCertMem;
    }

    public void setCertMem(boolean certMem) {
        isCertMem = certMem;
    }

    @JsonProperty("cert_mortdoc")
    public boolean isCertMortDoc() {
        return isCertMortDoc;
    }

    public void setCertMortDoc(boolean certMortDoc) {
        isCertMortDoc = certMortDoc;
    }

    @JsonProperty("cert_nomc")
    public boolean isCertNomc() {
        return isCertNomc;
    }

    public void setCertNomc(boolean certNomc) {
        isCertNomc = certNomc;
    }

    @JsonProperty("cert_nonexis")
    public boolean isCertNonExis() {
        return isCertNonExis;
    }

    public void setCertNonExis(boolean certNonExis) {
        isCertNonExis = certNonExis;
    }

    @JsonProperty("cert_only")
    public boolean isCertOnly() {
        return isCertOnly;
    }

    public void setCertOnly(boolean certOnly) {
        isCertOnly = certOnly;
    }

    @JsonProperty("cert_other_c")
    public boolean isCertOtherC() {
        return isCertOtherC;
    }

    public void setCertOtherC(boolean certOtherC) {
        isCertOtherC = certOtherC;
    }

    @JsonProperty("cert_ret")
    public boolean isCertRet() {
        return isCertRet;
    }

    public void setCertRet(boolean certRet) {
        isCertRet = certRet;
    }

    @JsonProperty("cert_roc")
    public boolean isCertRoc() {
        return isCertRoc;
    }

    public void setCertRoc(boolean certRoc) {
        isCertRoc = certRoc;
    }

    @JsonProperty("cert_sec")
    public boolean isCertSec() {
        return isCertSec;
    }

    public void setCertSec(boolean certSec) {
        isCertSec = certSec;
    }

    @JsonProperty("cert_shar")
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
                ", isCertMem=" + isCertMem +
                ", isCertMortDoc=" + isCertMortDoc +
                ", isCertNomc=" + isCertNomc +
                ", isCertNonExis=" + isCertNonExis +
                ", isCertOnly=" + isCertOnly +
                ", isCertOtherC=" + isCertOtherC +
                ", isCertRet=" + isCertRet +
                ", isCertRoc=" + isCertRoc +
                ", isCertSec=" + isCertSec +
                ", isCertShar=" + isCertShar +
                '}';
    }
}
