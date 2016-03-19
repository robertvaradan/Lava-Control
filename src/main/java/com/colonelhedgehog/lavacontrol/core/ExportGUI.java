package com.colonelhedgehog.lavacontrol.core;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class ExportGUI
{
    private JPanel FilePanel;
    private JFileChooser fc;

    public ExportGUI()
    {
        fc = new JFileChooser();
    }

    public void show()
    {
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fc.setMultiSelectionEnabled(false);
        final int result = fc.showOpenDialog(FilePanel);

        final Thread[] exporthread = {null};
        exporthread[0] = new Thread(new Runnable()
        {
            @Override
            public void run()
            {

                if (result == JFileChooser.APPROVE_OPTION)
                {
                    File chosen = fc.getSelectedFile().isDirectory() ? fc.getSelectedFile() : fc.getSelectedFile().getParentFile();
                    Date date = new Date(System.currentTimeMillis());
                    SimpleDateFormat df2 = new SimpleDateFormat("MM-dd-yy, HH.mm.ss");
                    String dateText = df2.format(date);

                    File log = new File(chosen, "Lava Control @ " + dateText + ".log");

                    try
                    {
                        BufferedWriter bw = new BufferedWriter(new FileWriter(log));
                        for (String line : Main.consoleGUI.getConsoleText().getText().split("\n"))
                        {
                            bw.write(line + "\n");
                        }

                        bw.flush();
                        bw.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    JOptionPane.showMessageDialog(Main.consoleFrame, "The log \"LavaControl_" + dateText + "\" has been successfully saved!");

                    try
                    {
                        exporthread[0].join();
                    }
                    catch (InterruptedException e)
                    {
                        exporthread[0].interrupt();
                        //e.printStackTrace();
                    }
                }
            }
        });

        exporthread[0].start();
    }
}
