package com.ecommerce.project.controller;


import com.ecommerce.project.payload.AnalyticResponse;
import com.ecommerce.project.service.AnalyticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AnalyticsController {

    @Autowired
    private AnalyticService analyticService;

    @GetMapping("/admin/app/analytics")
    public ResponseEntity<AnalyticResponse> getAnalytics(){
        AnalyticResponse response = analyticService.getAnalytics();
        return new ResponseEntity<AnalyticResponse>(response, HttpStatus.OK);
    }
}
