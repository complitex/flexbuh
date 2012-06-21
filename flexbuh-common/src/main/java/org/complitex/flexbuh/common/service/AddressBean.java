package org.complitex.flexbuh.common.service;

import org.complitex.flexbuh.common.entity.Address;
import org.complitex.flexbuh.common.mybatis.Transactional;

import javax.ejb.Stateless;
import java.util.Locale;

/**
 * @author Pavel Sknar
 *         Date: 04.06.12 11:58
 */
@Stateless
public class AddressBean extends AbstractBean {

    public static final String NS = AddressBean.class.getName();

    /**
     * Create address if address do not exist.
     *
     * @param address New address.
     * @return This new address or exist address.
     */
    @Transactional
    public Address create(Address address) {
        if (address == null || address.isEmptyAddress()) {
            return null;
        }
        Address existAddress = getAddressExist(address);
        if (existAddress != null) {
            return existAddress;
        }
        sqlSession().insert(NS + ".insertAddress", address);
        return address;
    }

    @Transactional
    public void update(Address address) {
        sqlSession().update(NS + ".updateOrganization", address);
    }

    private Address getAddressExist(Address address) {
        return sqlSession().selectOne(NS + ".getAddressExist", address);
    }
}
