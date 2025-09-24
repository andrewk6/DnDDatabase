package builders.class_builder;

import javax.swing.*;

import data.DataContainer.Source;

import java.awt.*;

// Example enum for Source, replace with your actual Source class/enum

@SuppressWarnings("serial")
public class SubclassDialog extends JDialog {
    private JTextField nameField;
    private JComboBox<Source> sourceBox;
    private boolean confirmed = false;

    public SubclassDialog(Container owner) {
        super(SwingUtilities.getWindowAncestor(owner), "Create Subclass", ModalityType.APPLICATION_MODAL);
        setLayout(new BorderLayout(10, 10));

        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        inputPanel.add(new JLabel("Subclass Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Source:"));
        sourceBox = new JComboBox<>(Source.values());
        inputPanel.add(sourceBox);

        add(inputPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        okButton.addActionListener(_ -> {
            confirmed = true;
            setVisible(false);
        });
        cancelButton.addActionListener(_ -> {
            confirmed = false;
            setVisible(false);
        });

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(owner);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getSubclassName() {
        return nameField.getText().trim();
    }

    public Source getSubclassSource() {
        return (Source) sourceBox.getSelectedItem();
    }
}
