package kb.gui;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class Key {
	private final int modifiers;
	private final int keyCode;
	
	public Key(int modifiers, int keyCode) {
		this.modifiers = modifiers;
		this.keyCode = keyCode;
	}

	public String toText() {
		String result = "";
		if (modifiers != 0) {
			result += KeyEvent.getKeyModifiersText(modifiers) + "+";
		}
		return result + KeyEvent.getKeyText(keyCode);
	}
	
	public int getModifiers() {
		return modifiers;
	}

	public int getKeyCode() {
		return keyCode;
	}
	
	public KeyStroke getKeyStroke() {
		return KeyStroke.getKeyStroke(getKeyCode(), getModifiers());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + keyCode;
		result = prime * result + modifiers;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Key other = (Key) obj;
		if (keyCode != other.keyCode)
			return false;
		if (modifiers != other.modifiers)
			return false;
		return true;
	}
}
