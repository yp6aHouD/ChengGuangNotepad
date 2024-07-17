package com.guangnotepad;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;


// Class for search functionality
// 搜索功能类

public class SearchFunction implements ActionListener, KeyListener
{
    private GUI gui;
    private JDialog findReplaceDialog;
    private JTextField searchField, replaceField;
    private JButton closeButton, searchButton, replaceButton, prevButton, nextButton;
    private JLabel searchLabel, counterLabel;
    private List<Integer> indices;
    private int currentIndex;
    private static boolean isActive = false;

    // Constructor
    // 构造函数
    public SearchFunction(final GUI gui)
    {
        if (isActive) return;
        
        isActive = true;
        this.gui = gui;

        // Get the location of the menu bar and its size
        // 获取菜单栏的位置和大小
        Point location = gui.menuBar.getLocationOnScreen();
        final Dimension screenSize = gui.menuBar.getSize();
        findReplaceDialog = new JDialog(gui.window, false);
        GridBagLayout dialogLayout = new GridBagLayout();

        // Set the size and default settings of the window
        // 设置窗口的大小和默认设置
        findReplaceDialog.setLayout(dialogLayout);
        findReplaceDialog.setSize(250, 150);
        findReplaceDialog.setUndecorated(true);
        findReplaceDialog.setLocationRelativeTo(gui.menuBar);
        
        // Set the window to the top right corner
        // 设置窗口在右上角
        findReplaceDialog.setLocation(location.x + screenSize.width - 
            findReplaceDialog.getWidth() - 10, location.y + 40);

        // Create a button to close the search window
        // 创建一个按钮来关闭搜索窗口
        closeButton = new JButton("X");
        Dimension closeButtonSize = new Dimension(15, 15);
        closeButton.setPreferredSize(closeButtonSize);
        closeButton.addActionListener(this);
        closeButton.setActionCommand("Close");

        // Create a GridBagConstraints object for layout
        // 创建一个GridBagConstraints对象
        GridBagConstraints gbc = new GridBagConstraints();

        // Set the search label
        // 设置搜索标签
        searchLabel = new JLabel("Search in the text");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        findReplaceDialog.add(searchLabel, gbc);

        //Set the exit button in the top right corner
        // 设置退出按钮在右上角
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        findReplaceDialog.add(closeButton, gbc);
        
        // Create input fields and search and replace buttons
        // 创建输入字段和搜索和替换按钮
        searchField = new JTextField(10);
        searchField.addKeyListener(this);

        searchButton = new JButton("Search");
        searchButton.addActionListener(this);
        searchButton.setActionCommand("Search");

        replaceField = new JTextField(10);
        replaceField.addKeyListener(this);

        replaceButton = new JButton("Replace");
        replaceButton.addActionListener(this);
        replaceButton.setActionCommand("Replace");

        nextButton = new JButton("Next");
        nextButton.addActionListener(this);
        nextButton.setActionCommand("Next");

        // Create a panel for the counter label and prev button
        // 创建一个面板用于计数器标签和上一个按钮
        JPanel panel = new JPanel();
        counterLabel = new JLabel("0/0");
        prevButton = new JButton("Prev");
        
        panel.add(counterLabel); panel.add(prevButton);

        prevButton.addActionListener(this);
        prevButton.setActionCommand("Prev");

        // Resetting the GridBagConstraints values
        // 重置GridBagConstraints值
        gbc.anchor = GridBagConstraints.WEST;

        // Search field and "Search" button
        // 搜索字段和"搜索"按钮
        gbc.gridx = 0;
        gbc.gridy = 1;
        findReplaceDialog.add(searchField, gbc);

        gbc.gridx = 1;
        findReplaceDialog.add(searchButton, gbc);

        // Panel with prev button and counter + next button
        // 面板与上一个按钮和计数器+下一个按钮
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        findReplaceDialog.add(panel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        findReplaceDialog.add(nextButton, gbc);

        // Replace field and "Replace" button
        // 替换字段和“替换”按钮
        gbc.gridx = 0;
        gbc.gridy = 3;
        findReplaceDialog.add(replaceField, gbc);

        gbc.gridx = 1;
        findReplaceDialog.add(replaceButton, gbc);


        // Handling window movement when parent window is moved
        // 处理父窗口移动时的窗口移动
        gui.window.addComponentListener(new ComponentAdapter() 
        {
            // Прописываем, как будет вести себя окно при сдвиге родительского компонента
            @Override
            public void componentMoved(ComponentEvent e) 
            {
                Point newLocation = gui.menuBar.getLocationOnScreen();
                findReplaceDialog.setLocation(newLocation.x + screenSize.width - 
                    findReplaceDialog.getWidth() - 10, newLocation.y + 40);
            }
        });

        // Display the window
        // 显示窗口
        findReplaceDialog.setVisible(true);

        // Set the focus on the search field
        // 设置焦点在搜索字段上
        searchField.requestFocusInWindow();

        // Add a listener for the text input field after creating the window
        // 为文本输入字段创建窗口后添加侦听器
        gui.textArea.addKeyListener(this);
    }

    // Search method
    // 搜索方法
    public void performSearch()
    {
        // Get the text to search in
        // 获取要搜索的文本
        String text = gui.textArea.getText();

        // Get the word to search for
        // 获取要搜索的单词
        String word = searchField.getText();
        
        // List to store the indices of all occurrences
        // 用于存储所有出现的索引的列表
        indices = new ArrayList<>();

        // Search for all occurrences of the word in the text
        // 在文本中搜索单词的所有出现
        if (!word.equals("") && !text.equals(""))
        {
            currentIndex = text.indexOf(word);
            while (currentIndex != -1) 
            {
                indices.add(currentIndex);
                currentIndex = text.indexOf(word, currentIndex + 1);
            }
            currentIndex = 0;

            // If there are no matches, show a popup message
            // 如果没有匹配项，则显示弹出消息
            if (indices.isEmpty()) 
            {
                counterLabel.setText("0/0");
                gui.currentPopup = new PopupMessage(gui, "No matches found!");
                gui.currentPopup.setVisible(true);
            } 

            // If there is match, select first one
            // 如果有匹配项，则选择第一个
            else 
            {
                // Update the counterLabel
                // 更新counterLabel
                counterLabel.setText("1/" + indices.size());
                
                // Set the cursor to the first occurrence
                // 将光标设置为第一次出现
                gui.textArea.setCaretPosition(indices.get(0));
                
                // Highlight the found word
                // 高亮显示找到的单词
                gui.textArea.setSelectionStart(indices.get(0));
                gui.textArea.setSelectionEnd(indices.get(0) + word.length());
            }
        }

        // If the search field is empty, show a popup message
        // 如果搜索字段为空，则显示弹出消息
        else if (word.equals(""))
        {
            gui.currentPopup = new PopupMessage(gui, "You did'nt enter anything to search!");
            gui.currentPopup.setVisible(true);
        }

        // If the text field is empty, show a popup message
        // 如果文本字段为空，则显示弹出消息
        else if (text.equals(""))
        {
            gui.currentPopup = new PopupMessage(gui, "There's no text to search!");
            gui.currentPopup.setVisible(true);
        }
        
        // If there is an error, show a popup message
        // 如果有别的错误，则显示弹出消息
        else
        {
            gui.currentPopup = new PopupMessage(gui, "Search error");
            gui.currentPopup.setVisible(true);
        }
    }

    // Switch to the next occurrence method
    // 切换到下一个出现的方法
    private void nextMatch()
    {
        if (!indices.isEmpty())
        {
            // Increase the current index and wrap it if it exceeds the size of the list
            // 增加当前索引并在超出列表大小时进行包装
            currentIndex = (currentIndex + 1) % indices.size();
    
            // Update the counterLabel
            // 更新counterLabel
            counterLabel.setText((currentIndex + 1) + "/" + indices.size());
    
            // Set the cursor to the next occurrence
            // 将光标设置为下一个出现
            gui.textArea.setCaretPosition(indices.get(currentIndex));
    
            // Highlight the found word
            // 高亮显示找到的单词
            gui.textArea.setSelectionStart(indices.get(currentIndex));
            gui.textArea.setSelectionEnd(indices.get(currentIndex) + searchField.getText().length());
        }
    }
        
    // Switch to the previous occurrence method
    // 切换到上一个出现的方法
    private void prevMatch()
    {
        if (!indices.isEmpty())
        {
            // Decrease the current index and wrap it if it is less than 0
            // 减少当前索引并在小于0时进行包装
            currentIndex = (currentIndex - 1 + indices.size()) % indices.size();

            // Update the counterLabel    
            // 更新counterLabel
            counterLabel.setText((currentIndex + 1) + "/" + indices.size());
    
            // Set the cursor to the previous occurrence
            // 将光标设置为上一个出现
            gui.textArea.setCaretPosition(indices.get(currentIndex));
    
            // Highlight the found word
            // 高亮显示找到的单词
            gui.textArea.setSelectionStart(indices.get(currentIndex));
            gui.textArea.setSelectionEnd(indices.get(currentIndex) + searchField.getText().length());
        }
    }


    // Replace the selected text method
    // 替换所选文本的方法
    public void replaceText()
    {
        // Check if there are any matches
        // 检查是否有任何匹配项
        if (indices != null)
        {
            // Check if the search field and replace field are not empty
            // 检查搜索字段和替换字段是否不为空
            if (!indices.isEmpty()) 
            {
                try 
                {
                    // Get the text to replace with
                    // 获取要替换的文本
                    String replacement = replaceField.getText();

                    // Get the document
                    // 获取文档
                    Document doc = gui.textArea.getDocument();

                    // Get the start and end indices of the selected text
                    // 获取所选文本的开始和结束索引
                    int start = indices.get(currentIndex);
                    int end = start + searchField.getText().length();

                    // Replace the text
                    // 替换文本
                    doc.remove(start, end - start);
                    doc.insertString(start, replacement, null);

                    // Update the list of indices since the text has changed
                    // 所以我们需要重新搜索
                    performSearch();
                } 
                catch (BadLocationException e) 
                {
                    e.printStackTrace();
                }
            }

            // If there are no text to replace, show a popup message
            // 如果没有文本可以替换，则显示弹出消息
            else
            {
                gui.currentPopup = new PopupMessage(gui, "There's no text to replace");
                gui.currentPopup.setVisible(true);
            }
        }

        else
        {
            gui.currentPopup = new PopupMessage(gui, "There's no text to replace or no text to enter replaced text");
            gui.currentPopup.setVisible(true);
        }
    }

    // Action listener method
    // 操作侦听器方法
    @Override
    public void actionPerformed(ActionEvent e) 
    {
        switch (e.getActionCommand())
        {
            case "Close":
                findReplaceDialog.dispose(); break;
            case "Search":
                performSearch(); break;
            case "Next":
                nextMatch(); break;
            case "Prev":
                prevMatch(); break;
            case "Replace":
                replaceText(); break;
        }
    }


    // Key listener methods
    // 键盘侦听器方法
    @Override
    public void keyPressed(KeyEvent e) 
    {
        // Two cases for closing the window
        if (e.getSource() == gui.textArea && e.getKeyCode() == KeyEvent.VK_ESCAPE) findReplaceDialog.dispose();  
        else if (e.getSource() == replaceField && e.getKeyCode() == KeyEvent.VK_ESCAPE) findReplaceDialog.dispose();
        
        // Case for closing or performing search when search field is focused
        // 当搜索字段聚焦时关闭或执行搜索的情况
        else if (KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() == searchField)
        {
            switch (e.getKeyCode())
            {
                case KeyEvent.VK_ESCAPE:
                    findReplaceDialog.dispose(); break;
                case KeyEvent.VK_ENTER:
                    performSearch(); break;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e)
    {

    }

    @Override
    public void keyReleased(KeyEvent e)
    {

    }
}
