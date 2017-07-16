
package com.sch.fakecontacts.model.generator;

import android.support.annotation.Nullable;
import javax.annotation.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_PostalAddress extends PostalAddress {

  private final String country;
  private final String city;
  private final String region;
  private final String street;
  private final String postcode;

  private AutoValue_PostalAddress(
      String country,
      String city,
      @Nullable String region,
      String street,
      @Nullable String postcode) {
    this.country = country;
    this.city = city;
    this.region = region;
    this.street = street;
    this.postcode = postcode;
  }

  @Override
  public String country() {
    return country;
  }

  @Override
  public String city() {
    return city;
  }

  @Nullable
  @Override
  public String region() {
    return region;
  }

  @Override
  public String street() {
    return street;
  }

  @Nullable
  @Override
  public String postcode() {
    return postcode;
  }

  @Override
  public String toString() {
    return "PostalAddress{"
        + "country=" + country + ", "
        + "city=" + city + ", "
        + "region=" + region + ", "
        + "street=" + street + ", "
        + "postcode=" + postcode
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof PostalAddress) {
      PostalAddress that = (PostalAddress) o;
      return (this.country.equals(that.country()))
           && (this.city.equals(that.city()))
           && ((this.region == null) ? (that.region() == null) : this.region.equals(that.region()))
           && (this.street.equals(that.street()))
           && ((this.postcode == null) ? (that.postcode() == null) : this.postcode.equals(that.postcode()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.country.hashCode();
    h *= 1000003;
    h ^= this.city.hashCode();
    h *= 1000003;
    h ^= (region == null) ? 0 : this.region.hashCode();
    h *= 1000003;
    h ^= this.street.hashCode();
    h *= 1000003;
    h ^= (postcode == null) ? 0 : this.postcode.hashCode();
    return h;
  }

  static final class Builder extends PostalAddress.Builder {
    private String country;
    private String city;
    private String region;
    private String street;
    private String postcode;
    Builder() {
    }
    Builder(PostalAddress source) {
      this.country = source.country();
      this.city = source.city();
      this.region = source.region();
      this.street = source.street();
      this.postcode = source.postcode();
    }
    @Override
    public PostalAddress.Builder setCountry(String country) {
      this.country = country;
      return this;
    }
    @Override
    public PostalAddress.Builder setCity(String city) {
      this.city = city;
      return this;
    }
    @Override
    public PostalAddress.Builder setRegion(@Nullable String region) {
      this.region = region;
      return this;
    }
    @Override
    public PostalAddress.Builder setStreet(String street) {
      this.street = street;
      return this;
    }
    @Override
    public PostalAddress.Builder setPostcode(@Nullable String postcode) {
      this.postcode = postcode;
      return this;
    }
    @Override
    public PostalAddress build() {
      String missing = "";
      if (country == null) {
        missing += " country";
      }
      if (city == null) {
        missing += " city";
      }
      if (street == null) {
        missing += " street";
      }
      if (!missing.isEmpty()) {
        throw new IllegalStateException("Missing required properties:" + missing);
      }
      return new AutoValue_PostalAddress(
          this.country,
          this.city,
          this.region,
          this.street,
          this.postcode);
    }
  }

}
