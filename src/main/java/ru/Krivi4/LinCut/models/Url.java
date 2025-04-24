package ru.Krivi4.LinCut.models;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "url")
@Getter
@Setter
@ToString
public class Url {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String longUrl;

    @Column(nullable = false)
    private Date createdDate;

    private Date expiresDate;


}
