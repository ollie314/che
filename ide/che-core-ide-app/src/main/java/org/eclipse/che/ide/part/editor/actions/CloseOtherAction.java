/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.part.editor.actions;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.api.parts.EditorPartStack;
import org.eclipse.che.ide.part.editor.multipart.EditorMultiPartStackPresenter;

import javax.validation.constraints.NotNull;

/**
 * Performs closing all opened editors except selected one for current editor part stack.
 *
 * @author Vlad Zhukovskiy
 * @author Roman Nikitenko
 */
@Singleton
public class CloseOtherAction extends EditorAbstractAction {

    @Inject
    public CloseOtherAction(EditorAgent editorAgent,
                            EventBus eventBus,
                            CoreLocalizationConstant locale,
                            EditorMultiPartStackPresenter editorMultiPartStackPresenter) {
        super(locale.editorTabCloseAllExceptSelected(),
              locale.editorTabCloseAllExceptSelectedDescription(),
              null, editorAgent, eventBus,
              editorMultiPartStackPresenter);
    }

    /** {@inheritDoc} */
    @Override
    public void updateInPerspective(@NotNull ActionEvent event) {
        event.getPresentation().setEnabled(isFilesToCloseExist(event));
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent event) {
        EditorPartPresenter currentEditor = getEditorTab(event).getRelativeEditorPart();
        EditorPartStack currentPartStack = editorMultiPartStack.getPartStackByPart(currentEditor);

        for (EditorPartPresenter editorPart : editorAgent.getOpenedEditors()) {
            if (currentEditor != editorPart && currentPartStack.containsPart(editorPart)) {
                editorAgent.closeEditor(editorPart);
            }
        }
    }

    private boolean isFilesToCloseExist(ActionEvent event) {
        EditorPartPresenter currentEditor = getEditorTab(event).getRelativeEditorPart();
        EditorPartStack currentPartStack = editorMultiPartStack.getPartStackByPart(currentEditor);

        for (EditorPartPresenter openedEditor : editorAgent.getOpenedEditors()) {
            if (currentEditor != openedEditor && currentPartStack.containsPart(openedEditor)) {
                return true;
            }
        }
        return false;
    }
}
