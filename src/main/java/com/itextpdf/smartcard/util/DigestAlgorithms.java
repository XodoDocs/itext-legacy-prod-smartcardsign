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

import java.util.HashMap;
import java.util.Map;

/**
 * When signing a message digest using a smart card, you have to pass the hashing algorithm that was used.
 * This class contains the byte array for all the hashing algorithms that are allowed.
 */
public class DigestAlgorithms {
	
	/** SHA-1 prefix. */
	public static final byte[] SHA1_PREFIX = new byte[] { 0x30, 0x1f, 0x30, 0x07,
			0x06, 0x05, 0x2b, 0x0e, 0x03, 0x02, 0x1a, 0x04, 0x14 };

	/** SHA-224 prefix. */
	public static final byte[] SHA224_PREFIX = new byte[] { 0x30, 0x2b, 0x30, 0x0b,
			0x06, 0x09, 0x60, (byte) 0x86, 0x48, 0x01, 0x65, 0x03, 0x04, 0x02,
			0x04, 0x04, 0x1c };

	/** SHA-256 prefix. */
	public static final byte[] SHA256_PREFIX = new byte[] { 0x30, 0x2f, 0x30, 0x0b,
			0x06, 0x09, 0x60, (byte) 0x86, 0x48, 0x01, 0x65, 0x03, 0x04, 0x02,
			0x01, 0x04, 0x20 };

	/** SHA-384 prefix. */
	public static final byte[] SHA384_PREFIX = new byte[] { 0x30, 0x3f, 0x30, 0x0b,
			0x06, 0x09, 0x60, (byte) 0x86, 0x48, 0x01, 0x65, 0x03, 0x04, 0x02,
			0x02, 0x04, 0x30 };

	/** SHA-512 prefix. */
	public static final byte[] SHA512_PREFIX = new byte[] { 0x30, 0x4f, 0x30, 0x0b,
			0x06, 0x09, 0x60, (byte) 0x86, 0x48, 0x01, 0x65, 0x03, 0x04, 0x02,
			0x03, 0x04, 0x40 };

	/** RIPE Message Digest 128 prefix. */
	public static final byte[] RIPEMD128_PREFIX = new byte[] { 0x30, 0x1b, 0x30, 0x07,
			0x06, 0x05, 0x2b, 0x24, 0x03, 0x02, 0x02, 0x04, 0x10 };

	/** RIPE Message Digest 160 prefix. */
	public static final byte[] RIPEMD160_PREFIX = new byte[] { 0x30, 0x1f, 0x30, 0x07,
			0x06, 0x05, 0x2b, 0x24, 0x03, 0x02, 0x01, 0x04, 0x14 };

	/** RIPE Message Digest 256 prefix. */
	public static final byte[] RIPEMD256_PREFIX = new byte[] { 0x30, 0x2b, 0x30, 0x07,
			0x06, 0x05, 0x2b, 0x24, 0x03, 0x02, 0x03, 0x04, 0x20 };

	/** A Map mapping the most common names of the allowed digests to their prefix. */
	public static final Map<String, byte[]> DIGESTS;
	static {
		DIGESTS = new HashMap<String, byte[]>();
	    DIGESTS.put("SHA1", SHA1_PREFIX);
	    DIGESTS.put("SHA-1", SHA1_PREFIX);
	    DIGESTS.put("SHA224", SHA224_PREFIX);
	    DIGESTS.put("SHA-224", SHA224_PREFIX);
	    DIGESTS.put("SHA256", SHA256_PREFIX);
	    DIGESTS.put("SHA-256", SHA256_PREFIX);
	    DIGESTS.put("SHA384", SHA384_PREFIX);
	    DIGESTS.put("SHA-384", SHA384_PREFIX);
	    DIGESTS.put("SHA512", SHA512_PREFIX);
	    DIGESTS.put("SHA-512", SHA512_PREFIX);
	    DIGESTS.put("RIPEMD128", RIPEMD128_PREFIX);
	    DIGESTS.put("RIPEMD-128", RIPEMD128_PREFIX);
	    DIGESTS.put("RIPEMD160", RIPEMD160_PREFIX);
	    DIGESTS.put("RIPEMD-160", RIPEMD160_PREFIX);
	    DIGESTS.put("RIPEMD256", RIPEMD256_PREFIX);
	    DIGESTS.put("RIPEMD-256", RIPEMD256_PREFIX);
	}

	/**
	 * THe prefix for plain text.
	 * Second 0xff (offset 14) is the size of the message.
	 * First 0xff (offset 1) is the size of the message + 13
	 */
	public static final byte[] PLAIN_TEXT_PREFIX = new byte[] { 0x30, (byte) 0xff,
			0x30, 0x09, 0x06, 0x07, 0x60, 0x38, 0x01, 0x02, 0x01, 0x03, 0x01,
			0x04, (byte) 0xff };
	
	/** The digest name for plain text. */
	public static String PLAIN_TEXT = "2.16.56.1.2.1.3.1";
}
