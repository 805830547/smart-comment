import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
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
    private String authorText = "";
    private String emailText = "";
    private boolean classAuthor = true;
    private boolean classEmail = true;
    private boolean className = true;
    private boolean classDescription = true;
    private boolean classDate = true;
    private boolean methodAuthor = true;
    private boolean methodMethod = true;
    private boolean methodDescription = true;
    private boolean methodParam = true;
    private boolean methodReturn = true;
    private boolean fieldName = true;
    private boolean methodThrows = true;

    SmartCommentConfig() {
    }


    /**
     * @author zhiqiangzhang
     * @method SmartCommentConfig#getAuthorTextOrDefault
     * @description 获取用户名，没有配置则取系统用户名
     * @return
     */
    public String getAuthorTextOrDefault() {
        String userName = authorText;
        if (null != userName && userName.trim().length() > 0) {
            return userName;
        }
        userName = System.getProperty("user.name");
        if (null != userName && userName.trim().length() > 0) {
            return userName;
        }
        userName = System.getenv("USER");
        if (null != userName && userName.trim().length() > 0) {
            return userName;
        }
        return "";
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

    public boolean isClassAuthor() {
        return classAuthor;
    }

    public void setClassAuthor(boolean classAuthor) {
        this.classAuthor = classAuthor;
    }

    public boolean isClassEmail() {
        return classEmail;
    }

    public void setClassEmail(boolean classEmail) {
        this.classEmail = classEmail;
    }

    public boolean isClassName() {
        return className;
    }

    public void setClassName(boolean className) {
        this.className = className;
    }

    public boolean isClassDescription() {
        return classDescription;
    }

    public void setClassDescription(boolean classDescription) {
        this.classDescription = classDescription;
    }

    public boolean isClassDate() {
        return classDate;
    }

    public void setClassDate(boolean classDate) {
        this.classDate = classDate;
    }

    public boolean isMethodAuthor() {
        return methodAuthor;
    }

    public void setMethodAuthor(boolean methodAuthor) {
        this.methodAuthor = methodAuthor;
    }

    public boolean isMethodMethod() {
        return methodMethod;
    }

    public void setMethodMethod(boolean methodMethod) {
        this.methodMethod = methodMethod;
    }

    public boolean isMethodDescription() {
        return methodDescription;
    }

    public void setMethodDescription(boolean methodDescription) {
        this.methodDescription = methodDescription;
    }

    public boolean isMethodParam() {
        return methodParam;
    }

    public void setMethodParam(boolean methodParam) {
        this.methodParam = methodParam;
    }

    public boolean isMethodReturn() {
        return methodReturn;
    }

    public void setMethodReturn(boolean methodReturn) {
        this.methodReturn = methodReturn;
    }

    public boolean isFieldName() {
        return fieldName;
    }

    public void setFieldName(boolean fieldName) {
        this.fieldName = fieldName;
    }

    public boolean isMethodThrows() {
        return methodThrows;
    }

    public void setMethodThrows(boolean methodThrows) {
        this.methodThrows = methodThrows;
    }

    /**
     * @author zhiqiangzhang
     * @method SmartCommentConfig#getInstance
     * @description 通过传入的工程获取核心配置
     * @return
     */
    @Nullable
    public static SmartCommentConfig getInstance(Project project) {
        return ServiceManager.getService(project, SmartCommentConfig.class);
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
