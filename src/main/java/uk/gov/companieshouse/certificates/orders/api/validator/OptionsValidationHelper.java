package uk.gov.companieshouse.certificates.orders.api.validator;

import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class OptionsValidationHelper {
    private final CertificateItemOptions options;
    private final List<String> errors = new ArrayList<>();

    OptionsValidationHelper(CertificateItemOptions options) {
        this.options = options;
    }

    String getCompanyType() {
        return options.getCompanyType();
    }

    void validateLimitedCompanyOptions() {
        notLPDetails();
        notLLPDetails();
    }

    void validateLimitedLiabilityPartnershipOptions() {
        notLimitedCompanyDetails();
        notLPDetails();
    }

    void validateLimitedPartnershipOptions() {
        notLimitedCompanyDetails();
        notLLPDetails();
    }

    boolean notCompanyTypeIsNull() {
        return isNotNull(options.getCompanyType(), "company type: is a mandatory field");
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

    private void notDirectorsDetails() {
        isNull(options.getDirectorDetails(), "include_director_details: must not exist when company type is %s", options.getCompanyType());
    }

    private void notSecretaryDetails() {
        isNull(options.getSecretaryDetails(), "include_secretary_details: must not exist when company type is %s", options.getCompanyType());
    }

    private void notDesignatedMemberDetails() {
        isNull(options.getDesignatedMemberDetails(), "include_designated_member_details: must not exist when company type is %s", options.getCompanyType());
    }

    private void notMemberDetails() {
        isNull(options.getMemberDetails(), "include_member_details: must not exist when company type is %s", options.getCompanyType());
    }

    private void notGeneralPartnerDetails() {
        isNull(options.getGeneralPartnerDetails(), "include_general_partner_details: must not exist when company type is %s", options.getCompanyType());
    }

    private void notLimitedPartnerDetails() {
        isNull(options.getLimitedPartnerDetails(), "include_limited_partner_details: must not exist when company type is %s", options.getCompanyType());
    }

    private void notPrincipalPlaceOfBusinessDetails() {
        isNull(options.getPrincipalPlaceOfBusinessDetails(), "include_principal_place_of_business_details: must not exist when company type is %s", options.getCompanyType());
    }

    private void notIncludeGeneralNatureOfBusinessInformation() {
        isNull(options.getIncludeGeneralNatureOfBusinessInformation(), "include_general_nature_of_business_information: must not exist when company type is %s", options.getCompanyType());
    }

    private boolean isNull(Object object, String message, String... messageArgs) {
        if (object != null) {
            errors.add(String.format(message, messageArgs));
            return false;
        }
        return true;
    }

    private boolean isNotNull(Object object, String message, String... messageArgs) {
        if (object == null) {
            errors.add(String.format(message, messageArgs));
            return false;
        }
        return true;
    }
}
