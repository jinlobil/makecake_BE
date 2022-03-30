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

    //json으로 데이터 담아주면 그대로 받아서 데이터 베이스에 저장하는 방법 알아야함....
    //일단은 미봉책으로 <br> 이라는 애를 넣어서 stringify하겠음
    @Builder
    public UserOrders(String formFilled, OrderForm orderForm, User user, Design design){
        this.formFilled = formFilled;
        this.orderForm = orderForm;
        this.user = user;
        this.design = design;
    }

}
