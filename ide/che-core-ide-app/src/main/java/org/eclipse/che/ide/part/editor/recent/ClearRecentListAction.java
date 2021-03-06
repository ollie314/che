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
package org.eclipse.che.ide.part.editor.recent;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.api.action.AbstractPerspectiveAction;
import org.eclipse.che.ide.api.action.ActionEvent;

import javax.validation.constraints.NotNull;

import static java.util.Collections.singletonList;
import static org.eclipse.che.ide.workspace.perspectives.project.ProjectPerspective.PROJECT_PERSPECTIVE_ID;

/**
 * Action clears list of recently opened files.
 *
 * @author Oleksii Orel
 */
@Singleton
public class ClearRecentListAction extends AbstractPerspectiveAction {

    private RecentFileList recentFileList;

    @Inject
    public ClearRecentListAction(RecentFileList recentFileList,
                                 CoreLocalizationConstant locale) {
        super(singletonList(PROJECT_PERSPECTIVE_ID), locale.openRecentFileClearTitle(), locale.openRecentFileClearDescription(), null,
              null);

        this.recentFileList = recentFileList;
    }

    /** {@inheritDoc} */
    @Override
    public void updateInPerspective(@NotNull ActionEvent event) {
        event.getPresentation().setEnabledAndVisible(!recentFileList.isEmpty());
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        recentFileList.clear();
    }
}
