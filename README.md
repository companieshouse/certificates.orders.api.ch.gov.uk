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


