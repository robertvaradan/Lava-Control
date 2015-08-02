package com.colonelhedgehog.lavacontrol.core;

import com.colonelhedgehog.lavacontrol.core.components.RoundedCornerBorder;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ColonelHedgehog on 12/30/14.
 * You have freedom to modify given sources. Please credit me as original author.
 * Keep in mind that this is not for sale.
 */
public class ConsoleGUI
{

    public JPanel ConsolePanel;
    public JTextField consoleInput;
    public JTextArea consoleText;
    public JScrollPane scrollPane;
    private JButton exportLogButton;
    private JButton killButton;
    private JButton stopButton;
    private JCheckBox stickyScrollbar;
    private JButton sendCommand;
    private JProgressBar commandProgress;
    public InputStream in;
    public BufferedWriter writer;
    public Thread consoleThread;
    private int index = -1;
    private List<String> messageHistory = new ArrayList<>();

    public ConsoleGUI()
    {

        //in = new JTextFieldInputStream(consoleInput);
        //char c;
        /*try
        {
            while ((c = (char) in.read()) != -1)
            {
                System.out.printf(String.valueOf(c));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }*/
        consoleInput.setText("");

        consoleText.setLineWrap(true);

        killButton.addActionListener(killListener);
        stopButton.addActionListener(stopListener);
        exportLogButton.addActionListener(exportLogListener);
        consoleInput.setBorder(new RoundedCornerBorder(6));
        scrollPane.setBorder(new RoundedCornerBorder(6));
        DefaultCaret caret = (DefaultCaret) consoleText.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        consoleText.setCaret(caret);
        commandProgress.setValue(10);
    }

    final ActionListener sendListener = new ActionListener()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            consoleInput.postActionEvent();
        }
    };

    final ActionListener killListener = new ActionListener()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            System.out.println("[Lava Control] >>> ! FORCE KILL: Destroying process, skipping save procedures. ! <<<");
            System.out.println("[Lava Control] If possible, stop the server using the \"stop\" command/button to prevent issues.");
            consoleThread.interrupt();

            if(Main.mainGUI.p != null)
            {
                Main.mainGUI.p.destroy();
            }
        }
    };

    final ActionListener stopListener = new ActionListener()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            stopButton.setText("Stopping...");
            consoleInput.setText("stop");
            consoleInput.postActionEvent();
            commandProgress.setIndeterminate(true);
        }
    };

    final ActionListener exportLogListener = new ActionListener()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            ExportGUI exportGUI = new ExportGUI();
            exportGUI.show();
        }
    };

    public void createConsoleInput(final BufferedWriter writer)
    {
        this.writer = writer;
        this.consoleThread = new Thread()
        {
            @Override
            public void run()
            {
                consoleInput.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent action)
                    {
                        try
                        {
                            commandProgress.setValue(20);
                            String cmd = consoleInput.getText();
                            messageHistory.add(cmd);
                            commandProgress.setValue(40);

                            System.out.println("» /" + cmd);
                            commandProgress.setValue(60);
                            writer.write(cmd + "\n");
                            commandProgress.setValue(80);
                            consoleInput.setText("");
                            commandProgress.setValue(100);
                            writer.flush();
                            index++;
                            commandProgress.setValue(0);
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });

                consoleInput.addKeyListener(new KeyAdapter()
                {
                    public void keyPressed(KeyEvent e)
                    {
                        if (index == -1)
                        {
                            return;
                        }

                        if (e.getKeyCode() == KeyEvent.VK_UP)
                        {
                            consoleInput.setText(messageHistory.get(index != 0 ? index-- : 0));
                        }
                        else if (e.getKeyCode() == KeyEvent.VK_DOWN)
                        {
                            consoleInput.setText(messageHistory.get(index != (messageHistory.size() - 1) ? index++ : 0));
                        }
                    }
                });
            }
        };

        sendCommand.addActionListener(sendListener);
        consoleThread.start();
        //DefaultCaret caret = (DefaultCaret) consoleText.getCaret();
        //caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);*/
        stickyScrollbar.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                DefaultCaret caret = (DefaultCaret) consoleText.getCaret();
                if (((JCheckBox) e.getSource()).isSelected())
                {
                    caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
                }
                else
                {
                    caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
                }

                consoleText.setCaret(caret);
            }
        });
    }

    public void disableWindow()
    {
        consoleInput.setText("Server stopped.");
        consoleInput.setEnabled(false);
        stopButton.setEnabled(false);
        stopButton.setText("Stopped");
        killButton.setEnabled(false);
        commandProgress.setIndeterminate(false);
        commandProgress.setValue(0);
    }
}
