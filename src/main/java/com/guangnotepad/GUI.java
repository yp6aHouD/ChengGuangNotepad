package com.guangnotepad;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.undo.UndoManager;


// GUI class
// GUI 类

public class GUI implements ActionListener
{
    public StyledDocument doc;
    // Main window 
    // 主窗口
    JFrame window;

    // Popup message 
    // 弹出消息
    PopupMessage currentPopup;
    
    // TextArea 
    // 文本区
    JTextPane textArea;
    JScrollPane scrollPane;

    //Top Menu Bar 
    // 顶部菜单栏
    JMenuBar menuBar;
    JMenu fileMenu, editMenu, formatMenu;
    JMenuItem fNew, fOpen, fSave, fSaveAs, fExit,
    eUndo, eRedo, eCut, eCopy, ePaste, eFind,
    fFontAndSize, fTextColor, fTextHighlightColor,fBackgroundColor;
    
    // Buttons resetText and quickHighlight 
    // 按钮 resetText 和 quickHighlight
    JButton resetText = new JButton("Reset text settings");
    JButton quickHighlight = new JButton("Highlight");

    // Methods of the framework 
    // 框架的方法
    FileFunction fileFunction = new FileFunction(this);
    FormatFunction formatFunction = new FormatFunction(this, doc);
    EditFunction editFunction = new EditFunction(this);
    HotkeyHandler hotkeyHandler = new HotkeyHandler(this);
    UndoManager um = new UndoManager();
    RightClickMenu rightClickMenu;

    // Constructor 
    // 构造方法
    public GUI()
    {
        // Create window 
        // 创建窗口
        createWindow();

        // Create text area 
        // 创建文本区
        createTextPane();

        // Create menu bar 
        // 创建菜单栏
        createMenuBar();

        // Create menu "File"
        // 创建 "文件" 菜单
        createFileMenu();

        // Create menu "Edit"
        // 创建 "编辑" 菜单
        createEditMenu();

        // Create menu "Format"
        // 创建 "格式" 菜单
        createFormatMenu();

        // Add right mouse click menu
        // 添加右键菜单
        addRightMouseClickMenu();
        
        // Set window visible
        // 设置窗口可见
        window.setVisible(true); 
    }

    // Create window method 
    // 创建窗口方法
    public void createWindow()
    {
        window = new JFrame("NotePad by Neliubin Daniil");
        window.setSize(650, 450);
        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        window.setLocationRelativeTo(null);

        // Add window listener to replace default closing operation
        // 添加窗口监听器以替换默认关闭操作
        window.addWindowListener(new WindowAdapter() 
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                fileFunction.exit();
            }
        });
    }

    // Create text area method
    // 创建文本区方法
    public void createTextPane()
    {
        /* 
         * When creating text area using JTextPane instead of JTextArea
         * To make text area more flexible and customizable
         * Also added undo manager using UndoManager class
         * 
         * 创建文本区时使用 JTextPane 而不是 JTextArea
         * 使文本区更加灵活和可定制
         * 还使用 UndoManager 类添加了撤销管理器
         */
        textArea = new JTextPane();
        textArea.addKeyListener(hotkeyHandler);

        // Add undo manager 
        // 添加撤销管理器
        textArea.getDocument().addUndoableEditListener(new UndoableEditListener() {
            public void undoableEditHappened(UndoableEditEvent e)
            {
                um.addEdit(e.getEdit());
            }
        });

        // Add scroll pane 
        // 添加滚动窗格
        scrollPane = new JScrollPane(textArea, 
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        this.scrollPane.setBorder(BorderFactory.createEmptyBorder());
        window.add(scrollPane);
    
        // Receiving the document from the text area 
        // And setting default text color and background color
        doc = textArea.getStyledDocument();
        MutableAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setBackground(attrs, textArea.getBackground()); // Установка цвета фона
        StyleConstants.setForeground(attrs, Color.BLACK); // Установка цвета текста
        doc.setCharacterAttributes(0, doc.getLength(), attrs, false);

        // Add a document listener to the text area
        // 为文本区域添加文档监听器
        textArea.getDocument().addDocumentListener(new DocumentListener() 
        {
            // Method for tracking text area changes
            // 跟踪文本区域变化的方法
            public void change() 
            {
                // Set isSaved to false when the text area is changed
                // 当文本区域发生变化时，将isSaved设置为false
                fileFunction.isSaved = false;

                // Setting Modified in the title of the window when the text area is changed
                // 当文本区域发生变化时，在窗口的标题中设置"Modified"
                if (!window.getTitle().endsWith(" — Modified"))
                {
                    window.setTitle(window.getTitle() + " — Modified");
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e)
            {
                change();
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                change();
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
                change();
            }
        });
    }

    // Create menu bar method
    // 创建菜单栏方法
    public void createMenuBar()
    {
        // Menu bar 
        // 菜单栏
        menuBar = new JMenuBar();

        // Menu 
        // 菜单
        fileMenu = new JMenu("File");
        editMenu = new JMenu("Edit");
        formatMenu = new JMenu("Format");

        // Add menus to menu bar
        // 将菜单添加到菜单栏
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(formatMenu);
        
        // Button "Quick reset"
        // "快速重置" 按钮
        menuBar.add(resetText);
        resetText.addActionListener(this);
        resetText.setActionCommand("Reset");
        resetText.setBorderPainted(false);
        
        // Button "Quick highlight"
        // "快速高亮" 按钮
        menuBar.add(quickHighlight);
        quickHighlight.addActionListener(this);
        quickHighlight.setActionCommand("QuickHighlight");
        quickHighlight.setBackground(Color.YELLOW);
        quickHighlight.setOpaque(true);
        quickHighlight.setBorderPainted(false);   

        // Making border invisible
        // 使边框不可见
        this.menuBar.setBorder(BorderFactory.createEmptyBorder());
        window.setJMenuBar(menuBar);
    }

    // Creating menu "File" method
    // 创造"文件" 菜单的方法
    public void createFileMenu()
    {
        /* 
         * All buttons made with html style to make hotkey text gray color
         * When creating also added ActionListener to every button
         * 
         * 所有按钮都使用 html 样式制作，使热键文本为灰色
         * 创建时还为每个按钮添加了 ActionListener
         */

        // New file
        // 新文件
        fNew = new JMenuItem("<html>New<span style='color: gray;'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Ctrl+N</span></html>");
        fNew.addActionListener(this);
        fNew.setActionCommand("New");
        fileMenu.add(fNew);
        
        // Open file
        // 打开文件
        fOpen = new JMenuItem("<html>Open<span style='color: gray;'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Ctrl+O</span></html>");
        fOpen.addActionListener(this);
        fOpen.setActionCommand("Open");
        fileMenu.add(fOpen);

        // Save file
        // 保存文件
        fSave = new JMenuItem("<html>Save<span style='color: gray;'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Ctrl+S</span></html>");
        fSave.addActionListener(this);
        fSave.setActionCommand("Save");
        fileMenu.add(fSave);

        // Save file as
        // 另存为文件
        fSaveAs = new JMenuItem("<html>Save as<span style='color: gray;'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Ctrl+Shift+S</span></html>");
        fSaveAs.addActionListener(this);
        fSaveAs.setActionCommand("SaveAs");
        fileMenu.add(fSaveAs);
        fileMenu.addSeparator();

        // Exit program
        // 退出程序
        fExit = new JMenuItem("<html>Exit<span style='color: gray;'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Alt + F4</span></html>");
        fExit.addActionListener(this);
        fExit.setActionCommand("Exit");
        fileMenu.add(fExit);
    }

    // Creating menu "Edit" method
    // 创造"编辑" 菜单的方法
    public void createEditMenu()
    {
        // Undo
        // 撤销
        eUndo = new JMenuItem("<html>Undo<span style='color: gray;'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Ctrl + Z</span></html>");
        eUndo.addActionListener(this);
        eUndo.setActionCommand("Undo");
        editMenu.add(eUndo);

        // Redo
        // 重做
        eRedo = new JMenuItem("<html>Redo<span style='color: gray;'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Ctrl + Shift + Z</span></html>");
        eRedo.addActionListener(this);
        eRedo.setActionCommand("Redo");
        editMenu.add(eRedo);

        // Cut
        // 剪切
        eCut = new JMenuItem("<html>Cut<span style='color: gray;'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Ctrl + X</span></html>");
        eCut.addActionListener(this);
        eCut.setActionCommand("Cut");
        editMenu.add(eCut);
        
        // Copy
        // 复制
        eCopy = new JMenuItem("<html>Copy<span style='color: gray;'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Ctrl + C</span></html>");
        eCopy.addActionListener(this);
        eCopy.setActionCommand("Copy");
        editMenu.add(eCopy);
        
        // Paste
        // 粘贴
        ePaste = new JMenuItem("<html>Paste<span style='color: gray;'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Ctrl + V</span></html>");
        ePaste.addActionListener(this);
        ePaste.setActionCommand("Paste");
        editMenu.add(ePaste);
        editMenu.addSeparator();

        // Find
        // 查找
        eFind = new JMenuItem("<html>Find<span style='color: gray;'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Ctrl + F</span></html>");
        eFind.addActionListener(this);
        eFind.setActionCommand("Find");
        editMenu.add(eFind);
    }

    // Creating menu "Format" method
    // 创造"格式" 菜单的方法
    public void createFormatMenu()
    {
        // Font and size
        // 字体和大小
        fFontAndSize = new JMenuItem("<html>Font<span style='color: gray;'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Ctrl+Shift+T</span></html>");
        fFontAndSize.addActionListener(this);
        fFontAndSize.setActionCommand("FontAndSize");
        formatMenu.add(fFontAndSize);     
        
        // Text color
        // 文本颜色
        fTextColor = new JMenuItem("<html>Text color<span style='color: gray;'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Ctrl+Shift+C</span></html>");
        fTextColor.addActionListener(this);
        fTextColor.setActionCommand("SetTextColor");
        formatMenu.add(fTextColor);

        // Text highlight
        // 文本高亮
        fTextHighlightColor = new JMenuItem("<html>Text highlight<span style='color: gray;'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Ctrl+B</span></html>");
        fTextHighlightColor.addActionListener(this);
        fTextHighlightColor.setActionCommand("SetTextHighlight");
        formatMenu.add(fTextHighlightColor);
        formatMenu.addSeparator();

        // Background color
        // 背景颜色
        fBackgroundColor = new JMenuItem("<html>Area color<span style='color: gray;'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Ctrl+Shift+B</span></html>");
        fBackgroundColor.addActionListener(this);
        fBackgroundColor.setActionCommand("SetBackground");
        formatMenu.add(fBackgroundColor);
    }

    // Add right mouse click menu
    // 添加右键菜单
    private void addRightMouseClickMenu() 
    {
        rightClickMenu = new RightClickMenu(this);    
    }

    // Tracking button clicks
    // 跟踪按钮点击
    @Override
    public void actionPerformed(ActionEvent e)
    {
        /* 
         * Because before assigned action command to every button,
         * We're using action command switch to determine which button was clicked
         * 
         * 因为在为每个按钮分配动作命令之前，
         * 我们正在使用动作命令切换来确定哪个按钮被点击
         */

        String command = e.getActionCommand();

        switch(command)
        {
            case "New": 
                fileFunction.newFile(); break;
            case "Open":
                fileFunction.openFile(); break;
            case "Save":
                fileFunction.save(); break;
            case "SaveAs":
                fileFunction.saveAs(); break;
            case "Exit":
                fileFunction.exit(); break;
            case "FontAndSize":
                formatFunction.setTextFontAndSize(); break;
            case "SetTextColor":
                formatFunction.setTextColor(true); break;
            case "SetTextHighlight":
                formatFunction.setTextColor(false); break;
            case "SetBackground":
                formatFunction.setBackgroundColor(); break;
            case "Reset":
                formatFunction.resetText(); break;
            case "QuickHighlight":
                formatFunction.quickHighlight(); break;
            case "Undo":
                editFunction.undo(); break;
            case "Redo":
                editFunction.redo(); break;
            case "Cut":
                editFunction.cut(); break;
            case "Copy":
                editFunction.copy(); break;
            case "Paste":
                editFunction.paste(); break;
            case "Find":
                editFunction.find(); break;
            default:
                break;
        }
    }
}