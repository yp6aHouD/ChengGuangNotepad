package com.guangnotepad;

import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.*;
import javax.swing.text.rtf.RTFEditorKit;

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
    private File selectedFile;
    private StyledDocument doc;
    private GUI gui;
    private String fileName, fileExtension;
    private String selectedEncoding = System.getProperty("file.encoding");;
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
        doc = gui.textArea.getStyledDocument();

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
        // Setting file chooser dialog window
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter txtFilter = new FileNameExtensionFilter("Text Files (*.txt)", "txt");
        FileNameExtensionFilter rtfFilter = new FileNameExtensionFilter("Rich Text Format (*.rtf)", "rtf");

        fileChooser.addChoosableFileFilter(txtFilter);
        fileChooser.addChoosableFileFilter(rtfFilter);

        fileChooser.setFileFilter(txtFilter);
        
        // Showing dialog window and getting result
        int result = fileChooser.showOpenDialog(gui.window);

        // If user selects a file
        if (result == JFileChooser.APPROVE_OPTION)
        {
            selectedFile = fileChooser.getSelectedFile();
    
            // Getting name and file extension
            fileName = selectedFile.getName();
            fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);

            // If there's no extension or it's not a txt or rtf file
            if (!fileExtension.equalsIgnoreCase("txt") && !fileExtension.equalsIgnoreCase("rtf"))
            {
                gui.currentPopup = new PopupMessage(gui, "Only txt/rtf files can be opened!");
                gui.currentPopup.setVisible(true);
                return;
            }

            selectedEncoding = detectFileEncoding(selectedFile);

            switch (fileExtension)
            {
                case "txt":
                    readTxtFile(selectedFile);
                    break;
                case "rtf":
                    readRtfFile(selectedFile);
                    break;
            }

            gui.window.setTitle(fileName);
            isNewFile = false;
            isSaved = true;

            // Removing "Modified" title after opening file, because program automatically adds it
            if (gui.window.getTitle().endsWith(" — Modified"))
            {
                String title = gui.window.getTitle();
                title = title.replace(" — Modified", "");
                gui.window.setTitle(title);
            }
        }

        // If user selects "cancel" option
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
            fileName = selectedFile.getName();
            if (fileName.endsWith(".rtf"))
            {
                RTFEditorKit rtfKit = new RTFEditorKit();

                try (FileOutputStream fos = new FileOutputStream(selectedFile))
                {
                    rtfKit.write(fos, gui.textArea.getStyledDocument(), 0, gui.textArea.getStyledDocument().getLength());
                }             
                catch (Exception e)
                {
                    gui.currentPopup = new PopupMessage(gui, "Error saving rtf file (211): " + e.getMessage());
                    gui.currentPopup.setVisible(true);
                } 
            }
            else if (fileName.endsWith(".txt"))
            {
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(selectedFile), selectedEncoding)))
                {
                    writer.write(gui.textArea.getText());
                }
                catch (Exception e)
                {
                    gui.currentPopup = new PopupMessage(gui, "Error saving txt file (223): " + e.getMessage());
                    gui.currentPopup.setVisible(true);
                } 
            }
            

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
    }


    // Save as method
    // 另存为方法
    public void saveAs()
    {
        // Create a new FileChooser
        // 在保存模式下创建一个新的文件对话框
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save as...");

        // Создание фильтров для txt и rtf форматов
        FileNameExtensionFilter txtFilter = new FileNameExtensionFilter("Text Files (*.txt)", "txt");
        FileNameExtensionFilter rtfFilter = new FileNameExtensionFilter("Rich Text Format (*.rtf)", "rtf");

        // Добавление фильтров в JFileChooser
        fileChooser.addChoosableFileFilter(txtFilter);
        fileChooser.addChoosableFileFilter(rtfFilter);

        // Установка фильтра txt как фильтра по умолчанию
        fileChooser.setFileFilter(txtFilter);

        int userSelection = fileChooser.showSaveDialog(gui.window);

        if (userSelection == JFileChooser.APPROVE_OPTION)
        {
            File fileToSave = fileChooser.getSelectedFile();
            String selectedExtension = ((FileNameExtensionFilter)fileChooser.getFileFilter()).getExtensions()[0];
            
            // Проверка выбранного расширения и сохранение файла в соответствующем формате
            if ("txt".equals(selectedExtension)) 
            {
                // Сохранение файла в формате .txt
                saveAsTxt(fileToSave);
            } 

            else if ("rtf".equals(selectedExtension))
            {
                // Сохранение файла в формате .rtf
                saveAsRtf(fileToSave);
            }
        }
    }


    // Exit method
    // 退出方法
    public void exit()
    {
        doc = gui.textArea.getStyledDocument();

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

    // Read txt file
    void readTxtFile(File file)
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
            JOptionPane.showMessageDialog(null, "Error reading txt file: " + e.getMessage());
        }
    }


    // Read rtf file
    void readRtfFile(File file)
    {
        RTFEditorKit rtfKit = new RTFEditorKit();
        selectedEncoding = detectFileEncoding(file);

        try (InputStreamReader isr = new InputStreamReader(new FileInputStream(file), selectedEncoding))
        {
            StyledDocument doc = gui.textArea.getStyledDocument();
            doc.remove(0, doc.getLength());
            rtfKit.read(isr, doc, 0);
        }
        catch (IOException | BadLocationException e)
        {
            e.printStackTrace();
        }
    }


    // file encoding detector
    private String detectFileEncoding(File file) 
    {
        byte[] buf = new byte[4096];
        try (FileInputStream fis = new FileInputStream(file))
        {
            UniversalDetector detector = new UniversalDetector(null);
            
            int nread;
            while ((nread = fis.read(buf)) > 0 && !detector.isDone())
            {
                detector.handleData(buf, 0, nread);
            }
            detector.dataEnd();
            
            String encoding = detector.getDetectedCharset();
            detector.reset();
            
            if (encoding != null)
            {
                return encoding;
            } 
            else 
            {
                return "CP1251"; // Возвращаем значение по умолчанию, если кодировка не была определена
            }

        } 
        
        catch (IOException e)
        {
            e.printStackTrace();
            return "CP1251"; // Возвращаем значение по умолчанию в случае ошибки
        }
    }

    
    // save as txt file
    void saveAsTxt(File file)
    {   
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter
            (new FileOutputStream(
                file.getName().contains(".txt") ? file.getAbsolutePath() : file.getAbsolutePath() + ".txt"), selectedEncoding)))
        {
            writer.write(gui.textArea.getText());

            if (gui.window.getTitle().endsWith(" — Modified"))
            {
                String title = gui.window.getTitle();
                title = title.replace(" — Modified", "");
                gui.window.setTitle(title);
            }

            isNewFile = false;
            isSaved = true;

            gui.currentPopup = new PopupMessage(gui, "File saved in txt!");
            gui.currentPopup.setVisible(true);

            gui.window.setTitle(file.getName().contains(".txt") ? file.getName() : file.getName() + ".txt");

            String filePath = file.getAbsolutePath().contains(".txt") ? file.getAbsolutePath() : file.getAbsolutePath() + ".txt";
            selectedFile = new File(filePath);
        }

        catch (Exception e)
        {
            gui.currentPopup = new PopupMessage(gui, "Exception when saving file in txt format!\n" + e.toString());
            gui.currentPopup.setVisible(true);
        }
    }


    // save as rtf file
    void saveAsRtf(File file)
    {
        String filePath = file.getAbsolutePath().contains(".rtf") ? file.getAbsolutePath() : file.getAbsolutePath() + ".rtf";
        selectedFile = new File(filePath);

        RTFEditorKit rtfKit = new RTFEditorKit();
        try (BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(selectedFile)))
        {
            rtfKit.write(fos, gui.doc, 0, gui.doc.getLength());
            
            if (gui.window.getTitle().endsWith(" — Modified"))
            {
                String title = gui.window.getTitle();
                title = title.replace(" — Modified", "");
                gui.window.setTitle(title);
            }

            isNewFile = false;
            isSaved = true;

            gui.currentPopup = new PopupMessage(gui, "File saved in rtf!");
            gui.currentPopup.setVisible(true);

            gui.window.setTitle(file.getName().contains(".rtf") ? file.getName() : file.getName() + ".rtf");
        }

        catch (Exception e)
        {
            gui.currentPopup = new PopupMessage(gui, "Exception when saving file in rtf format!\n" + e.toString());
            gui.currentPopup.setVisible(true);
        }
    }
}
