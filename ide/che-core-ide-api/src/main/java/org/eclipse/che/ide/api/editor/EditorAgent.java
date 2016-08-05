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
package org.eclipse.che.ide.api.editor;

import com.google.gwt.user.client.rpc.AsyncCallback;

import org.eclipse.che.commons.annotation.Nullable;
import org.eclipse.che.ide.api.constraints.Constraints;
import org.eclipse.che.ide.api.parts.EditorPartStack;
import org.eclipse.che.ide.api.resources.VirtualFile;
import org.eclipse.che.ide.resource.Path;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Editor Agent manages Editors, it allows to open a new editor with given file,
 * retrieve current active editor and find all the opened editors.
 *
 * @author Nikolay Zamosenchuk
 */
public interface EditorAgent {
    /**
     * Open editor with given file
     *
     * @param file the file to open
     */
    void openEditor(@NotNull final VirtualFile file);

    /**
     * Open editor with given file
     *
     * @param file
     *         the file to open
     */
    void openEditor(@NotNull final VirtualFile file, Constraints constraints);

    /**
     * Close editor part
     *
     * @param editorPart
     *         the part to close
     */
    void closeEditor(EditorPartPresenter editorPart);

    /**
     * Open editor with given file, call callback when editor fully loaded and initialized.
     * @param file the file to open
     * @param callback
     */
    void openEditor(@NotNull VirtualFile file, @NotNull OpenEditorCallback callback);

    /**
     * Sets editor as active(switch tabs and pace cursor)
     * @param editor the editor that must be active
     */
    void activateEditor(@NotNull EditorPartPresenter editor);

    /**
     * Returns array of EditorPartPresenters whose content have changed since the last save operation.
     *
     * @return Array<EditorPartPresenter>
     */
    List<EditorPartPresenter> getDirtyEditors();

    /**
     * Get all opened editors
     *
     * @return list with all opened editors
     */
    @NotNull
    List<EditorPartPresenter> getOpenedEditors();

    /**
     * Get opened editor by related file path
     *
     * @param path path of the file opened in editor
     * @return opened editor or null if it does not exist
     */
    @Nullable
    EditorPartPresenter getOpenedEditor(Path path);

    /**
     * Saves all opened files whose content have changed since the last save operation
     *
     * @param callback
     */
    void saveAll(AsyncCallback callback);

    /**
     * Current active editor
     *
     * @return the current active editor
     */
    @Nullable
    EditorPartPresenter getActiveEditor();

    interface OpenEditorCallback {
        void onEditorOpened(EditorPartPresenter editor);

        void onEditorActivated(EditorPartPresenter editor);

        void onInitializationFailed();
    }
}
