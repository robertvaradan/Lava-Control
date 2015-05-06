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
        int result = fc.showOpenDialog(FilePanel);

        if (result == JFileChooser.APPROVE_OPTION)
        {
            File chosen = fc.getSelectedFile().isDirectory() ? fc.getSelectedFile() : fc.getSelectedFile().getParentFile();
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat df2 = new SimpleDateFormat("dd-MM-yy_HH-mm-ss");
            String dateText = df2.format(date);

            File log = new File(chosen + "/LavaControl_" + dateText + ".log");

            try
            {
                BufferedWriter bw = new BufferedWriter(new FileWriter(log));
                for (String line : Main.consoleGUI.consoleText.getText().split("\n"))
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
        }
    }
}
