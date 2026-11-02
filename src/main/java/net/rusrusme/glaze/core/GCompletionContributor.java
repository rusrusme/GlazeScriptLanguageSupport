package net.rusrusme.glaze.core;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.ConstantNode;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import net.rusrusme.glaze.psi.*;
import org.jetbrains.annotations.NotNull;

public class GCompletionContributor extends CompletionContributor {
    public GCompletionContributor() {
        InsertHandler<LookupElement> parensInsertHandler = (context, item) -> {
            Editor editor = context.getEditor();
            Project project = context.getProject();
            TemplateManager templateManager = TemplateManager.getInstance(project);
            Template template = templateManager.createTemplate("", "", " ($EXPR$)$END$");
            template.addVariable("EXPR", new ConstantNode(""), true);
            templateManager.startTemplate(editor, template);
        };

        InsertHandler<LookupElement> listInsertHandler = (ctx, item) -> {
            Editor editor = ctx.getEditor();
            Project project = ctx.getProject();
            TemplateManager templateManager = TemplateManager.getInstance(project);
            // Inserts <$TYPE$> with tab-stop, pressing Enter/Tab jumps to $END$
            Template template = templateManager.createTemplate("", "", "<$TYPE$>$END$");
            template.addVariable("TYPE", new ConstantNode(""), true);
            templateManager.startTemplate(editor, template);
        };

        extend(CompletionType.BASIC, PlatformPatterns.psiElement(), new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters,
                                          @NotNull ProcessingContext context,
                                          @NotNull CompletionResultSet result) {
                int offset = parameters.getOffset();
                Document document = parameters.getEditor().getDocument();
                CharSequence text = document.getCharsSequence();
                PsiElement position = parameters.getPosition();

                // 1. Intrinsics and Field Access after dot (.)
                if (offset > 0 && text.charAt(offset - 1) == '.') {
                    addDotCompletions(result, position, text, offset - 1);
                    return;
                }

                // 2. Control flow keywords with auto-parens
                for (String kw : new String[]{"if", "while", "for", "switch"}) {
                    LookupElementBuilder element = LookupElementBuilder.create(kw)
                            .bold()
                            .withTailText(" ()", true)
                            .withInsertHandler(parensInsertHandler);
                    result.addElement(PrioritizedLookupElement.withPriority(element, 100.0));
                }

                // 3. Declarations and Top-Level Keywords
                String[] declKeywords = {"system", "component", "struct", "enum", "fn", "let", "var", "on_update", "on_event", "return", "break", "continue"};
                for (String kw : declKeywords) {
                    result.addElement(PrioritizedLookupElement.withPriority(LookupElementBuilder.create(kw).bold(), 110.0));
                }

                // 4. Primitive & Builtin Types
                String[] types = {
                        "void", "uint8", "int8", "uint16", "int16", "uint32", "int32", "uint64", "int64",
                        "float", "double", "char", "string", "bool", "vec2", "vec3", "vec4", "mat2", "mat3", "mat4",
                        "quat", "entity", "event", "list"
                };
                for (String t : types) {
                    result.addElement(PrioritizedLookupElement.withPriority(
                            LookupElementBuilder.create(t).bold().withIcon(AllIcons.Nodes.Type), 90.0));
                }

                // 5. User-Defined Components & Structs
                for (GComponentDeclaration comp : GPsiUtil.findComponents(position)) {
                    if (comp.getNameIdentifier() != null) {
                        result.addElement(PrioritizedLookupElement.withPriority(
                                LookupElementBuilder.create(comp.getNameIdentifier().getText())
                                        .withTypeText("component", true)
                                        .withIcon(AllIcons.Nodes.Class)
                                        .bold(), 120.0));
                    }
                }
                for (GStructDeclaration str : GPsiUtil.findStructs(position)) {
                    if (str.getNameIdentifier() != null) {
                        result.addElement(PrioritizedLookupElement.withPriority(
                                LookupElementBuilder.create(str.getNameIdentifier().getText())
                                        .withTypeText("struct", true)
                                        .withIcon(AllIcons.Nodes.Class)
                                        .bold(), 120.0));
                    }
                }

                // 6. Visible Variables & Parameters
                for (GPsiUtil.VisibleSymbol sym : GPsiUtil.findVisibleSymbols(position)) {
                    result.addElement(PrioritizedLookupElement.withPriority(
                            LookupElementBuilder.create(sym.name())
                                    .withTypeText(sym.type(), true)
                                    .withIcon(AllIcons.Nodes.Variable)
                                    .bold(), 150.0));
                }

                LookupElementBuilder listElement = LookupElementBuilder.create("list")
                        .bold()
                        .withTailText("<T>", true)
                        .withTypeText("Container", true)
                        .withIcon(AllIcons.Nodes.Type)
                        .withInsertHandler(listInsertHandler);

                result.addElement(PrioritizedLookupElement.withPriority(listElement, 140.0));
            }

            private void addDotCompletions(CompletionResultSet result, PsiElement position, CharSequence text, int dotOffset) {
                // Entity / Container Intrinsics
                result.addElement(LookupElementBuilder.create("get").withTailText("(Component)").withIcon(AllIcons.Nodes.Method).bold());
                result.addElement(LookupElementBuilder.create("add").withTailText("(Component)").withIcon(AllIcons.Nodes.Method).bold());
                result.addElement(LookupElementBuilder.create("has").withTailText("(Component)").withIcon(AllIcons.Nodes.Method).bold());
                result.addElement(LookupElementBuilder.create("remove").withTailText("(Component)").withIcon(AllIcons.Nodes.Method).bold());
                result.addElement(LookupElementBuilder.create("remove_at").withTailText("(index)").withIcon(AllIcons.Nodes.Method).bold());
                result.addElement(LookupElementBuilder.create("clear").withTailText("()").withIcon(AllIcons.Nodes.Method).bold());

                // Math Swizzling on vectors
                String[] swizzles = {"x", "y", "z", "w", "xy", "xyz", "xyzw"};
                for (String s : swizzles) {
                    result.addElement(LookupElementBuilder.create(s).withTypeText("float").withIcon(AllIcons.Nodes.Field));
                }
            }
        });
    }
}