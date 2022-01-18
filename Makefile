SHELL := $(shell which bash)
.DEFAULT_GOAL := help

help: ## This help.
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)

MVN_EXEC=$(shell which mvn || echo ${M2_HOME}'/bin/mvn')

SOURCES_DIRECTORY?=./
MVN_OPTIONS?=-f $(SOURCES_DIRECTORY)pom.xml

test:
	$(MVN_EXEC) clean gatling:test -Dgatling.simulationClass=com.robinbobin.tests.testsdemo.TestsDemo -Drps=100 -Dtime=300

