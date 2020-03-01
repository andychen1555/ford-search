package com.cognizant.ford.repository;

import com.cognizant.ford.entity.SearchRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SearchRepository extends JpaRepository<SearchRecord, Long> {

    List<SearchRecord> findByUserAndAndGasNameAndGasAddressAndFord4SNameAndFord4SAddress(
            String user, String gasName, String gasAddress, String ford4SName, String ford4SAddress
    );


    @Query(value = "SELECT a.id,a.USER,a.gas_name,a.gas_address,a.ford4s_name,a.ford4s_address,a.numbers FROM search_record a INNER JOIN (SELECT USER,MAX(numbers) AS numbers FROM search_record GROUP BY USER) c ON a.USER=c.USER AND a.numbers=c.numbers ORDER BY a.numbers DESC"
            , nativeQuery = true)
    List<SearchRecord> queryHistory();

}
