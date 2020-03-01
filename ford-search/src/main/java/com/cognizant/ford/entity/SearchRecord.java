package com.cognizant.ford.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Table(name = "search_record",
        //唯一约束
        uniqueConstraints = {@UniqueConstraint(columnNames = {"gas_name","gas_address","ford4s_name","ford4s_address"})})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchRecord {

    @Id //主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic
    @Column(name = "user", nullable = false)
    private String user;

    @Basic
    @Column(name = "gas_name", nullable = false)
    private String gasName;

    @Basic
    @Column(name = "gas_address", nullable = false)
    private String gasAddress;

    @Basic
    @Column(name = "ford4s_name", nullable = false)
    private String ford4SName;

    @Basic
    @Column(name = "ford4s_address", nullable = false)
    private String ford4SAddress;

    @Basic
    @Column(name = "numbers", nullable = false)
    private Long numbers;

}
