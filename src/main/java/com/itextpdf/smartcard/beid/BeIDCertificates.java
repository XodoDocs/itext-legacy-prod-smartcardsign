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
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.LinkedList;
import java.util.List;

import javax.smartcardio.CardException;

import com.itextpdf.smartcard.SmartCardWithKey;
import com.itextpdf.text.log.Logger;
import com.itextpdf.text.log.LoggerFactory;

/**
 * The Belgian eID card can be used to add digital signatures using specific certificates.
 */
public class BeIDCertificates {

	/** Logger instance. */
	private final static Logger LOGGER = LoggerFactory.getLogger(BeIDCertificates.class);
	
	/** The ID for the authentication certificate. */
	public static final byte AUTHENTICATION_KEY_ID = (byte) 0x82;
	
	/** The ID for the non-repudiation certificate. */
	public static final byte NON_REPUDIATION_KEY_ID = (byte) 0x83;
	
	/** Certificate file ID for the sign certificate (non-repudiation). */
	public static final byte[] SIGN_CERT_FILE_ID = new byte[] { 0x3F, 0x00,
		(byte) 0xDF, 0x00, 0x50, 0x39 };
	
	/** Certificate file ID for the CA certificate. */
	public static final byte[] CA_CERT_FILE_ID = new byte[] { 0x3F, 0x00,
		(byte) 0xDF, 0x00, 0x50, 0x3A };
	
	/** Certificate file ID for the root certificate. */
	public static final byte[] ROOT_CERT_FILE_ID = new byte[] { 0x3F, 0x00,
		(byte) 0xDF, 0x00, 0x50, 0x3B };

	/** Certificate file ID for the authentication. */
	public static final byte[] AUTHN_CERT_FILE_ID = new byte[] { 0x3F, 0x00,
		(byte) 0xDF, 0x00, 0x50, 0x38 };

	/** Certificate file ID for the national registry certificate. */
	public static final byte[] RRN_CERT_FILE_ID = new byte[] { 0x3F, 0x00,
		(byte) 0xDF, 0x00, 0x50, 0x3C };
	
	/**
	 * Generates a certificate chain that can be used for signing
	 * @param card	an instance of the BeIDCard
	 * @return	a List of X509
	 * @throws CertificateException
	 * @throws CardException
	 * @throws IOException
	 */
	public static Certificate[] getSignCertificateChain(SmartCardWithKey card) throws CertificateException, CardException, IOException{
		LOGGER.info("creating sign certificate chain...");
		List<X509Certificate> signCertificateChain = new LinkedList<X509Certificate>();
		
		LOGGER.info("reading sign certificate...");
		signCertificateChain.add(card.readCertificate(BeIDCertificates.SIGN_CERT_FILE_ID));
		
		LOGGER.info("reading CA certificate...");
		signCertificateChain.add(card.readCertificate(BeIDCertificates.CA_CERT_FILE_ID));
		
		LOGGER.info("reading Root CA certificate...");
		signCertificateChain.add(card.readCertificate(BeIDCertificates.ROOT_CERT_FILE_ID));
		
		Certificate[] certs = new Certificate[signCertificateChain.size()];
		int i = 0;
		for (X509Certificate c : signCertificateChain) {
			certs[i++] = c;
		}
		return certs;
	}
}
