package com.upgrad.FoodOrderingApp.service.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "payment")
@NamedQueries({@NamedQuery(name = "allPayments", query = "select p from PaymentEntity p")})
public class PaymentEntity implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid")
    @Size(max = 200)
    @NotNull
    private UUID uuid;

    @Column(name = "payment_name")
    @Size(max = 500)
    private String paymentName;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = UUID.fromString(uuid);
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(final String paymentName) {
        this.paymentName = paymentName;
    }

    @Override
    public boolean equals(Object obj) {
        return new EqualsBuilder().append(this, obj).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this).hashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
