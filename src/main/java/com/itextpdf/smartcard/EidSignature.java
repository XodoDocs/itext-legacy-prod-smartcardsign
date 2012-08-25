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
package com.itextpdf.smartcard;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

import javax.smartcardio.CardException;

import com.itextpdf.text.pdf.security.ExternalSignature;

/**
 * Implementation of the ExternalSignature interface.
 * Instead of signing using the PdfPKCS7 class,
 * we'll create the signature on a smart card.
 */
public class EidSignature implements ExternalSignature {

	/** You need a SmartCardWithKey object to sign. */
	protected SmartCardWithKey card;
	/** You need to pick a digest algorithm for the encryption. */
	protected String hashAlgorithm;
	/** You can pick a provider to create the digest. */
	protected String provider;
	
	/**
	 * Creates an EidSignature object that can be passed to MakeSignature.
	 * @param card		a SmartCardWithKey instance
	 * @param hashAlgorithm	a digest algorithm (e.g. "SHA256")
	 * @param provider	a provider (e.g. "BC")
	 */
	public EidSignature(SmartCardWithKey card, String hashAlgorithm, String provider) {
		this.card = card;
		this.hashAlgorithm = hashAlgorithm;
		this.provider = provider;
	}
	
	/**
	 * This method will do the actual signing.
	 * @see com.itextpdf.text.pdf.security.ExternalSignature#sign(byte[])
	 */
	public byte[] sign(byte[] digest) throws GeneralSecurityException {
		try {
	        MessageDigest messageDigest = MessageDigest.getInstance(hashAlgorithm, provider);
	        byte sh[] = messageDigest.digest(digest);
			return card.sign(sh, hashAlgorithm);
		} catch (CardException e) {
			throw new GeneralSecurityException(e);
		} catch (IOException e) {
			throw new GeneralSecurityException(e);
		}
	}

	/**
	 * Getter for the encryption algorithm.
	 * @see com.itextpdf.text.pdf.security.ExternalSignature#getEncryptionAlgorithm()
	 */
	public String getEncryptionAlgorithm() {
		return card.getEncryptionAlgorithm();
	}

	/**
	 * Getter for the hashing algorithm.
	 * @see com.itextpdf.text.pdf.security.ExternalSignature#getHashAlgorithm()
	 */
	public String getHashAlgorithm() {
		return hashAlgorithm;
	}
}
