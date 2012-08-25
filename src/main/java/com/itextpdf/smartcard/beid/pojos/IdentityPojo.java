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
package com.itextpdf.smartcard.beid.pojos;

import java.util.Date;

/**
 * Contains identity information about the owner of a Belgian eID.
 */
public class IdentityPojo {

	/** The card number. */
	private String cardNumber;
	/** Start date of the validity of the card. */
	private Date cardValidityDateBegin;
	/** End date of the validity of the card. */
	private Date cardValidityDateEnd;
	/** Municipality where the card was delivered */
	private String cardDeliveryMunicipality;
	/** National number. */
	private String nationalNumber;
	/** Family name of the owner */
	private String name;
	/** First two given names of the owner. */
	private String givenNames;
	/** First letter of the 3rd given name. */
	private String thirdGivenNameInitial;
	/** Nationality of the owner. */
	private String nationality;
	/** Place of birth of the owner. */
	private String birthLocation;
	/** Day of birth of the owner. */
	private String birthDate;
	/** Gender of the owner. */
	private String sex;
	/** Nobility title of the owner. */
	private String nobleCondition;
	/** Document type */
	private int documentType;
	/** Special status */
	private int specialStatus;
	
	/**
	 * @return the cardNumber
	 */
	public String getCardNumber() {
		return cardNumber;
	}
	/**
	 * @param cardNumber the cardNumber to set
	 */
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	/**
	 * @return the cardValidityDateBegin
	 */
	public Date getCardValidityDateBegin() {
		return cardValidityDateBegin;
	}
	/**
	 * @param cardValidityDateBegin the cardValidityDateBegin to set
	 */
	public void setCardValidityDateBegin(Date cardValidityDateBegin) {
		this.cardValidityDateBegin = cardValidityDateBegin;
	}
	/**
	 * @return the cardValidityDateEnd
	 */
	public Date getCardValidityDateEnd() {
		return cardValidityDateEnd;
	}
	/**
	 * @param cardValidityDateEnd the cardValidityDateEnd to set
	 */
	public void setCardValidityDateEnd(Date cardValidityDateEnd) {
		this.cardValidityDateEnd = cardValidityDateEnd;
	}
	/**
	 * @return the cardDeliveryMunicipality
	 */
	public String getCardDeliveryMunicipality() {
		return cardDeliveryMunicipality;
	}
	/**
	 * @param cardDeliveryMunicipality the cardDeliveryMunicipality to set
	 */
	public void setCardDeliveryMunicipality(String cardDeliveryMunicipality) {
		this.cardDeliveryMunicipality = cardDeliveryMunicipality;
	}
	/**
	 * @return the nationalNumber
	 */
	public String getNationalNumber() {
		return nationalNumber;
	}
	/**
	 * @param nationalNumber the nationalNumber to set
	 */
	public void setNationalNumber(String nationalNumber) {
		this.nationalNumber = nationalNumber;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the givenNames
	 */
	public String getGivenNames() {
		return givenNames;
	}
	/**
	 * @param givenNames the givenNames to set
	 */
	public void setGivenNames(String givenNames) {
		this.givenNames = givenNames;
	}
	/**
	 * @return the thirdGivenNameInitial
	 */
	public String getThirdGivenNameInitial() {
		return thirdGivenNameInitial;
	}
	/**
	 * @param thirdGivenNameInitial the thirdGivenNameInitial to set
	 */
	public void setThirdGivenNameInitial(String thirdGivenNameInitial) {
		this.thirdGivenNameInitial = thirdGivenNameInitial;
	}
	/**
	 * @return the nationality
	 */
	public String getNationality() {
		return nationality;
	}
	/**
	 * @param nationality the nationality to set
	 */
	public void setNationality(String nationality) {
		this.nationality = nationality;
	}
	/**
	 * @return the birthLocation
	 */
	public String getBirthLocation() {
		return birthLocation;
	}
	/**
	 * @param birthLocation the birthLocation to set
	 */
	public void setBirthLocation(String birthLocation) {
		this.birthLocation = birthLocation;
	}
	/**
	 * @return the birthDate
	 */
	public String getBirthDate() {
		return birthDate;
	}
	/**
	 * @param birthDate the birthDate to set
	 */
	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}
	/**
	 * @return the sex
	 */
	public String getSex() {
		return sex;
	}
	/**
	 * @param sex the sex to set
	 */
	public void setSex(String sex) {
		this.sex = sex;
	}
	/**
	 * @return the nobleCondition
	 */
	public String getNobleCondition() {
		return nobleCondition;
	}
	/**
	 * @param nobleCondition the nobleCondition to set
	 */
	public void setNobleCondition(String nobleCondition) {
		this.nobleCondition = nobleCondition;
	}
	/**
	 * @return the documentType
	 */
	public int getDocumentType() {
		return documentType;
	}
	/**
	 * @param documentType the documentType to set
	 */
	public void setDocumentType(int documentType) {
		this.documentType = documentType;
	}
	/**
	 * @return the specialStatus
	 */
	public int getSpecialStatus() {
		return specialStatus;
	}
	/**
	 * @param specialStatus the specialStatus to set
	 */
	public void setSpecialStatus(int specialStatus) {
		this.specialStatus = specialStatus;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("card number: ").append(this.cardNumber)
				.append("\ncard valid from: ").append(this.cardValidityDateBegin)
				.append(" until ").append(this.cardValidityDateEnd)
				.append("\nissued: ").append(this.cardDeliveryMunicipality)
				.append("\nnational number: ").append(this.nationalNumber)
				.append("\nName: ").append(this.name).append(", ")
				.append(this.givenNames).append(" ").append(thirdGivenNameInitial)
				.append("\nNationality: ").append(this.nationality)
				.append("\nBirth Location: ").append(this.birthLocation)
				.append("\nBirth Date: ").append(this.birthDate)
				.append("\nSex: ").append(this.sex)
				.append("\nNoble condition: ").append(this.nobleCondition)
				.append("\nDocument type: ").append(this.documentType)
				.append("\nSpecial status: ").append(this.specialStatus);
		return sb.toString();
	}
}
