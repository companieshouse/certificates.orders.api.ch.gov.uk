package uk.gov.companieshouse.certificates.orders.api.validator;

import uk.gov.companieshouse.api.error.ApiError;
import uk.gov.companieshouse.certificates.orders.api.config.FeatureOptions;
import uk.gov.companieshouse.certificates.orders.api.controller.ApiErrors;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.isNull;

class OptionsValidationHelper {
    private final RequestValidatable requestValidatable;
    private final List<ApiError> errors = new ArrayList<>();
    private final CertificateItemOptions options;
    private final FeatureOptions featureOptions;

    OptionsValidationHelper(RequestValidatable requestValidatable, FeatureOptions featureOptions) {
        this.requestValidatable = requestValidatable;
        this.featureOptions = featureOptions;
        this.options = requestValidatable.getItemOptions();
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
        notLimitedCompanyDetails();
        notLLPDetails();
        if (options.getLiquidatorsDetails() != null) {
            if (featureOptions.isLiquidatedCompanyCertificateEnabled()) {
                ApiErrors.raiseError(errors, ApiErrors.ERR_LIQUIDATORS_DETAILS_SUPPLIED,
                        String.format("include_liquidators_details: must not exist when company type is %s",
                        options.getCompanyType()));
            } else {
                ApiErrors.raiseError(errors, ApiErrors.ERR_LIQUIDATORS_DETAILS_SUPPLIED,
                        "include_liquidators_details: must not exist");
            }
        }

        if (options.getAdministratorsDetails() != null) {
            if (featureOptions.isAdministratorCompanyCertificateEnabled()) {
                ApiErrors.raiseError(errors,  ApiErrors.ERR_ADMINISTRATORS_DETAILS_SUPPLIED,
                        String.format("include_administrators_details: must not exist when company type is %s",
                        options.getCompanyType()));

            } else {
                ApiErrors.raiseError(errors,  ApiErrors.ERR_ADMINISTRATORS_DETAILS_SUPPLIED,
                        "include_administrators_details: must not exist");
            }
        }

        if (CompanyStatus.ACTIVE != requestValidatable.getCompanyStatus()) {
            ApiErrors.raiseError(errors, ApiErrors.ERR_COMPANY_STATUS_INVALID,
                    String.format("company_status: %s not valid for company type %s",
                            requestValidatable.getCompanyStatus().toString(), options.getCompanyType()));
        }
    }

    boolean notCompanyTypeIsNull() {
        if (!isNull(options.getCompanyType())) {
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
        validateGoodStanding();
        if (featureOptions.isLiquidatedCompanyCertificateEnabled()) {
            validateLiquidatorsDetails();
        } else if (options.getLiquidatorsDetails() != null) {
            ApiErrors.raiseError(errors, ApiErrors.ERR_LIQUIDATORS_DETAILS_SUPPLIED,
                    "include_liquidators_details: must not exist");
        }
        if (featureOptions.isAdministratorCompanyCertificateEnabled()) {
            validateAdministratorsDetails();
        } else if (options.getAdministratorsDetails() != null) {
            ApiErrors.raiseError(errors, ApiErrors.ERR_ADMINISTRATORS_DETAILS_SUPPLIED,
                    "include_administrators_details: must not exist");
        }
    }

    private void validateGoodStanding() {
        if (CompanyStatus.ACTIVE != requestValidatable.getCompanyStatus() && Boolean.TRUE == options.getIncludeGoodStandingInformation()) {
            ApiErrors.raiseError(errors, ApiErrors.ERR_INCLUDE_GOOD_STANDING_INFORMATION_SUPPLIED,
                    "include_good_standing_information: must not exist when company status is %s",
                            requestValidatable.getCompanyStatus());
        }
    }

    private void validateLiquidatorsDetails() {
        if (CompanyStatus.LIQUIDATION != requestValidatable.getCompanyStatus() && options.getLiquidatorsDetails() != null) {
            ApiErrors.raiseError(errors, ApiErrors.ERR_LIQUIDATORS_DETAILS_SUPPLIED,
                    "include_liquidators_details: must not exist when company status is %s",
                            requestValidatable.getCompanyStatus());
        }
    }

    private void validateAdministratorsDetails() {
        if (CompanyStatus.ADMINISTRATION != requestValidatable.getCompanyStatus() && options.getAdministratorsDetails() != null) {
            ApiErrors.raiseError(errors, ApiErrors.ERR_ADMINISTRATORS_DETAILS_SUPPLIED,
                    "include_administrators_details: must not exist when company status is %s",
                            requestValidatable.getCompanyStatus());
        }
    }

    private void notDirectorsDetails() {
        if (isNull(options.getDirectorDetails())) {
            ApiErrors.raiseError(errors, ApiErrors.ERR_DIRECTOR_DETAILS_SUPPLIED,
                    "include_director_details: must not exist when company type is %s",
                            options.getCompanyType());
        }
    }

    private void notSecretaryDetails() {
        if (isNull(options.getSecretaryDetails())) {
            ApiErrors.raiseError(errors, ApiErrors.ERR_SECRETARY_DETAILS_SUPPLIED,
                    "include_secretary_details: must not exist when company type is %s",
                            options.getCompanyType());
        }
    }

    private void notDesignatedMemberDetails() {
        if (isNull(options.getDesignatedMemberDetails())) {
            ApiErrors.raiseError(errors, ApiErrors.ERR_DESIGNATED_MEMBERS_DETAILS_SUPPLIED,
                    "include_designated_member_details: must not exist when company type is %s",
                    options.getCompanyType());
        }
    }

    private void notMemberDetails() {
        if (isNull(options.getDesignatedMemberDetails())) {
            ApiErrors.raiseError(errors, ApiErrors.ERR_MEMBERS_DETAILS_SUPPLIED,
                    "include_member_details: must not exist when company type is %s",
                    options.getCompanyType());
        }
    }

    private void notGeneralPartnerDetails() {
        if (isNull(options.getGeneralPartnerDetails())) {
            ApiErrors.raiseError(errors, ApiErrors.ERR_GENERAL_PARTNER_DETAILS_SUPPLIED,
                    "include_general_partner_details: must not exist when company type is %s",
                    options.getCompanyType());
        }
    }

    private void notLimitedPartnerDetails() {
        if (isNull(options.getLimitedPartnerDetails())) {
            ApiErrors.raiseError(errors, ApiErrors.ERR_LIMITED_PARTNER_DETAILS_SUPPLIED,
                    "include_limited_partner_details: must not exist when company type is %s",
                    options.getCompanyType());
        }
    }

    private void notPrincipalPlaceOfBusinessDetails() {
        if (isNull(options.getPrincipalPlaceOfBusinessDetails())) {
            ApiErrors.raiseError(errors, ApiErrors.ERR_PRINCIPAL_PLACE_OF_BUSINESS_DETAILS_SUPPLIED,
                    "include_principal_place_of_business_details: must not exist when company type is %s",
                    options.getCompanyType());
        }
    }

    private void notIncludeGeneralNatureOfBusinessInformation() {
        if (isNull(options.getIncludeGeneralNatureOfBusinessInformation())) {
            ApiErrors.raiseError(errors, ApiErrors.ERR_INCLUDE_GENERAL_NATURE_OF_BUSINESS_INFORMATION_SUPPLIED,
                    "include_general_nature_of_business_information: must not exist when company type is %s",
                    options.getCompanyType());

        }
    }
}
