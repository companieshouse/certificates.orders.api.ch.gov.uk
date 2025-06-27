#!/bin/bash
#
# Start script for certificates-orders-api

PORT=8080

exec java -jar -Dserver.port="${PORT}" -XX:MaxRAMPercentage=80 "certificates.orders.api.ch.gov.uk.jar"
