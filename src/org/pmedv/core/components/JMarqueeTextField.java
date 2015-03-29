/**

	Jake
	Written and maintained by Matthias Pueski 
	
	Copyright (c) 2009 Matthias Pueski
	
	This program is free software; you can redistribute it and/or
	modify it under the terms of the GNU General Public License
	as published by the Free Software Foundation; either version 2
	of the License, or (at your option) any later version.
	
	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

 */
package org.pmedv.core.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;
import javax.swing.Timer;

public class JMarqueeTextField extends JTextField {

	public final static int LEFT_TO_RIGHT = JTextField.LEADING;
	public final static int RIGHT_TO_LEFT = JTextField.TRAILING;

	private final Animater animater;
	private final Timer timer;
	private String buffer2;
	private String delimiter;
	private char[] buffer1;
	private int maximumDisplayedCharacters;
	private int scrollDirection;
	private boolean useDelimiter;
	private int _columns;

	public JMarqueeTextField(int columns) {

		super(columns);
		this._columns = columns;
		animater = new Animater();
		timer = new Timer(200, animater);		
		delimiter = " +++ ";

		maximumDisplayedCharacters = 512;
		scrollDirection = RIGHT_TO_LEFT;
		useDelimiter = true;

		setHorizontalAlignment(scrollDirection);
		setText("");
		setEditable(false);

	}
 

	public String getText()	{
		return buffer2;
	}

	public void setText(String t) {

		buffer2 = t;

		buffer1 = (useDelimiter) ? (t + delimiter).toCharArray(): t.toCharArray();
		animater.reset();

		_setText("");

	}

	public void setDelay(int milliseconds) {
		timer.setDelay(milliseconds);
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public void setMaximumDisplayedCharacters(int maximumDisplayedCharacters) {
		this.maximumDisplayedCharacters = maximumDisplayedCharacters;
	}

	public void setScrollDirection(int scrollDirection){
		this.scrollDirection = scrollDirection;
	}

	public void setUseDelimiter(boolean useDelimiter) {
		this.useDelimiter = useDelimiter;
	}

	public void startAnimation() {
		
		for (int i=0;i< _columns;i++)
			animater.scrollRightToLeft();
		
		timer.start();
	}

	public void stopAnimation() {
		timer.stop();
	}

	private void _setText(String t)	{
		super.setText(t);
	}

	private String _getText() {
		return super.getText();
	}

	private class Animater implements ActionListener {

		private int offset;

		public Animater() {
		}

		public void actionPerformed(ActionEvent e) {

			if (JMarqueeTextField.this.scrollDirection == RIGHT_TO_LEFT) {
				scrollRightToLeft();				
			}
			else {
				scrollLeftToRight();
			}

		}

		public void reset() {
			offset = 0;
		}

		private void scrollLeftToRight() {

			final int length = JMarqueeTextField.this.buffer1.length;
			final String text = JMarqueeTextField.this.buffer1[length - offset - 1] + JMarqueeTextField.this._getText();

			int end = text.length();

			if (end >= JMarqueeTextField.this.maximumDisplayedCharacters) {
				end--;
			}

			JMarqueeTextField.this._setText(text.substring(0, end));

			offset++;
			offset %= length;

		}

		private void scrollRightToLeft() {

			final int length = JMarqueeTextField.this.buffer1.length;
			final String text = JMarqueeTextField.this._getText() + JMarqueeTextField.this.buffer1[offset];
			final int textLength = text.length();

			int start = 0;

			if (textLength >= JMarqueeTextField.this.maximumDisplayedCharacters) {
				start++;
			}

			JMarqueeTextField.this._setText(text.substring(start, textLength));

			offset++;
			offset %= length;

		}

	}

}
