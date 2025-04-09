# certificates.orders.api.ch.gov.uk
* CHS API handling CRUD operations on several item kinds for the CH Ordering Service. This 
API will provide ordering services for Certificates.
* For Certificates, the API is the back end service directly driven by the `certificates.orders.web.ch.gov.uk` 
web application.
 
## MVP
 
* The MVP version of this API will be limited to Certificates.

## Building the API

Issue the following command from the project directory:

```
make dev
```

## Configuring the API

Variable                          | Default                                                              | Description
--------------------------------- | -------------------------------------------------------------------- | -----------------------------------
MONGODB_HOST                      | chs-mongo (in Vagrant) / localhost                                   | Mongo database host.
MONGODB_PORT                      | Environment specific port / 27017                                    | Mongo database port.
ITEMS_DATABASE                    | items                                                                | MongoDB Items database name.
MONGODB_URL                       | $MONGODB_URL/$ITEMS_DATABASE                                         | Mongo database URL.
CERTIFICATES_ORDERS_API_CH_GOV_UK_PORT   | 10020 (in Vagrant)                                                   | API port.
CERTIFICATES_ORDERS_API_CH_GOV_UK_URL    | http://${API_DOMAIN}:${CERTIFICATES_ORDERS_API_CH_GOV_UK_PORT}              | API URL.
API_URL                           | -                                                                    | Base URL for requests to internal APIs.
CHS_API_KEY                       | -                                                                    | Key identifying this client for requests to internal APIs.

## Running the API

In Vagrant, use this command:

```
ubic start chs.orders.certificates-orders-api
```

## Testing the API 

### Automated Testing

There is a suite of unit and integration tests within the project. These can be executed with the following command line:

```
mvn test
```

Alternatively, the tests can be executed from within an IDE such as Idea.

### Manual Testing - MVP

A Postman collection has been created for this API. It may be imported into Postman from:

```
src/test/postman/Certificates_API.postman_collection.json
``` 

## Terraform ECS

### What does this code do?

The code present in this repository is used to define and deploy a dockerised container in AWS ECS.
This is done by calling a [module](https://github.com/companieshouse/terraform-modules/tree/main/aws/ecs) from terraform-modules. Application specific attributes are injected and the service is then deployed using Terraform via the CICD platform 'Concourse'.


Application specific attributes | Value                                | Description
:---------|:-----------------------------------------------------------------------------|:-----------
**ECS Cluster**        | order-service                                     | ECS cluster (stack) the service belongs to
**Load balancer**      | {env}-chs-apichgovuk <br> {env}-chs-apichgovuk-private                                 | The load balancer that sits in front of the service
**Concourse pipeline**     |[Pipeline link](https://ci-platform.companieshouse.gov.uk/teams/team-development/pipelines/certificates.orders.api.ch.gov.uk) <br> [Pipeline code](https://github.com/companieshouse/ci-pipelines/blob/master/pipelines/ssplatform/team-development/certificates.orders.api.ch.gov.uk)                                  | Concourse pipeline link in shared services


### Contributing
- Please refer to the [ECS Development and Infrastructure Documentation](https://companieshouse.atlassian.net/wiki/spaces/DEVOPS/pages/4390649858/Copy+of+ECS+Development+and+Infrastructure+Documentation+Updated) for detailed information on the infrastructure being deployed.

### Testing
- Ensure the terraform runner local plan executes without issues. For information on terraform runners please see the [Terraform Runner Quickstart guide](https://companieshouse.atlassian.net/wiki/spaces/DEVOPS/pages/1694236886/Terraform+Runner+Quickstart).
- If you encounter any issues or have questions, reach out to the team on the **#platform** slack channel.

### Vault Configuration Updates
- Any secrets required for this service will be stored in Vault. For any updates to the Vault configuration, please consult with the **#platform** team and submit a workflow request.

### Useful Links
- [ECS service config dev repository](https://github.com/companieshouse/ecs-service-configs-dev)
- [ECS service config production repository](https://github.com/companieshouse/ecs-service-configs-production)