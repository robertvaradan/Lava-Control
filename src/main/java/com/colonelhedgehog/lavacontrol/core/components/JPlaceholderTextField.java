package com.colonelhedgehog.lavacontrol.core.components;

import javax.swing.FocusManager;
import javax.swing.*;
import java.awt.*;

/**
 * Lava Control
 * Created by ColonelHedgehog on 3/25/16.
 */
public class JPlaceholderTextField extends JTextField
{
    private String defaultText;

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        if(defaultText == null)
        {
            defaultText = "";
        }

        if (getText().isEmpty() && !(FocusManager.getCurrentKeyboardFocusManager().getFocusOwner() == this))
        {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setFont(getFont().deriveFont(Font.ITALIC));
            Color color = getDisabledTextColor();
            g2.setColor(color);
            g2.drawString(getDefaultText(), getInsets().left, g.getFontMetrics()
                    .getMaxAscent() + getInsets().top);
            g2.dispose();
        }
    }

    public String getDefaultText()
    {
        return defaultText;
    }

    public void setDefaultText(String defaultText)
    {
        this.defaultText = defaultText;
    }
}