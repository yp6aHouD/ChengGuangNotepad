package com.guangnotepad;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


// RightClickMenu class
// 右键菜单类

public class RightClickMenu implements ActionListener
{
    private GUI gui;
    FormatFunction formatFunction;

    public RightClickMenu(GUI gui) 
    {
        this.gui = gui;
        formatFunction = new FormatFunction(gui, gui.doc);

        // Create the right-click menu
        // 创建右键菜单
        final JPopupMenu popupMenu = new JPopupMenu();

        // Create the menu items and add action listeners
        // 创建菜单项并添加动作监听器
        JMenuItem mouseCut = new JMenuItem("Cut");
        mouseCut.addActionListener(this);
        mouseCut.setActionCommand("Cut");

        JMenuItem mouseCopy = new JMenuItem("Copy");
        mouseCopy.addActionListener(this);
        mouseCopy.setActionCommand("Copy");

        JMenuItem mousePaste = new JMenuItem("Paste");
        mousePaste.addActionListener(this);
        mousePaste.setActionCommand("Paste");

        JMenuItem mouseFont = new JMenuItem("Font settings");
        mouseFont.addActionListener(this);
        mouseFont.setActionCommand("Font");

        // Add the menu items to the popup menu
        // 将菜单项添加到弹出菜单
        popupMenu.add(mouseCut);
        popupMenu.add(mouseCopy);
        popupMenu.add(mousePaste);
        popupMenu.add(mouseFont);
        gui.textArea.setComponentPopupMenu(popupMenu);

        // Attach the popup menu to the text component
        // 将弹出菜单附加到文本组件
        gui.textArea.addMouseListener(new MouseAdapter() 
        {
            @Override
            public void mouseReleased(MouseEvent e)
            {
                if (e.isPopupTrigger())
                {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

    }

    // Action listener for the right-click menu
    // 右键菜单的动作监听器
    @Override
    public void actionPerformed(ActionEvent e)
    {
        switch (e.getActionCommand())
        {
            case "Cut": gui.editFunction.cut(); break;
            case "Copy": gui.editFunction.copy(); break;
            case "Paste": gui.editFunction.paste(); break;
            case "Font": gui.formatFunction.setTextFontAndSize(); break;
        }
    }
}