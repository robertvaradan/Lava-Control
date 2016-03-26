package com.colonelhedgehog.lavacontrol.core;

import com.colonelhedgehog.lavacontrol.core.components.RoundedCornerBorder;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by ColonelHedgehog on 8/4/15.
 */
@SuppressWarnings("Convert2Lambda")
public class SearchGUI
{
    private JButton prevResultButton;
    private JButton nextResultButton;
    private JCheckBox matchCaseCheckBox;
    //private JCheckBox useRegExCheckBox;
    private JButton searchButton;
    private JTextField searchField;
    private JPanel searchPanel;

    private ArrayList<Integer> indexList;
    private int currentIndex; // This may get confusing.
    private String lastSearch;
    private int currentPosition;


    public SearchGUI()
    {
        searchField.setBorder(new RoundedCornerBorder(6));
        lastSearch = "";
        indexList = new ArrayList<>();
        indexList.add(0);
        currentIndex = 0;
        currentPosition = 0;

        searchButton.addActionListener(
                e -> {
                    currentIndex = 0;
                    currentPosition = 0;

                    lastSearch = searchField.getText();
                    indexList = FindTools.searchTextIndexes(Main.consoleGUI.getConsoleText(), lastSearch, matchCaseCheckBox.isSelected());

                    if (indexList.size() == 0)
                    {
                        //System.out.println("Index list size is 0!");
                        nextResultButton.setEnabled(false);
                        prevResultButton.setEnabled(false);
                        return;
                    }

                    if (indexList.size() <= 1)
                    {
                        nextResultButton.setEnabled(false);
                        prevResultButton.setEnabled(false);
                    }
                    else
                    {
                        nextResultButton.setEnabled(true);
                        prevResultButton.setEnabled(true);
                    }

                    //System.out.println("NEW LIST: " + indexList);
                    int pos = indexList.get(currentIndex);
                    currentPosition = pos + lastSearch.length();

                    highlightPoints(pos, currentPosition);
                }

        );

        nextResultButton.addActionListener(
                e -> {
                    currentIndex++;

                    if (currentIndex >= indexList.size())
                    {
                        currentIndex = 0;
                    }

                    int pos = indexList.get(currentIndex);
                    currentPosition = pos + lastSearch.length();

                    highlightPoints(pos, currentPosition);
                }

        );

        prevResultButton.addActionListener(
                e -> {
                    currentIndex--;

                    if (currentIndex <= 0)
                    {
                        currentIndex = indexList.size() - 1;
                    }

                    int pos = indexList.get(currentIndex);
                    currentPosition = pos + lastSearch.length();

                    highlightPoints(pos, currentPosition);
                }
        );
    }

    private void highlightPoints(int start, int end)
    {
        JTextArea textArea = Main.consoleGUI.getConsoleText();
        textArea.grabFocus();

        Rectangle viewRect = null;

        try
        {
            viewRect = textArea.modelToView(start);
        }
        catch (BadLocationException e)
        {
            e.printStackTrace();
        }

        textArea.scrollRectToVisible(viewRect);
        textArea.setCaretPosition(end);
        textArea.moveCaretPosition(start);
        //textArea.setRequestFocusEnabled(true);
        //textArea.requestFocus();
    }

    public Container getSearchPanel()
    {
        return searchPanel;
    }

    public JTextField getSearchField()
    {
        return searchField;
    }

    public String getLastSearch()
    {
        return lastSearch;
    }

    public void setLastSearch(String lastSearch)
    {
        this.lastSearch = lastSearch;
    }
}
