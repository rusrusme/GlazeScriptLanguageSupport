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
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import net.rusrusme.glaze.psi.*;
import org.jetbrains.annotations.NotNull;

public class GCompletionContributor extends CompletionContributor {
    record GlmFunction(@NotNull String name, @NotNull String tailText, @NotNull String typeText) {}

    GlmFunction[] glmFunctions = new GlmFunction[]{
            // Vektoren
            new GlmFunction("normalize", "(vec v)", "vec"),
            new GlmFunction("dot", "(vec a, vec b)", "float"),
            new GlmFunction("cross", "(vec3 a, vec3 b)", "vec3"),
            new GlmFunction("length", "(vec v)", "float"),
            new GlmFunction("distance", "(vec a, vec b)", "float"),
            new GlmFunction("reflect", "(vec v, vec n)", "vec"),
            // Matrizen
            new GlmFunction("inverse", "(mat m)", "mat"),
            new GlmFunction("transpose", "(mat m)", "mat"),
            new GlmFunction("determinant", "(mat m)", "float"),
            // Transformationen
            new GlmFunction("translate", "(mat4 m, vec3 v)", "mat4"),
            new GlmFunction("rotate", "(mat4 m, float angle, vec3 axis)", "mat4"),
            new GlmFunction("scale", "(mat4 m, vec3 v)", "mat4"),
            new GlmFunction("lookAt", "(vec3 eye, vec3 center, vec3 up)", "mat4"),
            // Quaternionen
            new GlmFunction("slerp", "(quat a, quat b, float t)", "quat"),
            new GlmFunction("conjugate", "(quat q)", "quat"),
            new GlmFunction("mat4_cast", "(quat q)", "mat4"),
            // Skalare / Common
            new GlmFunction("clamp", "(val, min, max)", "T"),
            new GlmFunction("mix", "(a, b, t)", "T"),
            new GlmFunction("radians", "(float degrees)", "float"),
            new GlmFunction("degrees", "(float radians)", "float"),
            new GlmFunction("sin", "(float rad)", "float"),
            new GlmFunction("cos", "(float rad)", "float"),
            new GlmFunction("sqrt", "(float val)", "float"),

            new GlmFunction("step", "(edge, x)", "T"),
            new GlmFunction("smoothstep", "(edge0, edge1, x)", "T"),
            new GlmFunction("min", "(a, b)", "T"),
            new GlmFunction("max", "(a, b)", "T"),
            new GlmFunction("abs", "(x)", "T"),
            new GlmFunction("pow", "(base, exp)", "float"),
            new GlmFunction("tan", "(float rad)", "float"),
            new GlmFunction("asin", "(float x)", "float"),
            new GlmFunction("acos", "(float x)", "float"),
            new GlmFunction("atan", "(float y_over_x)", "float"),
            new GlmFunction("perspective", "(float fov, float aspect, float near, float far)", "mat4"),
            new GlmFunction("angleAxis", "(float angle, vec3 axis)", "quat")
    };

    public GCompletionContributor() {

        // ========================================================================
        // LIVE TEMPLATE INSERT HANDLERS
        // ========================================================================

        InsertHandler<LookupElement> forCInsertHandler = (ctx, item) -> {
            Editor editor = ctx.getEditor();
            TemplateManager tm = TemplateManager.getInstance(ctx.getProject());
            Template template = tm.createTemplate("", "", " (var int32 $INDEX$ = 0; $INDEX$ < $LIMIT$; $INDEX$++) {\n    $END$\n}");
            template.addVariable("INDEX", new ConstantNode("i"), true);
            template.addVariable("LIMIT", new ConstantNode("count"), true);
            tm.startTemplate(editor, template);
        };

        InsertHandler<LookupElement> forRangeInsertHandler = (ctx, item) -> {
            Editor editor = ctx.getEditor();
            TemplateManager tm = TemplateManager.getInstance(ctx.getProject());
            Template template = tm.createTemplate("", "", " ($TYPE$ $ITEM$ : $COLLECTION$) {\n    $END$\n}");
            template.addVariable("TYPE", new ConstantNode("var"), true);
            template.addVariable("ITEM", new ConstantNode("item"), true);
            template.addVariable("COLLECTION", new ConstantNode("list"), true);
            tm.startTemplate(editor, template);
        };

        InsertHandler<LookupElement> forEcsInsertHandler = (ctx, item) -> {
            Editor editor = ctx.getEditor();
            TemplateManager tm = TemplateManager.getInstance(ctx.getProject());
            Template template = tm.createTemplate("", "", " (entity $E$, $COMP$ $VAR$) {\n    $END$\n}");
            template.addVariable("E", new ConstantNode("e"), true);
            template.addVariable("COMP", new ConstantNode("Transform"), true);
            template.addVariable("VAR", new ConstantNode("t"), true);
            tm.startTemplate(editor, template);
        };

        InsertHandler<LookupElement> systemInsertHandler = (ctx, item) -> {
            Editor editor = ctx.getEditor();
            TemplateManager tm = TemplateManager.getInstance(ctx.getProject());
            Template template = tm.createTemplate("", "", " $NAME$ {\n    on_update(float $DT$) {\n        $END$\n    }\n}");
            template.addVariable("NAME", new ConstantNode("MovementSystem"), true);
            template.addVariable("DT", new ConstantNode("delta_time"), true);
            tm.startTemplate(editor, template);
        };

        InsertHandler<LookupElement> onUpdateInsertHandler = (ctx, item) -> {
            Editor editor = ctx.getEditor();
            TemplateManager tm = TemplateManager.getInstance(ctx.getProject());
            Template template = tm.createTemplate("", "", "(float $DT$) {\n    $END$\n}");
            template.addVariable("DT", new ConstantNode("delta_time"), true);
            tm.startTemplate(editor, template);
        };

        InsertHandler<LookupElement> onEventInsertHandler = (ctx, item) -> {
            Editor editor = ctx.getEditor();
            TemplateManager tm = TemplateManager.getInstance(ctx.getProject());
            Template template = tm.createTemplate("", "", "(event $EVT$) {\n    $END$\n}");
            template.addVariable("EVT", new ConstantNode("e"), true);
            tm.startTemplate(editor, template);
        };

        InsertHandler<LookupElement> parensInsertHandler = (ctx, item) -> {
            Editor editor = ctx.getEditor();
            TemplateManager tm = TemplateManager.getInstance(ctx.getProject());
            Template template = tm.createTemplate("", "", " ($EXPR$)$END$");
            template.addVariable("EXPR", new ConstantNode(""), true);
            tm.startTemplate(editor, template);
        };

        InsertHandler<LookupElement> callParensInsertHandler = (ctx, item) -> {
            Editor editor = ctx.getEditor();
            TemplateManager tm = TemplateManager.getInstance(ctx.getProject());
            Template template = tm.createTemplate("", "", "($PARAMS$)$END$");
            template.addVariable("PARAMS", new ConstantNode(""), true);
            tm.startTemplate(editor, template);
        };

        InsertHandler<LookupElement> listInsertHandler = (ctx, item) -> {
            Editor editor = ctx.getEditor();
            TemplateManager tm = TemplateManager.getInstance(ctx.getProject());
            Template template = tm.createTemplate("", "", "<$TYPE$>$END$");
            template.addVariable("TYPE", new ConstantNode(""), true);
            tm.startTemplate(editor, template);
        };

        // ========================================================================
        // COMPLETION PROVIDER
        // ========================================================================

        extend(CompletionType.BASIC, PlatformPatterns.psiElement(), new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters,
                                          @NotNull ProcessingContext context,
                                          @NotNull CompletionResultSet result) {
                int offset = parameters.getOffset();
                Document document = parameters.getEditor().getDocument();
                CharSequence text = document.getCharsSequence();
                PsiElement position = parameters.getPosition();

                // 1. Typsichere Member & Swizzles nach Punkt (.)
                if (offset > 0 && text.charAt(offset - 1) == '.') {
                    addDotCompletions(result, position, text, offset - 1);
                    return;
                }

                // 2. Loop Live Templates
                result.addElement(PrioritizedLookupElement.withPriority(
                        LookupElementBuilder.create("for")
                                .bold()
                                .withPresentableText("for (indexed)")
                                .withTailText(" (var int32 i = 0; i < count; i++)", true)
                                .withTypeText("Loop", true)
                                .withIcon(AllIcons.Nodes.Template)
                                .withInsertHandler(forCInsertHandler), 125.0));

                result.addElement(PrioritizedLookupElement.withPriority(
                        LookupElementBuilder.create("for")
                                .bold()
                                .withPresentableText("for (range)")
                                .withTailText(" (type item : list)", true)
                                .withTypeText("Loop", true)
                                .withIcon(AllIcons.Nodes.Template)
                                .withInsertHandler(forRangeInsertHandler), 124.0));

                result.addElement(PrioritizedLookupElement.withPriority(
                        LookupElementBuilder.create("for")
                                .bold()
                                .withPresentableText("for (ecs query)")
                                .withTailText(" (entity e, Component c)", true)
                                .withTypeText("Loop", true)
                                .withIcon(AllIcons.Nodes.Template)
                                .withInsertHandler(forEcsInsertHandler), 123.0));

                // 3. System & Callback Live Templates
                result.addElement(PrioritizedLookupElement.withPriority(
                        LookupElementBuilder.create("system")
                                .bold()
                                .withTailText(" Name { on_update... }", true)
                                .withTypeText("Declaration", true)
                                .withIcon(AllIcons.Nodes.Class)
                                .withInsertHandler(systemInsertHandler), 120.0));

                result.addElement(PrioritizedLookupElement.withPriority(
                        LookupElementBuilder.create("on_update")
                                .bold()
                                .withTailText("(float dt) { ... }", true)
                                .withTypeText("Callback", true)
                                .withIcon(AllIcons.Nodes.Method)
                                .withInsertHandler(onUpdateInsertHandler), 120.0));

                result.addElement(PrioritizedLookupElement.withPriority(
                        LookupElementBuilder.create("on_event")
                                .bold()
                                .withTailText("(event e) { ... }", true)
                                .withTypeText("Callback", true)
                                .withIcon(AllIcons.Nodes.Method)
                                .withInsertHandler(onEventInsertHandler), 120.0));

                // 4. Standard-Schlüsselwörter
                for (String kw : new String[]{"if", "while", "switch"}) {
                    result.addElement(PrioritizedLookupElement.withPriority(
                            LookupElementBuilder.create(kw).bold().withTailText(" ()", true).withInsertHandler(parensInsertHandler), 100.0));
                }
                for (String kw : new String[]{"else", "do", "return", "break", "continue", "let", "var", "fn", "component", "struct", "enum", "exclude"}) {
                    result.addElement(PrioritizedLookupElement.withPriority(LookupElementBuilder.create(kw).bold(), 95.0));
                }

                // 5. Primitive & Builtin Typen
                for (String t : new String[]{
                        "void", "uint8", "int8", "uint16", "int16", "uint32", "int32", "uint64", "int64",
                        "float", "double", "char", "string", "bool", "entity", "event"
                }) {
                    result.addElement(PrioritizedLookupElement.withPriority(
                            LookupElementBuilder.create(t).bold().withIcon(AllIcons.Nodes.Type), 90.0));
                }

                // 6. Math-Typen & Konstruktoren (vec2..4, mat2..4, quat)
                for (String mathType : new String[]{"vec2", "vec3", "vec4", "mat2", "mat3", "mat4", "quat"}) {
                    // Als Typ-Bezeichner
                    result.addElement(PrioritizedLookupElement.withPriority(
                            LookupElementBuilder.create(mathType).bold().withIcon(AllIcons.Nodes.Type), 90.0));

                    // Als Konstruktor mit Argument-Klammern
                    result.addElement(PrioritizedLookupElement.withPriority(
                            LookupElementBuilder.create(mathType)
                                    .bold()
                                    .withPresentableText(mathType + "(...)")
                                    .withTailText(" (constructor)", true)
                                    .withTypeText(mathType, true)
                                    .withIcon(AllIcons.Nodes.Method)
                                    .withInsertHandler(callParensInsertHandler), 92.0));
                }

                result.addElement(PrioritizedLookupElement.withPriority(
                        LookupElementBuilder.create("list")
                                .bold()
                                .withTailText("<T>", true)
                                .withIcon(AllIcons.Nodes.Type)
                                .withInsertHandler(listInsertHandler), 95.0));

                // 7. User-Defined Components, Structs & Enums
                for (GComponentDeclaration comp : GPsiUtil.findComponents(position)) {
                    if (comp.getNameIdentifier() != null) {
                        result.addElement(PrioritizedLookupElement.withPriority(
                                LookupElementBuilder.create(comp.getNameIdentifier().getText())
                                        .withTypeText("component", true)
                                        .withIcon(AllIcons.Nodes.Class)
                                        .bold(), 110.0));
                    }
                }
                for (GStructDeclaration str : GPsiUtil.findStructs(position)) {
                    if (str.getNameIdentifier() != null) {
                        result.addElement(PrioritizedLookupElement.withPriority(
                                LookupElementBuilder.create(str.getNameIdentifier().getText())
                                        .withTypeText("struct", true)
                                        .withIcon(AllIcons.Nodes.Class)
                                        .bold(), 110.0));
                    }
                }
                for (GEnumDeclaration en : GPsiUtil.findEnums(position)) {
                    if (en.getNameIdentifier() != null) {
                        result.addElement(PrioritizedLookupElement.withPriority(
                                LookupElementBuilder.create(en.getNameIdentifier().getText())
                                        .withTypeText("enum", true)
                                        .withIcon(AllIcons.Nodes.Enum)
                                        .bold(), 110.0));
                    }
                }

                // 8. Sichtbare Variablen & Parameter
                for (GPsiUtil.VisibleSymbol sym : GPsiUtil.findVisibleSymbols(position)) {
                    result.addElement(PrioritizedLookupElement.withPriority(
                            LookupElementBuilder.create(sym.name())
                                    .withTypeText(sym.type(), true)
                                    .withIcon(AllIcons.Nodes.Variable)
                                    .bold(), 150.0));
                }

                for (GlmFunction fn : glmFunctions) {
                    result.addElement(PrioritizedLookupElement.withPriority(
                            LookupElementBuilder.create(fn.name())
                                    .bold()
                                    .withTailText(fn.tailText(), true)
                                    .withTypeText(fn.typeText(), true)
                                    .withIcon(AllIcons.Nodes.Function)
                                    .withInsertHandler(callParensInsertHandler),
                            98.0));
                }
            }

            private void addDotCompletions(CompletionResultSet result, PsiElement position, CharSequence text, int dotOffset) {
                String varName = getIdentifierBeforeDot(text, dotOffset);
                String varType = resolveVariableType(position, varName);

                // 1. Entity
                if ("entity".equals(varType)) {
                    result.addElement(PrioritizedLookupElement.withPriority(
                            LookupElementBuilder.create("id").withTypeText("uint64", true).withIcon(AllIcons.Nodes.Field).bold(), 200.0));
                    result.addElement(PrioritizedLookupElement.withPriority(
                            LookupElementBuilder.create("get").withTailText("(Component)", true).withIcon(AllIcons.Nodes.Method).bold(), 200.0));
                    result.addElement(PrioritizedLookupElement.withPriority(
                            LookupElementBuilder.create("add").withTailText("(Component)", true).withIcon(AllIcons.Nodes.Method).bold(), 200.0));
                    result.addElement(PrioritizedLookupElement.withPriority(
                            LookupElementBuilder.create("has").withTailText("(Component)", true).withIcon(AllIcons.Nodes.Method).bold(), 200.0));
                    result.addElement(PrioritizedLookupElement.withPriority(
                            LookupElementBuilder.create("remove").withTailText("(Component)", true).withIcon(AllIcons.Nodes.Method).bold(), 200.0));
                    return;
                }

                // 2. List<T>
                if (varType.startsWith("list")) {
                    result.addElement(PrioritizedLookupElement.withPriority(
                            LookupElementBuilder.create("size").withTypeText("int32", true).withIcon(AllIcons.Nodes.Property).bold(), 200.0));
                    result.addElement(PrioritizedLookupElement.withPriority(
                            LookupElementBuilder.create("add").withTailText("(item)", true).withIcon(AllIcons.Nodes.Method).bold(), 200.0));
                    result.addElement(PrioritizedLookupElement.withPriority(
                            LookupElementBuilder.create("remove_at").withTailText("(index)", true).withIcon(AllIcons.Nodes.Method).bold(), 200.0));
                    result.addElement(PrioritizedLookupElement.withPriority(
                            LookupElementBuilder.create("clear").withTailText("()", true).withIcon(AllIcons.Nodes.Method).bold(), 200.0));
                    return;
                }

                // 3. Vektoren & Quat: Ausschließlich Swizzles (x, y, z, w etc.)
                if (isVectorType(varType)) {
                    addVectorSwizzles(result, varType);
                    return;
                }

                // 4. Custom Structs & Components: Eigene Felder
                if (!varType.isEmpty()) {
                    boolean found = addCustomMembers(result, position, varType);
                    if (found) return;
                }
            }

            private boolean addCustomMembers(CompletionResultSet result, PsiElement position, String typeName) {
                boolean matched = false;
                for (GStructDeclaration str : GPsiUtil.findStructs(position)) {
                    if (str.getNameIdentifier() != null && typeName.equals(str.getNameIdentifier().getText())) {
                        for (GFieldDeclaration field : str.getFieldDeclarationList()) {
                            if (field.getNameIdentifier() != null) {
                                result.addElement(LookupElementBuilder.create(field.getNameIdentifier().getText())
                                        .withTypeText(field.getType().getText(), true)
                                        .withIcon(AllIcons.Nodes.Field)
                                        .bold());
                                matched = true;
                            }
                        }
                        return matched;
                    }
                }
                for (GComponentDeclaration comp : GPsiUtil.findComponents(position)) {
                    if (comp.getNameIdentifier() != null && typeName.equals(comp.getNameIdentifier().getText())) {
                        for (GFieldDeclaration field : comp.getFieldDeclarationList()) {
                            if (field.getNameIdentifier() != null) {
                                result.addElement(LookupElementBuilder.create(field.getNameIdentifier().getText())
                                        .withTypeText(field.getType().getText(), true)
                                        .withIcon(AllIcons.Nodes.Field)
                                        .bold());
                                matched = true;
                            }
                        }
                        return matched;
                    }
                }
                return matched;
            }

            private String getIdentifierBeforeDot(CharSequence text, int dotOffset) {
                int cur = dotOffset;
                while (cur > 0 && Character.isWhitespace(text.charAt(cur - 1))) {
                    cur--;
                }
                int end = cur;
                while (cur > 0 && Character.isJavaIdentifierPart(text.charAt(cur - 1))) {
                    cur--;
                }
                return text.subSequence(cur, end).toString().trim();
            }

            private String resolveVariableType(PsiElement position, String varName) {
                if (varName.isEmpty()) return "";
                for (GPsiUtil.VisibleSymbol sym : GPsiUtil.findVisibleSymbols(position)) {
                    if (varName.equals(sym.name())) {
                        return sym.type();
                    }
                }
                return "";
            }

            private boolean isVectorType(String typeName) {
                return "vec2".equals(typeName) || "vec3".equals(typeName) || "vec4".equals(typeName) || "quat".equals(typeName);
            }

            private void addVectorSwizzles(CompletionResultSet result, String varType) {
                int dim = switch (varType) {
                    case "vec2" -> 2;
                    case "vec3" -> 3;
                    default -> 4;
                };
                char[] coords = {'x', 'y', 'z', 'w'};
                for (int i = 0; i < dim; i++) {
                    result.addElement(LookupElementBuilder.create(String.valueOf(coords[i])).withTypeText("float", true).withIcon(AllIcons.Nodes.Field));
                }
                if (dim >= 2) {
                    for (String s : new String[]{"xy", "yx", "xx", "yy"}) {
                        result.addElement(LookupElementBuilder.create(s).withTypeText("vec2", true).withIcon(AllIcons.Nodes.Field));
                    }
                }
                if (dim >= 3) {
                    for (String s : new String[]{"xyz", "xzy", "yxz", "zyx"}) {
                        result.addElement(LookupElementBuilder.create(s).withTypeText("vec3", true).withIcon(AllIcons.Nodes.Field));
                    }
                }
                if (dim == 4) {
                    result.addElement(LookupElementBuilder.create("xyzw").withTypeText("vec4", true).withIcon(AllIcons.Nodes.Field));
                }
            }
        });
    }
}