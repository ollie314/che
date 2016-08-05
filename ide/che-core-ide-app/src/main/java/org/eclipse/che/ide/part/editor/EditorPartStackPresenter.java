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
package org.eclipse.che.ide.part.editor;

import com.google.common.base.Predicate;
import com.google.gwt.core.client.Scheduler;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import org.eclipse.che.commons.annotation.Nullable;
import org.eclipse.che.ide.api.constraints.Constraints;
import org.eclipse.che.ide.api.editor.AbstractEditorPresenter;
import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.api.editor.EditorWithErrors;
import org.eclipse.che.ide.api.editor.EditorWithErrors.EditorState;
import org.eclipse.che.ide.api.event.FileEvent;
import org.eclipse.che.ide.api.event.FileEvent.FileEventHandler;
import org.eclipse.che.ide.api.parts.EditorPartStack;
import org.eclipse.che.ide.api.parts.EditorTab;
import org.eclipse.che.ide.api.parts.PartPresenter;
import org.eclipse.che.ide.api.parts.PartStackView.TabItem;
import org.eclipse.che.ide.api.parts.PropertyListener;
import org.eclipse.che.ide.api.resources.ResourceChangedEvent;
import org.eclipse.che.ide.api.resources.ResourceChangedEvent.ResourceChangedHandler;
import org.eclipse.che.ide.api.resources.ResourceDelta;
import org.eclipse.che.ide.api.resources.VirtualFile;
import org.eclipse.che.ide.part.PartStackPresenter;
import org.eclipse.che.ide.part.PartsComparator;
import org.eclipse.che.ide.part.editor.event.CloseNonPinnedEditorsEvent;
import org.eclipse.che.ide.part.editor.event.CloseNonPinnedEditorsEvent.CloseNonPinnedEditorsHandler;
import org.eclipse.che.ide.part.editor.event.PinEditorTabEvent;
import org.eclipse.che.ide.part.editor.event.PinEditorTabEvent.PinEditorTabEventHandler;
import org.eclipse.che.ide.part.widgets.TabItemFactory;
import org.eclipse.che.ide.part.widgets.listtab.ListButton;
import org.eclipse.che.ide.part.widgets.listtab.ListItem;
import org.eclipse.che.ide.part.widgets.listtab.ListItemWidget;
import org.eclipse.che.ide.resource.Path;
import org.eclipse.che.ide.util.loging.Log;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Iterables.filter;
import static org.eclipse.che.ide.api.editor.EditorWithErrors.EditorState.ERROR;
import static org.eclipse.che.ide.api.editor.EditorWithErrors.EditorState.WARNING;
import static org.eclipse.che.ide.api.resources.ResourceDelta.REMOVED;

/**
 * EditorPartStackPresenter is a special PartStackPresenter that is shared among all
 * Perspectives and used to display Editors.
 *
 * @author Nikolay Zamosenchuk
 * @author Stéphane Daviet
 * @author Dmitry Shnurenko
 * @author Vlad Zhukovskyi
 */
public class EditorPartStackPresenter extends PartStackPresenter implements EditorPartStack,
                                                                            EditorTab.ActionDelegate,
                                                                            ListButton.ActionDelegate,
                                                                            PinEditorTabEventHandler,
                                                                            CloseNonPinnedEditorsHandler,
                                                                            ResourceChangedHandler {

    private final EventBus               eventBus;
    private final ListButton             listButton;
    private final Map<ListItem, TabItem> items;

    //this list need to save order of added parts
    private final LinkedList<PartPresenter> partsOrder;
    private final LinkedList<PartPresenter> closedParts;

    private HandlerRegistration pinEditorTabHandler;
    private HandlerRegistration closeNonPinnedEditorsHandler;
    private HandlerRegistration resourceChangeHandler;

    @Inject
    public EditorPartStackPresenter(EditorPartStackView view,
                                    PartsComparator partsComparator,
                                    EventBus eventBus,
                                    TabItemFactory tabItemFactory,
                                    PartStackEventHandler partStackEventHandler,
                                    ListButton listButton) {
        //noinspection ConstantConditions
        super(eventBus, partStackEventHandler, tabItemFactory, partsComparator, view, null);
        this.eventBus = eventBus;

        this.listButton = listButton;
        this.listButton.setDelegate(this);

        this.view.setDelegate(this);
        view.setListButton(listButton);

        this.items = new HashMap<>();
        this.partsOrder = new LinkedList<>();
        this.closedParts = new LinkedList<>();

        pinEditorTabHandler = eventBus.addHandler(PinEditorTabEvent.getType(), this);
        closeNonPinnedEditorsHandler = eventBus.addHandler(CloseNonPinnedEditorsEvent.getType(), this);
        resourceChangeHandler = eventBus.addHandler(ResourceChangedEvent.getType(), this);
    }

    @Nullable
    private ListItem getListItemByTab(@NotNull TabItem tabItem) {
        for (Entry<ListItem, TabItem> entry : items.entrySet()) {
            if (tabItem.equals(entry.getValue())) {
                return entry.getKey();
            }
        }

        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus(boolean focused) {
        view.setFocus(focused);
    }

    /** {@inheritDoc} */
    @Override
    public void addPart(@NotNull PartPresenter part, Constraints constraint) {
        addPart(part);
    }

    /** {@inheritDoc} */
    @Override
    public void addPart(@NotNull PartPresenter part) {
        checkArgument(part instanceof AbstractEditorPresenter, "Can not add part " + part.getTitle() + "to editor part stack");
        EditorPartPresenter editorPart = (AbstractEditorPresenter)part;

        VirtualFile file = editorPart.getEditorInput().getFile();
        checkArgument(file != null, "File doesn't provided");

        updateListClosedParts(file);

        editorPart.addPropertyListener(propertyListener);

        final EditorTab editorTab = tabItemFactory.createEditorPartButton(file, editorPart);

        editorPart.addPropertyListener(new PropertyListener() {
            @Override
            public void propertyChanged(PartPresenter source, int propId) {
                if (propId == EditorPartPresenter.PROP_INPUT && source instanceof EditorPartPresenter) {
                    editorTab.setReadOnlyMark(((EditorPartPresenter)source).getEditorInput().getFile().isReadOnly());
                }
            }
        });

        editorTab.setDelegate(this);

        parts.put(editorTab, editorPart);
        partsOrder.add(editorPart);

        view.addTab(editorTab, editorPart);

        TabItem tabItem = getTabByPart(editorPart);

        if (tabItem != null) {
            ListItem item = new ListItemWidget(tabItem);
            listButton.addListItem(item);
            items.put(item, tabItem);
        }

        if (editorPart instanceof EditorWithErrors) {
            final EditorWithErrors presenter = ((EditorWithErrors)editorPart);

            editorPart.addPropertyListener(new PropertyListener() {
                @Override
                public void propertyChanged(PartPresenter source, int propId) {
                    EditorState editorState = presenter.getErrorState();

                    editorTab.setErrorMark(ERROR.equals(editorState));
                    editorTab.setWarningMark(WARNING.equals(editorState));
                }
            });
        }
    }

    private void updateListClosedParts(VirtualFile file) {
        if (closedParts.isEmpty()) {
            return;
        }

        for (PartPresenter closedEditorPart : closedParts) {
            Path path = ((EditorPartPresenter)closedEditorPart).getEditorInput().getFile().getLocation();
            if (path.equals(file.getLocation())) {
                closedParts.remove(closedEditorPart);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public PartPresenter getActivePart() {
        return activePart;
    }

    /** {@inheritDoc} */
    @Override
    public void setActivePart(@NotNull PartPresenter part) {
        activePart = part;
        view.selectTab(part);
    }

    /** {@inheritDoc} */
    @Override
    public void onTabClicked(@NotNull TabItem tab) {
        activePart = parts.get(tab);
        view.selectTab(parts.get(tab));
    }

    /** {@inheritDoc} */
    @Override
    public void removePart(PartPresenter part) {
        Log.error(getClass(), "=== remove part " + parts.size());
        super.removePart(part);
        partsOrder.remove(part);
        activePart = partsOrder.isEmpty() ? null : partsOrder.getLast();

        if (activePart != null) {
            onRequestFocus();
        }

        if (parts.isEmpty()) {
            pinEditorTabHandler.removeHandler();
            closeNonPinnedEditorsHandler.removeHandler();
            resourceChangeHandler.removeHandler();
        }
    }

    @Override
    public void openPreviousActivePart() {
        if (activePart != null) {
            view.selectTab(activePart);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onTabClose(@NotNull TabItem tab) {
        Log.error(getClass(), "=== onTabClose " + parts.size());
        ListItem listItem = getListItemByTab(tab);
        listButton.removeListItem(listItem);
        items.remove(listItem);

        PartPresenter part = ((EditorTab)tab).getRelativeEditorPart();
        closedParts.add(part);
    }

    /** {@inheritDoc} */
    @Override
    public void onEditorTabPinned(PinEditorTabEvent event) {
        String tabId = event.getTabId();
        for (TabItem tab : parts.keySet()) {
            if (tab instanceof EditorTab && ((EditorTab)tab).getId().equals(tabId)) {
                ((EditorTab)tab).setPinMark(event.isPin());
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseNonPinnedEditors(CloseNonPinnedEditorsEvent event) {
        EditorPartPresenter editorPart = getPartByTabId(event.getEditorTab().getId());
        if (editorPart == null) {
            return;
        }

        Iterable<TabItem> nonPinned = filter(parts.keySet(), new Predicate<TabItem>() {
            @Override
            public boolean apply(@Nullable TabItem input) {
                return input instanceof EditorTab && !((EditorTab)input).isPinned();
            }
        });

        for (final TabItem tabItem : nonPinned) {
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    eventBus.fireEvent(FileEvent.createCloseFileEvent((EditorTab)tabItem));
                }
            });
        }
    }

    @Override
    public EditorPartPresenter getPartByTabId(@NotNull String tabId) {
        for (TabItem tab : parts.keySet()) {
            if (tab instanceof EditorTab && ((EditorTab)tab).getId().equals(tabId)) {
                return (EditorPartPresenter)parts.get(tab);
            }
        }
        return null;
    }

    @Nullable
    public PartPresenter getPartByPath(Path path) {
        for (TabItem tab : parts.keySet()) {
            if (((EditorTab)tab).getFile().getLocation().equals(path)) {
                return parts.get(tab);
            }
        }
        return null;
    }

    @Override
    public PartPresenter getNextFor(EditorPartPresenter editorPart) {
        int indexForNext = partsOrder.indexOf(editorPart) + 1;
        return indexForNext >= partsOrder.size() ? partsOrder.getFirst() : partsOrder.get(indexForNext);
    }

    @Override
    public PartPresenter getPreviousFor(EditorPartPresenter editorPart) {
        int indexForNext = partsOrder.indexOf(editorPart) - 1;
        return indexForNext < 0 ? partsOrder.getLast() : partsOrder.get(indexForNext);
    }

    @Nullable
    @Override
    public PartPresenter getLastClosed() {
        if (closedParts.isEmpty()) {
            return null;
        }
        return closedParts.getLast();
    }

    @Override
    public void onResourceChanged(ResourceChangedEvent event) {
        Log.error(getClass(), "=== onResourceChanged hash " + EditorPartStackPresenter.this.hashCode());
        Log.error(getClass(), "=== onResourceChanged " + parts.size());
        final ResourceDelta delta = event.getDelta();
        if (delta.getKind() != REMOVED) {
            return;
        }

        for (PartPresenter editorPart : closedParts) {
            Path resourcePath = delta.getResource().getLocation();
            Path editorPath = ((EditorPartPresenter)editorPart).getEditorInput().getFile().getLocation();
            if (editorPath.equals(resourcePath)) {
                closedParts.remove(editorPart);
            }
        }
    }
}
