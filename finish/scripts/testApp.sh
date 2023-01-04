# tag::build[]
mvn -pl system -ntp clean package liberty:create liberty:install-feature liberty:deploy
mvn -pl query -ntp clean package liberty:create liberty:install-feature liberty:deploy
# end::build[]

# tag::start[]
mvn -pl system -ntp -P testing liberty:start
mvn -pl query -ntp -Dliberty.var.mp.config.profile="testing" liberty:start
# end::start[]

# tag::test[]
mvn -pl system -ntp -P testing failsafe:integration-test
mvn -pl query -ntp failsafe:integration-test
# end::test[]

# tag::stop[]
mvn -pl query -ntp liberty:stop
mvn -pl system -ntp liberty:stop
# end::stop[]
