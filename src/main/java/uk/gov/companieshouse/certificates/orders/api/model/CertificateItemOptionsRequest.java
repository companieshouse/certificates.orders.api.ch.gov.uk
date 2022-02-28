package uk.gov.companieshouse.certificates.orders.api.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.gson.Gson;

import java.util.Objects;

/**
 * An instance of this represents the item options for a certificate item.
 */
@JsonPropertyOrder(alphabetic = true)
public class CertificateItemOptionsRequest {

    private CollectionLocation collectionLocation;

    private String contactNumber;

    private DeliveryMethod deliveryMethod;

    private DeliveryTimescale deliveryTimescale;

    private DirectorOrSecretaryDetails directorDetails;

    private String forename;

    private Boolean includeCompanyObjectsInformation;

    private Boolean includeEmailCopy;

    private Boolean includeGoodStandingInformation;

    private RegisteredOfficeAddressDetails registeredOfficeAddressDetails;

    private DirectorOrSecretaryDetails secretaryDetails;

    private String surname;

    private DesignatedMemberDetails designatedMemberDetails;

    private MemberDetails memberDetails;

    private GeneralPartnerDetails generalPartnerDetails;

    private LimitedPartnerDetails limitedPartnerDetails;

    private PrincipalPlaceOfBusinessDetails principalPlaceOfBusinessDetails;

    private Boolean includeGeneralNatureOfBusinessInformation;

    private LiquidatorsDetails liquidatorsDetails;

    private AdministratorsDetails administratorsDetails;

    public CollectionLocation getCollectionLocation() {
        return collectionLocation;
    }

    public void setCollectionLocation(CollectionLocation collectionLocation) {
        this.collectionLocation = collectionLocation;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public DeliveryMethod getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(DeliveryMethod deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public DeliveryTimescale getDeliveryTimescale() {
        return deliveryTimescale;
    }

    public void setDeliveryTimescale(DeliveryTimescale deliveryTimescale) {
        this.deliveryTimescale = deliveryTimescale;
    }

    public DirectorOrSecretaryDetails getDirectorDetails() {
        return directorDetails;
    }

    public void setDirectorDetails(DirectorOrSecretaryDetails directorOrSecretaryDetails) {
        this.directorDetails = directorOrSecretaryDetails;
    }

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public Boolean getIncludeCompanyObjectsInformation() {
        return includeCompanyObjectsInformation;
    }

    public void setIncludeCompanyObjectsInformation(Boolean includeCompanyObjectsInformation) {
        this.includeCompanyObjectsInformation = includeCompanyObjectsInformation;
    }

    public Boolean getIncludeEmailCopy() {
        return includeEmailCopy;
    }

    public void setIncludeEmailCopy(Boolean includeEmailCopy) {
        this.includeEmailCopy = includeEmailCopy;
    }

    public Boolean getIncludeGoodStandingInformation() {
        return includeGoodStandingInformation;
    }

    public void setIncludeGoodStandingInformation(Boolean includeGoodStandingInformation) {
        this.includeGoodStandingInformation = includeGoodStandingInformation;
    }

    public RegisteredOfficeAddressDetails getRegisteredOfficeAddressDetails() {
        return registeredOfficeAddressDetails;
    }

    public void setRegisteredOfficeAddressDetails(RegisteredOfficeAddressDetails registeredOfficeAddressDetails) {
        this.registeredOfficeAddressDetails = registeredOfficeAddressDetails;
    }

    public DirectorOrSecretaryDetails getSecretaryDetails() {
        return secretaryDetails;
    }

    public void setSecretaryDetails(DirectorOrSecretaryDetails secretaryDetails) {
        this.secretaryDetails = secretaryDetails;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public DesignatedMemberDetails getDesignatedMemberDetails() {
        return designatedMemberDetails;
    }

    public void setDesignatedMemberDetails(DesignatedMemberDetails designatedMemberDetails) {
        this.designatedMemberDetails = designatedMemberDetails;
    }

    public MemberDetails getMemberDetails() {
        return memberDetails;
    }

    public void setMemberDetails(MemberDetails memberDetails) {
        this.memberDetails = memberDetails;
    }

    public GeneralPartnerDetails getGeneralPartnerDetails() {
        return generalPartnerDetails;
    }

    public void setGeneralPartnerDetails(GeneralPartnerDetails generalPartnerDetails) {
        this.generalPartnerDetails = generalPartnerDetails;
    }

    public LimitedPartnerDetails getLimitedPartnerDetails() {
        return limitedPartnerDetails;
    }

    public void setLimitedPartnerDetails(LimitedPartnerDetails limitedPartnerDetails) {
        this.limitedPartnerDetails = limitedPartnerDetails;
    }

    public PrincipalPlaceOfBusinessDetails getPrincipalPlaceOfBusinessDetails() {
        return principalPlaceOfBusinessDetails;
    }

    public void setPrincipalPlaceOfBusinessDetails(PrincipalPlaceOfBusinessDetails principalPlaceOfBusinessDetails) {
        this.principalPlaceOfBusinessDetails = principalPlaceOfBusinessDetails;
    }

    public Boolean getIncludeGeneralNatureOfBusinessInformation() {
        return includeGeneralNatureOfBusinessInformation;
    }

    public void setIncludeGeneralNatureOfBusinessInformation(Boolean includeGeneralNatureOfBusinessInformation) {
        this.includeGeneralNatureOfBusinessInformation = includeGeneralNatureOfBusinessInformation;
    }

    public LiquidatorsDetails getLiquidatorsDetails() {
        return liquidatorsDetails;
    }

    public void setLiquidatorsDetails(LiquidatorsDetails liquidatorsDetails) {
        this.liquidatorsDetails = liquidatorsDetails;
    }

    public AdministratorsDetails getAdministratorsDetails() {
        return administratorsDetails;
    }

    public void setAdministratorsDetails(AdministratorsDetails administratorsDetails) {
        this.administratorsDetails = administratorsDetails;
    }

    @Override
    public String toString() { return new Gson().toJson(this); }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CertificateItemOptionsRequest that = (CertificateItemOptionsRequest) o;
        return collectionLocation == that.collectionLocation &&
                Objects.equals(contactNumber, that.contactNumber) &&
                deliveryMethod == that.deliveryMethod &&
                deliveryTimescale == that.deliveryTimescale &&
                Objects.equals(directorDetails, that.directorDetails) &&
                Objects.equals(forename, that.forename) &&
                Objects.equals(includeCompanyObjectsInformation,
                        that.includeCompanyObjectsInformation) &&
                Objects.equals(includeEmailCopy, that.includeEmailCopy) &&
                Objects.equals(includeGoodStandingInformation,
                        that.includeGoodStandingInformation) &&
                Objects.equals(registeredOfficeAddressDetails,
                        that.registeredOfficeAddressDetails) &&
                Objects.equals(secretaryDetails, that.secretaryDetails) &&
                Objects.equals(surname, that.surname) &&
                Objects.equals(designatedMemberDetails, that.designatedMemberDetails) &&
                Objects.equals(memberDetails, that.memberDetails) &&
                Objects.equals(generalPartnerDetails, that.generalPartnerDetails) &&
                Objects.equals(limitedPartnerDetails, that.limitedPartnerDetails) &&
                Objects.equals(principalPlaceOfBusinessDetails,
                        that.principalPlaceOfBusinessDetails) &&
                Objects.equals(includeGeneralNatureOfBusinessInformation,
                        that.includeGeneralNatureOfBusinessInformation) &&
                Objects.equals(liquidatorsDetails, that.liquidatorsDetails) &&
                Objects.equals(administratorsDetails, that.administratorsDetails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(collectionLocation, contactNumber, deliveryMethod,
                deliveryTimescale, directorDetails, forename, includeCompanyObjectsInformation,
                includeEmailCopy, includeGoodStandingInformation, registeredOfficeAddressDetails,
                secretaryDetails, surname, designatedMemberDetails, memberDetails,
                generalPartnerDetails, limitedPartnerDetails, principalPlaceOfBusinessDetails,
                includeGeneralNatureOfBusinessInformation, liquidatorsDetails, administratorsDetails);
    }
}
