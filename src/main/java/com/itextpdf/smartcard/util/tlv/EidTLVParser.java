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
package com.itextpdf.smartcard.util.tlv;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.itextpdf.text.log.Logger;
import com.itextpdf.text.log.LoggerFactory;

/**
 * Smart cards can store data in a simplified form of the
 * Tag-Length-Value format (Simple-TLV data objects).
 * Apparently, there are different implementations of LTV.
 * There's the implementation according to ISO/IEC 7816,
 * and then there's the implementation you need to use
 * for the Belgian eID. (I wonder: why is it different?)
 */
public class EidTLVParser implements SimpleTLVParser {
	
	/** Logger instance. */
	private final static Logger LOGGER = LoggerFactory.getLogger(EidTLVParser.class);
	
	/**
	 * Parses a byte array and returns a map of tags and values.
	 * Note that we assume that the tag is only one byte long
	 * (which isn't always the case for TLV sequences) and that
	 * the length is calculated in a simplified way.
	 * @param data a byte array obtained from a smart card
	 * @return	a map with tags and values
	 */
	public Map<Byte,String> parse(byte[] data) {
		LOGGER.info("Parsing LTV data...");
		Map<Byte, String> map = new HashMap<Byte, String>();
		// position in the data array
		int pos = 0;
		// the tag
		byte tag;
		// the length of the value
		byte l;
		int length;
		// we loop over the data array
		while (pos < data.length) {
			// we assume that the tag consists of one byte
			tag = data[pos++];
			LOGGER.info("Tag: " + Integer.toHexString(tag));
			// The length of the value
			length = 0;
			l = data[pos++];
			// As long as the length = 255, we add more to the length
			while (l == 0xFF) {
				length += l;
				l = data[pos++];
			}
			length += l;
			LOGGER.info("Length: " + length);
			// we add the value to the map
			try {
				map.put(tag, new String(Arrays.copyOfRange(data, pos, pos + length), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				map.put(tag, new String(Arrays.copyOfRange(data, pos, pos + length)));
			}
			LOGGER.info("Value: " + map.get(tag));
			// we skip the value bytes
			pos += length;
		}
		LOGGER.info("Done parsing LTV data...");
		return map;
	}
}
