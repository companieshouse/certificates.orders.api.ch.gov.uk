package uk.gov.companieshouse.certificates.orders.api.validator;

import uk.gov.companieshouse.api.error.ApiError;
import uk.gov.companieshouse.certificates.orders.api.config.FeatureOptions;
import uk.gov.companieshouse.certificates.orders.api.controller.ApiErrors;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

class OptionsValidationHelper {
    private final List<ApiError> errors = new ArrayList<>();
    private final CertificateItemOptions options;
    private final FeatureOptions featureOptions;
    private final CompanyStatus companyStatus;

    OptionsValidationHelper(RequestValidatable requestValidatable, FeatureOptions featureOptions) {
        this.featureOptions = featureOptions;
        this.options = requestValidatable.itemOptions();
        this.companyStatus = CompanyStatus.getEnumValue(this.options.getCompanyStatus());
    }

    String getCompanyType() {
        return options.getCompanyType();
    }

    void validateLimitedCompanyOptions() {
        notLPDetails();
        notLLPDetails();
        verifyCompanyStatus();
    }

    void validateLimitedLiabilityPartnershipOptions() {
        notLimitedCompanyDetails();
        notLPDetails();
        verifyCompanyStatus();
    }

    void validateLimitedPartnershipOptions() {
        notDirectorsDetails();
        notSecretaryDetails();
        notDesignatedMemberDetails();
        notMemberDetails();
        notRegisteredOfficeAddressDetails();
        if (options.getLiquidatorsDetails() != null) {
            if (featureOptions.liquidatedCompanyCertificateEnabled()) {
                ApiErrors.raiseError(errors, ApiErrors.ERR_LIQUIDATORS_DETAILS_SUPPLIED,
                        String.format("include_liquidators_details: must not exist when company type is %s",
                        options.getCompanyType()));
            } else {
                ApiErrors.raiseError(errors, ApiErrors.ERR_LIQUIDATORS_DETAILS_SUPPLIED,
                        "include_liquidators_details: must not exist");
            }
        }

        if (options.getAdministratorsDetails() != null) {
            if (featureOptions.administratorCompanyCertificateEnabled()) {
                ApiErrors.raiseError(errors,  ApiErrors.ERR_ADMINISTRATORS_DETAILS_SUPPLIED,
                        String.format("include_administrators_details: must not exist when company type is %s",
                        options.getCompanyType()));

            } else {
                ApiErrors.raiseError(errors,  ApiErrors.ERR_ADMINISTRATORS_DETAILS_SUPPLIED,
                        "include_administrators_details: must not exist");
            }
        }

        if (CompanyStatus.ACTIVE != companyStatus) {
            ApiErrors.raiseError(errors, ApiErrors.ERR_COMPANY_STATUS_INVALID,
                    String.format("company_status: %s not valid for company type %s",
                            companyStatus.getStatusName(), options.getCompanyType()));
        }
    }

    boolean companyTypeIsNull() {
        if (isNull(options.getCompanyType())) {
            ApiErrors.raiseError(errors, ApiErrors.ERR_COMPANY_TYPE_REQUIRED, "company type: is a mandatory field");
            return true;
        }
        return false;
    }

    List<ApiError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    private void notLPDetails() {
        notPrincipalPlaceOfBusinessDetails();
        notGeneralPartnerDetails();
        notLimitedPartnerDetails();
        notIncludeGeneralNatureOfBusinessInformation();
    }

    private void notLLPDetails() {
        notDesignatedMemberDetails();
        notMemberDetails();
    }

    private void notLimitedCompanyDetails() {
        notDirectorsDetails();
        notSecretaryDetails();
    }

    private void verifyCompanyStatus() {
        validateDissolvedCompany();
        validateGoodStanding();
        if (featureOptions.liquidatedCompanyCertificateEnabled()) {
            validateLiquidatorsDetails();
        } else if (options.getLiquidatorsDetails() != null) {
            ApiErrors.raiseError(errors, ApiErrors.ERR_LIQUIDATORS_DETAILS_SUPPLIED,
                    "include_liquidators_details: must not exist");
        }
        if (featureOptions.administratorCompanyCertificateEnabled()) {
            validateAdministratorsDetails();
        } else if (options.getAdministratorsDetails() != null) {
            ApiErrors.raiseError(errors, ApiErrors.ERR_ADMINISTRATORS_DETAILS_SUPPLIED,
                    "include_administrators_details: must not exist");
        }
    }

    private void validateDissolvedCompany() {
        if (CompanyStatus.DISSOLVED == companyStatus) {
            validateObjectIsNull(options.getIncludeCompanyObjectsInformation(), ApiErrors.ERR_INCLUDE_COMPANY_OBJECTS_INFORMATION_SUPPLIED, "include_company_objects_information: must not exist when company status is dissolved");
            validateObjectIsNull(options.getIncludeGeneralNatureOfBusinessInformation(), ApiErrors.ERR_INCLUDE_GENERAL_NATURE_OF_BUSINESS_INFORMATION_SUPPLIED, "include_general_nature_of_business_information: must not exist when company status is dissolved");
            validateObjectIsNull(options.getRegisteredOfficeAddressDetails(), ApiErrors.ERR_REGISTERED_OFFICE_ADDRESS_DETAILS_SUPPLIED, "include_registered_office_address_details: must not exist when company status is dissolved");
            validateObjectIsNull(options.getSecretaryDetails(), ApiErrors.ERR_SECRETARY_DETAILS_SUPPLIED, "include_secretary_details: must not exist when company status is dissolved");
            validateObjectIsNull(options.getDirectorDetails(), ApiErrors.ERR_DIRECTOR_DETAILS_SUPPLIED, "include_director_details: must not exist when company status is dissolved");
            validateObjectIsNull(options.getDesignatedMemberDetails(), ApiErrors.ERR_DESIGNATED_MEMBERS_DETAILS_SUPPLIED, "include_designated_member_details: must not exist when company status is dissolved");
            validateObjectIsNull(options.getMemberDetails(), ApiErrors.ERR_MEMBERS_DETAILS_SUPPLIED, "include_member_details: must not exist when company status is dissolved");
            validateObjectIsNull(options.getGeneralPartnerDetails(), ApiErrors.ERR_GENERAL_PARTNER_DETAILS_SUPPLIED, "include_general_partner_details: must not exist when company status is dissolved");
            validateObjectIsNull(options.getLimitedPartnerDetails(), ApiErrors.ERR_LIMITED_PARTNER_DETAILS_SUPPLIED, "include_limited_partner_details: must not exist when company status is dissolved");
            validateObjectIsNull(options.getPrincipalPlaceOfBusinessDetails(), ApiErrors.ERR_PRINCIPAL_PLACE_OF_BUSINESS_DETAILS_SUPPLIED, "include_principal_place_of_business_details: must not exist when company status is dissolved");
        }
    }

    private void validateObjectIsNull(Object object, ApiError failureError, String errorMessage) {
        if (nonNull(object)) {
            ApiErrors.raiseError(errors, failureError, errorMessage);
        }
    }

    private void validateGoodStanding() {
        if (CompanyStatus.ACTIVE != companyStatus && Boolean.TRUE == options.getIncludeGoodStandingInformation()) {
            ApiErrors.raiseError(errors, ApiErrors.ERR_INCLUDE_GOOD_STANDING_INFORMATION_SUPPLIED,
                    "include_good_standing_information: must not exist when company status is %s",
                            companyStatus);
        }
    }

    private void validateLiquidatorsDetails() {
        if (CompanyStatus.LIQUIDATION != companyStatus && options.getLiquidatorsDetails() != null) {
            ApiErrors.raiseError(errors, ApiErrors.ERR_LIQUIDATORS_DETAILS_SUPPLIED,
                    "include_liquidators_details: must not exist when company status is %s",
                            companyStatus);
        }
    }

    private void validateAdministratorsDetails() {
        if (CompanyStatus.ADMINISTRATION != companyStatus && options.getAdministratorsDetails() != null) {
            ApiErrors.raiseError(errors, ApiErrors.ERR_ADMINISTRATORS_DETAILS_SUPPLIED,
                    "include_administrators_details: must not exist when company status is %s",
                            companyStatus);
        }
    }

    private void notDirectorsDetails() {
        if (!isNull(options.getDirectorDetails())) {
            ApiErrors.raiseError(errors, ApiErrors.ERR_DIRECTOR_DETAILS_SUPPLIED,
                    "include_director_details: must not exist when company type is %s",
                            options.getCompanyType());
        }
    }

    private void notSecretaryDetails() {
        if (nonNull(options.getSecretaryDetails())) {
            ApiErrors.raiseError(errors, ApiErrors.ERR_SECRETARY_DETAILS_SUPPLIED,
                    "include_secretary_details: must not exist when company type is %s",
                            options.getCompanyType());
        }
    }

    private void notDesignatedMemberDetails() {
        if (nonNull(options.getDesignatedMemberDetails())) {
            ApiErrors.raiseError(errors, ApiErrors.ERR_DESIGNATED_MEMBERS_DETAILS_SUPPLIED,
                    "include_designated_member_details: must not exist when company type is %s",
                    options.getCompanyType());
        }
    }

    private void notMemberDetails() {
        if (nonNull(options.getDesignatedMemberDetails())) {
            ApiErrors.raiseError(errors, ApiErrors.ERR_MEMBERS_DETAILS_SUPPLIED,
                    "include_member_details: must not exist when company type is %s",
                    options.getCompanyType());
        }
    }

    private void notGeneralPartnerDetails() {
        if (nonNull(options.getGeneralPartnerDetails())) {
            ApiErrors.raiseError(errors, ApiErrors.ERR_GENERAL_PARTNER_DETAILS_SUPPLIED,
                    "include_general_partner_details: must not exist when company type is %s",
                    options.getCompanyType());
        }
    }

    private void notLimitedPartnerDetails() {
        if (nonNull(options.getLimitedPartnerDetails())) {
            ApiErrors.raiseError(errors, ApiErrors.ERR_LIMITED_PARTNER_DETAILS_SUPPLIED,
                    "include_limited_partner_details: must not exist when company type is %s",
                    options.getCompanyType());
        }
    }

    private void notRegisteredOfficeAddressDetails() {
        if (nonNull(options.getRegisteredOfficeAddressDetails())) {
            ApiErrors.raiseError(errors,
                    ApiErrors.ERR_REGISTERED_OFFICE_ADDRESS_DETAILS_SUPPLIED,
                    "include_registered_office_address_details: must not exist when company type is %s",
                    options.getCompanyType());
        }
    }

    private void notPrincipalPlaceOfBusinessDetails() {
        if (nonNull(options.getPrincipalPlaceOfBusinessDetails())) {
            ApiErrors.raiseError(errors, ApiErrors.ERR_PRINCIPAL_PLACE_OF_BUSINESS_DETAILS_SUPPLIED,
                    "include_principal_place_of_business_details: must not exist when company type is %s",
                    options.getCompanyType());
        }
    }

    private void notIncludeGeneralNatureOfBusinessInformation() {
        if (nonNull(options.getIncludeGeneralNatureOfBusinessInformation())) {
            ApiErrors.raiseError(errors, ApiErrors.ERR_INCLUDE_GENERAL_NATURE_OF_BUSINESS_INFORMATION_SUPPLIED,
                    "include_general_nature_of_business_information: must not exist when company type is %s",
                    options.getCompanyType());

        }
    }
}
