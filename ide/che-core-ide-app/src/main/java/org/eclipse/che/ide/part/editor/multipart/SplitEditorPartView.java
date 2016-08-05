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
package org.eclipse.che.ide.part.editor.multipart;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.ImplementedBy;

import org.eclipse.che.ide.api.constraints.Constraints;
import org.eclipse.che.ide.api.constraints.Direction;
import org.eclipse.che.ide.api.parts.PartStack;
import org.eclipse.che.ide.api.parts.PartStackView;

import javax.validation.constraints.NotNull;

/**
 * Provides methods to control view representation of multi part stack container.
 *
 * @author Roman nikitenko
 */
@ImplementedBy(SplitEditorPartViewImpl.class)
public interface SplitEditorPartView extends IsWidget {

    /**
     * Adds container to main panel which contains all containers.
     *
     * @param partStack
     *         partStack which will be added
     */

    void split(IsWidget replicaWidget, Direction direction);

    SplitEditorPartView getSpecimen();

    SplitEditorPartView getReplica();

    void removeChild(SplitEditorPartView child);

    void removeFromParent();
}
