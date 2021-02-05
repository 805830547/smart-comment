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
    private JCheckBox classAuthor;
    private JCheckBox classEmail;
    private JCheckBox className;
    private JCheckBox classDescription;
    private JCheckBox classDate;
    private JCheckBox methodAuthor;
    private JCheckBox methodMethod;
    private JCheckBox methodDescription;
    private JCheckBox methodParam;
    private JCheckBox methodReturn;
    private JCheckBox fieldName;
    private JCheckBox methodThrows;
    private JCheckBox commentInspection;

    private SmartCommentConfig mConfig;

    private void createUIComponents() {
    }

    public void createUI(Project project) {
        this.mConfig = SmartCommentConfig.getInstance(project);
        reloadConfig();
    }

    public JPanel getRootPanel() {
        return this.rootPanel;
    }

    public boolean isNotModified() {
        return this.authorText.getText().equals(this.mConfig.getAuthorText())
                && this.emailText.getText().equals(this.mConfig.getEmailText())

                && this.classAuthor.isSelected() == this.mConfig.isClassAuthor()
                && this.classEmail.isSelected() == this.mConfig.isClassEmail()
                && this.className.isSelected() == this.mConfig.isClassName()
                && this.classDescription.isSelected() == this.mConfig.isClassDescription()
                && this.classDate.isSelected() == this.mConfig.isClassDate()

                && this.methodAuthor.isSelected() == this.mConfig.isMethodAuthor()
                && this.methodMethod.isSelected() == this.mConfig.isMethodMethod()
                && this.methodDescription.isSelected() == this.mConfig.isMethodDescription()
                && this.methodParam.isSelected() == this.mConfig.isMethodParam()
                && this.methodReturn.isSelected() == this.mConfig.isMethodReturn()
                && this.methodThrows.isSelected() == this.mConfig.isMethodThrows()

                && this.fieldName.isSelected() == this.mConfig.isFieldName()

                && this.commentInspection.isSelected() == this.mConfig.isCommentInspection();
    }

    public void apply() {
        this.mConfig.setAuthorText(this.authorText.getText());
        this.mConfig.setEmailText(this.emailText.getText());

        this.mConfig.setClassAuthor(this.classAuthor.isSelected());
        this.mConfig.setClassEmail(this.classEmail.isSelected());
        this.mConfig.setClassName(this.className.isSelected());
        this.mConfig.setClassDescription(this.classDescription.isSelected());
        this.mConfig.setClassDate(this.classDate.isSelected());

        this.mConfig.setMethodAuthor(this.methodAuthor.isSelected());
        this.mConfig.setMethodMethod(this.methodMethod.isSelected());
        this.mConfig.setMethodDescription(this.methodDescription.isSelected());
        this.mConfig.setMethodParam(this.methodParam.isSelected());
        this.mConfig.setMethodReturn(this.methodReturn.isSelected());
        this.mConfig.setMethodThrows(this.methodThrows.isSelected());

        this.mConfig.setFieldName(this.fieldName.isSelected());

        this.mConfig.setCommentInspection(this.commentInspection.isSelected());
    }

    public void reloadConfig() {
        this.authorText.setText(this.mConfig.getAuthorText());
        this.emailText.setText(this.mConfig.getEmailText());

        this.classAuthor.setSelected(this.mConfig.isClassAuthor());
        this.classEmail.setSelected(this.mConfig.isClassEmail());
        this.className.setSelected(this.mConfig.isClassName());
        this.classDescription.setSelected(this.mConfig.isClassDescription());
        this.classDate.setSelected(this.mConfig.isClassDate());

        this.methodAuthor.setSelected(this.mConfig.isMethodAuthor());
        this.methodMethod.setSelected(this.mConfig.isMethodMethod());
        this.methodDescription.setSelected(this.mConfig.isMethodDescription());
        this.methodParam.setSelected(this.mConfig.isMethodParam());
        this.methodReturn.setSelected(this.mConfig.isMethodReturn());
        this.methodThrows.setSelected(this.mConfig.isMethodThrows());

        this.fieldName.setSelected(this.mConfig.isFieldName());

        this.commentInspection.setSelected(this.mConfig.isCommentInspection());
    }

}
