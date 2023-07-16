package models;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
  String firstname;
  String lastname;
  Double totalprice;
  Boolean depositpaid;
  Bookingdates bookingdates;
  String additionalneeds;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Booking booking = (Booking) o;
    return Objects.equals(firstname, booking.firstname) && Objects.equals(
        lastname, booking.lastname) && Objects.equals(totalprice, booking.totalprice)
        && Objects.equals(depositpaid, booking.depositpaid) && Objects.equals(
        additionalneeds, booking.additionalneeds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(firstname, lastname, totalprice, depositpaid, additionalneeds);
  }
}
