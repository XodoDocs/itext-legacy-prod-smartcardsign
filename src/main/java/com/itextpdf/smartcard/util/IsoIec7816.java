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

/**
 * Some constant values that are defined in ISO/IEC 7816.
 */
public class IsoIec7816 {

	// class bytes
	public static final byte CLA_00 = (byte)0x00;
	
	// instruction bytes (ins parameter in CommandAPDU)
	public static final byte INS_VERIFY_DATA = (byte)0x20;
	public static final byte INS_MANAGE_SECURITY_ENVIRONMENT = (byte)0x22;
	public static final byte INS_PERFORM_SECURITY_OPERATION = (byte)0x2A;
	public static final byte INS_SELECT = (byte)0xA4;
	public static final byte INS_READ_BINARY = (byte)0xB0;
	
	// parameters
	public static final byte P1_00 = (byte)0x00;
	public static final byte P1_COMPUTATION_SET = (byte)0x41;
	public static final byte P1_DIGITAL_SIGNATURE = (byte)0x9E;
	public static final byte P1_SELECT_FROM_MF = (byte)0x08;
	
	public static final byte P2_CRT_DIGITAL_SIGNATURE = (byte)0xB6;
	public static final byte P2_INPUT_DATA = (byte)0x9A;
	public static final byte P2_ONLY_OCCURRENCE_NO_RESPONSE_DATA = (byte)0x0C;
	
	// status bytes
	
	// SW1
	public static final int SW1_WARNING = 0x63;
	public static final int SW1_ABORTED = 0x6C;
	
	// SW
	public static final int SW_TIMEOUT = 0x6400;
	public static final int SW_USER_ABORTED = 0x6401;
	public static final int SW_SECURITY_STATUS_NOT_SATISFIED = 0x6982;
	public static final int SW_AUTHENTICATION_METHOD_BLOCKED = 0x6983;
	public static final int SW_WRONG_PARAMETERS = 0x6B00;
	public static final int SW_NO_FURTHER_QUALIFICATION = 0x9000;
}
