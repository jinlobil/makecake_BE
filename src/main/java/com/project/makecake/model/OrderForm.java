package com.project.makecake.model;

import com.project.makecake.dto.orders.OrderFormRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor // 자동 생성 롬복을 이걸로 해놓는 이유는 왜??
public class OrderForm extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long orderFormId;

    @Column
    private String form;

    @Column
    private String name;

    @Column
    private String instruction;

    @ManyToOne
    @JoinColumn(name="STORE_ID")
    private Store store;


    // 생성자
    @Builder
    // 궁금 (여기서 orderFormRepository.getByStoreId(requestDto.getStoreId()) 해서 넣으면 안 되나?
    // 1. repository를 여기에 불러오는 것
    // 2. getByStoreId() -> null 에러 처리를 여기에...???
    public OrderForm(OrderFormRequestDto requestDto, Store store){
        this.form = requestDto.getForm();
        this.name = requestDto.getName();
        this.instruction = requestDto.getInstruction();
        this.store = store;
    }
}
