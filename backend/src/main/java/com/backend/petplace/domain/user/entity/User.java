package com.backend.petplace.domain.user.entity;

import com.backend.petplace.domain.order.entity.Order;
import com.backend.petplace.domain.pet.entity.Pet;
import com.backend.petplace.domain.user.dto.request.UserSignupRequest;
import com.backend.petplace.global.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "userId")
  private Long id;

  @Column(unique = true, nullable = false, name = "userEmail")
  private String email;

  @Column(nullable = false) // 암호화
  private String password;

  @Column(unique = true, nullable = false, name = "userName")
  private String nickName;

  @Column(nullable = false)
  private String address;

  @Column(nullable = false)
  private String zipcode;

  private String addressDetail;

  private Integer totalPoint;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private List<Order> orders = new ArrayList<>();

  @BatchSize(size = 100)
  @OneToMany(mappedBy = "user")
  private List<Pet> pets = new ArrayList<>();

  @Builder
  public User(String nickName, String password, String email, String address,
      String zipcode, String addressDetail) {

    this.nickName = nickName;
    this.password = password;
    this.email = email;
    this.address = address;
    this.zipcode = zipcode;
    this.addressDetail = addressDetail;
    this.totalPoint = 10;
  }

  public static User create(UserSignupRequest request, String password) {
    return User.builder()
        .nickName(request.getNickName())
        .password(password)
        .email(request.getEmail())
        .address(request.getAddress())
        .zipcode(request.getZipcode())
        .addressDetail(request.getAddressDetail())
        .build();
  }

  public void addOrders(List<Order> orders) {
    for (Order order : orders) {
      addOrder(order);
    }
  }

  public void addOrder(Order order) {
    this.orders.add(order);
    order.setUser(this);
  }

  public void addPoints(Integer amount) {
    if (this.totalPoint == null) {
      this.totalPoint = 0;
    }
    this.totalPoint += amount;

  }

  public void abstractPoints(Integer amount) {
    if (this.totalPoint == null) {
      this.totalPoint = 0;
    }
    this.totalPoint -= amount;
  }
}