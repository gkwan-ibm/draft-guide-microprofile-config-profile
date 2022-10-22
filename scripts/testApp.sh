#!/bin/bash
set -euxo pipefail

mvn -Dhttp.keepAlive=false \
    -Dmaven.wagon.http.pool=false \
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=120 \
    -pl system -q clean package liberty:create liberty:install-feature liberty:deploy

mvn -Dhttp.keepAlive=false \
    -Dmaven.wagon.http.pool=false \
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=120 \
    -pl query -q clean package liberty:create liberty:install-feature liberty:deploy

# Testing testing environment

mvn -pl system -P testing liberty:start
mvn -pl query liberty:start

mvn -Dhttp.keepAlive=false \
    -Dmaven.wagon.http.pool=false \
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=120 \
    -pl query failsafe:integration-test

mvn -pl query failsafe:verify

mvn -pl system liberty:stop
mvn -pl query liberty:stop

# Testing development environment

mvn -pl system -P development liberty:start 
mvn -pl query liberty:start -Dliberty.var.mp.config.profile="development"

mvn -Dhttp.keepAlive=false \
    -Dmaven.wagon.http.pool=false \
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=120 \
    -Dliberty.var.mp.config.profile="development" \
    -pl query failsafe:integration-test

mvn -pl query failsafe:verify

mvn -pl system liberty:stop
mvn -pl query liberty:stop