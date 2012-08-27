/*
 * $Id$
 *
 * This file is part of the iText (R) project.
 * Copyright (c) 1998-2012 1T3XT BVBA
 * Author: Bruno Lowagie
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3
 * as published by the Free Software Foundation with the addition of the
 * following permission added to Section 15 as permitted in Section 7(a):
 * FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY 1T3XT,
 * 1T3XT DISCLAIMS THE WARRANTY OF NON INFRINGEMENT OF THIRD PARTY RIGHTS.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA, 02110-1301 USA, or download the license from the following URL:
 * http://itextpdf.com/terms-of-use/
 *
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License.
 *
 * In accordance with Section 7(b) of the GNU Affero General Public License,
 * a covered work must retain the producer line in every PDF that is created
 * or manipulated using iText.
 *
 * You can be released from the requirements of the license by purchasing
 * a commercial license. Buying such a license is mandatory as soon as you
 * develop commercial activities involving the iText software without
 * disclosing the source code of your own applications.
 * These activities include: offering paid services to customers as an ASP,
 * serving PDFs on the fly in a web application, shipping iText with a closed
 * source product.
 *
 * For more information, please contact iText Software Corp. at this
 * address: sales@itextpdf.com
 */
package com.itextpdf.smartcard.beid;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.smartcardio.CardException;

import com.itextpdf.smartcard.SmartCard;
import com.itextpdf.smartcard.beid.pojos.AddressPojo;
import com.itextpdf.smartcard.beid.pojos.IdentityPojo;
import com.itextpdf.smartcard.beid.pojos.PhotoPojo;
import com.itextpdf.smartcard.util.tlv.EidTLVParser;
import com.itextpdf.smartcard.util.tlv.SimpleTLVParser;
import com.itextpdf.text.log.Logger;
import com.itextpdf.text.log.LoggerFactory;

/**
 * Factory producing objects containing personal data about the owner of a Belgian eID card.
 */
public class BeIDFileFactory {

	/** Logger instance. */
	private final static Logger LOGGER = LoggerFactory.getLogger(BeIDFileFactory.class);
	
	/** LTV parser implementation that will be used. */
	private final static SimpleTLVParser SIMPLETLVPARSER = new EidTLVParser();
	
	/** The file id for the identity file. */
	public static final byte[] IDENTITY_FILE_ID = new byte[] { 0x3F, 0x00,
			(byte) 0xDF, 0x01, 0x40, 0x31 };
	
	/**
	 * Enumeration with all the possible tags inside an identity file.
	 */
	public enum IdentityTag {
		CARD_NUMBER((byte)0x01),
		CARD_VALIDITY_DATE_BEGIN((byte)0x03),
		CARD_VALIDITY_DATE_END((byte)0x04),
		CARD_DELIVERY_MUNICIPALITY((byte)0x05),
		NATIONAL_NUMBER((byte)0x06),
		NAME((byte)0x07),
		GIVEN_NAMES((byte)0x08),
		THIRD_NAME_INITIAL((byte)0x09),
		NATIONALITY((byte)0x0A),
		BIRTH_LOCATION((byte)0x0B),
		BIRTH_DATE((byte)0x0C),
		SEX((byte)0x0D),
		NOBLE_CONDITION((byte)0x0E),
		DOCUMENT_TYPE((byte)0x0F),
		SPECIAL_STATUS((byte)0x10);
		
		private byte tag;
		private IdentityTag(byte tag) {
			this.tag = tag;
		}
		public byte getTag() {
			return tag;
		}
	}
	
	/**
	 * Reads the identity file from an eID and returns an IdentityPojo.
	 * @param card	the BeIDCard
	 * @return	an object containing all the data about the identity of the card owner
	 * @throws CardException
	 * @throws IOException
	 */
	public static IdentityPojo getIdentity(SmartCard card) throws CardException, IOException {
		LOGGER.info("Get identity...");
		IdentityPojo file = new IdentityPojo();
		byte[] data = card.readFile(IDENTITY_FILE_ID);
		Map<Byte, String> map = SIMPLETLVPARSER.parse(data);
		file.setCardNumber(map.get(IdentityTag.CARD_NUMBER.getTag()));
		file.setCardValidityDateBegin(parseDate(map.get(IdentityTag.CARD_VALIDITY_DATE_BEGIN.getTag())));
		file.setCardValidityDateEnd(parseDate(map.get(IdentityTag.CARD_VALIDITY_DATE_END.getTag())));
		file.setCardDeliveryMunicipality(map.get(IdentityTag.CARD_DELIVERY_MUNICIPALITY.getTag()));
		file.setNationalNumber(map.get(IdentityTag.NATIONAL_NUMBER.getTag()));
		file.setName(map.get(IdentityTag.NAME.getTag()));
		file.setGivenNames(map.get(IdentityTag.GIVEN_NAMES.getTag()));
		file.setThirdGivenNameInitial(map.get(IdentityTag.THIRD_NAME_INITIAL.getTag()));
		file.setNationality(map.get(IdentityTag.NATIONALITY.getTag()));
		file.setBirthLocation(map.get(IdentityTag.BIRTH_LOCATION.getTag()));
		file.setBirthDate(map.get(IdentityTag.BIRTH_DATE.getTag()));
		file.setSex(map.get(IdentityTag.SEX.getTag()));
		file.setNobleCondition(map.get(IdentityTag.NOBLE_CONDITION.getTag()));
		file.setDocumentType(parseInt(map.get(IdentityTag.DOCUMENT_TYPE.getTag())));
		file.setSpecialStatus(parseInt(map.get(IdentityTag.SPECIAL_STATUS.getTag())));
		return file;
	}

	/** The file id for the address file */
	public static final byte[] ADDRESS_FILE_ID = new byte[] { 0x3F, 0x00,
		(byte) 0xDF, 0x01, 0x40, 0x33 };

	/**
	 * Enumeration with all the possible tags inside an address file.
	 */
	public enum AddressTag {
		STREET((byte)0x01), ZIP((byte)0x02), MUNICIPALITY((byte)0x03);
		private byte tag;
		private AddressTag(byte tag) {
			this.tag = tag;
		}
		public byte getTag() {
			return tag;
		}
	}
	
	/**
	 * Reads the address file from an eID and returns an AddressPojo.
	 * @param card	the BeIDCard
	 * @return	an object containing the address of the card owner
	 * @throws CardException
	 * @throws IOException
	 */
	public static AddressPojo getAddress(SmartCard card) throws CardException, IOException {
		AddressPojo file = new AddressPojo();
		byte[] data = card.readFile(ADDRESS_FILE_ID);
		Map<Byte, String> map = SIMPLETLVPARSER.parse(data);
		file.setStreet(map.get(AddressTag.STREET.getTag()));
		file.setZip(map.get(AddressTag.ZIP.getTag()));
		file.setMunicipality(map.get(AddressTag.MUNICIPALITY.getTag()));
		return file;
	}

	/** The file id for the photo file */
	public static final byte[] PHOTO_FILE_ID = new byte[] { 0x3F, 0x00,
			(byte) 0xDF, 0x01, 0x40, 0x35 };
	
	/**
	 * Reads the photo file from an eID and returns a PhotoPojo.
	 * @param card	the BeIDCard
	 * @return	an object containing the photo of the card owner
	 * @throws CardException
	 * @throws IOException
	 */
	public static PhotoPojo getPhoto(SmartCard card) throws CardException, IOException {
		PhotoPojo file = new PhotoPojo();
		file.setPhoto(card.readFile(PHOTO_FILE_ID));
		return file;
	}
	
	/**
	 * Parses a Date String as defined for the BeID to a Date object.
	 * 
	 * @param date
	 *            String to be converted
	 * @return a Date object
	 */
	public static Date parseDate(String date) {
		DateFormat formatter;
		try {
			if (date == null)
				return null;
			formatter = new SimpleDateFormat("dd.MM.yyyy");
			return formatter.parse(date);
		} catch (ParseException e) {
			return null;
		}
	}
	
	/**
	 * Parses an Integer String to an int value.
	 *
	 * @param integer
	 * @return
	 */
	public static int parseInt(String integer) {
		try {
			return Integer.parseInt(integer);
		}
		catch(NumberFormatException nfe) {
			return -1;
		}
	}
}
