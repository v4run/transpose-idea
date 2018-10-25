import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TransposerClass extends AnAction {

    /**
     * rotateSelections removes the first element from the list and adds
     * it to the end of the list
     *
     * @param list all the strings
     */
    private static void rotateSelections(@NotNull List<String> list) {
        list.add(list.remove(0));
    }

    /**
     * getSelectedText returns all the selected texts at the different caret
     * positions. If no text is selected at any particular caret position,
     * an empty string is used.
     *
     * @param carets all the carets
     * @return List of all the selected texts at the different caret positions with
     * a boolean variable indication whether a selection is made
     */
    private static Transposee getSelectedText(@NotNull List<Caret> carets) {
        final ArrayList<String> selectedTexts = new ArrayList<>();
        final Transposee transposee = new Transposee();
        carets.forEach(caret -> {
            if (!caret.hasSelection()) {
                selectedTexts.add("");
                return;
            }
            transposee.hasSelection();
            selectedTexts.add(caret.getSelectedText());
        });
        transposee.setList(selectedTexts);
        return transposee;
    }

    /**
     * swapCharacters swaps the characters around the caret and updates its offset by 1
     *
     * @param document the document object
     * @param carets   all the carets
     */
    private static void swapCharacters(Document document, @NotNull List<Caret> carets) {
        carets.forEach(caret -> {
            final int offset = caret.getOffset();
            final int startOffset = offset - 1, endOffset = offset + 1;
            caret.setSelection(startOffset, endOffset);
            final String selection = caret.getSelectedText();
            if (selection != null) {
                StringBuilder sb = new StringBuilder(selection).reverse();
                document.replaceString(startOffset, endOffset, sb.toString());
                caret.removeSelection();
                caret.moveToOffset(offset + 1);
            }
        });
    }

    /**
     * updateTextAtCarets updates the selected text at the carets with the contents with
     * the new ones provided
     *
     * @param editor   the editor object
     * @param carets   all the carets
     * @param newTexts List of texts that should be updated in the editor
     */
    private static void updateTextAtCarets(Editor editor, @NotNull List<Caret> carets, List<String> newTexts) {
        EditorModificationUtil.deleteSelectedTextForAllCarets(editor);
        carets.forEach(caret -> {
            final String text = newTexts.remove(0);
            editor.getDocument().insertString(caret.getSelectionStart(), text);
            if ("".equals(text)) {
                return;
            }
            caret.setSelection(caret.getOffset(), caret.getOffset() + text.length());
        });
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        final Project project = event.getData(PlatformDataKeys.PROJECT);
        final Editor editor = event.getRequiredData(CommonDataKeys.EDITOR);
        final CaretModel caretModel = editor.getCaretModel();
        final List<Caret> carets = caretModel.getAllCarets();
        final Transposee transposee = getSelectedText(carets);

        WriteCommandAction.runWriteCommandAction(project, () -> {
            if (transposee.isHavingSelection()) {
                List<String> selectedTexts = transposee.getList();
                rotateSelections(selectedTexts);
                updateTextAtCarets(editor, carets, selectedTexts);
            } else {
                swapCharacters(editor.getDocument(), carets);
            }
        });
    }
}
