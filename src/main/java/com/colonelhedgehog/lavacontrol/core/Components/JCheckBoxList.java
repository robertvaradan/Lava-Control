package com.colonelhedgehog.lavacontrol.core.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

@SuppressWarnings("serial")
public class JCheckBoxList extends JList<JCheckBox>
{
    // protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
    protected String lastPath;
    private JCheckBoxList jcbl;

    public JCheckBoxList(final String lastPath)
    {
         jcbl = this;
        this.lastPath = lastPath;
        setCellRenderer(new CellRenderer());
        addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent e)
            {
                int index = locationToIndex(e.getPoint());
                if (index != -1)
                {
                    JCheckBox checkBox = getModel().getElementAt(index);
                    if(!jcbl.isEnabled())
                    {
                        return;
                    }

                    checkBox.setSelected(!checkBox.isSelected());
                    repaint();

                    final String oldname = checkBox.getText();
                    if (!checkBox.isSelected())
                    {
                        checkBox.setName(checkBox.getText().substring(0, checkBox.getText().length() - 4) + "._jar");
                    }
                    else
                    {
                        checkBox.setName(checkBox.getText().substring(0, checkBox.getText().length() - 5) + ".jar");
                    }
                    //System.out.println("Changed! Sel: " + checkBox.isSelected() + ", Name: " + checkBox.getName());
                    checkBox.setText(checkBox.getName());
                    String base = new File(lastPath).getParent() + "/plugins/";
                    boolean rename = new File(base + oldname).renameTo(new File(base + checkBox.getText()));
                }
            }
        });
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setOpaque(false);
        validate();
        repaint();
    }

    public JCheckBoxList(ListModel<JCheckBox> model, String lastPath)
    {
        this(lastPath);
        setModel(model);
    }

    protected class CellRenderer implements ListCellRenderer<JCheckBox>
    {
        public Component getListCellRendererComponent(
                JList<? extends JCheckBox> list, JCheckBox value, int index,
                boolean isSelected, boolean cellHasFocus)
        {
            //Drawing checkbox, change the appearance here
            Color grey = Color.getHSBColor(0, 0, 0.90F);
            Color color = (index % 2 == 0 ? Color.white : grey);

            value.setBackground(isSelected ? getSelectionBackground()
                    : color);
            value.setForeground(isSelected ? getSelectionForeground()
                    : getForeground());
            value.setEnabled(isEnabled());
            value.setFont(getFont());
            value.setFocusPainted(false);
            //value.setBorderPainted(true);
            //value.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 0));
            return value;
        }
    }
}
