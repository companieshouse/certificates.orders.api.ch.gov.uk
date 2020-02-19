# items.orders.api.ch.gov.uk
* CHS API handling CRUD operations on several item kinds for the CH Ordering Service. This 
API will provide ordering services for Certificates, Certified Copies and scanned document images
 (aka SCUD).
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
ITEMS_ORDERS_API_CH_GOV_UK_PORT   | 10020 (in Vagrant)                                                   | API port.
ITEMS_ORDERS_API_CH_GOV_UK_URL    | http://${API_DOMAIN}:${ITEMS_ORDERS_API_CH_GOV_UK_PORT}              | API URL.

## Running the API

In Vagrant, use this command:

```
ubic start chs.orders.items-orders-api
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
src/test/postman/Items_API.postman_collection.json
```

The Postman collection facilitates the manual testing of all of the 
[Certificates API methods](https://developer-specs.kermit.aws.chdev.org/order-company-products/reference/certificates).

It can be configured to work against a deployment of the API to a local developer Vagrant environment or to an AWS 
environment as described in 
[Using Postman](https://companieshouse.atlassian.net/wiki/spaces/~229231946/pages/1317339144/Using+Postman+Collections+Variables+and+Environments).
 


