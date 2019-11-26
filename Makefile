artifact_name       := items.orders.api.ch.gov.uk

.PHONY: all
all: build

.PHONY: clean
clean:
	mvn clean
	rm -f ./$(artifact_name).jar
	rm -f ./$(artifact_name)-*.zip
	rm -rf ./build-*

.PHONY: build
build:
	mvn compile

.PHONY: test
test: test-unit test-integration

.PHONY: test-unit
test-unit: clean
	mvn -Dtest=*UnitTest test

.PHONY: test-integration
test-integration: clean
	mvn -Dtest=*IntegrationTest test

.PHONY: test-contract-provider
test-contract-provider: clean
	mvn -Dtest=*ProviderContractTest test

.PHONY: test-contract-consumer
test-contract-consumer: clean
	mvn -Dtest=*ConsumerContractTest test

.PHONY: dev
dev: clean
	mvn package -DskipTests=true
	cp target/$(artifact_name)-unversioned.jar $(artifact_name).jar

.PHONY: package
package:
ifndef version
	$(error No version given. Aborting)
endif
	$(info Packaging version: $(version))
	mvn versions:set -DnewVersion=$(version) -DgenerateBackupPoms=false
	mvn package -DskipTests=true
	$(eval tmpdir:=$(shell mktemp -d build-XXXXXXXXXX))
	cp ./start.sh $(tmpdir)
	cp ./routes.yaml $(tmpdir)
	cp ./target/$(artifact_name)-$(version).jar $(tmpdir)/$(artifact_name).jar
	cd $(tmpdir); zip -r ../$(artifact_name)-$(version).zip *
	rm -rf $(tmpdir)

.PHONY: dist
dist: clean build package

.PHONY: sonar
sonar:
	mvn sonar:sonar

.PHONY: sonar-pr-analysis
sonar-pr-analysis:
	mvn sonar:sonar	-P sonar-pr-analysis

