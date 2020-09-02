package uk.gov.companieshouse.certificates.orders.api.environment;

public enum RequiredEnvironmentVariables {

    ITEMS_DATABASE("ITEMS_DATABASE"),
    MONGODB_URL("MONGODB_URL"),
    CHS_API_KEY("CHS_API_KEY"),
    API_URL("API_URL");

    private String name;
    
    RequiredEnvironmentVariables(String name){
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

}
