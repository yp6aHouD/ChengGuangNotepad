package com.guangnotepad;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;

/* 
 * All formatting in the program works through StyledDocument
 * These classes allow you to format parts of the text, not the entire text
 * Including you can change the color, font, text size in the selected area
 * 
 * 这个程序中的所有格式化都是通过StyledDocument类来实现的
 * 这些类允许你格式化文本的部分，而不是整个文本
 * 包括你可以在选定区域改变颜色，字体，文本大小
 */


// "Format" menu class
// 功能"字体和大小"的类

public class FormatFunction
{
    private GUI gui;
    private static String selectedFont;
    private static String selectedFontStyle;
    private static int selectedFontSize;
    private StyledDocument doc;

    public FormatFunction(GUI gui)
    {
        this.gui = gui;
    }

    // Method "Font and size"
    // "字体和大小" 方法
    public void setTextFontAndSize() 
    {
        // Getting the current font, size, and style of the text
        // 获取当前文本的字体、大小和样式
        if (selectedFont == null)
        {
            selectedFont = gui.textArea.getFont().getFamily();
        }
        if (selectedFontSize == 0)
        {
            selectedFontSize = gui.textArea.getFont().getSize();
        }
        if (selectedFontStyle == null)
        {
            selectedFontStyle = "Default";
        }
        
        // Getting and creating a dropdown list with fonts
        // 获取并创建一个带有字体的下拉列表
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        JComboBox<String> fontList = new JComboBox<>(fonts);
        fontList.setEditable(true);
        fontList.setSelectedItem(selectedFont);
        
        // Creating a dropdown list with sizes
        // 创建一个包含大小的下拉列表
        Integer[] fontSizes = {8, 10, 11, 12, 13, 14, 16, 18, 20, 22, 24, 26, 28, 30};
        JComboBox<Integer> fontSizeList = new JComboBox<>(fontSizes);
        fontSizeList.setEditable(false);
        fontSizeList.setSelectedItem(selectedFontSize);

        // Creating a dropdown list with text styles
        // 创建一个包含文本样式的下拉列表
        String[] fontStyles = {"Default", "Bold", "Italic", "Bold Italic"};
        JComboBox<String> fontStyleList = new JComboBox<>(fontStyles);
        fontStyleList.setEditable(false);
        fontStyleList.setSelectedItem(selectedFontStyle);
        
        // Creating a panel with dropdown lists
        // 创建一个带有下拉列表的面板
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Creating a GridBagLayout and GridBagConstraints
        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Creating the rows of the panel
        // 创建面板的第一行
        JPanel firstRow = new JPanel(gridBag);
        JPanel secondRow = new JPanel(gridBag);
        JPanel thirdRow = new JPanel(gridBag);

        gbc.fill = GridBagConstraints.HORIZONTAL;

        // First row
        gbc.gridx = 0;
        gbc.gridy = 0; // First row
        firstRow.add(new JLabel("Font:"), gbc);

        gbc.gridx = 1;
        firstRow.add(fontList, gbc);

        gbc.gridx = 2;
        firstRow.add(Box.createHorizontalStrut(10), gbc);

        gbc.gridx = 3;
        firstRow.add(new JLabel("Size:"), gbc);

        gbc.gridx = 4;
        firstRow.add(fontSizeList, gbc);

        // Second row
        gbc.gridx = 4; 
        gbc.gridy = 1; 
        secondRow.add(new JLabel("Style:"), gbc);

        gbc.gridx = 5;
        secondRow.add(fontStyleList, gbc);

        gbc.gridx = 6;
        secondRow.add(Box.createHorizontalStrut(20), gbc);
        
        // Third row
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 5; // Let the component take 5 columns
        gbc.anchor = GridBagConstraints.CENTER; // Center the component
        JLabel previewLabel = new JLabel("Preview text | Текст для предпросмотра");

        thirdRow.add(previewLabel, gbc);
        gbc.gridwidth = 1;

        previewLabel.setFont(new Font(selectedFont, Font.PLAIN, selectedFontSize));

        // Adding rows to the panel
        // 将行添加到面板
        panel.add(firstRow); 
        panel.add(secondRow);
        panel.add(thirdRow);

        // Updating the preview text when selecting a font, size, or style
        // 选择字体、大小或样式时更新预览文本
        fontList.addActionListener(e -> updatePreview(previewLabel, fontList, fontSizeList, fontStyleList));
        fontSizeList.addActionListener(e -> updatePreview(previewLabel, fontList, fontSizeList, fontStyleList));
        fontStyleList.addActionListener(e -> updatePreview(previewLabel, fontList, fontSizeList, fontStyleList));

        // Show the window and choose the font / size / style
        // 显示窗口并选择字体/大小/样式
        JOptionPane.showMessageDialog(null, panel, "Choose Font and Size", JOptionPane.QUESTION_MESSAGE);
        selectedFont = (String) fontList.getSelectedItem();
        selectedFontSize = (Integer) fontSizeList.getSelectedItem();
        selectedFontStyle = (String) fontStyleList.getSelectedItem();

        // Getting the selected text
        // 获取选定的文本
        String selectedText = gui.textArea.getSelectedText();

        if (selectedText != null) 
        {
            // Getting the start and end of the selected text
            // 获取选定文本的开始和结束
            int start = gui.textArea.getSelectionStart();
            int end = gui.textArea.getSelectionEnd();

            // Creating a new style
            // 创建新样式
            Style style = gui.doc.addStyle(null, null);
            StyleConstants.setFontFamily(style, selectedFont);
            StyleConstants.setFontSize(style, selectedFontSize);

            // Switch for setting the text style
            // Switch用于设置文本样式
            switch (selectedFontStyle) 
            {
                case "Bold":
                    StyleConstants.setBold(style, true);
                    break;
                case "Italic":
                    StyleConstants.setItalic(style, true);
                    break;
                case "Bold Italic":
                    StyleConstants.setBold(style, true);
                    StyleConstants.setItalic(style, true);
                    break;
                default:
                    break;
            }

            // Applying the style to the selected text
            // 将样式应用于选定的文本
            gui.doc.setCharacterAttributes(start, end - start, style, false);
        } 

        else 
        {
            // Creating a new style
            // 创建新样式
            Style style = gui.doc.addStyle(null, null);
            StyleConstants.setFontFamily(style, selectedFont);
            StyleConstants.setFontSize(style, selectedFontSize);

            // Switch for setting the text style
            // 用Switch于设置文本样式
            switch (selectedFontStyle) 
            {
                case "Bold":
                    StyleConstants.setBold(style, true);
                    break;
                case "Italic":
                    StyleConstants.setItalic(style, true);
                    break;
                case "Bold Italic":
                    StyleConstants.setBold(style, true);
                    StyleConstants.setItalic(style, true);
                    break;
                default:
                    break;
            }

            // Setting the font, size and font style for all text if nothing is selected
            // 如果没有选择任何内容，则为所有文本设置字体、大小和字体样式
            MutableAttributeSet attrs = gui.textArea.getInputAttributes();
            attrs.removeAttribute(StyleConstants.FontFamily);
            attrs.removeAttribute(StyleConstants.FontSize);
            attrs.removeAttribute(StyleConstants.Bold);
            attrs.removeAttribute(StyleConstants.Italic);
            attrs.addAttributes(style);
        }
    }


    // Method "Text color"
    // "文本颜色" 方法
    public void setTextColor(boolean isTextColor)
    {
        // Displaying a dialog box
        // 显示对话框
        String dialogName = isTextColor ? "Choose text color" : "Choose text background color";
        Color defaultColor = isTextColor ? Color.BLACK : Color.WHITE;
        Color selectedColor = JColorChooser.showDialog(null, dialogName, defaultColor);
        String selectedText = gui.textArea.getSelectedText();
        
        // If text was selected
        // 如果选择了文本
        if (selectedText != null)
        {
            // Creating a style to change the color of only the selected text
            // 创建一个样式，只改变选定文本的颜色
            Style style = gui.doc.addStyle(null, null);

            // Getting the start and end of the selected text
            // 获取选定文本的开始和结束
            int start = gui.textArea.getSelectionStart();
            int end = gui.textArea.getSelectionEnd();
            
            // If the text color is selected
            // 如果选择了文本颜色
            if (selectedColor != null)
            {
                // Setting the color of the selected text using setAttributes to Document
                // 使用setAttributes将选定文本的颜色设置为Document
                if (isTextColor)
                {
                    StyleConstants.setForeground(style, selectedColor);
                    gui.doc.setCharacterAttributes(start, end - start, style, false);
                }
                else
                {
                    StyleConstants.setBackground(style, selectedColor);
                    gui.doc.setCharacterAttributes(start, end - start, style, false);
                }
            }
        }
        
        // If no text was selected
        // 如果没有选择文本
        else
        {
            if (selectedColor != null)
            {
                // Creating a new style
                // 创建新样式
                Style style = gui.doc.addStyle(null, null);

                // Setting new style
                // 设置新样式
                if (isTextColor)
                {
                    StyleConstants.setForeground(style, selectedColor);
                    gui.textArea.getInputAttributes().removeAttribute(StyleConstants.Foreground);
                }
                else
                {
                    StyleConstants.setBackground(style, selectedColor);
                    gui.textArea.getInputAttributes().removeAttribute(StyleConstants.Background);
                }
                
                // Setting properties for new input text
                // 为新输入的文本设置属性
                MutableAttributeSet attrs = gui.textArea.getInputAttributes();
                attrs.addAttributes(style);
            }
        }

    }

    // Method "Area color"
    // "区域颜色" 方法
    public void setBackgroundColor() 
    {
        // Displaying the JColorChooser dialog box and getting the selected color
        // 显示JColorChooser对话框并获取选定的颜色
        Color selectedColor = JColorChooser.showDialog(null, "Choose area color", Color.WHITE);

        if (selectedColor == null) return;

        gui.textArea.setBackground(selectedColor);
    }

    // Method “Quick highlight”
    // “快速高亮” 方法
    public void quickHighlight()
    {
        // Getting the selected text
        // 获取选定的文本

        doc = gui.doc;

        String selectedText = gui.textArea.getSelectedText();

        if (selectedText == null) return;
        {
            // Receiving the start and end of the selected text
            // 获取选定文本的开始和结束
            int start = gui.textArea.getSelectionStart();
            int end = gui.textArea.getSelectionEnd();

            // Creating new style
            // 创建新样式
            MutableAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setBackground(attrs, Color.YELLOW);

            // Applying new style to selected region
            // 将新样式应用于选定区域\
            doc.setCharacterAttributes(start, end - start, attrs, false);
            
            // Popup message
            // 弹出消息
            gui.currentPopup = new PopupMessage(gui, "Text highlighted!");
            gui.currentPopup.setVisible(true);
        }
    }

    // Method "Reset text"
    // "重置文本" 方法
    public void resetText()
    {
        // Getting the current caret position and StyledDocument
        // 获取当前插入符位置和StyledDocument
        int caret = gui.textArea.getCaretPosition();
        Style style = gui.doc.addStyle(null, null);

        // Setting the color of the text to black
        // 将文本颜色设置为黑色
        StyleConstants.setForeground(style, Color.BLACK);
        gui.doc.setCharacterAttributes(caret, gui.doc.getLength() - caret, style, false);

        // Setting the background color of the text to the background color of JTextArea
        // 将文本的背景颜色设置为JTextArea的背景颜色
        MutableAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setBackground(attrs, gui.textArea.getBackground());

        // Setting the font and size of the text to the default
        // 将文本的字体和大小设置为默认值
        StyleConstants.setFontFamily(attrs, "Default");
        StyleConstants.setFontSize(attrs, 13);

        // Applying the attributes to the text
        // 将属性应用于文本
        gui.textArea.setCharacterAttributes(attrs, true);
        StyleConstants.setBackground(style, gui.textArea.getBackground());
        gui.doc.setCharacterAttributes(caret, gui.doc.getLength() - caret, style, false);

        // Popup message
        // 弹出消息
        gui.currentPopup = new PopupMessage(gui, "Text settings has been reset!");
        gui.currentPopup.setVisible(true);
    }

    // Method "Update preview text"
    // "更新预览文本" 方法
    private void updatePreview(JLabel label, JComboBox<String> fontList,
        JComboBox<Integer> fontSizeList, JComboBox<String> fontStyleList) 
    {
        // Updating the preview text when selecting a font, size, or style
        // 选择字体、大小或样式时更新预览文本
        String fontName = (String)fontList.getSelectedItem();
        int fontSize = (Integer)fontSizeList.getSelectedItem();
        String fontStyle = (String)fontStyleList.getSelectedItem();
    
        // Setting the font, size, and style for the preview text
        // 为预览文本设置字体、大小和样式
        int style = Font.PLAIN;

        // Using bitwise OR to combine the font style
        // 使用按位或运算符来组合字体样式
        if (fontStyle.contains("Bold")) style |= Font.BOLD;
        if (fontStyle.contains("Italic")) style |= Font.ITALIC;
        label.setFont(new Font(fontName, style, fontSize));

        // Updating the size of the window (to avoid text size clipping)
        // 更新窗口的大小（以避免文本大小裁剪）
        SwingUtilities.getWindowAncestor(label).pack();
    }
}
