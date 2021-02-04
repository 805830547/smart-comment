import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.psi.javadoc.PsiDocComment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class SmartCommentAction extends AnAction {
    //换行
    private static final String STR_WRAP = "\n";
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
        if (psiFile.getFileType() == JavaFileType.INSTANCE) {
            //java文件，获取光标位置
            DataContext dataContext = e.getDataContext();
            Editor editor = CommonDataKeys.EDITOR.getData(dataContext);;
            Caret caret = editor.getCaretModel().getCurrentCaret();

            //光标位置偏移量
            int offset = caret.getOffset();

            //获取类方法
            PsiJavaFileImpl psiJavaFile = (PsiJavaFileImpl) psiFile;
            PsiClass[] classes = psiJavaFile.getClasses();
            PsiClass psiClass = classes[0];

            //获取项目
            Project project = e.getProject();
            //获取配置
            SmartCommentConfig config = SmartCommentConfig.getInstance();

            if (!psiClass.getTextRange().contains(offset)) {
                //光标在类外-类注释
                addCommentToTarget(project, psiClass, getClassComment(psiClass, config));
                return;
            }

            //查找光标所在方法
            PsiMethod targetMethod = getTargetMethod(caret.getOffset(), psiClass.getMethods());
            if (Objects.isNull(targetMethod)) {
                //光标在类内但是不属于任何一个方法-类注释
                //光标在方法内-方法注释
                addCommentToTarget(project, psiClass, getClassComment(psiClass, config));
                return;
            }

            //光标在方法内-方法注释
            addCommentToTarget(project, targetMethod,
                    getMethodComment(psiClass.getQualifiedName(), targetMethod, config));
        }
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
        if (!"void".equals(targetMethod.getReturnType().getPresentableText())) {
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

}
