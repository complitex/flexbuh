package org.complitex.flexbuh.common.entity;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 15.12.11 17:00
 */
public class OrderString implements Comparable<OrderString>{
    private int order;
    private String string;

    public OrderString() {
    }

    public OrderString(int order, String string) {
        this.order = order;
        this.string = string;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    @Override
    public int compareTo(OrderString o) {
        return Integer.valueOf(order).compareTo(o.getOrder());
    }
}
