/*
Створити публічний репозиторій GitHub з назвою dz–16.

Написати тести для тестового API http://restful-booker.herokuapp.com/:
1. Створити бронювання книжки (POST)
2. Отримати id усіх доступних бронювань книжок (GET /booking)
3. Вибрати одне з id з п.2 та змінити ціну бронювання (PATCH)
4. Вибрати інший id з отриманих у п.2 та змінити ім’я та additionalneeds (PUT)
5. Видалити одне бронювання з отриманих у п.2 (DELETE)

Документація до API знаходиться тут http://restful-booker.herokuapp.com/apidoc/index.html

Формат здачі: Прикріпити посилання на Pull request з файлами на GitHub.
 */
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import models.Booking;
import models.BookingId;
import models.BookingResponse;
import models.Bookingdates;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class RestApiTest {

  private List<BookingId> bookingIds;

  @BeforeClass
  public void setup() throws JsonProcessingException {
    RestAssured.baseURI = "https://restful-booker.herokuapp.com";
    String token = getToken();
    RestAssured.requestSpecification = new RequestSpecBuilder()
        .addHeader("Content-Type", "application/json")
        .addHeader("Cookie", "token=" + token)
        .build();
  }

  @Test(dataProviderClass = ApiTestDataProvider.class, dataProvider = "generate booking")
  public void createBookingTest(String firstname, String lastname, Double totalprice,
      Boolean depositpaid, String checkin, String checkout, String additionalneeds) {

    LocalDate checkinValue = LocalDate.parse(checkin, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    LocalDate checkoutValue = LocalDate.parse(checkout, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    Booking booking = new Booking().builder()
        .firstname(firstname)
        .lastname(lastname)
        .totalprice(totalprice)
        .depositpaid(depositpaid)
        .bookingdates(new Bookingdates().builder()
            .checkin(checkinValue)
            .checkout(checkoutValue)
            .build())
        .additionalneeds(additionalneeds)
        .build();

    Response response = RestAssured.given()
        .body(booking)
        .post("/booking");
    response.then().statusCode(200);
    response.prettyPrint();

    Assert.assertTrue(response.as(BookingResponse.class).getBooking().equals(booking));
  }

  @Test
  public void getAllBookings() {
    Response response = RestAssured.given()
        .log().all()
        .get("/booking");
    response.then().statusCode(200);
    bookingIds = response.jsonPath().getList(".", BookingId.class);
    System.out.println(bookingIds.size());
    Assert.assertTrue(bookingIds.size() > 0);
  }

  @Test(dependsOnMethods = "getAllBookings",
      dataProviderClass = ApiTestDataProvider.class, dataProvider = "generate inc price")
  public void changeTotalpriceTest(Double incPrice) {
    Integer id = bookingIds.get(bookingIds.size() / 2).getBookingid();
    Booking booking = getBooking(id);
    booking.setTotalprice(booking.getTotalprice() + incPrice);
    Response response = RestAssured.given()
        .header("Accept", "application/json")
        .body(String.format("{\"totalprice\":\"%.0f\"}", booking.getTotalprice()))
        .log().all()
        .patch("/booking/{id}", id);
    response.then().statusCode(200);
    response.prettyPrint();
    Assert.assertEquals(response.as(Booking.class).getTotalprice(), booking.getTotalprice());
  }

  @Test(dependsOnMethods = "getAllBookings")
  public void changeBookingTest() {
    Integer id = bookingIds.get(bookingIds.size() / 3).getBookingid();
    Booking booking = getBooking(id);
    booking.setFirstname(booking.getFirstname() + "_updated");
    booking.setLastname(booking.getLastname() + "_updated");
    booking.setAdditionalneeds(booking.getAdditionalneeds() + "_updated");
    Response response = RestAssured.given()
        .header("Accept", "application/json")
        .body(booking)
        .log().all()
        .put("/booking/{id}", id);
    response.then().statusCode(200);
    response.prettyPrint();
    Assert.assertTrue(response.as(Booking.class).equals(booking));
  }

  @Test(dependsOnMethods = "getAllBookings")
  public void deleteBookingTest() {
    Integer id = bookingIds.get(bookingIds.size() / 4).getBookingid();
    Response response = RestAssured.given()
        .log().all()
        .delete("/booking/{id}", id);
    response.then().statusCode(201);
    RestAssured.when().get("/booking/{id}", id).then().statusCode(404);
  }

  private Booking getBooking(Integer id) {
    return RestAssured.given()
        .header("Accept", "application/json")
        .when()
        .get("/booking/{id}", id).as(Booking.class);
  }

  private String getToken() throws JsonProcessingException {
    return new ObjectMapper().readTree(
        RestAssured.given()
            .header("Content-Type", "application/json")
            .body("{\"username\":\"admin\",\"password\":\"password123\"}")
            .when()
            .post("/auth").asString()
    ).get("token").asText();
  }


}
