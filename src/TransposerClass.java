import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.List;

public class TransposerClass extends AnAction {

    /**
     * rotateSelections removes the first element from the list and adds
     * it to the end of the list
     *
     * @param list all the strings
     * @return a boolean value indicating whether the list is changed.
     */
    private static void rotateSelections(List<String> list) {
        list.add(list.remove(0));
    }

    /**
     * getSelectedText returns all the selected texts at the different caret
     * positions. If no text is selected at any particular caret position,
     * an empty string is used.
     *
     * @param carets all the carets
     * @return List of all the selected texts at the different caret positions
     */
    private static List<String> getSelectedText(List<Caret> carets) {
        final ArrayList<String> selectedTexts = new ArrayList<>();
        carets.forEach(caret -> {
            if (!caret.hasSelection()) {
                selectedTexts.add("");
                return;
            }
            selectedTexts.add(caret.getSelectedText());
        });
        return selectedTexts;
    }

    /**
     * swapCharacters swaps the characters around the caret and updates its offset by 1
     *
     * @param document the document object
     * @param carets   all the carets
     */
    private static void swapCharacters(Document document, List<Caret> carets) {
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
     * @param editor        the editor object
     * @param carets        all the carets
     * @param selectedTexts List of texts that should be updated in the editor
     */
    private static void updateTextAtCarets(Editor editor, List<Caret> carets, List<String> selectedTexts) {
        EditorModificationUtil.deleteSelectedTextForAllCarets(editor);
        carets.forEach(caret -> {
            final String text = selectedTexts.remove(0);
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
        final List<String> selectedTexts = getSelectedText(carets);
        rotateSelections(selectedTexts);

        WriteCommandAction.runWriteCommandAction(project, () -> {
            // TODO: separate logic for selections and single characters
            swapCharacters(editor.getDocument(), carets);
            // updateTextAtCarets(editor, carets, selectedTexts);
        });
    }
}
