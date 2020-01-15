package uk.gov.companieshouse.items.orders.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.gson.Gson;

/**
 * An instance of this represents the item options for a certificate item.
 */
@JsonPropertyOrder(alphabetic = true)
public class CertificateItemOptions {

    @JsonProperty("additional_information")
    private String additionalInformation;

    // TODO PCI-504 Give these options human readable names.
    private Boolean isCertAcc;

    private Boolean isCertArts;

    private Boolean isCertCobj;

    private Boolean isCertDir;

    private Boolean isCertDissLiq;

    private Boolean isCertDomicil;

    private Boolean isCertExtraI;

    private Boolean isCertInc;

    private Boolean isCertIncCon;

    private Boolean isCertIncConLast;

    private Boolean isCertIssCap;

    private Boolean isCertMem;

    private Boolean isCertMortDoc;

    private Boolean isCertNomc;

    private Boolean isCertNonExis;

    private Boolean isCertOnly;

    private Boolean isCertOtherC;

    private Boolean isCertRet;

    private Boolean isCertRoc;

    private Boolean isCertSec;

    private Boolean isCertShar;

    private DeliveryTimescale deliveryTimescale;

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    @JsonProperty("cert_acc")
    public Boolean isCertAcc() {
        return isCertAcc;
    }

    public void setCertAcc(boolean certAcc) {
        isCertAcc = certAcc;
    }

    @JsonProperty("cert_arts")
    public Boolean isCertArts() {
        return isCertArts;
    }

    public void setCertArts(boolean certArts) {
        isCertArts = certArts;
    }

    @JsonProperty("cert_cobj")
    public Boolean isCertCobj() {
        return isCertCobj;
    }

    public void setCertCobj(boolean certCobj) {
        isCertCobj = certCobj;
    }

    @JsonProperty("cert_dir")
    public Boolean isCertDir() {
        return isCertDir;
    }

    public void setCertDir(boolean certDir) {
        isCertDir = certDir;
    }

    @JsonProperty("cert_dissliq")
    public Boolean isCertDissLiq() {
        return isCertDissLiq;
    }

    public void setCertDissLiq(boolean certDissLiq) {
        isCertDissLiq = certDissLiq;
    }

    @JsonProperty("cert_domicil")
    public Boolean isCertDomicil() {
        return isCertDomicil;
    }

    public void setCertDomicil(boolean certDomicil) {
        isCertDomicil = certDomicil;
    }

    @JsonProperty("cert_extra_i")
    public Boolean isCertExtraI() {
        return isCertExtraI;
    }

    public void setCertExtraI(boolean certExtraI) {
        isCertExtraI = certExtraI;
    }

    @JsonProperty("cert_inc")
    public Boolean isCertInc() {
        return isCertInc;
    }

    public void setCertInc(boolean certInc) {
        isCertInc = certInc;
    }

    @JsonProperty("cert_inc_con")
    public Boolean isCertIncCon() {
        return isCertIncCon;
    }

    public void setCertIncCon(boolean certIncCon) {
        isCertIncCon = certIncCon;
    }

    @JsonProperty("cert_inc_con_last")
    public Boolean isCertIncConLast() {
        return isCertIncConLast;
    }

    public void setCertIncConLast(boolean certIncConLast) {
        isCertIncConLast = certIncConLast;
    }

    @JsonProperty("cert_isscap")
    public Boolean isCertIssCap() {
        return isCertIssCap;
    }

    public void setCertIssCap(boolean certIssCap) {
        isCertIssCap = certIssCap;
    }

    @JsonProperty("cert_mem")
    public Boolean isCertMem() {
        return isCertMem;
    }

    public void setCertMem(boolean certMem) {
        isCertMem = certMem;
    }

    @JsonProperty("cert_mortdoc")
    public Boolean isCertMortDoc() {
        return isCertMortDoc;
    }

    public void setCertMortDoc(boolean certMortDoc) {
        isCertMortDoc = certMortDoc;
    }

    @JsonProperty("cert_nomc")
    public Boolean isCertNomc() {
        return isCertNomc;
    }

    public void setCertNomc(boolean certNomc) {
        isCertNomc = certNomc;
    }

    @JsonProperty("cert_nonexis")
    public Boolean isCertNonExis() {
        return isCertNonExis;
    }

    public void setCertNonExis(boolean certNonExis) {
        isCertNonExis = certNonExis;
    }

    @JsonProperty("cert_only")
    public Boolean isCertOnly() {
        return isCertOnly;
    }

    public void setCertOnly(boolean certOnly) {
        isCertOnly = certOnly;
    }

    @JsonProperty("cert_other_c")
    public Boolean isCertOtherC() {
        return isCertOtherC;
    }

    public void setCertOtherC(boolean certOtherC) {
        isCertOtherC = certOtherC;
    }

    @JsonProperty("cert_ret")
    public Boolean isCertRet() {
        return isCertRet;
    }

    public void setCertRet(boolean certRet) {
        isCertRet = certRet;
    }

    @JsonProperty("cert_roc")
    public Boolean isCertRoc() {
        return isCertRoc;
    }

    public void setCertRoc(boolean certRoc) {
        isCertRoc = certRoc;
    }

    @JsonProperty("cert_sec")
    public Boolean isCertSec() {
        return isCertSec;
    }

    public void setCertSec(boolean certSec) {
        isCertSec = certSec;
    }

    @JsonProperty("cert_shar")
    public Boolean isCertShar() {
        return isCertShar;
    }

    public void setCertShar(boolean certShar) {
        isCertShar = certShar;
    }

    public DeliveryTimescale getDeliveryTimescale() {
        return deliveryTimescale;
    }

    public void setDeliveryTimescale(DeliveryTimescale deliveryTimescale) {
        this.deliveryTimescale = deliveryTimescale;
    }

    @Override
    public String toString() { return new Gson().toJson(this); }

}
