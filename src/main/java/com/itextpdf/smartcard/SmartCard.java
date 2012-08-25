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
import java.util.Arrays;

import javax.smartcardio.ATR;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardNotPresentException;
import javax.smartcardio.CardTerminal;

import com.itextpdf.smartcard.util.SmartCardIO;
import com.itextpdf.text.log.Logger;
import com.itextpdf.text.log.LoggerFactory;

/**
 * A Generic SmartCard object.
 */
public class SmartCard {

	/** Logger instance. */
	private final static Logger LOGGER = LoggerFactory.getLogger(SmartCard.class);
	
	/** A smart card object */
	protected Card card;
	
	/** A channel connection to a smart card */
	protected CardChannel channel;
	
	/** Answer to Reset */
	protected ATR atr;

	/**
	 * Super-constructor for creating a SmartCard object, the purpose of this
	 * constructor is to connect to the card present in the card terminal
	 * provided as a parameter and get the basic card channel for use in future
	 * code.
	 * 
	 * @param cardTerminal
	 *            the CardTerminal which holds the smartcard
	 * @throws CardException
	 * @throws NoValidCardException 
	 */
	public SmartCard(CardTerminal cardTerminal) throws CardException {
		if (cardTerminal.isCardPresent()) {
			try {
				LOGGER.info("trying to connect to card terminal: "
						+ cardTerminal.getName());
				card = cardTerminal.connect("*");
				atr = card.getATR();
				if (!isValidCard()) {
					throw new CardException("The card doesn't match with the expected pattern.");
				}
				channel = card.getBasicChannel();
			} catch (CardException e) {
				LOGGER.error("couldn't connect to card terminal: "
						+ cardTerminal.getName() + ", " + e.getMessage());
				Throwable cause = e.getCause();
				if (null != cause) {
					LOGGER.error("cause: " + cause.getMessage());
					LOGGER.error("cause type: "
							+ cause.getClass().getName());
				}

			}
		} else {
			LOGGER.error("No Card present in card terminal: "
					+ cardTerminal.getName());
			throw new CardNotPresentException("No card present");
		}
	}
	
	/**
	 * @see java.lang.Object#finalize()
	 */
	protected void finalize() throws Throwable {
		LOGGER.info("Garbage Collecting SmartCard");
		disconnect();
	}

	/**
	 * Disconnects the card from the application.
	 * @throws CardException
	 */
	public void disconnect() throws CardException{
		LOGGER.info("Disconnecting card");
		card.disconnect(true);
		card = null;
		atr = null;
		channel = null;
	}

	/**
	 * Tries to match the ATR with a specific pattern.
	 */
	private boolean isValidCard() throws CardException {
		LOGGER.info("Checking card ATR");
		byte[] pattern = getPattern();
		if (pattern == null)
			return true;
		byte[] mask = getMask();
		byte[] atrBytes = atr.getBytes();
		if (atrBytes.length != pattern.length) {
			LOGGER.error("Not a valid card!");
			return false;
		}
		if (mask != null) {
			for (int idx = 0; idx < atrBytes.length; idx++) {
				atrBytes[idx] &= mask[idx];
			}
		}
		if (Arrays.equals(atrBytes, pattern)) {
			LOGGER.info("smartcard is valid");
			return true;
		}
		LOGGER.error("Not a valid card!");
		return false;
	}
	
	/**
	 * Returns a pattern that is specific for the smart card implementation.
	 * @param	a specific pattern or null for the generic SmartCard object.
	 */
	public byte[] getPattern() {
		return null;
	}

	/**
	 * Returns a mask for the pattern that is specific for the smart card implementation.
	 * @param	a specific pattern or null if the pattern needs to be an exact match.
	 */
	public byte[] getMask() {
		return null;
	}
	
	/**
	 * Reads a file from the card.
	 * @throws IOException 
	 * @throws CardException 
	 */
	public byte[] readFile(byte[] fileId) throws CardException, IOException {
		return SmartCardIO.readFile(getChannel(), fileId);
	}
	
	/**
	 * Gets the Card object.
	 */
	public Card getCard() {
		return card;
	}

	/**
	 * Gets the card channel.
	 */
	public CardChannel getChannel() {
		return channel;
	}
}
