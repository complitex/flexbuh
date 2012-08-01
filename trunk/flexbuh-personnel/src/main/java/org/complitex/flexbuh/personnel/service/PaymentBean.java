package org.complitex.flexbuh.personnel.service;

import org.apache.commons.lang.NotImplementedException;
import org.complitex.flexbuh.personnel.entity.Payment;

import javax.ejb.Stateless;

/**
 * @author Pavel Sknar
 *         Date: 18.07.12 14:35
 */
@Stateless
public class PaymentBean extends TemporalDomainObjectBean<Payment> {

    public static final String NS = PaymentBean.class.getName();

    public PaymentBean() {
        super(NS);
    }

    @Override
    public void save(Payment object) {
        throw new NotImplementedException();
    }
}
