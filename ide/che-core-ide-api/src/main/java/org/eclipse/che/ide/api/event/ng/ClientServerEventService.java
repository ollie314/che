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
package org.eclipse.che.ide.api.event.ng;

import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.api.core.jsonrpc.shared.JsonRpcRequest;
import org.eclipse.che.api.project.shared.dto.event.FileTrackingOperationDto;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.jsonrpc.JsonRpcRequestTransmitter;
import org.eclipse.che.ide.util.loging.Log;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Dmitry Kuleshov
 */
@Singleton
public class ClientServerEventService {
    private final JsonRpcRequestTransmitter transmitter;
    private final DtoFactory                dtoFactory;

    @Inject
    public ClientServerEventService(final JsonRpcRequestTransmitter transmitter,
                                    final EventBus eventBus,
                                    final DtoFactory dtoFactory) {
        this.transmitter = transmitter;
        this.dtoFactory = dtoFactory;

        Log.debug(getClass(), "Adding file event listener");
        eventBus.addHandler(FileTrackingEvent.TYPE, new FileTrackingEvent.FileTrackingEventHandler() {
            @Override
            public void onEvent(FileTrackingEvent event) {
                final FileTrackingOperationDto.Type type = event.getType();
                final String path = event.getPath();
                final String oldPath = event.getOldPath();

                transmit(path, oldPath, type);
            }
        });
    }

    private void transmit(String path, String oldPath, FileTrackingOperationDto.Type type) {
        final String params = dtoFactory.createDto(FileTrackingOperationDto.class)
                                        .withPath(path)
                                        .withType(type)
                                        .withOldPath(oldPath)
                                        .toString();

        final JsonRpcRequest request = dtoFactory.createDto(JsonRpcRequest.class)
                                                 .withJsonrpc("2.0")
                                                 .withMethod("track:editor-file")
                                                 .withParams(params);

        transmitter.transmit(request);
    }
}
