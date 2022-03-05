package com.project.makecake.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class OpenTime extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long openTimeId;

    @Column
    private String type;

    @Column
    private String startTime;

    @Column
    private String endTime;

    @Column
    private String descriptionTime;

    @Column
    private Boolean isDayOff;

    @ManyToOne
    @JoinColumn(name = "STORE_ID")
    private Store store;
}
