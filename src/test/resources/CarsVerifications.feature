@CarsVerification
Feature: Compare cars details from files against website

    Scenario Outline: Compare car details
      Given I read registration details from <actualCarDetails> and verify match against <expectedCardetails>
      When I compare the details on cartaxcheck website
      Then The details should match

      Examples:
        | actualCarDetails                            | expectedCardetails                              |
        | src/main/resources/carsDataFiles/car_in.txt | src/main/resources/carsDataFiles/car_output.txt |
