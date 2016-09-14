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
package org.eclipse.che.api.workspace.shared.dto.compose;

import org.eclipse.che.api.core.model.workspace.compose.BuildContext;
import org.eclipse.che.dto.shared.DTO;

/**
 * @author Alexander Garagatyi
 */
@DTO
public interface BuildContextDto extends BuildContext {
    void setContext(String context);

    BuildContextDto withContext(String context);

    void setDockerfile(String dockerfile);

    BuildContextDto withDockerfile(String dockerfile);
}
