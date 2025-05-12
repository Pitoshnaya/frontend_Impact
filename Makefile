GRADLEW = $(shell if [ "$(OS)" = "Windows_NT" ]; then echo gradlew.bat; else echo ./gradlew; fi)

.PHONY: analyze
analyze:
	$(GRADLEW) ktlintCheck
	$(GRADLEW) detekt

.PHONY: start
start:
	$(GRADLEW) run
