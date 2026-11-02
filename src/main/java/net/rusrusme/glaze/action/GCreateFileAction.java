package net.rusrusme.glaze.action;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import net.rusrusme.glaze.core.GIcons;
import org.jetbrains.annotations.NotNull;

public class GCreateFileAction extends CreateFileFromTemplateAction {

    public GCreateFileAction() {
        super("Glaze Script File", "Creates a Glaze Script file", GIcons.FILE);
    }

    @Override
    protected void buildDialog(@NotNull Project project, @NotNull PsiDirectory directory, CreateFileFromTemplateDialog.Builder builder) {
        builder.setTitle("New Glaze Script File")
                .addKind("Glaze Script File", GIcons.FILE, "Glaze Script File");
    }

    @Override
    protected String getActionName(PsiDirectory directory, @NotNull String newName, String templateName) {
        return "Create Glaze Script File " + newName;
    }
}