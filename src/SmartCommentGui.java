import com.intellij.openapi.project.Project;

import javax.swing.*;

/**
 * @author zhiqiangzhang
 * @email ly805830547@gmail.com
 * @name SmartCommentGui
 * @description 配置界面
 * @date 2021-02-04 00:07:26
 */
public class SmartCommentGui {
    private JTextField authorText;
    private JTextField emailText;
    private JPanel rootPanel;

    private SmartCommentConfig mConfig;

    private void createUIComponents() {
    }

    public void createUI() {
        this.mConfig = SmartCommentConfig.getInstance();
        reloadConfig();
    }



    public JPanel getRootPanel() {
        return this.rootPanel;
    }

    public boolean isNotModified() {
        return this.authorText.getText().equals(this.mConfig.getAuthorText())
                && this.emailText.getText().equals(this.mConfig.getEmailText());
    }

    public void apply() {
        this.mConfig.setAuthorText(this.authorText.getText());
        this.mConfig.setEmailText(this.emailText.getText());
    }

    public void reloadConfig() {
        this.authorText.setText(this.mConfig.getAuthorText());
        this.emailText.setText(this.mConfig.getEmailText());
    }

}
