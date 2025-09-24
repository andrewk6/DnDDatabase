package gui.gui_helpers;

import javax.swing.JCheckBox;

public class EnumCheckbox<T extends Enum<T>> extends JCheckBox
{
	private final T value;
	
	public EnumCheckbox(T value) {
		super(value.toString());
		this.value = value;
	}
	
	public T getValue() {
		return value;
	}
}