# tag::build[]
mvn -pl system clean package liberty:create liberty:install-feature liberty:deploy
mvn -pl query clean package liberty:create liberty:install-feature liberty:deploy
# end::build[]
# tag::start[]
mvn -pl system liberty:start
mvn -pl query liberty:start
# end::start[]
# tag::test[]
mvn -pl system failsafe:integration-test
mvn -pl query failsafe:integration-test
# end::test[]
# tag::stop[]
mvn -pl query liberty:stop
mvn -pl system liberty:stop
# end::stop[]
