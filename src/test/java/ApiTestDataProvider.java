import org.testng.annotations.DataProvider;

public class ApiTestDataProvider {

  @DataProvider(name = "generate booking")
  public Object[][] generateBooking() {
    return new Object[][]{
        {"David", "Coverdale", 1000.0, true, "2023-06-10", "2023-06-20", "no needs"}
    };
  }

  @DataProvider(name = "generate inc price")
  public Object[][] generateTotalprice() {
    return new Object[][]{
        {1500.0}
    };
  }


}
