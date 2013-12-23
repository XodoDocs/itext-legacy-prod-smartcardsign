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
package com.itextpdf.smartcard.util;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

import com.itextpdf.text.log.Logger;
import com.itextpdf.text.log.LoggerFactory;


/**
 * Class that reads bytes from a SmartCard.
 */
public class SmartCardIO {
	/** Logger instance. */
	private final static Logger LOGGER = LoggerFactory.getLogger(SmartCardIO.class);
	
	/** Block size */
	private static final int BLOCK_SIZE = 0xff;
	

	/**
	 * Selects a file on a card, reads it, and returns the bytes.
	 * @param channel	The CardChannel.
	 * @param fileId	A file ID referring to the file you want to read.
	 * @return	a byte array containing the file
	 * @throws CardException
	 * @throws IOException
	 */
	public static synchronized byte[] readFile(CardChannel channel, byte[] fileId) throws CardException, IOException{
		selectFile(channel, fileId);
		byte[] data = readBinary(channel);
		LOGGER.info("Done reading...");
		return data;
	}
	
	/**
	 * Selects a file on the card.
	 * @param channel	The CardChannel.
	 * @param fileId	A file ID referring to the file you want to read.
	 * @throws CardException
	 * @throws FileNotFoundException
	 */
	private static void selectFile(CardChannel channel, byte[] fileId) throws CardException, FileNotFoundException{
		LOGGER.info("Selecting file...");
		// Create a command to select a file
		CommandAPDU selectFileApdu = new CommandAPDU(
				IsoIec7816.CLA_00, IsoIec7816.INS_SELECT,
				IsoIec7816.P1_SELECT_FROM_MF,
				IsoIec7816.P2_ONLY_OCCURRENCE_NO_RESPONSE_DATA,
				fileId);
		// execute the command
		ResponseAPDU responseApdu = transmit(channel, selectFileApdu);
		int sw = responseApdu.getSW();
		if(sw != IsoIec7816.SW_NO_FURTHER_QUALIFICATION){
			throw new FileNotFoundException(
					"Wrong status after selecting file: 0x"
					+ Integer.toHexString(sw));
		}
		// some time is needed after a select file command
		try{
			Thread.sleep(200);
		} catch(InterruptedException e){
			throw new CardException(e);
		}
	}
	
	/**
	 * Reads binary data from a card after you've selected a file.
	 * @param channel	The CardChannel.
	 * @return	a byte array containing the file
	 * @throws CardException
	 * @throws IOException
	 */
	private static byte[] readBinary(CardChannel channel) throws CardException,IOException{
		LOGGER.info("Reading binary...");
		int offset = 0;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] data;
		do {
			// create command read a block of bytes
			CommandAPDU readBinaryApdu = new CommandAPDU(
					IsoIec7816.CLA_00, IsoIec7816.INS_READ_BINARY,
					offset >> 8, offset & 0xFF, BLOCK_SIZE);
			// execute the command
			ResponseAPDU responseApdu = transmit(channel, readBinaryApdu);
			// check the status bytes as a single word
			int sw = responseApdu.getSW();
			if(sw == IsoIec7816.SW_WRONG_PARAMETERS)
				break;
			if(sw != IsoIec7816.SW_NO_FURTHER_QUALIFICATION)
				throw new IOException("APDU response error: 0x" + Integer.toHexString(sw));
			// get the data
			data = responseApdu.getData();
			baos.write(data);
			offset += data.length;
		} while (BLOCK_SIZE == data.length);
		
		return baos.toByteArray();
	}

	/**
	 * Communicates with a smart card using an
	 * application protocol data unit command and response.
	 * @param channel		The CardChannel.
	 * @param commandApdu	The CommandAPDU send to the card
	 * @return The ResponseAPDU received from the card
	 * @throws CardException
	 */
	public static ResponseAPDU transmit(CardChannel channel, CommandAPDU commandApdu)
			throws CardException {
		LOGGER.info("start transmitting...");
		ResponseAPDU responseApdu = channel.transmit(commandApdu);
		if (IsoIec7816.SW1_ABORTED == responseApdu.getSW1()) {
			/*
			 * A minimum delay of 10 msec between the answer and the
			 * next APDU is mandatory for eID v1.0 and v1.1 cards.
			 */
			LOGGER.info("sleeping...");
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				throw new CardException(e);
			}
			responseApdu = channel.transmit(commandApdu);
		}
		return responseApdu;
	}
}
