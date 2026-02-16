package com.fulfilment.application.monolith.products;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.math.BigDecimal;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Entity
@Cacheable
public class Product extends PanacheEntityBase {

  @Id
  @GeneratedValue
  @Schema(readOnly = true)
  public Long id;

  @Column(length = 40, unique = true)
  public String name;

  @Column(nullable = true)
  public String description;

  @Column(precision = 10, scale = 2, nullable = true)
  public BigDecimal price;

  public int stock;

  public Product() {
  }

  public Product(String name) {
    this.name = name;
  }
}
