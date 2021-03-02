import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassImpl;
import com.intellij.psi.impl.source.PsiFieldImpl;
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
    /**
     * STR_WRAP 换行
     */
    private static final String STR_WRAP = "\n";
    /**
     * COMMENT_PRE 注释前缀
     */
    private static final String COMMENT_PRE = "/**\n";
    /**
     * KW_VOID 关键词 void
     */
    private static final String KW_VOID = "void";
    /**
     * DATE_TIME_FORMATTER 日期格式化
     */
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
        //查找光标所在方法
        PsiMethod psiMethod = getTargetMethod(offset, psiClass.getMethods());
        if (null != psiMethod) {
            //光标在方法内-方法注释
            addCommentToTarget(project, psiMethod);
            return;
        }
        //查找光标所在属性
        PsiField psiField = getTargetField(offset, psiClass.getFields());
        if (null != psiField) {
            //光标在属性内-属性注释
            addCommentToTarget(project, psiField);
            return;
        }
        //光标在类内但是不属于任何一个方法或属性-类注释
        addCommentToTarget(project, psiClass);
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
    private static String getClassComment(PsiClass psiClass, SmartCommentConfig config) {
        StringBuilder sb = new StringBuilder();
        sb.append("/**" + STR_WRAP);
        if (config.isClassDescription()) {
            sb.append(" * TODO" + STR_WRAP);
        }
        //类名
        if (config.isClassName()) {
            sb.append(" * @name " + psiClass.getQualifiedName() + STR_WRAP);
        }
        //作者
        if (config.isClassAuthor()) {
            sb.append(" * @author " + config.getAuthorTextOrDefault() + STR_WRAP);
        }
        //Email
        if (config.isClassEmail()) {
            sb.append(" * @email " + config.getEmailText() + STR_WRAP);
        }
        //日期
        if (config.isClassDate()) {
            sb.append(" * @date " + LocalDateTime.now().format(DATE_TIME_FORMATTER) + STR_WRAP);
        }
        if (COMMENT_PRE.equals(sb.toString())) {
            sb.append(" * " + STR_WRAP);
        }
        sb.append(" */");

        return sb.toString();
    }

    /**
     * @author zhiqiangzhang
     * @method SmartCommentAction#getMethodComment
     * @description 获取方法注释
     * @param psiMethod
     * @param config
     * @return
     */
    private static String getMethodComment(PsiMethod psiMethod, SmartCommentConfig config) {
        StringBuilder sb = new StringBuilder();
        sb.append("/**" + STR_WRAP);
        //描述
        if (config.isMethodDescription()) {
            sb.append(" * TODO" + STR_WRAP);
            sb.append(" * " + STR_WRAP);
        }
        //入参
        if (config.isMethodParam()) {
            for (PsiParameter parameter : psiMethod.getParameterList().getParameters()) {
                sb.append(" * @param " + parameter.getName() + STR_WRAP);
            }
        }
        //返回
        if (config.isMethodReturn()) {
            PsiType returnType = psiMethod.getReturnType();
            if (null != returnType && !KW_VOID.equals(returnType.getPresentableText())) {
                sb.append(" * @return" + STR_WRAP);
            }
        }
        //异常
        if (config.isMethodThrows()) {
            for (PsiClassType referencedType : psiMethod.getThrowsList().getReferencedTypes()) {
                sb.append(" * @throws " + referencedType.getClassName() + STR_WRAP);
            }
        }
        //方法名
        if (config.isMethodMethod()) {
            sb.append(" * @method " + ((PsiClassImpl) psiMethod.getParent()).getQualifiedName()
                    + "#" + psiMethod.getHierarchicalMethodSignature().getName() + STR_WRAP);
        }
        //作者
        if (config.isMethodAuthor()) {
            sb.append(" * @author " + config.getAuthorTextOrDefault() + STR_WRAP);
        }
        if (COMMENT_PRE.equals(sb.toString())) {
            sb.append(" * " + STR_WRAP);
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
    private static String getFieldComment(PsiField psiField, SmartCommentConfig config) {
        StringBuilder sb = new StringBuilder();
        sb.append("/**" + STR_WRAP);
        sb.append(" * " + (config.isFieldName() ? ((PsiFieldImpl)psiField).getName() + " " : "") + "TODO" + STR_WRAP);
        sb.append(" */");

        return sb.toString();
    }


    /**
     * @author zhiqiangzhang
     * @method SmartCommentAction#addCommentToTarget
     * @description 添加注释到目标
     * @param project
     * @param psiElement
     */
    public static void addCommentToTarget(Project project, PsiElement psiElement) {
        //获取配置
        SmartCommentConfig config = SmartCommentConfig.getInstance(project);
        if (psiElement instanceof PsiClass) {
            addCommentToTarget(project, psiElement, getClassComment((PsiClass)psiElement, config));
            return;
        }
        if (psiElement instanceof PsiMethod) {
            addCommentToTarget(project, psiElement, getMethodComment((PsiMethod)psiElement, config));
            return;
        }
        if (psiElement instanceof PsiField) {
            addCommentToTarget(project, psiElement, getFieldComment((PsiField)psiElement, config));
            return;
        }
    }

    /**
     * @author zhiqiangzhang
     * @method SmartCommentAction#addCommentToTarget
     * @description 添加注释到目标
     * @param project
     * @param target
     * @param comment
     */
    private static void addCommentToTarget(Project project, PsiElement target, String comment) {
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
