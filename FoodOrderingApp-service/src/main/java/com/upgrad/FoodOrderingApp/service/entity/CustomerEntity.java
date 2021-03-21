package com.upgrad.FoodOrderingApp.service.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "customer")
@NamedQueries(
        {
                @NamedQuery(name = "customerByContactNumber", query = "select u from CustomerEntity u where u.contact_number=:contact_number"),
                @NamedQuery(name = "customerByFirstname", query = "select u from CustomerEntity u where u.firstName =:firstname"),
                @NamedQuery(name = "customerByUuid", query = "SELECT c from CustomerEntity c where c.uuid = :uuid")
        }
)
public class CustomerEntity implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid")
    @Size(max = 200)
    private String uuid;

    @Column(name = "firstname")
    @Size(max = 30)
    private String firstName;

    @Column(name = "lastname")
    @Size(max = 30)
    private String lastName;

    @Column(name = "email")
    @Size(max = 50)
    private String email;

    @Column(name = "contact_number")
    @Size(max = 30)
    private String contact_number;

    @Column(name = "password")
    @Size(max = 255)
    private String password;

    @Column(name = "salt")
    @Size(max = 255)
    @NotNull
    private String salt;

    @ManyToMany(mappedBy = "customers")
    private List<AddressEntity> addresses;

    public List<AddressEntity> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressEntity> addresses) {
        this.addresses = addresses;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactnumber() {
        return contact_number;
    }

    public void setContactnumber(String contactnumber) {
        this.contact_number = contactnumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    @OneToMany(mappedBy = "customer")
    private Collection<CustomerAuthEntity> customerAuthEntity;

    public Collection<CustomerAuthEntity> getCustomerAuthTokenEntity() {
        return customerAuthEntity;
    }

    public void setCustomerAuthTokenEntity(Collection<CustomerAuthEntity> customerAuthEntity) {
        this.customerAuthEntity = customerAuthEntity;
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
