package com.inn.cafe.rest;


import com.inn.cafe.POJO.Bill;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping(path = "/bill")
public interface BillRest {
    @PostMapping(path = "/generateReport")
    ResponseEntity<String> generateReport(@RequestBody(required = false) Map<String,Object> reqMap);

    @GetMapping(path = "/getBills")
    ResponseEntity<List<Bill>> getBills();

    @PostMapping(path = "/getBillPdf")
    ResponseEntity<byte[]> getBillPdf(@RequestBody(required = false) Map<String,Object> reqMap);

    @GetMapping(path = "/deleteBill/{id}")
    ResponseEntity<String> deletedBill(@PathVariable Integer id);

}
