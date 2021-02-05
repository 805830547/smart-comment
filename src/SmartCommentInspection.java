import com.intellij.codeInspection.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class SmartCommentInspection extends AbstractBaseJavaLocalInspectionTool {

    public static final String QUICK_FIX_NAME = InspectionsBundle.message("todo.comment.display.name");
    private static final Logger LOG = Logger.getInstance("#SmartCommentInspection");
    private final CriQuickFix myQuickFix = new CriQuickFix();

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @NonNls
            private final String DESCRIPTION_TEMPLATE = InspectionsBundle.message("todo.comment.display.name");

            @Override
            public void visitClass(PsiClass aClass) {
                super.visitClass(aClass);
                doCheckForClassElement(aClass, "Class");
            }

            @Override
            public void visitMethod(PsiMethod method) {
                super.visitMethod(method);
                doCheckForClassElement(method, "Method");
            }

            @Override
            public void visitField(PsiField field) {
                super.visitField(field);
                doCheckForClassElement(field, "Field");
            }

            private void doCheckForClassElement(PsiJavaDocumentedElement element, String elementName) {
                PsiDocComment docComment = element.getDocComment();
                if (null == docComment) {
                    holder.registerProblem(element, elementName + ": " + DESCRIPTION_TEMPLATE, myQuickFix);
                }
            }

        };
    }

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
