package portfolio.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * API tests for the public Restful-Booker practice API.
 * Covers the full create / read / update / delete lifecycle plus error cases.
 */
public class BookingApiTests {

    private static final String BASE_URI = "https://restful-booker.herokuapp.com";

    private static final String CREATE_BODY = """
            {
              "firstname": "Test",
              "lastname": "Tester",
              "totalprice": 150,
              "depositpaid": true,
              "bookingdates": { "checkin": "2026-12-01", "checkout": "2026-12-05" },
              "additionalneeds": "Breakfast"
            }
            """;

    private static final String UPDATE_BODY = """
            {
              "firstname": "Test",
              "lastname": "Updated",
              "totalprice": 200,
              "depositpaid": false,
              "bookingdates": { "checkin": "2026-12-02", "checkout": "2026-12-06" },
              "additionalneeds": "Late checkout"
            }
            """;

    private String token;
    private int bookingId;

    @BeforeClass
    public void authenticate() {
        RestAssured.baseURI = BASE_URI;
        token = given()
                .contentType(ContentType.JSON)
                .body("{\"username\": \"admin\", \"password\": \"password123\"}")
            .when()
                .post("/auth")
            .then()
                .statusCode(200)
                .body("token", notNullValue())
                .extract().path("token");
    }

    @Test(description = "Create a booking and return its id")
    public void createBooking() {
        bookingId = given()
                .contentType(ContentType.JSON)
                .header("Accept", "application/json")
                .body(CREATE_BODY)
            .when()
                .post("/booking")
            .then()
                .statusCode(200)
                .body("booking.firstname", equalTo("Test"))
                .body("booking.totalprice", equalTo(150))
                .extract().path("bookingid");
    }

    @Test(dependsOnMethods = "createBooking", description = "Read back the booking that was created")
    public void getBooking() {
        given()
                .header("Accept", "application/json")
            .when()
                .get("/booking/" + bookingId)
            .then()
                .statusCode(200)
                .body("firstname", equalTo("Test"))
                .body("lastname", equalTo("Tester"))
                .body("bookingdates.checkin", equalTo("2026-12-01"));
    }

    @Test(dependsOnMethods = "getBooking", description = "Update the booking (requires auth token)")
    public void updateBooking() {
        given()
                .contentType(ContentType.JSON)
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(UPDATE_BODY)
            .when()
                .put("/booking/" + bookingId)
            .then()
                .statusCode(200)
                .body("lastname", equalTo("Updated"))
                .body("totalprice", equalTo(200));
    }

    @Test(dependsOnMethods = "updateBooking", description = "Delete the booking (requires auth token)")
    public void deleteBooking() {
        given()
                .header("Cookie", "token=" + token)
            .when()
                .delete("/booking/" + bookingId)
            .then()
                .statusCode(201);
    }

    @Test(dependsOnMethods = "deleteBooking", description = "A deleted booking can no longer be found")
    public void deletedBookingReturns404() {
        given()
                .header("Accept", "application/json")
            .when()
                .get("/booking/" + bookingId)
            .then()
                .statusCode(404);
    }

    @Test(description = "Bad credentials are rejected with a reason in the response")
    public void badCredentialsAreRejected() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"username\": \"admin\", \"password\": \"wrong\"}")
            .when()
                .post("/auth")
            .then()
                .body("reason", equalTo("Bad credentials"));
    }

    @Test(description = "Requesting a booking that does not exist returns 404")
    public void unknownBookingReturns404() {
        given()
                .header("Accept", "application/json")
            .when()
                .get("/booking/0")
            .then()
                .statusCode(404);
    }
}
