# QA Automation Portfolio

![Tests](https://github.com/hshinwar/qa-automation-portfolio/actions/workflows/tests.yml/badge.svg)

A small test automation framework covering both UI and API testing, run automatically on every push with GitHub Actions.

## What it tests

| Layer | Target | Scenarios |
|-------|--------|-----------|
| UI | [saucedemo.com](https://www.saucedemo.com) | Valid login; 4 negative login cases (locked-out user, wrong password, missing username, missing password) using a data provider; full purchase flow (add 2 items, verify cart, checkout, confirmation) |
| API | [Restful-Booker](https://restful-booker.herokuapp.com) | Auth token; create, read, update and delete a booking; deleted booking returns 404; bad credentials; unknown booking returns 404 |

## Tech stack

Java 17, Selenium WebDriver, TestNG, REST-Assured, Maven, GitHub Actions

## Design

- **Page Object Model** keeps locators and page actions out of the tests (`src/test/java/portfolio/ui/pages`).
- **Data-driven tests** use a TestNG `@DataProvider` for the negative login cases.
- **Dependent API tests** run the booking lifecycle in order (`dependsOnMethods`).
- **CI**: `.github/workflows/tests.yml` runs the full suite on every push and uploads the TestNG report as a build artifact.

## Run it

Requirements: JDK 17, Maven, and Google Chrome.

```bash
mvn test                      # runs UI (headless Chrome) and API tests
mvn test -Dheadless=false     # run the UI tests with a visible browser
```

Reports are written to `target/surefire-reports` (open `index.html`).

## Project structure

```
src/test/java/portfolio
  ui/
    BaseTest.java          browser setup and teardown
    LoginTests.java
    CheckoutTests.java
    pages/                 LoginPage, InventoryPage, CheckoutPage
  api/
    BookingApiTests.java
testng.xml                 suite definition
.github/workflows/tests.yml
```

## Notes

The tests run against public practice sites that are maintained by third parties, so an occasional failure can come from the site being slow or unavailable rather than from the test code.

## Next improvements

- Add Playwright tests alongside Selenium
- Add Allure reporting
- Run the UI tests across more browsers in parallel
