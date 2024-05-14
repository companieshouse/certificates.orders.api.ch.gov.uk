#!/bin/bash
#
# Start script for certificates-orders-api

PORT=8080

exec java -jar -Dserver.port="${PORT}" "certificates.orders.api.ch.gov.uk.jar"
