import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.List;

public class TransposerClass extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        final Editor editor = event.getRequiredData(CommonDataKeys.EDITOR);
        CaretModel caretModel = editor.getCaretModel();
        List<Caret> carets = caretModel.getAllCarets();
        ArrayList<String> selectedTexts = new ArrayList<>();
        boolean selection = false;
        for (Caret cs : carets) {
            final String selectedText = cs.getSelectedText();
            if (selectedText == null) {
                selection = true;
            }
            selectedTexts.add(selectedText);
        }
        selectedTexts.add(selectedTexts.remove(0));
        if (selection) {
            return;
        }
        Runnable r = () -> {
            EditorModificationUtil.deleteSelectedTextForAllCarets(editor);
            int i = 0;
            for (Caret caret: carets) {
                editor.getDocument().insertString(caret.getOffset(), selectedTexts.get(i));
                i++;
            }
        };
        WriteCommandAction.runWriteCommandAction(project, r);
    }
}
