package com.backend.petplace.domain.place.projection;

public interface PlaceSearchRow {
  Long getId();
  String getName();
  String getCategory2();
  Double getLatitude();
  Double getLongitude();
  Integer getDistanceMeters();
  Double getAverageRating();
  String getAddress();

}
