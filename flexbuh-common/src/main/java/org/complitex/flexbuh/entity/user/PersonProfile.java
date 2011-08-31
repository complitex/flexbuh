package org.complitex.flexbuh.entity.user;

import org.complitex.flexbuh.entity.DomainObject;

import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 31.08.11 14:26
 */
public class PersonProfile extends DomainObject {
	private static final String TABLE = "user";

	private String name;
	private String codeTIN; // Код ЕДРПОУ
	private Integer codeTaxInspection; // Код ДПІ
	private String codeKVED; // Код основного виду економічної діяльності (за КВЕД)
	private PersonType personType; // Тип платника
	private Date contractDate; // Договір про спільну (сумісну) діяльність (дата)
	private String contractNumber; // Договір про спільну (сумісну) діяльність (номер)
	private String zipCode; // Поштовий індекс
	private String address; // Адреса
	private String phone; // Телефон
	private String fax; // Факс
	private String email; // E-mail
	private String directorFIO; // ФИО директора предприятия
	private String accountantFIO; // ФИО бухгалтера
	private String directorINN; // Код ДРФО директора
	private String accountantINN; // Код ДРФО  бухгалтера
	private String ipn; // Індивідуальний податковий номер
	private String numSvdPDV; // Номер свідоцтва ПДВ

	@Override
	public String getTable() {
		return TABLE;
	}

	public enum PersonType {
		JURIDICAL_PERSON(1), PHYSICAL_PERSON(2), COOPERATION(3), RESIDENT_REPRESENTATIVE(4);

		private int code;

		PersonType(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}
	}
}
