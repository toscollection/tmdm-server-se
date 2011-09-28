package org.talend.mdm.webapp.browserecords.client.widget;

import java.util.Collections;
import java.util.List;

import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsEvents;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.model.ItemBaseModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.resources.icon.Icons;
import org.talend.mdm.webapp.browserecords.client.util.Locale;
import org.talend.mdm.webapp.browserecords.client.util.UserSession;
import org.talend.mdm.webapp.browserecords.client.widget.integrity.ContainerUpdate;
import org.talend.mdm.webapp.browserecords.client.widget.integrity.DeleteAction;
import org.talend.mdm.webapp.browserecords.client.widget.integrity.DeleteCallback;
import org.talend.mdm.webapp.browserecords.client.widget.integrity.LogicalDeleteAction;
import org.talend.mdm.webapp.browserecords.client.widget.integrity.NoOpPostDeleteAction;
import org.talend.mdm.webapp.browserecords.client.widget.integrity.PostDeleteAction;
import org.talend.mdm.webapp.browserecords.client.widget.treedetail.ForeignKeyTreeDetail;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Widget;

public class ItemDetailToolBar extends ToolBar {

    public final static String CREATE_OPERATION = "CREATE"; //$NON-NLS-1$

    public final static String VIEW_OPERATION = "VIEW"; //$NON-NLS-1$

    public final static String SMARTVIEW_OPERATION = "SMARTVIEW"; //$NON-NLS-1$

    public final static String PERSONALEVIEW_OPERATION = "PERSONALVIEW"; //$NON-NLS-1$

    public final static String DUPLICATE_OPERATION = "DUPLICATE_OPERATION"; //$NON-NLS-1$

    private Button saveButton;

    private Button saveAndCloseButton;

    private Button deleteButton;

    private Button relationButton;

    private Button personalviewButton;

    private Button generatedviewButton;

    private Button duplicateButton;

    private Button journalButton;

    private Button refreshButton;

    private Button launchProcessButton;

    private ComboBox<ItemBaseModel> smartViewCombo;

    private ComboBox<ItemBaseModel> workFlowCombo;

    private ItemBean itemBean;

    private String operation;

    private boolean isFkToolBar;

    private BrowseRecordsServiceAsync service = (BrowseRecordsServiceAsync) Registry.get(BrowseRecords.BROWSERECORDS_SERVICE);

    private ItemBaseModel selectItem;

    public ItemDetailToolBar() {
        this.setBorders(false);
    }

    public ItemDetailToolBar(ItemBean itemBean, String operation) {
        this();
        this.itemBean = itemBean;
        this.operation = operation;
        initToolBar();
    }

    public ItemDetailToolBar(ItemBean itemBean, String operation, boolean isFkToolBar) {
        this();
        this.itemBean = itemBean;
        this.operation = operation;
        this.isFkToolBar = isFkToolBar;
        initToolBar();

    }

    private void initToolBar() {
        if (operation.equalsIgnoreCase(ItemDetailToolBar.VIEW_OPERATION)
                || (operation.equalsIgnoreCase(ItemDetailToolBar.PERSONALEVIEW_OPERATION))) {
            initViewToolBar();
        } else if (operation.equalsIgnoreCase(ItemDetailToolBar.CREATE_OPERATION)) {
            initCreateToolBar();
        } else if (operation.equalsIgnoreCase(ItemDetailToolBar.SMARTVIEW_OPERATION)) {
            initSmartViewToolBar();
        }
    }

    private void initViewToolBar() {
        if (!operation.equalsIgnoreCase(ItemDetailToolBar.VIEW_OPERATION)) {
            addPersonalViewButton();
            this.addSeparator();
        }
        this.addSaveButton();
        this.addSeparator();
        if (isFkToolBar) {
            this.addSaveQuitButton();
            this.addSeparator();
        }
        this.addDeleteMenu();
        this.addSeparator();
        this.addDuplicateButton();
        this.addSeparator();
        this.addJournalButton();
        this.addSeparator();
        this.addFreshButton();
        this.addRelationButton();
    }

    private void initCreateToolBar() {
        this.addSaveButton();
        this.addSeparator();
        this.addSaveQuitButton();
        this.addRelationButton();
    }

    private void addSaveButton() {
        if (saveButton == null) {
            saveButton = new Button(MessagesFactory.getMessages().save_btn());
            saveButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Save()));
            saveButton.setToolTip(MessagesFactory.getMessages().save_tip());

            saveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

                @Override
                public void componentSelected(ButtonEvent ce) {
                    // TODO the following code need to be refactor, it is the demo code
                    saveItemAndClose(false);
                }
            });
        }
        add(saveButton);
    }

    private void addSaveQuitButton() {
        if (saveAndCloseButton == null) {
            saveAndCloseButton = new Button(MessagesFactory.getMessages().save_close_btn());
            saveAndCloseButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.save_and_close()));
            saveAndCloseButton.setToolTip(MessagesFactory.getMessages().save_close_tip());

            saveAndCloseButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

                @Override
                public void componentSelected(ButtonEvent ce) {
                    saveItemAndClose(true);
                    ItemsDetailPanel.getInstance().closeCurrentTab();
                }
            });
        }
        add(saveAndCloseButton);
    }

    private void addDeleteMenu() {
        if (deleteButton == null) {
            deleteButton = new Button(MessagesFactory.getMessages().delete_btn());
            deleteButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Delete()));

            Menu deleteMenu = new Menu();
            MenuItem delete_SendToTrash = new MenuItem(MessagesFactory.getMessages().trash_btn());
            delete_SendToTrash.addSelectionListener(new SelectionListener<MenuEvent>() {

                @Override
                public void componentSelected(MenuEvent ce) {
                    final MessageBox box = MessageBox.prompt(MessagesFactory.getMessages().path(), MessagesFactory.getMessages()
                            .path_desc());
                    box.getTextBox().setValue("/"); //$NON-NLS-1$
                    box.addCallback(new Listener<MessageBoxEvent>() {
                        public void handleEvent(MessageBoxEvent be) {
                            PostDeleteAction postDeleteAction = new ContainerUpdate(NoOpPostDeleteAction.INSTANCE);
                            DeleteAction deleteAction = new LogicalDeleteAction(be.getValue());
                            service.checkFKIntegrity(Collections.singletonList(itemBean),
                                        new DeleteCallback(deleteAction, postDeleteAction, service));
                        }
                    });
                }
            });
            delete_SendToTrash.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Send_to_trash()));
            deleteMenu.add(delete_SendToTrash);

            MenuItem delete_Delete = new MenuItem(MessagesFactory.getMessages().delete_btn());
            deleteMenu.add(delete_Delete);
            delete_Delete.addSelectionListener(new SelectionListener<MenuEvent>() {

                @Override
                public void componentSelected(MenuEvent ce) {
                    MessageBox.confirm(MessagesFactory.getMessages().confirm_title(), MessagesFactory.getMessages()
                            .delete_confirm(), new Listener<MessageBoxEvent>() {

                        public void handleEvent(MessageBoxEvent be) {
                            if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                                PostDeleteAction postDeleteAction = new ContainerUpdate(NoOpPostDeleteAction.INSTANCE);
                                service.checkFKIntegrity(Collections.singletonList(itemBean),
                                        new DeleteCallback(DeleteAction.PHYSICAL, postDeleteAction, service));
                            }
                        }
                    });

                }
            });

            delete_Delete.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Delete()));

            deleteButton.setMenu(deleteMenu);
        }

        add(deleteButton);
    }

    private void doLogicalDelete(String url, boolean override) {

    }

    private void doItemDelete(boolean override) {

    }

    private void addDuplicateButton() {
        if (duplicateButton == null) {
            duplicateButton = new Button(MessagesFactory.getMessages().duplicate_btn());
            duplicateButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.duplicate()));
            duplicateButton.setToolTip(MessagesFactory.getMessages().duplicate_tip());
            duplicateButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

                @Override
                public void componentSelected(ButtonEvent ce) {
                    String title = itemBean.getConcept() + " " + itemBean.getIds(); //$NON-NLS-1$
                    if (isFkToolBar) {
                        ForeignKeyTreeDetail fkTree = (ForeignKeyTreeDetail) ItemsDetailPanel.getInstance().getTabPanel()
                                .getSelectedItem()
                                .getWidget(0);
                        ForeignKeyTreeDetail duplicateFkTree = new ForeignKeyTreeDetail(fkTree.getFkModel(), true);
                        ItemsDetailPanel.getInstance().addTabItem(title, duplicateFkTree, ItemsDetailPanel.MULTIPLE, title);
                    } else {
                        ItemPanel itemPanel = new ItemPanel(itemBean, ItemDetailToolBar.CREATE_OPERATION);
                        ItemsDetailPanel.getInstance().addTabItem(title, itemPanel, ItemsDetailPanel.MULTIPLE, title);
                    }

                }

            });
        }
        add(duplicateButton);
    }

    private void addJournalButton() {
        if (journalButton == null) {
            journalButton = new Button(MessagesFactory.getMessages().journal_btn());
            journalButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.journal()));
            journalButton.setToolTip(MessagesFactory.getMessages().journal_tip());

            journalButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

                @Override
                public void componentSelected(ButtonEvent ce) {
                    String ids = itemBean.getIds();
                    if (ids.indexOf("@") != -1) { //$NON-NLS-1$
                        ids = ids.replaceAll("@", "."); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    initJournal(ids, itemBean.getConcept());
                }

            });
        }
        add(journalButton);
    }

    private void addFreshButton() {
        if (refreshButton == null) {
            refreshButton = new Button();
            refreshButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.refreshToolbar()));
            refreshButton.setToolTip(MessagesFactory.getMessages().refresh_tip());
            refreshButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

                @Override
                public void componentSelected(ButtonEvent ce) {
                    refreshTreeDetail();
                }

            });
        }
        add(refreshButton);
    }

    private void refreshTreeDetail() {
        if (isFkToolBar) {
            final ForeignKeyTreeDetail fkTree = (ForeignKeyTreeDetail) ItemsDetailPanel.getInstance().getTabPanel()
                    .getSelectedItem().getWidget(0);
            ItemNodeModel root = fkTree.getRootModel();
            refreshTree(null, fkTree, root);
        } else {
            final ItemPanel itemPanel = (ItemPanel) ItemsDetailPanel.getInstance().getTabPanel().getSelectedItem().getWidget(0);
            ItemNodeModel root = (ItemNodeModel) itemPanel.getTree().getTree().getItem(0).getUserObject();
            refreshTree(itemPanel, null, root);
        }

    }

    private void refreshTree(final ItemPanel itemPanel, final ForeignKeyTreeDetail fkTree, final ItemNodeModel root) {
        ItemBean itemBean = isFkToolBar ? fkTree.getFkModel().getItemBean() : itemPanel.getItem();
        service.isItemModifiedByOthers(itemBean, new AsyncCallback<Boolean>() {

            public void onSuccess(Boolean result) {
                if (isChangeValue(root) || result) {
                    MessageBox
                            .confirm(MessagesFactory.getMessages().confirm_title(),
                                    MessagesFactory.getMessages().msg_confirm_refresh_tree_detail(),
                                    new Listener<MessageBoxEvent>() {

                                        public void handleEvent(MessageBoxEvent be) {
                                            if (Dialog.YES.equals(be.getButtonClicked().getItemId())) {
                                                if (isFkToolBar)
                                                    fkTree.refreshTree();
                                                else
                                                    itemPanel.refreshTree();
                                            }
                                        }
                                    }).getDialog().setWidth(600);
                } else {
                    if (isFkToolBar)
                        fkTree.refreshTree();
                    else
                        itemPanel.refreshTree();
                }
            }

            public void onFailure(Throwable caught) {
                Dispatcher.forwardEvent(BrowseRecordsEvents.Error, caught);
                MessageBox.alert(MessagesFactory.getMessages().refresh_tip(), MessagesFactory.getMessages().refresh_tip()
                        + " " + MessagesFactory.getMessages().message_fail(), null); //$NON-NLS-1$
            }

        });
    }

    private boolean isChangeValue(ItemNodeModel model) {
        if (model.isChangeValue())
            return true;
        for (ModelData node : model.getChildren()) {
            if (isChangeValue((ItemNodeModel) node))
                return true;
        }
        return false;
    }

    private void addRelationButton() {
        service.getLineageEntity(itemBean.getConcept(), new AsyncCallback<List<String>>() {

            public void onSuccess(List<String> list) {
                if (list == null || list.size() == 0) {
                    ItemDetailToolBar.this.addWorkFlosCombo();
                    return;
                }

                final List<String> lineageList = list;
                relationButton = new Button(MessagesFactory.getMessages().relations_btn());
                relationButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.relations()));
                relationButton.setToolTip(MessagesFactory.getMessages().relations_tooltip());
                relationButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

                    @Override
                    public void componentSelected(ButtonEvent ce) {
                        StringBuilder entityStr = new StringBuilder();
                        for (String str : lineageList)
                            entityStr.append(str).append(","); //$NON-NLS-1$
                        String arrStr = entityStr.toString().substring(0, entityStr.length() - 1);
                        String ids = itemBean.getIds();
                        if (ids == null || ids.trim() == "") //$NON-NLS-1$
                            ids = ""; //$NON-NLS-1$
                        initSearchEntityPanel(arrStr, ids, itemBean.getConcept());
                    }
                });
                ItemDetailToolBar.this.addSeparator();
                add(relationButton);
                ItemDetailToolBar.this.addWorkFlosCombo();

            }

            public void onFailure(Throwable arg0) {

            }
        });
    }

    private void addWorkFlosCombo() {
        service.getRunnableProcessList(itemBean.getConcept(), Locale.getLanguage(), new AsyncCallback<List<ItemBaseModel>>() {

            public void onSuccess(List<ItemBaseModel> processList) {
                add(new FillToolItem());
                ListStore<ItemBaseModel> workFlowList = new ListStore<ItemBaseModel>();
                workFlowList.add(processList);
                if (workFlowCombo == null) {
                    workFlowCombo = new ComboBox<ItemBaseModel>();
                    workFlowCombo.setStore(workFlowList);
                    workFlowCombo.setDisplayField("value");//$NON-NLS-1$
                    workFlowCombo.setValueField("key");//$NON-NLS-1$
                    workFlowCombo.setTypeAhead(true);
                    workFlowCombo.setTriggerAction(TriggerAction.ALL);
                    workFlowCombo.addSelectionChangedListener(new SelectionChangedListener<ItemBaseModel>() {

                        @Override
                        public void selectionChanged(SelectionChangedEvent<ItemBaseModel> se) {
                            selectItem = se.getSelectedItem();
                        }
                    });
                }
                add(workFlowCombo);
                if (launchProcessButton == null) {
                    launchProcessButton = new Button();
                    launchProcessButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.launch_process()));
                    launchProcessButton.setToolTip(MessagesFactory.getMessages().launch_process_tooltip());
                    launchProcessButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

                        @Override
                        public void componentSelected(ButtonEvent ce) {
                            if (selectItem == null) {
                                MessageBox.alert(MessagesFactory.getMessages().warning_title(),
                                        "Please select a process first!", null); //$NON-NLS-1$
                                return;
                            }
                            final MessageBox waitBar = MessageBox.wait(
                                    "Processing", "Processing, please wait...", "Processing..."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                            String[] ids = itemBean.getIds().split("@"); //$NON-NLS-1$

                            service.processItem(itemBean.getConcept(), ids,
                                    (String) selectItem.get("key"), new AsyncCallback<String>() { //$NON-NLS-1$

                                        public void onSuccess(String result) {
                                            waitBar.close();
                                            if (result.indexOf("Ok") >= 0) { //$NON-NLS-1$
                                                MessageBox.alert("Status", "Process done!", null); //$NON-NLS-1$ //$NON-NLS-2$
                                            } else {
                                                MessageBox.alert("Status", "Process failed!", null); //$NON-NLS-1$ //$NON-NLS-2$
                                            }
                                        }

                                        public void onFailure(Throwable arg0) {

                                        }
                                    });
                        }
                    });
                }
                add(launchProcessButton);
            }

            public void onFailure(Throwable arg0) {

            }
        });
    }

    private void addPersonalViewButton() {
        if (personalviewButton == null) {
            personalviewButton = new Button(MessagesFactory.getMessages().personalview_btn());
            personalviewButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

                @Override
                public void componentSelected(ButtonEvent ce) {
                    updateSmartViewToolBar();
                    if (ItemsDetailPanel.getInstance().getTabPanel().getItem(0).getWidget(0) instanceof ItemPanel) {
                        ItemPanel itemPanel = (ItemPanel) ItemsDetailPanel.getInstance().getTabPanel()
                                .getItem(0).getWidget(0);
                        itemPanel.getTree().setVisible(false);
                        itemPanel.getSmartPanel().setVisible(true);
                    }
                }

            });
        }
        add(personalviewButton);
    }

    private void addGeneratedViewButton() {
        if (generatedviewButton == null) {
            generatedviewButton = new Button(MessagesFactory.getMessages().generatedview_btn());
            generatedviewButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

                @Override
                public void componentSelected(ButtonEvent ce) {
                    updateViewToolBar();
                    if (ItemsDetailPanel.getInstance().getTabPanel().getItem(0).getWidget(0) instanceof ItemPanel) {
                        ItemPanel itemPanel = (ItemPanel) ItemsDetailPanel.getInstance().getTabPanel()
                                .getItem(0).getWidget(0);
                        itemPanel.getTree().setVisible(true);
                        itemPanel.getSmartPanel().setVisible(false);
                    }
                }

            });
        }
        add(generatedviewButton);
    }

    private void initSmartViewToolBar() {
        addGeneratedViewButton();
        addSeparator();
        addSmartViewCombo();
        addSeparator();
        addPrintButton();
        addSeparator();
        this.addDuplicateButton();
        this.addSeparator();
        this.addJournalButton();
        this.addSeparator();
        this.addFreshButton();
        this.addSeparator();
        this.addWorkFlosCombo();
    }

    private void updateSmartViewToolBar() {
        this.removeAll();
        initSmartViewToolBar();
    }

    private void updateViewToolBar() {
        this.removeAll();
        initViewToolBar();
    }

    private void addSmartViewCombo() {
        final ListStore<ItemBaseModel> smartViewList = new ListStore<ItemBaseModel>();
        if (smartViewCombo == null) {
            smartViewCombo = new ComboBox<ItemBaseModel>();
            smartViewCombo.setStore(smartViewList);
            smartViewCombo.setDisplayField("value"); //$NON-NLS-1$
            smartViewCombo.setValueField("key"); //$NON-NLS-1$
            smartViewCombo.setTypeAhead(true);
            smartViewCombo.setTriggerAction(TriggerAction.ALL);
            smartViewCombo.addSelectionChangedListener(new SelectionChangedListener<ItemBaseModel>() {

                @Override
                public void selectionChanged(SelectionChangedEvent<ItemBaseModel> se) {
                    if (ItemsDetailPanel.getInstance().getTabPanel().getItem(0).getWidget(0) instanceof ItemPanel) {
                        ItemPanel itemPanel = (ItemPanel) ItemsDetailPanel.getInstance().getTabPanel()
                                .getItem(0).getWidget(0);
                        String frameUrl = "/itemsbrowser/secure/SmartViewServlet?ids=" + itemBean.getIds() + "&concept=" //$NON-NLS-1$ //$NON-NLS-2$
                                + itemBean.getConcept() + "&language=" + Locale.getLanguage(); //$NON-NLS-1$
                        if (se.getSelectedItem().get("key") != null) //$NON-NLS-1$
                            frameUrl += ("&name=" + se.getSelectedItem().get("key"));//$NON-NLS-1$ //$NON-NLS-2$
                        itemPanel.getSmartPanel().setUrl(frameUrl);
                        itemPanel.getSmartPanel().layout(true);
                        if (itemPanel.getTree().isVisible()) {
                            itemPanel.getSmartPanel().setVisible(true);
                            itemPanel.getTree().setVisible(false);
                        }
                    }
                }

            });
        }

        String regex = itemBean.getConcept() + "&" + Locale.getLanguage(); //$NON-NLS-1$
        service.getSmartViewList(regex, new AsyncCallback<List<ItemBaseModel>>() {

            public void onFailure(Throwable caught) {
                Dispatcher.forwardEvent(BrowseRecordsEvents.Error, caught);
            }

            public void onSuccess(List<ItemBaseModel> list) {
                smartViewList.add(list);
                String smartView = "Smart_view_"; //$NON-NLS-1$
                for (ItemBaseModel item : list) {
                    if (item.get("key").equals(smartView + itemBean.getConcept()) //$NON-NLS-1$
                            || item.get("key").equals( //$NON-NLS-1$
                                    smartView + itemBean.getConcept() + "_" + Locale.getLanguage().toUpperCase())) { //$NON-NLS-1$
                        smartViewCombo.setValue(item);
                    }
                }
            }

        });
        add(smartViewCombo);
    }

    private void addPrintButton() {
        Button printBtn = new Button(MessagesFactory.getMessages().print_btn());
        printBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                if (smartViewCombo.getSelection() != null && smartViewCombo.getSelectionLength() > 0) {
                    Window printWin = new Window();
                    String url = "/itemsbrowser/secure/SmartViewServlet?ids=" + itemBean.getIds() //$NON-NLS-1$
                            + "&concept=" + itemBean.getConcept() + "&language=" + Locale.getLanguage(); //$NON-NLS-1$ //$NON-NLS-2$
                    url += "&name=" + smartViewCombo.getSelection().get(0).get("value"); //$NON-NLS-1$//$NON-NLS-2$
                    printWin.setUrl(url);
                    printWin.setHeading(MessagesFactory.getMessages().print_btn());
                    printWin.show();
                }
            }

        });
        add(printBtn);
    }

    private void addSeparator() {
        add(new SeparatorToolItem());
    }

    public void updateToolBar() {

    }

    private native boolean initJournal(String ids, String concept)/*-{
        $wnd.amalto.updatereport.UpdateReport.browseUpdateReportWithSearchCriteria(concept, ids, true);
        return true;
    }-*/;

    private native boolean initSearchEntityPanel(String arrStr, String ids, String dataObject)/*-{
        var lineageEntities = arrStr.split(",");
        $wnd.amalto.itemsbrowser.ItemsBrowser.lineageItem(lineageEntities, ids, dataObject);
        return true;
    }-*/;

    public static void addTreeDetail(String ids, String concept) {
        String[] idArr = ids.split(","); //$NON-NLS-1$
        BrowseRecordsServiceAsync brService = (BrowseRecordsServiceAsync) Registry.get(BrowseRecords.BROWSERECORDS_SERVICE);
        brService.getItemBeanById(concept, idArr, Locale.getLanguage(), new AsyncCallback<ItemBean>() {

            public void onSuccess(ItemBean item) {
                ItemPanel itemPanel = new ItemPanel(item, ItemDetailToolBar.VIEW_OPERATION);
                ItemsDetailPanel.getInstance().addTabItem(
                        item.getConcept() + " " + item.getIds(), itemPanel, ItemsDetailPanel.MULTIPLE, item.getIds()); //$NON-NLS-1$
            }

            public void onFailure(Throwable arg0) {

            }
        });
    }
    
    public void saveItemAndClose(boolean isClose){        
        boolean validate = false;
        TabPanel tabPanel = ItemsDetailPanel.getInstance().getTabPanel();
        TabItem tabItem = (TabItem) tabPanel.getSelectedItem();
        Widget widget = tabItem.getWidget(0);
        Dispatcher dispatch = Dispatcher.get();
        AppEvent app = new AppEvent(BrowseRecordsEvents.SaveItem);
        ItemNodeModel model = null;
        ViewBean viewBean = null;
        if (widget instanceof ItemPanel) {// save primary key
            viewBean = (ViewBean) BrowseRecords.getSession().get(UserSession.CURRENT_VIEW);
            ItemPanel itemPanel = (ItemPanel) tabItem.getWidget(0);
            if (itemPanel.getTree().validateTree()){     
                validate = true;
                model = (ItemNodeModel) itemPanel.getTree().getTree().getItem(0).getUserObject();
                app.setData("ItemBean", itemPanel.getItem()); //$NON-NLS-1$
                app.setData(
                        "isCreate", itemPanel.getOperation().equals(ItemDetailToolBar.CREATE_OPERATION) ? true : false); //$NON-NLS-1$
            }
            
        } else if (widget instanceof ForeignKeyTreeDetail) { // save foreign key
            ForeignKeyTreeDetail fkDetail = (ForeignKeyTreeDetail) tabItem.getWidget(0);
            model = fkDetail.getRootModel();
            app.setData(
                    "ItemBean", fkDetail.isCreate() ? new ItemBean(fkDetail.getViewBean().getBindingEntityModel().getConceptName(), "", "") : itemBean); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            app.setData("isCreate", fkDetail.isCreate()); //$NON-NLS-1$
        }
        app.setData("viewBean", viewBean); //$NON-NLS-1$
        app.setData(model);
        app.setData("isClose", isClose);
        if (validate){
            dispatch.dispatch(app);
        }
        else
        {
            com.google.gwt.user.client.Window.alert("save failure"); 
        }    
    }
}
