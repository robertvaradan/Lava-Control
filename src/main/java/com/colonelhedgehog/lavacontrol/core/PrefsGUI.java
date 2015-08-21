package com.colonelhedgehog.lavacontrol.core;

import com.colonelhedgehog.lavacontrol.core.components.RoundedCornerBorder;

import javax.swing.*;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by ColonelHedgehog on 8/19/15.
 */
public class PrefsGUI
{
    private JPanel PrefsPanel;
    private JTextField memoryBash;
    private JTextField maxConsoleLines;
    private JSeparator serverSSeperator;
    private JCheckBox sshEnabled;
    private JTextField sshUsername;
    private JTextField sshHost;
    private JPasswordField sshPassword;
    private JCheckBox stickyScrollbar;
    private JCheckBox closeOnStop;
    private JButton applyButton;
    private JButton restoreDefaultsButton;
    private JButton cancelButton;
    private JLabel passwordLabel;
    private JTabbedPane tabbedPane;
    private JCheckBox askToExport;

    public PrefsGUI()
    {
        final Settings settings = Main.getSettings();
        settings.reloadSettings();

        memoryBash.setText(String.valueOf(settings.getMemBash()));
        maxConsoleLines.setText(String.valueOf(settings.getMaxConsoleLines()));
        sshEnabled.setSelected(settings.getSSHEnabled());
        sshUsername.setText(settings.getSSHUsername());
        sshPassword.setText(settings.getSSHPassword());
        sshHost.setText(settings.getSSHHost());
        stickyScrollbar.setSelected(settings.getStickyScrollBar());
        closeOnStop.setSelected(settings.getCloseWindowOnStop());
        askToExport.setSelected(settings.getAskExportLog());

        if(closeOnStop.isSelected())
        {
            askToExport.setSelected(false);
            askToExport.setEnabled(false);
        }
        else
        {
            askToExport.setEnabled(true);
        }

        disableSSH();

        sshEnabled.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                sshUsername.setEnabled(sshEnabled.isSelected());
                sshHost.setEnabled(sshEnabled.isSelected());
                sshPassword.setEnabled(sshEnabled.isSelected());
                passwordLabel.setEnabled(sshEnabled.isSelected());
            }
        });

        closeOnStop.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(closeOnStop.isSelected())
                {
                    askToExport.setSelected(false);
                    askToExport.setEnabled(false);
                }
                else
                {
                    askToExport.setEnabled(true);
                }
            }
        });

        applyButton.addActionListener(new ActionListener()
        {
            @SuppressWarnings("deprecation")
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String errors = "";
                try
                {
                    settings.setMemBash(Integer.parseInt(memoryBash.getText()));
                }
                catch(NumberFormatException nfe)
                {
                    String message = "[Lava Control] ERROR: Text in memory bash settings is invalid.";
                    System.out.println(message);
                    errors = errors + "\n" + message;
                }

                try
                {
                    settings.setMaxConsoleLines(Integer.parseInt(maxConsoleLines.getText()));
                }
                catch(NumberFormatException nfe)
                {
                    String message = "[Lava Control] ERROR: Text in max console lines settings is\ninvalid.";
                    System.out.println(message);
                    errors = errors + "\n" + message;
                }

                settings.setSSHEnabled(sshEnabled.isSelected());

                settings.setSSHUsername(sshUsername.getText());
                settings.setSSHPassword(sshPassword.getText());
                settings.setSSHHost(sshHost.getText());

                settings.setStickyScrollBar(stickyScrollbar.isSelected());
                settings.setCloseWindowOnStop(closeOnStop.isSelected());
                settings.setAskExportLog(askToExport.isSelected());

                settings.saveSettings();

                ImageIcon scaled = new ImageIcon(Main.getIcon().getImage().getScaledInstance(96, 96, Image.SCALE_SMOOTH));
                if(!errors.equals(""))
                {
                    JOptionPane.showMessageDialog(Main.prefsFrame, "<html><b>Error!</b> Couldn't save your settings:\n" + errors + "\n\nPlease fix these errors and then hit apply again\nto completely save all settings.", "", JOptionPane.ERROR_MESSAGE, scaled);
                }
                else
                {
                    JOptionPane.showMessageDialog(Main.prefsFrame, "<html><b>Success!</b></html> Settings were successfully applied.", "", JOptionPane.INFORMATION_MESSAGE, scaled);
                }

            }
        });

        restoreDefaultsButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                Main.getSettings().deleteSettings();

                memoryBash.setText(String.valueOf(settings.getMemBash()));
                maxConsoleLines.setText(String.valueOf(settings.getMaxConsoleLines()));
                sshEnabled.setSelected(settings.getSSHEnabled());
                sshUsername.setText(settings.getSSHUsername());
                sshPassword.setText(settings.getSSHPassword());
                sshHost.setText(settings.getSSHHost());
                stickyScrollbar.setSelected(settings.getStickyScrollBar());
                closeOnStop.setSelected(settings.getCloseWindowOnStop());
                askToExport.setSelected(settings.getAskExportLog());
            }
        });

        cancelButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                Main.prefsFrame.setVisible(false);
            }
        });

        memoryBash.setBorder(new RoundedCornerBorder(6));
        sshUsername.setBorder(new RoundedCornerBorder(6));
        sshHost.setBorder(new RoundedCornerBorder(6));
        sshPassword.setBorder(new RoundedCornerBorder(6));
        maxConsoleLines.setBorder(new RoundedCornerBorder(6));
    }

    private void disableSSH()
    {
        sshEnabled.setEnabled(false);
        sshEnabled.setSelected(false);
        sshEnabled.setToolTipText("SSH connections are currently unsupported.");
    }

    public JPanel getPrefsPanel()
    {
        return PrefsPanel;
    }

    public JTextField getMemoryBash()
    {
        return memoryBash;
    }

    public JTextField getMaxConsoleLines()
    {
        return maxConsoleLines;
    }

    public JSeparator getServerSSeperator()
    {
        return serverSSeperator;
    }

    public JCheckBox getSshEnabled()
    {
        return sshEnabled;
    }

    public JTextField getSshUsername()
    {
        return sshUsername;
    }

    public JTextField getSshHost()
    {
        return sshHost;
    }

    public JPasswordField getSshPassword()
    {
        return sshPassword;
    }

    public JCheckBox getStickyScrollbar()
    {
        return stickyScrollbar;
    }

    public JCheckBox getCloseOnStop()
    {
        return closeOnStop;
    }

    public JButton getApplyButton()
    {
        return applyButton;
    }

    public JButton getRestoreDefaultsButton()
    {
        return restoreDefaultsButton;
    }

    public JButton getCancelButton()
    {
        return cancelButton;
    }

    public JLabel getPasswordLabel()
    {
        return passwordLabel;
    }

    public JTabbedPane getTabbedPane()
    {
        return tabbedPane;
    }
}
