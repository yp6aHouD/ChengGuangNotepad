package com.guangnotepad;

import java.awt.Dimension;
import java.awt.Point;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

/* 
 * The PopupMessage class creates a popup message window that appears in the top right corner of the screen.
 * The message window is displayed for a set amount of time before fading out.
 * 
 * PopupMessage类创建一个弹出消息窗口，出现在屏幕的右上角。
 * 消息窗口在一定时间内显示，然后淡出。
 */

// Popup message class
// 弹出消息类

public class PopupMessage extends JWindow 
{
    // Fade time in milliseconds
    // 淡出时间（毫秒）
    private static final int FADE_TIME = 1000; 
    private JLabel label;

    // Constructor
    // 构造函数
    public PopupMessage(GUI gui, String message) 
    {
        // Close any existing popups
        // 关闭任何现有的弹出窗口

        if (gui.currentPopup != null) 
        {
            gui.currentPopup.dispose();
        }

        setAlwaysOnTop(true);
        
        // Create a label with the message
        // 使用消息创建一个标签
        label = new JLabel(message, SwingConstants.CENTER);

        // Set the label spacing
        // 设置标签间距
        label.setBorder(new EmptyBorder(1, 10, 1, 10)); 
        add(label);

        // Set the size of the popup window
        // 设置弹出窗口的大小
        pack();

        // Set the location of the window in the top right corner
        // 窗口位置在右上角
        Point location = gui.menuBar.getLocationOnScreen();
        Dimension screenSize = gui.menuBar.getSize();
        setLocation(location.x + screenSize.width - getWidth() - 10, location.y + 7);

        // Add the message
        // 添加消息
        add(new JLabel(message, SwingConstants.CENTER));

        // Create a timer for the fade-out animation
        // 创建一个计时器以进行淡出动画
        Timer timer = new Timer(FADE_TIME / 100, e -> 
        {
            float opacity = getOpacity();
            opacity -= 0.01f;
            setOpacity(Math.max(opacity, 0));
            if (opacity <= 0) 
            {
                ((Timer) e.getSource()).stop();
                dispose();
            }
        });

        // Start the timer
        // 启动计时器
        timer.start();
    }
}
