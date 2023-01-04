#!/bin/bash
set -euxo pipefail

mvn -ntp -Dhttp.keepAlive=false \
    -Dmaven.wagon.http.pool=false \
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=120 \
    -pl system -q clean package liberty:create liberty:install-feature liberty:deploy

mvn -ntp -Dhttp.keepAlive=false \
    -Dmaven.wagon.http.pool=false \
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=120 \
    -pl query -q clean package liberty:create liberty:install-feature liberty:deploy

# Testing the testing environment

mvn -pl system -ntp -P testing liberty:start
mvn -pl query -ntp liberty:start

mvn -ntp -Dhttp.keepAlive=false \
    -Dmaven.wagon.http.pool=false \
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=120 \
    -pl query failsafe:integration-test

mvn -pl system -P testing failsafe:integration-test
mvn -pl query -ntp failsafe:verify

mvn -pl system -ntp liberty:stop
mvn -pl query -ntp liberty:stop

# Testing the development environment

mvn -pl system -ntp -P development liberty:start 
mvn -pl query -ntp liberty:start -Dliberty.var.mp.config.profile="development"

mvn -ntp Dhttp.keepAlive=false \
    -Dmaven.wagon.http.pool=false \
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=120 \
    -Dliberty.var.mp.config.profile="development" \
    -pl query failsafe:integration-test

mvn -pl system -P development failsafe:integration-test
mvn -pl query -ntp failsafe:verify

mvn -pl system -ntp liberty:stop
mvn -pl query -ntp liberty:stop

# Testing the production environment

mvn -pl system -ntp -P production liberty:start 
mvn -pl query -ntp liberty:start -Dliberty.var.mp.config.profile="production"

mvn -ntp Dhttp.keepAlive=false \
    -Dmaven.wagon.http.pool=false \
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=120 \
    -Dliberty.var.mp.config.profile="production" \
    -pl query failsafe:integration-test

mvn -pl system -P production failsafe:integration-test
mvn -pl query -ntp failsafe:verify

mvn -pl system -ntp liberty:stop
mvn -pl query -ntp liberty:stop