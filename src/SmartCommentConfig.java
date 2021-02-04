import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author zhiqiangzhang
 * @email ly805830547@gmail.com
 * @name SmartCommentConfig
 * @description 智能注释数据持久化配置类
 * @date 2021-02-04 15:13:13
 */
@State(
        name = "SmartCommentConfig",
        storages = {@Storage("SmartCommentConfig.xml")}
)
public class SmartCommentConfig implements PersistentStateComponent<SmartCommentConfig> {
    public String authorText = "";
    public String emailText = "";

    SmartCommentConfig() {
    }

    public String getAuthorText() {
        return authorText;
    }

    public void setAuthorText(String authorText) {
        this.authorText = authorText;
    }

    public String getEmailText() {
        return emailText;
    }

    public void setEmailText(String emailText) {
        this.emailText = emailText;
    }

    /**
     * @author zhiqiangzhang
     * @method SmartCommentConfig#getInstance
     * @description 通过传入的工程获取核心配置
     * @return
     */
    @Nullable
    public static SmartCommentConfig getInstance() {
        return ServiceManager.getService(SmartCommentConfig.class);
    }

    /**
     * @author zhiqiangzhang
     * @method SmartCommentConfig#getState
     * @description 获取配置
     * @return
     */
    @Nullable
    @Override
    public SmartCommentConfig getState() {
        return this;
    }

    /**
     * @author zhiqiangzhang
     * @method SmartCommentConfig#loadState
     * @description 从持久化数据中加载配置
     * @param singleFileExecutionConfig
     */
    @Override
    public void loadState(@NotNull SmartCommentConfig singleFileExecutionConfig) {
        if (singleFileExecutionConfig == null) {
            throw new NullPointerException();
        }

        XmlSerializerUtil.copyBean(singleFileExecutionConfig, this);
    }

}
