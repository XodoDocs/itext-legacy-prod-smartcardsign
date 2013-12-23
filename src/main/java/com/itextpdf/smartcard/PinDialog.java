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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

/**
 * Implementation of the PinProvider interface that creates a dialog
 * to enter a pin manually using the key board.
 */
public class PinDialog implements PinProvider {
	
	/** The number of digits needed for the PIN. */
	protected int digits;
	
	/**
	 * Creates a PinDialog object.
	 * @param	digits	the number of digits needed for the PIN.
	 */
	public PinDialog(int digits) {
		this.digits = digits;
	}
	
	/**
	 * Creates a dialog box that will ask for a pin.
	 * @param retries	the number of retries left
	 */
	public char[] getPin(int retries) throws CardException {
		if (retries == 0) {
			JOptionPane.showMessageDialog(null, "Your PIN code was made invalid!", "No tries left", JOptionPane.ERROR_MESSAGE);
			return new char[]{};
		}
		String labeltext = "Please enter your PIN:";
		if (retries > 0)
			labeltext += " (" + retries + " retries left):";
		JPasswordField pinfield = new JPasswordField();
		JLabel label = new JLabel(labeltext);
		int response = JOptionPane.showConfirmDialog(null,
				  new Object[]{label, pinfield}, "PIN code",
				  JOptionPane.OK_CANCEL_OPTION);
		if (response == JOptionPane.OK_OPTION) {
			char[] pin = pinfield.getPassword();
			if (pin.length != digits) {
				JOptionPane.showMessageDialog(null, "You need to enter " + digits + " digits!", "Wrong PIN", JOptionPane.ERROR_MESSAGE);
				return getPin(retries);
			}
			return pin;
		}
		return new char[]{};
	}
	
}
