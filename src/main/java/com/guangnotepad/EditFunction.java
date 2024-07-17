package com.guangnotepad;

// "Edit" menu class
// "编辑"菜单类

public class EditFunction 
{
    private GUI gui;

    public EditFunction(GUI gui)
    {
        this.gui = gui;
    }

    // "Undo" function
    // "撤销"功能
    public void undo()
    {
        // Use UndoManager and display notification
        // 使用UndoManager并显示通知
        gui.um.undo();
        gui.currentPopup = new PopupMessage(gui, "Undo");
        gui.currentPopup.setVisible(true);
    }

    // "Redo" function
    // "重做"功能
    public void redo()
    {
        // Use UndoManager and display notification
        // 使用UndoManager并显示通知
        gui.um.redo();
        gui.currentPopup = new PopupMessage(gui, "Redo");
        gui.currentPopup.setVisible(true);
    }


    // Text cut function
    // 文本剪切功能
    public void cut()
    {
        // Get selected text
        // 获取选定的文本
        String selectedText = gui.textArea.getSelectedText();

        // If text is selected, copy it and delete
        // 如果文本被选中，复制并删除
        if (selectedText != null)
        {
            gui.textArea.copy();
            gui.textArea.replaceSelection("");
        }

        // Otherwise, display a message
        // 否则，显示一条消息
        else 
        {
            gui.currentPopup = new PopupMessage(gui, "No text selected");
            gui.currentPopup.setVisible(true);
        }
    }

    // Text copy function
    // 文本复制功能
    public void copy()
    {
        // Get selected text
        // 获取选定的文本
        String selectedText = gui.textArea.getSelectedText();

        // If text is selected, copy it
        // 如果文本被选中，复制它
        if (selectedText != null)
        {
            gui.textArea.copy();
            gui.currentPopup = new PopupMessage(gui, "Copied!");
            gui.currentPopup.setVisible(true);
        }

        // Otherwise, display a message
        // 否则，显示一条消息
        else
        {
            gui.currentPopup = new PopupMessage(gui, "No text selected");
            gui.currentPopup.setVisible(true);
        }
    }

    // Paste function
    // 粘贴功能
    public void paste()
    {
        // Save the current cursor position
        // 保存当前光标位置
        int position = gui.textArea.getCaretPosition(); 

        // Paste text
        // 粘贴文本
        gui.textArea.paste();

        // Set the cursor position to the end of the pasted text
        // 将光标位置设置为粘贴文本的末尾
        gui.textArea.setCaretPosition(position + gui.textArea.getSelectedText().length());
    }

    // Search function
    // 搜索功能
    public void find() 
    {
        new SearchFunction(gui);
    }
}
