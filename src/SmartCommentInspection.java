import com.intellij.codeInspection.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * @author zhiqiangzhang
 * @email ly805830547@gmail.com
 * @name SmartCommentInspection
 * @description 代码检查类
 * @date 2021-02-19 22:23:33
 */
public class SmartCommentInspection extends AbstractBaseJavaLocalInspectionTool {

    public static final String QUICK_FIX_NAME = "TODO comment";
    private static final Logger LOG = Logger.getInstance("#SmartCommentInspection");
    private final CriQuickFix myQuickFix = new CriQuickFix();

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @NonNls
            private final String DESCRIPTION_TEMPLATE = QUICK_FIX_NAME;

            @Override
            public void visitClass(PsiClass aClass) {
                super.visitClass(aClass);
                doCheckForClassElement(aClass);
            }

            @Override
            public void visitMethod(PsiMethod method) {
                super.visitMethod(method);
                doCheckForClassElement(method);
            }

            @Override
            public void visitField(PsiField field) {
                super.visitField(field);
                doCheckForClassElement(field);
            }

            private void doCheckForClassElement(PsiJavaDocumentedElement element) {
                SmartCommentConfig commentConfig = SmartCommentConfig.getInstance(element.getProject());
                String elementName;
                if (element instanceof PsiClass) {
                    if (!commentConfig.isClassInspection()) {
                        return;
                    }
                    elementName = "Class";
                } else if (element instanceof PsiMethod) {
                    if (!commentConfig.isMethodInspection()) {
                        return;
                    }
                    elementName = "Method";
                } else if (element instanceof PsiField) {
                    if (!commentConfig.isFieldInspection()) {
                        return;
                    }
                    elementName = "Field";
                } else {
                    return;
                }
                PsiDocComment docComment = element.getDocComment();
                if (null == docComment) {
                    holder.registerProblem(element, elementName + ": " + DESCRIPTION_TEMPLATE, myQuickFix);
                }
            }

        };
    }

    /**
     * @author zhiqiangzhang
     * @email ly805830547@gmail.com
     * @name SmartCommentInspection.CriQuickFix
     * @description 注释检查-自动修改
     * @date 2021-02-19 22:23:54
     */
    private static class CriQuickFix implements LocalQuickFix {

        @NotNull
        @Override
        public String getName() {
            return QUICK_FIX_NAME;
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            try {
                SmartCommentAction.addCommentToTarget(project, descriptor.getPsiElement());
            } catch (IncorrectOperationException e) {
                LOG.error(e);
            }
        }

        @NotNull
        @Override
        public String getFamilyName() {
            return getName();
        }

    }

}
