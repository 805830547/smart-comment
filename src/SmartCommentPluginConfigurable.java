import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SmartCommentPluginConfigurable implements SearchableConfigurable {
    private SmartCommentGui mGUI;

    public SmartCommentPluginConfigurable() {
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        this.mGUI = new SmartCommentGui();
        this.mGUI.createUI();
        return this.mGUI.getRootPanel();
    }

    @Override
    public void disposeUIResources() {
        this.mGUI = null;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Smart Comment";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return "preference.SmartCommentPluginConfigurable";
    }

    @NotNull
    @Override
    public String getId() {
        return "preference.SmartCommentPluginConfigurable";
    }

    @Nullable
    @Override
    public Runnable enableSearch(String s) {
        return null;
    }

    /**
     * @author zhiqiangzhang
     * @method SmartCommentPluginConfigurable#isModified
     * @description 是否修改，关联配置页面的[reset]的状态，显示|隐藏
     * @return
     */
    @Override
    public boolean isModified() {
        return !this.mGUI.isNotModified();
    }

    /**
     * @author zhiqiangzhang
     * @method SmartCommentPluginConfigurable#apply
     * @description 保存用户改动配置 对应配置页面的[apply]
     * @throws ConfigurationException
     */
    @Override
    public void apply() throws ConfigurationException {
        this.mGUI.apply();
    }


    /**
     * @author zhiqiangzhang
     * @method SmartCommentPluginConfigurable#reset
     * @description 恢复默认设置，关联配置页面的[reset]
     */
    @Override
    public void reset() {
        this.mGUI.reloadConfig();
    }

}
