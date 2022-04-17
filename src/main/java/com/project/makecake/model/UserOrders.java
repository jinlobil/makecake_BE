package com.project.makecake.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class UserOrders extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userOrdersId;

    @Column
    private String formFilled;

    @ManyToOne
    @JoinColumn(name = "ORDER_FORM_ID")
    private OrderForm orderForm;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @OneToOne
    @JoinColumn(name="DESIGN_ID")
    private Design design;

    @Builder
    public UserOrders(String formFilled, OrderForm orderForm, User user, Design design){
        this.formFilled = formFilled;
        this.orderForm = orderForm;
        this.user = user;
        this.design = design;
    }

}
