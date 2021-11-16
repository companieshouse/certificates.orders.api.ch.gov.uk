package uk.gov.companieshouse.certificates.orders.api.validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptions;

class OptionsValidationHelper {
    private final RequestValidatable requestValidatable;
    private final List<String> errors = new ArrayList<>();
    private final CertificateItemOptions options;

    OptionsValidationHelper(RequestValidatable requestValidatable) {
        this.requestValidatable = requestValidatable;
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
            errors.add(String.format("include_liquidator_details: must not exist when "
                    + "company type is %s", options.getCompanyType()));
        }
        if (CompanyStatus.LIQUIDATION == requestValidatable.getCompanyStatus()) {
            errors.add(String.format("company_status: %s not valid for company type %s",
                    requestValidatable.getCompanyStatus().toString(), options.getCompanyType()));
        }
    }

    boolean notCompanyTypeIsNull() {
        return isNotNull(options.getCompanyType(), "company type: is a "
                + "mandatory field");
    }

    List<String> getErrors() {
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
        if (CompanyStatus.ACTIVE == requestValidatable.getCompanyStatus() &&
                options.getLiquidatorsDetails() != null) {
            errors.add(String.format("include_liquidator_details: must not exist when "
                    + "company status is %s", requestValidatable.getCompanyStatus()));
        }

        if (CompanyStatus.LIQUIDATION == requestValidatable.getCompanyStatus() &&
                Boolean.TRUE == options.getIncludeGoodStandingInformation()) {
            errors.add(String.format("include_good_standing_information: must not exist when "
                    + "company status is %s", requestValidatable.getCompanyStatus()));
        }
    }

    private void notDirectorsDetails() {
        isNull(options.getDirectorDetails(),
                "include_director_details: must not exist when company type is %s",
                options.getCompanyType());
    }

    private void notSecretaryDetails() {
        isNull(options.getSecretaryDetails(),
                "include_secretary_details: must not exist when company type is %s",
                options.getCompanyType());
    }

    private void notDesignatedMemberDetails() {
        isNull(options.getDesignatedMemberDetails(),
                "include_designated_member_details: must not exist when company type is %s",
                options.getCompanyType());
    }

    private void notMemberDetails() {
        isNull(options.getMemberDetails(),
                "include_member_details: must not exist when company type is %s",
                options.getCompanyType());
    }

    private void notGeneralPartnerDetails() {
        isNull(options.getGeneralPartnerDetails(),
                "include_general_partner_details: must not exist when company type is %s",
                options.getCompanyType());
    }

    private void notLimitedPartnerDetails() {
        isNull(options.getLimitedPartnerDetails(),
                "include_limited_partner_details: must not exist when company type is %s",
                options.getCompanyType());
    }

    private void notPrincipalPlaceOfBusinessDetails() {
        isNull(options.getPrincipalPlaceOfBusinessDetails(),
                "include_principal_place_of_business_details: must not exist when company type is"
                        + " %s",
                options.getCompanyType());
    }

    private void notIncludeGeneralNatureOfBusinessInformation() {
        isNull(options.getIncludeGeneralNatureOfBusinessInformation(),
                "include_general_nature_of_business_information: must not exist when company type"
                        + " is %s",
                options.getCompanyType());
    }

    private boolean isNull(Object object, String message, Object... messageArgs) {
        if (object != null) {
            errors.add(String.format(message, messageArgs));
            return false;
        }
        return true;
    }

    private boolean isNotNull(Object object, String message, Object... messageArgs) {
        if (object == null) {
            errors.add(String.format(message, messageArgs));
            return false;
        }
        return true;
    }
}
