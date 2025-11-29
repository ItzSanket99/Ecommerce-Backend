package com.ecommerce.project.service;

import com.ecommerce.project.payload.AnalyticResponse;
import com.ecommerce.project.repositories.OrderRepository;
import com.ecommerce.project.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnalyticServiceImpl implements AnalyticService{

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public AnalyticResponse getAnalytics() {
        AnalyticResponse analyticResponse = new AnalyticResponse();

        long productCount = productRepository.count();
        long totalOrders = orderRepository.count();
        Double totalRevenue = orderRepository.getTotalRevenue();

        analyticResponse.setProductCount(String.valueOf(productCount));
        analyticResponse.setTotalRevenue(String.valueOf(totalRevenue != null ? totalRevenue : 0));
        analyticResponse.setTotalOrders(String.valueOf(totalOrders));
        return analyticResponse;
    }
}
