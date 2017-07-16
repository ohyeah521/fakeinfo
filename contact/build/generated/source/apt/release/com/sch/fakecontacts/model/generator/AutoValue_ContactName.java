
package com.sch.fakecontacts.model.generator;

import android.support.annotation.Nullable;
import javax.annotation.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_ContactName extends ContactName {

  private final String firstName;
  private final String lastName;
  private final String middleName;

  AutoValue_ContactName(
      String firstName,
      String lastName,
      @Nullable String middleName) {
    if (firstName == null) {
      throw new NullPointerException("Null firstName");
    }
    this.firstName = firstName;
    if (lastName == null) {
      throw new NullPointerException("Null lastName");
    }
    this.lastName = lastName;
    this.middleName = middleName;
  }

  @Override
  public String firstName() {
    return firstName;
  }

  @Override
  public String lastName() {
    return lastName;
  }

  @Nullable
  @Override
  public String middleName() {
    return middleName;
  }

  @Override
  public String toString() {
    return "ContactName{"
        + "firstName=" + firstName + ", "
        + "lastName=" + lastName + ", "
        + "middleName=" + middleName
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof ContactName) {
      ContactName that = (ContactName) o;
      return (this.firstName.equals(that.firstName()))
           && (this.lastName.equals(that.lastName()))
           && ((this.middleName == null) ? (that.middleName() == null) : this.middleName.equals(that.middleName()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.firstName.hashCode();
    h *= 1000003;
    h ^= this.lastName.hashCode();
    h *= 1000003;
    h ^= (middleName == null) ? 0 : this.middleName.hashCode();
    return h;
  }

}
