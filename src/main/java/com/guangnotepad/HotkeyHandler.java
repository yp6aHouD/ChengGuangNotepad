package com.guangnotepad;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

// Hotkey handler class
// 热键处理器类

@SuppressWarnings("unused")
public class HotkeyHandler implements KeyListener
{
    GUI gui;
    public HotkeyHandler(GUI gui)
    {
        this.gui = gui;
    }

    // Key press handling
    // 键盘按键处理
    @Override
    public void keyPressed(KeyEvent e)
    {
        if (e.isAltDown() && e.getKeyCode() == KeyEvent.VK_F4) gui.fileFunction.exit();
        
        // Ctrl +
        if (e.isControlDown() & !e.isShiftDown()) 
        {
            switch (e.getKeyCode())
            {
                case KeyEvent.VK_N: gui.fileFunction.newFile(); break; // new file // 新文件
                case KeyEvent.VK_O: gui.fileFunction.openFile(); break; // open file // 打开文件
                case KeyEvent.VK_S: gui.fileFunction.save(); break; // save file // 保存文件
                case KeyEvent.VK_Z: gui.editFunction.undo(); break; // undo // 撤销
                case KeyEvent.VK_X: gui.editFunction.cut(); break; // cut // 剪切
                case KeyEvent.VK_C: gui.editFunction.copy(); break; // copy // 复制
                case KeyEvent.VK_V: gui.editFunction.paste(); break; // paste // 粘贴
                case KeyEvent.VK_F: gui.editFunction.find(); break; // find // 查找
                case KeyEvent.VK_B: gui.formatFunction.setTextColor(false); break; // change character background // 更改字符背景
                case KeyEvent.VK_R: gui.formatFunction.resetText(); break; // quick reset // 快速重置
                default: break;
            }
        }
        
        // Ctrl + Shift +
        if (e.isControlDown() & e.isShiftDown()) 
        {
            switch(e.getKeyCode())
            {
                case KeyEvent.VK_S: gui.fileFunction.saveAs(); break; // save as // 另存为
                case KeyEvent.VK_Z: gui.editFunction.redo(); break; // redo // 重做
                case KeyEvent.VK_T: gui.formatFunction.setTextFontAndSize(); break; // change font and size // 更改字体和大小
                case KeyEvent.VK_C: gui.formatFunction.setTextColor(true); break; // change text color // 更改文本颜色
                case KeyEvent.VK_H: gui.formatFunction.quickHighlight(); break; // quick highlight // 快速高亮
                case KeyEvent.VK_B: gui.formatFunction.setBackgroundColor(); break; // change background color // 更改背景颜色
                default: break;
            }
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e)
    {
        
    }

    @Override
    public void keyTyped(KeyEvent e) 
    {
        
    }
}