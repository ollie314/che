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
import org.eclipse.che.ide.api.constraints.Constraints;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.part.editor.multipart.EditorMultiPartStackPresenter;

import static org.eclipse.che.ide.api.constraints.Direction.HORIZONTALLY;

/**
 * Adds copy of selected editor and divides area of the editor horizontally.
 *
 * @author Roman Nikitenko
 */
@Singleton
public class SplitHorizontallyAction extends EditorAbstractAction {

    @Inject
    public SplitHorizontallyAction(EditorAgent editorAgent,
                                   EventBus eventBus,
                                   CoreLocalizationConstant locale,
                                   EditorMultiPartStackPresenter editorMultiPartStackPresenter) {
        super(locale.editorTabSplitHorizontally(), locale.editorTabSplitHorizontallyDescription(), null, editorAgent, eventBus,
              editorMultiPartStackPresenter);
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        Constraints constraints = new Constraints(HORIZONTALLY, getEditorTab(e).getId());
        editorAgent.openEditor(getEditorFile(e), constraints);
    }
}
