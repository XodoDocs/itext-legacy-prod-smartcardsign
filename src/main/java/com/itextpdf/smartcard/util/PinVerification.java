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
 * 
 * 
 * Some methods are taken from https://code.google.com/p/eid-applet/
 * More specifically createPINVerificationDataStructure() and parts
 * of the verifyPin() methods. The latter methods were rewritten to
 * match with ISO/IEC 7816 terminology.
 * 
 * The eID applet project is:
 * Copyright (C) 2008-2009 FedICT.
 * FedICT is a Belgian Governmental department
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License version
 * 3.0 as published by the Free Software Foundation.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, see 
 * http://www.gnu.org/licenses/.
 */
package com.itextpdf.smartcard.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

import com.itextpdf.smartcard.Features;
import com.itextpdf.smartcard.PinProvider;
import com.itextpdf.smartcard.SmartCardWithKey;
import com.itextpdf.text.log.Logger;
import com.itextpdf.text.log.LoggerFactory;

/**
 * Class that handles the pin verification.
 */
public class PinVerification {

	/** Logger instance. */
	private final static Logger LOGGER = LoggerFactory.getLogger(PinVerification.class);
	
	/** Minimum pin siz.e */
	public static final int MIN_PIN_SIZE = 4;

	/** Maximum pin size. */
	public static final int MAX_PIN_SIZE = 12;

	/**
	 * Verifies the pin on the smart card reader.
	 * @param card	the SmartCardWithKey instance
	 * @param verifyPinDirectCommand	the command expected by the smart card reader to verify the pin.
	 * @return	a ResponseAPDU
	 * @throws CardException
	 * @throws IOException
	 */
	public static ResponseAPDU verifyPinDirect(SmartCardWithKey card,
			Integer verifyPinDirectCommand) throws CardException, IOException {
		LOGGER.info("Verify PIN direct");
		byte[] commandData = createPINVerificationDataStructure(IsoIec7816.INS_VERIFY_DATA);
		byte[] result = card.getCard().transmitControlCommand(verifyPinDirectCommand, commandData);
		ResponseAPDU responseAPDU = new ResponseAPDU(result);
		if (responseAPDU.getSW() == IsoIec7816.SW_USER_ABORTED) {
			LOGGER.warn("Pin entry cancelled by user");
			throw new CardException("cancelled by user");
		} else if (responseAPDU.getSW() == IsoIec7816.SW_TIMEOUT) {
			LOGGER.warn("Pin entry timed out");
		}
		LOGGER.info("PIN verified");
		return responseAPDU;
	}

	/**
	 * Starts verifying the pin.
	 * @param card	the SmartCardWithKey instance
	 * @param verifyPinStartCommand	the command expected by the smart card reader to start verifying the pin
	 * @return	a ResponseAPDU
	 * @throws IOException
	 * @throws CardException
	 */
	public static ResponseAPDU verifyPinStart(SmartCardWithKey card,
			Integer verifyPinStartCommand) throws IOException, CardException {
		LOGGER.info("Verify PIN direct");
		byte[] commandData = createPINVerificationDataStructure(IsoIec7816.INS_VERIFY_DATA);
		card.getCard().transmitControlCommand(verifyPinStartCommand, commandData);
		try {
			waitForPin(card);
		} catch (InterruptedException e) {
			throw new IOException(e.getMessage());
		}
		int feature = card.getFeature(Features.FEATURE_VERIFY_PIN_FINISH_TAG);
		byte[] result = card.getCard().transmitControlCommand(feature, new byte[0]);
		LOGGER.info("PIN verified");
		return new ResponseAPDU(result);
	}

	/**
	 * Verifies the pin using a PinProvider.
	 * @param card	the SmartCardWithKey instance
	 * @param pinProvider	class that contains a method to retrieve a pin code
	 * @param retries	the number of retries that can be showed on the PinProvider
	 * @return a ResponseAPDU
	 * @throws CardException
	 */
	public static ResponseAPDU verifyPin(SmartCardWithKey card, PinProvider pinProvider, int retries) throws CardException {
		LOGGER.info("Obtaining pin code");
		char[] pin = pinProvider.getPin(retries);
		byte[] verifyData = new byte[] { (byte) (0x20 | pin.length),
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
		for (int idx = 0; idx < pin.length; idx += 2) {
			char digit1 = pin[idx];
			char digit2;
			if (idx + 1 < pin.length) {
				digit2 = pin[idx + 1];
			} else {
				digit2 = '0' + 0xf;
			}
			byte value = (byte) (byte) ((digit1 - '0' << 4) + (digit2 - '0'));
			verifyData[idx / 2 + 1] = value;
		}
		Arrays.fill(pin, (char) 0);
		
		LOGGER.info("Verifying pin code");
		CommandAPDU commandAPDU = new CommandAPDU(
				IsoIec7816.CLA_00, IsoIec7816.INS_VERIFY_DATA,
				IsoIec7816.P1_00, 0x01, verifyData
				);
		try {
			ResponseAPDU responseApdu = SmartCardIO.transmit(card.getChannel(), commandAPDU);
			return responseApdu;
		} finally {
			Arrays.fill(verifyData, (byte) 0);
		}
	}
	
	
	/**
	 * Creates a PIN verification data structure that can be used as parameter for a transmit command.
	 * @param ins should be 0x20 or 0x21 for PIN codes, 
	 * @return a byte array that can be used as command data for verifying a pin code
	 * @throws IOException
	 */
	public static byte[] createPINVerificationDataStructure(int ins)
			throws IOException {
		LOGGER.info("Create pin verification data structure");
		ByteArrayOutputStream verifyCommand = new ByteArrayOutputStream();
		verifyCommand.write(30); // bTimeOut
		verifyCommand.write(30); // bTimeOut2
		verifyCommand.write(0x80 | 0x08 | 0x00 | 0x01); // bmFormatString
		/*
		 * bmFormatString. bit 7: 1 = system units are bytes
		 * 
		 * bit 6-3: 1 = PIN position in APDU command after Lc, so just after the
		 * 0x20 | pinSize.
		 * 
		 * bit 2: 0 = left justify data
		 * 
		 * bit 1-0: 1 = BCD
		 */
		verifyCommand.write(0x47); // bmPINBlockString
		/*
		 * bmPINBlockString
		 * 
		 * bit 7-4: 4 = PIN length
		 * 
		 * bit 3-0: 7 = PIN block size (7 times 0xff)
		 */
		verifyCommand.write(0x04); // bmPINLengthFormat
		/*
		 * bmPINLengthFormat.
		 * 
		 * bit 7-5: 0 = RFU
		 * 
		 * bit 4: 0 = system units are bits
		 * 
		 * bit 3-0: 4 = PIN length position in APDU
		 */
		verifyCommand.write(new byte[] { (byte) MAX_PIN_SIZE,
				(byte) MIN_PIN_SIZE }); // wPINMaxExtraDigit
		/*
		 * first byte = maximum PIN size in digit
		 * 
		 * second byte = minimum PIN size in digit.
		 */
		verifyCommand.write(0x02); // bEntryValidationCondition
		/*
		 * 0x02 = validation key pressed. So the user must press the green
		 * button on his pinpad.
		 */
		verifyCommand.write(0x01); // bNumberMessage
		/*
		 * 0x01 = message with index in bMsgIndex
		 */
		verifyCommand.write(new byte[] { Environment.getLanguageId(), 0x04 }); // wLangId
		/*
		 * 0x04 = default sub-language
		 */
		verifyCommand.write(0x00); // bMsgIndex
		/*
		 * 0x00 = PIN insertion prompt
		 */
		verifyCommand.write(new byte[] { 0x00, 0x00, 0x00 }); // bTeoPrologue
		/*
		 * bTeoPrologue : only significant for T=1 protocol.
		 */
		byte[] verifyAPDU = new byte[] {
				0x00, // CLA
				(byte) ins, // INS
				0x00, // P1
				0x01, // P2
				0x08, // Lc = 8 bytes in command data
				(byte) 0x20, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
		verifyCommand.write(verifyAPDU.length & 0xff); // ulDataLength[0]
		verifyCommand.write(0x00); // ulDataLength[1]
		verifyCommand.write(0x00); // ulDataLength[2]
		verifyCommand.write(0x00); // ulDataLength[3]
		verifyCommand.write(verifyAPDU); // abData
		byte[] verifyCommandData = verifyCommand.toByteArray();
		return verifyCommandData;
	}
	
	/**
	 * Method that expects the end user to press keys on a pin pad to enter a pin.
	 * @param card	the SmartCardWithKey instance
	 * @throws CardException
	 * @throws InterruptedException
	 */
	private static void waitForPin(SmartCardWithKey card) throws CardException, InterruptedException {
		LOGGER.info("Waiting for pin");
		int feature = card.getFeature(Features.FEATURE_GET_KEY_PRESSED_TAG);
		boolean busy = true;
		while (busy) {
			byte[] getKeyPressedResult = card.getCard().transmitControlCommand(
					feature, new byte[0]);
			byte key = getKeyPressedResult[0];
			switch (key) {
			case 0x00:
				Thread.sleep(200);
				break;
			case 0x2b:
				LOGGER.info("PIN Digit");
				break;
			case 0x0a:
				LOGGER.info("erase PIN digit");
				break;
			case 0x0d:
				LOGGER.info("USER CONFIRMED!");
				busy = false;
				break;
			case 0x1b:
				LOGGER.warn("user canceled");
				throw new CardException("canceled by user");
			case 0x40:
				LOGGER.warn("PIN Abort");
				busy = false;
				break;
			default:
				LOGGER.info("Key pressed result: " + key
						+ " hex: " + Integer.toHexString(key));
			}
		}
	}
}
