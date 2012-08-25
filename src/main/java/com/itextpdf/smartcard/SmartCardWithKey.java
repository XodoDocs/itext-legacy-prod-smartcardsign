/*
 * $Id:   $
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

import com.itextpdf.smartcard.util.DigestAlgorithms;
import com.itextpdf.smartcard.util.IsoIec7816;
import com.itextpdf.smartcard.util.PinVerification;
import com.itextpdf.smartcard.util.SmartCardIO;
import com.itextpdf.text.log.Logger;
import com.itextpdf.text.log.LoggerFactory;

/**
 * A generic smart card object for smart cards containing a key that can be used for signing.
 */
public class SmartCardWithKey extends SmartCard {

	/** Logger instance. */
	private final static Logger LOGGER = LoggerFactory.getLogger(SmartCardWithKey.class);

	/** The id for the key that will be used to sign. */
	protected byte keyId;
	
	/** The encryption algorithm (e.g. "RSA"). */
	protected String encryptionAlgorithm;
	
	/** The pin provider (if necessary) */
	protected PinProvider pinProvider;
	
	/** Forces secure signing. */
	protected boolean secure = false;
	
	/** Features available on the smart card / reader. */
	protected Features features = null;
	
	/**
	 * Creates a SmartCardWithKey instance.
	 * @param cardTerminal	the terminal holding the card
	 * @param keyId		the id for the key that will be used for signing
	 * @param encryptionAlgorithm	the encryption algorithm used for the key
	 * @throws CardException
	 */
	public SmartCardWithKey(CardTerminal cardTerminal, byte keyId, String encryptionAlgorithm)
		throws CardException {
		super(cardTerminal);
		this.keyId = keyId;
		this.encryptionAlgorithm = encryptionAlgorithm;
	}
	
	/**
	 * Returns the encryption algorithm used for the private key.
	 * @return	an encryption algorithm (e.g. "RSA")
	 */
	public String getEncryptionAlgorithm() {
		return encryptionAlgorithm;
	}
	
	/**
	 * Reads an X509 Certificate from the card.
	 * @param fileID	the fileID for the certificate
	 * @return	an X506Certificate object.
	 * @throws CertificateException
	 * @throws CardException
	 * @throws IOException
	 */
	public X509Certificate readCertificate(byte[] fileID) throws CertificateException, CardException, IOException{
		byte[] certificateFile = readFile(fileID);
		CertificateFactory factory = CertificateFactory.getInstance("X.509");
		return (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(certificateFile));
	}
	
	/**
	 * Sets the pin provider.
	 * @param pinProvider	an implementation of the PinProvider interface
	 */
	public void setPinProvider(PinProvider pinProvider) {
		LOGGER.info("Setting pin provider: " + pinProvider.getClass().getName());
		this.pinProvider = pinProvider;
	}
	
	/**
	 * Tells the SmartCardWithKey if signing can only be done using a secure
	 * smart card reader with a pin pad.
	 * @param	secure	true if you want secure signing.
	 */
	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	/**
	 * Gets the command for a specific feature.
	 * @param	feature	the feature for which you want the corresponding command
	 * @return	the command that corresponds with the feature
	 * @throws CardException 
	 */
	public Integer getFeature(byte feature) throws CardException {
		LOGGER.info("get feature " + Integer.toHexString(feature));
		if (features == null) {
			features = new Features(this);
		}
		return features.get(feature);
	}
	
	/**
	 * Verifies the PIN code. Note that we don't pass the PIN to this method.
	 * Either the pin provider parameter will be used to get the PIN,
	 * or the smart card reader will ask the pin code on its pin pad (if available).
	 * @param retries	the current number of retries left (use -1 if you don't know)
	 * @return	the number of retries left after verifying the PIN
	 * @throws CardException
	 * @throws IOException
	 */
	public int verifyPin(int retries) throws CardException, IOException {
		LOGGER.info("verify PIN");
		Integer verifyPinDirectCommand = getFeature(Features.FEATURE_VERIFY_PIN_DIRECT_TAG);
		Integer verifyPinStartCommand = getFeature(Features.FEATURE_VERIFY_PIN_START_TAG);
		return verifyPin(verifyPinDirectCommand, verifyPinStartCommand, retries);
	}
	
	/**
	 * Verifies the PIN code.
	 * @param verifyPinDirectCommand	the command used to verify the pin
	 * on the pin pad of the card reader (might not be possible)
	 * @param verifyPinStartCommand		the command used to start verifying
	 * the pin using the pin pad of the card reader
	 * @param retries	the number of retries left (use -1 if you don't know)
	 * @return	the number of retries left
	 * @throws IOException
	 * @throws CardException
	 */
	private int verifyPin(Integer verifyPinDirectCommand, Integer verifyPinStartCommand, int retries) throws IOException, CardException {
		LOGGER.info("verifying PIN");
		ResponseAPDU responseAPDU;
		int sw = 0;
		while (sw != IsoIec7816.SW_NO_FURTHER_QUALIFICATION) {
			if (verifyPinDirectCommand != null) {
				LOGGER.info("verifying PIN on the pin pad directly");
				responseAPDU = PinVerification.verifyPinDirect(this, verifyPinDirectCommand);
			}
			else if (verifyPinStartCommand != null) {
				LOGGER.info("start verifying PIN on the pin pad");
				responseAPDU = PinVerification.verifyPinStart(this, verifyPinStartCommand);
			}
			else if (pinProvider != null) {
				LOGGER.info("verifying PIN using pin provider");
				responseAPDU = PinVerification.verifyPin(this, pinProvider, retries);
			}
			else {
				throw new CardException("Unable to retrieve PIN");
			}
			sw = responseAPDU.getSW();
			if (sw != IsoIec7816.SW_NO_FURTHER_QUALIFICATION) {
				LOGGER.warn("verifying PIN didn't succeed " + Integer.toHexString(sw));
				if (sw == IsoIec7816.SW_AUTHENTICATION_METHOD_BLOCKED) {
					throw new IOException("Pin is blocked");
				}
				if (responseAPDU.getSW1() != IsoIec7816.SW1_WARNING) {
					throw new IOException("Pin error: " + Integer.toHexString(sw));
				}
				LOGGER.error("Wrong pin");
				return responseAPDU.getSW2() & 0xF;
			}
		}
		LOGGER.info("PIN verified");
		return retries;
	}
	
	/**
	 * Signs a message digest on the smart card.
	 * @param digest	the message digest
	 * @param algorithm	the	algorithm used to create the message digest
	 * @return	a signed digest
	 * @throws CardException
	 * @throws IOException
	 */
	public byte[] sign(byte[] digest, String algorithm) throws CardException, IOException {
		LOGGER.info("Signing a digest created with " + algorithm);
		Integer verifyPinDirectCommand = getFeature(Features.FEATURE_VERIFY_PIN_DIRECT_TAG);
		Integer verifyPinStartCommand = getFeature(Features.FEATURE_VERIFY_PIN_START_TAG);
		Integer eIDPinPadReaderCommand = getFeature(Features.FEATURE_EID_PIN_PAD_READER_TAG);
		if (eIDPinPadReaderCommand != null) {
			LOGGER.info("Smart card reader with eID-aware pin pad!");
		}
		if (secure && verifyPinDirectCommand == null && verifyPinStartCommand == null) {
			LOGGER.info("Reader doesn't allow secure PIN entry");
			throw new CardException("No secure reader detected.");
		}
		
		byte algobyte;
		if ("SHA-1-PSS".equals(algorithm)) {
			algobyte = 0x10;
		} else if ("SHA-256-PSS".equals(algorithm)) {
			algobyte = 0x20;
		} else {
			algobyte = 0x01;
		}
		byte[] data = new byte[] {
				0x04, // Length
				(byte)0x80, algobyte, (byte) 0x84,
				keyId
		};
		
		LOGGER.info("Manage security environment");
		CommandAPDU commandAPDU = new CommandAPDU(
				IsoIec7816.CLA_00, IsoIec7816.INS_MANAGE_SECURITY_ENVIRONMENT,
				IsoIec7816.P1_COMPUTATION_SET, IsoIec7816.P2_CRT_DIGITAL_SIGNATURE,
				data);
		ResponseAPDU responseAPDU = SmartCardIO.transmit(channel, commandAPDU);
		
		if (responseAPDU.getSW() != IsoIec7816.SW_NO_FURTHER_QUALIFICATION) {
			throw new CardException("Incorrect response: " + Integer.valueOf(responseAPDU.getSW()));
		}
		
		LOGGER.info("Verify PIN for signing");
		int retries = verifyPin(verifyPinDirectCommand, verifyPinStartCommand, -1);

		LOGGER.info("Creating bytes for signing");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (algobyte == 0x01) {
			byte[] prefix = DigestAlgorithms.DIGESTS.get(algorithm);
			if (prefix != null) {
				baos.write(prefix);
			}
			else if (DigestAlgorithms.PLAIN_TEXT.equals(algorithm)) {
				System.out.println(digest.length);
				prefix = Arrays.copyOf(
						DigestAlgorithms.PLAIN_TEXT_PREFIX,
						DigestAlgorithms.PLAIN_TEXT_PREFIX.length);
				prefix[1] = (byte) (digest.length + 13);
				prefix[14] = (byte) digest.length;
				System.out.println(prefix[14]);
				baos.write(prefix);
			}
		}
		baos.write(digest);
		
		LOGGER.info("Sign the bytes");
		commandAPDU = new CommandAPDU(
				IsoIec7816.CLA_00, IsoIec7816.INS_PERFORM_SECURITY_OPERATION,
				IsoIec7816.P1_DIGITAL_SIGNATURE, IsoIec7816.P2_INPUT_DATA,
				baos.toByteArray());
		responseAPDU = SmartCardIO.transmit(channel, commandAPDU);
		
		int sw = responseAPDU.getSW();
		// A pin is needed, and it isn't cached on the reader
		if (sw == IsoIec7816.SW_SECURITY_STATUS_NOT_SATISFIED) {
			LOGGER.info("Pin code couldn't be verified");
			retries = verifyPin(verifyPinDirectCommand, verifyPinStartCommand, retries);
			responseAPDU = SmartCardIO.transmit(channel, commandAPDU);
			sw = responseAPDU.getSW();
		}
		if (sw == IsoIec7816.SW_NO_FURTHER_QUALIFICATION) {
			LOGGER.info("Signing done");
			return responseAPDU.getData();
		}
		else {
			throw new IOException("Digest could not be signed " + Integer.toHexString(sw));
		}
	}
}
