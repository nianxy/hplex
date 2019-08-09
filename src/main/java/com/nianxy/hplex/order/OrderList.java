package com.nianxy.hplex.order;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class OrderList {
    private static Logger logger = LogManager.getLogger(OrderList.class);

    public static OrderList from(IOrder order) {
        return new OrderList().addOrder(order);
    }

    private List<IOrder> orders;

    public OrderList() {
        orders = new ArrayList<>();
    }

    public OrderList addOrder(IOrder order) {
        orders.add(order);
        return this;
    }

    public List<IOrder> getOrders() {
        return orders;
    }
}
