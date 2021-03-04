// ============================================================================
//
// Copyright (C) 2006-2021 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.workbench.widgets;

import java.util.ArrayList;
import java.util.Observable;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;

import com.amalto.workbench.editors.AMainPageV2;
import com.amalto.workbench.editors.TransformerMainPage;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.ImageCache;

public class TableViewWrapper {

    private java.util.List<ObserverMetaData> ObserverList = new ArrayList<ObserverMetaData>();

    public TableViewWrapper() {

    }

    public void Wrap(AMainPageV2 page, Object view) {
        ObserverMetaData observer = new ObserverMetaData(page, view);
        ObserverList.add(observer);
    }

    class DeleteItemAction extends Action {

        private ObserverMetaData observer;

        public DeleteItemAction(ObserverMetaData obsv) {
            super();
            setImageDescriptor(ImageCache.getImage("icons/delete_obj.gif"));//$NON-NLS-1$
            setText(Messages.TableViewWrapper_DelSelectItems);
            setToolTipText(Messages.TableViewWrapper_DelSelectItems);

            observer = obsv;
        }

        public void run() {
            observer.notifyObserver(false);
        }

        public void runWithEvent(Event event) {
            super.runWithEvent(event);
        }

    }

    class RenameItemAction extends Action {

        private ObserverMetaData observer;

        public RenameItemAction(ObserverMetaData obsv) {
            super();
            setImageDescriptor(ImageCache.getImage("icons/rename.png"));//$NON-NLS-1$
            setText(Messages.TableViewWrapper_Rename);
            setToolTipText(Messages.TableViewWrapper_Rename);

            observer = obsv;
        }

        public void run() {
            observer.notifyObserver(true);
        }

        public void runWithEvent(Event event) {
            super.runWithEvent(event);
        }

    }

    class ObserverMetaData extends Observable {

        private AMainPageV2 mainPage;

        private Object observerView;

        public ObserverMetaData(AMainPageV2 page, Object view) {
            mainPage = page;
            observerView = view;

            MenuManager menuMgr = new MenuManager();
            menuMgr.setRemoveAllWhenShown(true);
            menuMgr.addMenuListener(new IMenuListener() {

                public void menuAboutToShow(IMenuManager manager) {
                    // ViewBrowserMainPage.this.fillContextMenu(manager);
                    if (observerView instanceof List) {
                        manager.add(new Separator());
                        manager.add(new RenameItemAction(ObserverMetaData.this));
                    }
                    manager.add(new DeleteItemAction(ObserverMetaData.this));

                }
            });

            KeyListener listener = new KeyListener() {

                public void keyPressed(KeyEvent e) {
                }

                public void keyReleased(KeyEvent e) {
                    // System.out.println("Table keyReleased() ");
                    if ((e.stateMask == 0) && (e.character == SWT.DEL) && (observerView != null)) {
                        notifyObserver(false);
                    }
                }
            };

            if (view instanceof TableViewer) {
                TableViewer tableView = (TableViewer) view;
                Menu menu = menuMgr.createContextMenu(tableView.getControl());
                tableView.getControl().setMenu(menu);
                page.getSite().registerContextMenu(menuMgr, tableView);

                tableView.getTable().addKeyListener(listener);
            } else if (view instanceof List) {
                List list = (List) view;
                Menu menu = menuMgr.createContextMenu(list);
                list.setMenu(menu);

                list.addKeyListener(listener);
            } else if (view instanceof ListViewer) {
                ListViewer listView = (ListViewer) view;
                Menu menu = menuMgr.createContextMenu(listView.getControl());
                listView.getControl().setMenu(menu);
                page.getSite().registerContextMenu(menuMgr, listView);

                listView.getControl().addKeyListener(listener);
            }
        }

        public void notifyObserver(boolean isRename) {
            if (isRename && mainPage instanceof TransformerMainPage)
                ((TransformerMainPage) mainPage).rename(observerView);
            else
                mainPage.update(ObserverMetaData.this, observerView);
        }

    }
}
