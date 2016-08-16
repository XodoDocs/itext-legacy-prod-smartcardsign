/*
 * $Id$
 *
 * This file is part of the iText (R) project.
 * Copyright (c) 1998-2014 iText Group NV
 * Author: Bruno Lowagie
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3
 * as published by the Free Software Foundation with the addition of the
 * following permission added to Section 15 as permitted in Section 7(a):
 * FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
 * ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
 * OF THIRD PARTY RIGHTS
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

import javax.smartcardio.Card;
import javax.smartcardio.CardException;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lists the features available on the smart card reader with the smart card.
 */
public class Features {

	/** Logger instance. */
	private final static Logger LOGGER = LoggerFactory.getLogger(Features.class);
	
	/** The control code to query the features */
	public static int CONTROL_CODE_QUERY_FEATURES = createControlCode(0xD48);
	
	/** A feature code. */
	public static final byte FEATURE_VERIFY_PIN_START_TAG = 0x01;
	/** A feature code. */
	public static final byte FEATURE_VERIFY_PIN_FINISH_TAG = 0x02;
	/** A feature code. */
	public static final byte FEATURE_MODIFY_PIN_START_TAG = 0x03;
	/** A feature code. */
	public static final byte FEATURE_MODIFY_PIN_FINISH_TAG = 0x04;
	/** A feature code. */
	public static final byte FEATURE_GET_KEY_PRESSED_TAG = 0x05;
	/** A feature code. */
	public static final byte FEATURE_VERIFY_PIN_DIRECT_TAG = 0x06;
	/** A feature code. */
	public static final byte FEATURE_MODIFY_PIN_DIRECT_TAG = 0x07;
	/** A feature code. */
	public static final byte FEATURE_EID_PIN_PAD_READER_TAG = (byte) 0x80;

	/** The map containing the features available on a specific smart card / reader. */
	protected Map<Byte, Integer> features = new HashMap<Byte, Integer>();
	
	/**
	 * Creates a map of features available on a SmartCard.
	 * @param smartCard	a SmartCard instance
	 */
	public Features(SmartCard smartCard) {
		Card card = smartCard.getCard();
		LOGGER.info("Transmitting command: " + Integer.toHexString(CONTROL_CODE_QUERY_FEATURES));
		try {
			byte[] b = card.transmitControlCommand(
				CONTROL_CODE_QUERY_FEATURES, new byte[0]);
			for (int i = 0; i < b.length; i += 6) {
				Byte feature = new Byte(b[i]);
				Integer command = new Integer((0xff & b[i + 2]) << 24)
		              | ((0xff & b[i + 3]) << 16)
		              | ((0xff & b[i + 4]) << 8)
		              | (0xff & b[i + 5]);
				LOGGER.info("Found feature " + Integer.toHexString(feature) + " command: " + Integer.toHexString(command));
				features.put(feature, command);
			}
		}
		catch (CardException e) {
			LOGGER.warn("Features couldn't be read: " + e.getMessage());
		}			
	}
	
	/**
	 * Gets the command code for a specific feature.
	 * @param feature	the feature for which you want the command code
	 * @return	the corresponding command code
	 */
	public Integer get(byte feature) {
		LOGGER.info("Get feature " + Integer.toHexString(feature));
		return features.get(feature);
	}
	
	/**
	 * Helper method that creates a control code (depending on
	 * the operating system).
	 * @param code	the code that needs to be adapted to the OS.
	 * @return	a code adapted to the OS.
	 */
	public static int createControlCode(int code) {
	    int ioctl;
	    String os_name = System.getProperty("os.name").toLowerCase();
	    if (os_name.indexOf("windows") > -1) {
	      ioctl = (0x31 << 16 | (code) << 2);
	    } else {
	      ioctl = 0x42000000 + (code);
	    }
	    return ioctl;
	}
}
