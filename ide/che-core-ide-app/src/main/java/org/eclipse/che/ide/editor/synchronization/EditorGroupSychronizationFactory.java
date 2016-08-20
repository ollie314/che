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
package org.eclipse.che.ide.editor.synchronization;

import org.eclipse.che.ide.api.editor.EditorPartPresenter;

import java.util.List;

/**
 * The factory creates instances of {@link EditorGroupSynchronization} to provide the synchronization of
 * the content for them.
 *
 * @author Roman Nikitenko
 */
public interface EditorGroupSychronizationFactory {
    /**
     * Creates implementation of {@link EditorGroupSynchronization}.
     *
     * @param editorsToSync
     *         list opened editors o sync the content for them
     * @return an instance of {@link EditorGroupSynchronization}
     */
    EditorGroupSynchronization create(List<EditorPartPresenter> editorsToSync);
}
