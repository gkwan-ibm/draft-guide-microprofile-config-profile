mvn -pl system clean package liberty:create liberty:install-feature liberty:deploy
mvn -pl query clean package liberty:create liberty:install-feature liberty:deploy
mvn -pl system liberty:start
mvn -pl query liberty:start
mvn -pl system failsafe:integration-test
mvn -pl query failsafe:integration-test
mvn -pl query liberty:stop
mvn -pl system liberty:stop
