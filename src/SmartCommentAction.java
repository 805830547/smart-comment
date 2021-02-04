import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.psi.javadoc.PsiDocComment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author zhiqiangzhang
 * @email ly805830547@gmail.com
 * @name SmartCommentAction
 * @description 智能注释动作触发类
 * @date 2021-02-04 15:07:51
 */
public class SmartCommentAction extends AnAction {
    //换行
    private static final String STR_WRAP = "\n";
    //关键词 void
    private static final String KW_VOID = "void";
    //日期格式化
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * @author zhiqiangzhang
     * @method SmartCommentAction#actionPerformed
     * @description 点击动作实现
     * @param e
     */
    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (psiFile.getFileType() != JavaFileType.INSTANCE) {
            //非java文件，不处理
            return;
        }
        //java文件，获取光标位置
        DataContext dataContext = e.getDataContext();
        Editor editor = CommonDataKeys.EDITOR.getData(dataContext);;
        //光标位置偏移量
        int offset = editor.getCaretModel().getCurrentCaret().getOffset();
        //获取类文件
        PsiJavaFileImpl psiJavaFile = (PsiJavaFileImpl) psiFile;
        //查找光标所在类
        PsiClass psiClass = getTargetClass(offset, psiJavaFile.getClasses());
        if (null == psiClass) {
            return;
        }
        //获取项目
        Project project = e.getProject();
        //获取配置
        SmartCommentConfig config = SmartCommentConfig.getInstance();
        //查找光标所在方法
        PsiMethod psiMethod = getTargetMethod(offset, psiClass.getMethods());
        if (null != psiMethod) {
            //光标在方法内-方法注释
            addCommentToTarget(project, psiMethod, getMethodComment(psiClass.getQualifiedName(), psiMethod, config));
            return;
        }
        //查找光标所在属性
        PsiField psiField = getTargetField(offset, psiClass.getFields());
        if (null != psiField) {
            //光标在属性内-属性注释
            addCommentToTarget(project, psiField, getFieldComment(psiField));
            return;
        }
        //光标在类内但是不属于任何一个方法或属性-类注释
        addCommentToTarget(project, psiClass, getClassComment(psiClass, config));
    }

    /**
     * @author 张志强
     * @method SmartCommentAction#getTargetClass
     * @description 查找光标所在类
     * @param offset
     * @param classes
     * @return
     */
    private PsiClass getTargetClass(int offset, PsiClass[] classes) {
        //获取光标所在的相对外部类
        PsiClass psiClass = getTargetClassCommon(offset, classes);
        if (null == psiClass) {
            //光标不在任一相对外部类内，返回null
            return null;
        }

        return getTargetInnerClass(offset, psiClass);
    }

    /**
     * @author 张志强
     * @method SmartCommentAction#getTargetInnerClass
     * @description 查找光标所在类
     * @param offset
     * @param psiClass
     * @return
     */
    private PsiClass getTargetInnerClass(int offset, PsiClass psiClass) {
        //相对外部类存在，获取相对外部类的相对内部类
        PsiClass[] classes = psiClass.getInnerClasses();
        if (classes.length == 0) {
            //相对内部类不存在，返回该相对外部类
            return psiClass;
        }
        //相对内部类存在，获取光标所在的相对内部类
        PsiClass innerClass = getTargetClassCommon(offset, classes);
        if (null == innerClass) {
            //光标不在任一相对内部类内，返回相对外部类外部类
            return psiClass;
        }

        return getTargetInnerClass(offset, innerClass);
    }

    /**
     * @author 张志强
     * @method SmartCommentAction#getTargetClassCommon
     * @description 查找光标所在类
     * @param offset
     * @param classes
     * @return
     */
    private PsiClass getTargetClassCommon(int offset, PsiClass[] classes) {
        for (PsiClass psiClass : classes) {
            if (psiClass.getTextRange().contains(offset)) {
                return psiClass;
            }
        }

        return null;
    }

    /**
     * @author zhiqiangzhang
     * @method SmartCommentAction#getTargetMethod
     * @description 查找光标所在方法
     * @param offset
     * @param allMethods
     * @return
     */
    private PsiMethod getTargetMethod(int offset, PsiMethod[] allMethods) {
        for (PsiMethod method : allMethods) {
            if (method.getTextRange().contains(offset)) {
                return method;
            }
        }

        return null;
    }

    /**
     * @author 张志强
     * @method SmartCommentAction#getTargetField
     * @description 查找光标所在属性
     * @param offset
     * @param allFields
     * @return
     */
    private PsiField getTargetField(int offset, PsiField[] allFields) {
        for (PsiField field : allFields) {
            if (field.getTextRange().contains(offset)) {
                return field;
            }
        }

        return null;
    }

    /**
     * @author zhiqiangzhang
     * @method SmartCommentAction#getClassComment
     * @description 获取类注释
     * @param psiClass
     * @param config
     * @return
     */
    private String getClassComment(PsiClass psiClass, SmartCommentConfig config) {
        StringBuilder sb = new StringBuilder();
        sb.append("/**" + STR_WRAP);
        sb.append(" * @author " + config.getAuthorText() + STR_WRAP);
        sb.append(" * @email " + config.getEmailText() + STR_WRAP);
        sb.append(" * @name " + psiClass.getQualifiedName() + STR_WRAP);
        sb.append(" * @description TODO" + STR_WRAP);
        sb.append(" * @date " + LocalDateTime.now().format(DATE_TIME_FORMATTER) + STR_WRAP);
        sb.append(" */");

        return sb.toString();
    }

    /**
     * @author zhiqiangzhang
     * @method SmartCommentAction#getMethodComment
     * @description 获取方法注释
     * @param classFullName
     * @param targetMethod
     * @param config
     * @return
     */
    private String getMethodComment(String classFullName, PsiMethod targetMethod, SmartCommentConfig config) {
        StringBuilder sb = new StringBuilder();
        sb.append("/**" + STR_WRAP);
        sb.append(" * @author " + config.getAuthorText() + STR_WRAP);
        sb.append(" * @method " + classFullName + "#" + targetMethod.getName() + STR_WRAP);
        sb.append(" * @description TODO" + STR_WRAP);
        //入参
        for (PsiParameter parameter : targetMethod.getParameterList().getParameters()) {
            sb.append(" * @param " + parameter.getName() + STR_WRAP);
        }
        //返回
        PsiType returnType = targetMethod.getReturnType();
        if (null != returnType && !KW_VOID.equals(returnType.getPresentableText())) {
            sb.append(" * @return" + STR_WRAP);
        }
        //异常
        for (PsiClassType referencedType : targetMethod.getThrowsList().getReferencedTypes()) {
            sb.append(" * @throws " + referencedType.getClassName() + STR_WRAP);
        }
        sb.append(" */" + STR_WRAP);

        return sb.toString();
    }

    /**
     * @author 张志强
     * @method SmartCommentAction#getFieldComment
     * @description 获取属性注释
     * @param psiField
     * @return
     */
    private String getFieldComment(PsiField psiField) {
        StringBuilder sb = new StringBuilder();
        sb.append("/**" + STR_WRAP);
        sb.append(" * " + psiField.getName() + STR_WRAP);
        sb.append(" */");

        return sb.toString();
    }

    /**
     * @author zhiqiangzhang
     * @method SmartCommentAction#addCommentToTarget
     * @description 添加注释到目标
     * @param project
     * @param target
     * @param comment
     */
    private void addCommentToTarget(Project project, PsiElement target, String comment) {
        //通过获取到PsiElementFactory来创建相应的Element，包括字段，方法，注解，类，内部类等等
        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
        //创建java doc
        PsiDocComment psiDocComment = elementFactory.createDocCommentFromText(comment);
        //将注释添加至方法前
        WriteCommandAction.runWriteCommandAction(project, () -> {
            target.addAfter(psiDocComment, null);
        });
    }

}
