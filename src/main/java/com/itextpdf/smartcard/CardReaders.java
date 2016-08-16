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

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.TerminalFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that creates a list of all connected card readers and
 * that allows you to get a list of <code>CardReaderTerminal</code>
 * objects of card readers containing a smart card.
 */
public class CardReaders {

	/** Logger instance. */
	private final static Logger LOGGER = LoggerFactory.getLogger(CardReaders.class.getName());
	
	/** List containing the card readers connected to the device running the app. */
	private List<CardTerminal> readers = null;

	/**
	 * Creates a card reader instance, initializing the static list of card readers.
	 * @throws CardException 
	 */
	public CardReaders() throws CardException {
		try {
			LOGGER.info( "Creating CardReader object...");
			linuxPcscLibraryConfig();
			LOGGER.info("Initializing Smart Card Readers...");
			initialize();
		} catch (CardException e) {
			LOGGER.error("CARD EXCEPTION: " + e.getMessage());
			throw new CardException("Init exception: " + e.getMessage());
		}
	}

	/**
	 * Initializes the cardreaders connected to the system.
	 * 
	 * @throws CardException
	 */
	public void initialize() throws CardException {
		LOGGER.info("Detecting smart card readers...");
		TerminalFactory factory = TerminalFactory.getDefault();
		CardTerminals cardTerminals = factory.terminals();
		try {
			readers = cardTerminals.list();
		} catch (CardException e) {
			LOGGER.error("error on card terminal list: " + e.getMessage());
			Throwable cause = e.getCause();
			if (null != cause) {
				LOGGER.error("cause: " + cause.getMessage());
				LOGGER.error("cause type: " + cause.getClass().getName());
			}
			throw new CardException("list error");
		}
		LOGGER.info("Number of readers found: " + readers.size());
	}

	/**
	 * Returns a list with all card readers connected to the device.
	 * @return	a list with readers.
	 */
	public List<CardTerminal> getReaders() {
		return readers;
	}
	
	/**
	 * returns a list with smart card readers containing a smart card.
	 * @return a list with readers containing a smart card.
	 * @throws CardException 
	 */
	public List<CardTerminal> getReadersWithCard() {
		List<CardTerminal> list = new ArrayList<CardTerminal>();
		if (readers != null) {
			for (CardTerminal cardTerminal : readers) {
				LOGGER.info("Checking " + cardTerminal.getName());
				try {
					if (cardTerminal.isCardPresent()) {
						list.add(cardTerminal);
						LOGGER.info("cardTerminal added to list...");
					} else {
						LOGGER.warn("cardTerminal not added to list...");
					}
				} catch (CardException e) {
					LOGGER.error(e.getMessage());
				}
			}
		}
		return list;
	}

	// for Linux only:
	
	/**
	 * Linux uses libpcsc, this method tries to find it and set the
	 * sun.security.smartcardio.library with it
	 */
	private static void linuxPcscLibraryConfig() {
		String osName = System.getProperty("os.name");

		if (osName.startsWith("Linux")) {
			LOGGER.info("Linux system detected");
			File libPcscLite = loadLinuxNativeLibrary("pcsclite", 1);

			if (libPcscLite != null) {
				LOGGER.info("Trying to set sun.security.smartcardio.library...");
				System.setProperty("sun.security.smartcardio.library",
						libPcscLite.getAbsolutePath());
				LOGGER.info("sun.security.smartcardio.library set...");
			} else {
				LOGGER.warn("libpcsc not found");
				String pathSought = System.getProperty("java.library.path");
				LOGGER.warn("java.library.path " + pathSought + ": null");
			}
		}
	}

	/**
	 * looks for the native .so on linux systems
	 * 
	 * @param baseName	the basename, in this case "pcsclite"
	 * @param version	the version of the library
	 * @return File location of the .so file
	 */
	private static File loadLinuxNativeLibrary(String baseName, int version) {
		String nativeLibraryPath = System.getProperty("java.library.path");

		if (nativeLibraryPath == null) {
			return null;
		}

		String libFileName = System.mapLibraryName(baseName) + "." + version;

		for (String path : nativeLibraryPath.split(":")) {
			File libraryFile = new File(path, libFileName);
			if (libraryFile.exists())
				return libraryFile;
		}

		return null;
	}
}
