Feature: Triangle
  Scenario Outline: Feature test triangle
    Given I have three number is <a>, <b> and <c>
    When It is three angle of triangle
    Then It should be <result>