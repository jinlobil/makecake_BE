package com.project.makecake.model;

import com.project.makecake.MakeCakeApplication;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.SpringApplication;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor

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

    public OpenTime(String type, String startTime, String endTime, String descriptionTime){
        this.type = type;
        this.startTime = startTime;
        this.endTime = endTime;
        this.descriptionTime = descriptionTime;
    }

    public static void main(String[] args) {
        OpenTime openTime= new OpenTime("HI", "1", "3", "3");
        System.out.println(openTime);
    }
}
