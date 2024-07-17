package com.guangnotepad;

import java.awt.FileDialog;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.Document;

import org.mozilla.universalchardet.UniversalDetector;

/* 
 * The "file" menu class uses the FileDialog class to display a dialog message
 * With a path selection for reading/writing a file, 
 * FileReader and FileWriter for reading text from a file and writing text to a file. 
 * Each method uses the isSaved and isNewFile variables to track whether the file has been saved or just created. 
 * For user convenience, the word "Modified" is added to the window title,
 * Indicating that changes have been made to the file.
 * 
 * "文件"菜单类使用FileDialog类显示一个对话框消息，用于选择读取/写入文件的路径，
 * FileReader和FileWriter用于从文件读取文本和写入文本到文件。
 * 每个方法都使用isSaved和isNewFile变量来跟踪文件是否已保存或刚刚创建。
 * 为了用户方便，窗口标题中添加了"Modified"字样，表示已对文件进行了修改。
*/



// "File" menu class
// "文件"菜单类

public class FileFunction
{
    private Document doc;
    private GUI gui;
    private String fileName, fileAddress, fileExtension, selectedEncoding;
    private boolean isNewFile = true;
    boolean isSaved = false;

    // Constructor
    // 构造函数
    public FileFunction(GUI gui)
    {
        this.gui = gui;
    }

    // New file method
    // 新建文件方法
    public void newFile()
    {
        // Get document from text area
        // 从文本区域获取文档
        doc = gui.textArea.getDocument();

        // If text area is not empty, ask user if they want to save
        // 如果文本区域不为空，询问用户是否要保存
        if (doc.getLength() > 0)
        {
            int result = JOptionPane.showConfirmDialog(gui.window,
            "Do you want to save the current file?",
              "Save", JOptionPane.YES_NO_CANCEL_OPTION);

            // If user selects "Yes", save file
            // 如果用户选择"是"，保存文件
            if (result == JOptionPane.YES_OPTION) 
            {
                save();
                gui.textArea.setText("");
                gui.window.setTitle(("New"));

                isNewFile = true;
                isSaved = false;

                gui.currentPopup = new PopupMessage(gui, "File saved, new file created!");
                gui.currentPopup.setVisible(true);
            } 
            
            // If user selects "No", clear text area and set title to "New"
            // 如果用户选择"否"，清空文本区域并将标题设置为"New"
            else if (result == JOptionPane.NO_OPTION)
            {
                gui.textArea.setText("");
                gui.window.setTitle(("New"));
                
                isSaved = false;

                gui.currentPopup = new PopupMessage(gui, "File Created!");
                gui.currentPopup.setVisible(true);
            }

            // If user selects "Cancel", return
            // 如果用户选择"取消"，返回
            else if (result == JOptionPane.CANCEL_OPTION)
            {
                return;
            }
        }

        // If text area is empty, clear text area and set title to "New"
        // 如果文本区域为空，清空文本区域并将标题设置为"New"
        else
        {
            gui.textArea.setText("");
            gui.window.setTitle(("New"));
    
            isNewFile = true;
            isSaved = false;

            gui.currentPopup = new PopupMessage(gui, "File Created!");
            gui.currentPopup.setVisible(true);
        }
    }
    
    // Open file method
    // 打开文件方法
    public void openFile()
    {
        // Окно выбора файла
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text document", "txt");
        fileChooser.setFileFilter(filter);
        
        // Отображаем диалоговое окно для выбора файла
        int result = fileChooser.showOpenDialog(gui.window);

        // Если файл выбран, обновляем имя файла, адрес и заголовок окна
        if (result == JFileChooser.APPROVE_OPTION)
        {
            File selectedFile = fileChooser.getSelectedFile();
    
            // Узнаём имя файла и расширение
            fileName = selectedFile.getName();
            fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);

            if (!fileExtension.equalsIgnoreCase("txt"))
            {
                gui.currentPopup = new PopupMessage(gui, "Only TXT files can be opened!");
                gui.currentPopup.setVisible(true);
                return;
            }

            // Если файл выбран и txt
            selectedEncoding = detectFileEncoding(selectedFile);
            fileAddress = selectedFile.getAbsolutePath();
            gui.window.setTitle(fileName);
            readFileContents(selectedFile);
            isNewFile = false;

            // Отменяем заголовок "modified", который автоматически ставится при открытии файла
            if (gui.window.getTitle().endsWith(" — Modified"))
                {
                    String title = gui.window.getTitle();
                    title = title.replace(" — Modified", "");
                    gui.window.setTitle(title);
                }
            isSaved = true;
        }

        else 
        {
            gui.currentPopup = new PopupMessage(gui, "File wasn't selected!");
            gui.currentPopup.setVisible(true);
        }
    }


    // Save file method
    // 保存文件方法
    public void save()
    {
        // If file is new, call saveAs method
        // 如果文件是新的，调用saveAs方法
        if (isNewFile)
        {
            saveAs();
        }

        // If file is not new, write to file using FileWriter
        // 如果文件不是新的，使用FileWriter写入文件
        else
        {
            // Try to write to file, if exception occurs, show error message
            // 尝试写入文件，如果发生异常，显示错误消息
            try
            {
                FileWriter fw = new FileWriter(fileAddress);
                fw.write(gui.textArea.getText());
                fw.close();

                if (gui.window.getTitle().endsWith(" — Modified"))
                {
                    String title = gui.window.getTitle();
                    title = title.replace(" — Modified", "");
                    gui.window.setTitle(title);
                }

                isSaved = true;

                gui.currentPopup = new PopupMessage(gui, "File Saved!");
                gui.currentPopup.setVisible(true);
            }
            catch (Exception e)
            {
                gui.currentPopup = new PopupMessage(gui, "Exception when saving file!\n" + e.toString());
                gui.currentPopup.setVisible(true);
            }
        }
    }


    // Save as method
    // 另存为方法
    public void saveAs()
    {
        // Create a new file dialog in save mode
        // 在保存模式下创建一个新的文件对话框
        FileDialog fd = new FileDialog(gui.window, "save", FileDialog.SAVE);
        fd.setVisible(true);

        // If user have chosen destination, set file name and address, and set title
        // 如果用户选择了目的地，设置文件名和地址，并设置标题
        if (fd.getFile() != null)
        {
            fileName = fd.getFile();
            fileAddress = fd.getDirectory();

            // Check if the file has an extension
            // 检查文件是否有扩展名
            if (!fileName.contains("."))
            {
                // If not, add .txt
                fileName += ".txt";
            }

            gui.window.setTitle(fileName);
        }

        // If file is not selected, return
        // 如果没有选择文件，返回
        else
        {
            gui.currentPopup = new PopupMessage(gui, "File wasn't saved!");
            gui.currentPopup.setVisible(true);
            return;
        }

        // Try to write to file using FileWriter, if exception occurs, show error message
        // 使用FileWriter尝试写入文件，如果发生异常，显示错误消息
        try 
        {
            FileWriter fw = new FileWriter(fileAddress + fileName);
            fw.write(gui.textArea.getText());
            fw.close();

            if (gui.window.getTitle().endsWith(" — Modified"))
                {
                    String title = gui.window.getTitle();
                    title = title.replace(" — Modified", "");
                    gui.window.setTitle(title);
                }

            isNewFile = false;
            isSaved = true;

            gui.currentPopup = new PopupMessage(gui, "File saved!");
            gui.currentPopup.setVisible(true);
        }
        catch (Exception e)
        {
            gui.currentPopup = new PopupMessage(gui, "Exception when saving file!\n" + e.toString());
            gui.currentPopup.setVisible(true);
        }
    }

    // Exit method
    // 退出方法
    public void exit()
    {
        doc = gui.textArea.getDocument();

        if (isSaved) System.exit(0);
        else if (!isSaved && isNewFile && doc.getLength() == 0) System.exit(0);

        else if (!isSaved && !isNewFile ||
                 !isSaved && isNewFile && doc.getLength() > 0)
        {
            int result = JOptionPane.showConfirmDialog(gui.window,
            "Do you want to save the file before leaving?",
              "Save", JOptionPane.YES_NO_CANCEL_OPTION);

            // If user selects "Yes", save file
            // 如果用户选择"是"，保存文件
            if (result == JOptionPane.YES_OPTION) 
            {
                save();
                if (isSaved) System.exit(0);
                else return;
            } 
            
            // If user selects "No", clear text area and set title to "New"
            // 如果用户选择"否"，清空文本区域并将标题设置为"New"
            else if (result == JOptionPane.NO_OPTION)
            {
                System.exit(0);
            }

            // If user selects "Cancel", return
            // 如果用户选择"取消"，返回
            else if (result == JOptionPane.CANCEL_OPTION)
            {
                return;
            }
        }
        else System.exit(0);
    }

    void readFileContents(File file)
    {
        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), selectedEncoding))) 
        {
            String line;
            while ((line = reader.readLine()) != null) 
            {
                content.append(line).append("\n");
            }
            
            gui.textArea.setText(content.toString());
        } 
        catch (Exception e) 
        {
            JOptionPane.showMessageDialog(null, "Error reading file: " + e.getMessage());
        }
    }

    private String detectFileEncoding(File file) {
        byte[] buf = new byte[4096];
        try (FileInputStream fis = new FileInputStream(file)) {
            UniversalDetector detector = new UniversalDetector(null);
            
            int nread;
            while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }
            detector.dataEnd();
            
            String encoding = detector.getDetectedCharset();
            detector.reset();
            
            if (encoding != null) {
                return encoding;
            } else {
                return "CP1251"; // Возвращаем значение по умолчанию, если кодировка не была определена
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "CP1251"; // Возвращаем значение по умолчанию в случае ошибки
        }
    }
}
