//package com.project.makecake.model;
//
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import javax.persistence.*;
//
//@Getter
//@NoArgsConstructor
//@Entity
//public class UserNoti extends Timestamped{
//
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Id
//    private Long userNotiId;
//
//    @ManyToOne
//    @JoinColumn(name="userId")
//    private User user;
//
//    @ManyToOne
//    @JoinColumn(name="notiId")
//    private Noti noti;
//
//    @Column
//    private boolean read;
//}
