package com.eunycesoft.psms.views.components.gridcrud;

import com.eunycesoft.psms.Application;
import com.eunycesoft.psms.Utils;
import com.eunycesoft.psms.data.AbstractEntity;
import com.eunycesoft.psms.data.ExtendedJpaRepository;
import com.eunycesoft.psms.data.entity.Personnel;
import com.eunycesoft.psms.data.entity.Student;
import com.eunycesoft.psms.data.entity.User;
import com.eunycesoft.psms.data.enums.Role;
import com.eunycesoft.psms.views.components.*;
import com.eunycesoft.psms.views.components.gridcrud.CrudForm.CrudFormEvent;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import static com.eunycesoft.psms.Constants.*;
import static com.eunycesoft.psms.views.components.gridcrud.CrudOperation.*;

@Getter
public class Crud<T extends AbstractEntity> extends VerticalLayout {
    protected boolean withSplitView;
    protected boolean withFilterRow;
    protected Class<T> domainType;
    protected String domainTypeName;
    protected T selected;
    protected ExtendedJpaRepository<T, Integer> repo;
    protected Button columnToggle = new Button(VaadinIcon.EYE.create());
    protected ContextMenu columnToggleContextMenu = new ContextMenu(columnToggle);
    protected Span gridHeaderCaption = new Span();
    protected Grid<T> grid;
    protected GridContextMenu<T> contextMenu;
    protected Span gridFooterCaption = new Span();
    protected Button refreshButton = new Button(VaadinIcon.REFRESH.create(), evt -> refreshButtonClickHandler());
    protected HorizontalLayout gridFooter = new HorizontalLayout(gridFooterCaption);
    protected VerticalLayout gridLayout = new VerticalLayout();
    protected VerticalLayout formLayout = new VerticalLayout();
    protected CrudForm<T> form;
    @Setter
    private boolean gridRefreshOnFormSubmit = true;
    protected Button addButton = new Button(VaadinIcon.PLUS.create(), evt -> addButtonClickHandler());
    protected Button updateButton = new Button(VaadinIcon.PENCIL.create(), evt -> updateButtonClickHandler());
    protected Button deleteButton = new Button(VaadinIcon.TRASH.create(), evt -> deleteButtonClickHandler());
    protected HorizontalLayout gridHeader = new HorizontalLayout(columnToggle, gridHeaderCaption, refreshButton, addButton, updateButton, deleteButton);

    public Crud(Class<T> domainType) {
        this(domainType, true, true);
    }

    public Crud(Class<T> domainType, boolean withSplitLayout) {
        this(domainType, withSplitLayout, true);
    }

    public Crud(Class<T> domainType, boolean withSplitLayout, boolean withFilterRow) {
        this.domainType = domainType;
        this.withFilterRow = withFilterRow;
        this.domainTypeName = Utils.toHumanFriendly(domainType.getSimpleName()).toLowerCase();
        this.withSplitView = !Utils.isMobileDevice() && withSplitLayout;
        repo = (ExtendedJpaRepository<T, Integer>) Application.repositories.getRepositoryFor(domainType).get();
        grid = new Grid<>(domainType, false);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
        contextMenu = new GridContextMenu<>(grid);
        gridLayout.add(gridHeader, grid, gridFooter);

        configureUserInterface();
        configureGridColumns();
        configureColumnToggleMenu();
        configureFilters();
        configureContextMenu();

        grid.addSelectionListener(evt -> onGridSelectionChanged());
        grid.addItemDoubleClickListener(evt -> showForm(UPDATE, evt.getItem()));
        grid.getListDataView().addItemCountChangeListener(evt -> {
            gridFooterCaption.setText(String.format(READ.getSuccessMassageFormat(), evt.getItemCount(), domainTypeName));
        });
        addAttachListener(evt -> refreshGrid());
    }

    public void configureUserInterface() {
        List.of(this, gridLayout, formLayout).forEach(layout -> {
            layout.setMargin(false);
            layout.setPadding(false);
            layout.setSpacing(false);
            layout.setSizeFull();
        });
        gridHeaderCaption.setWidthFull();
        gridHeaderCaption.getStyle().set("color", "var(--lumo-primary-text-color)")
                .set("font-size", "1.3em");
        List.of(columnToggle, refreshButton, addButton, updateButton, deleteButton)
                .forEach(btn -> btn.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL));
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        columnToggle.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
        columnToggleContextMenu.setOpenOnClick(true);

        List.of(gridHeader, gridFooter).forEach(layout -> {
            layout.setMargin(false);
            layout.setPadding(false);
            layout.getStyle().set("padding", "0px 10px");
            layout.setWidthFull();
        });

        if (withSplitView) {
            var root = new SplitLayout(gridLayout, formLayout);
            root.setSizeFull();
            root.setSplitterPosition(60);
            add(root);
        } else {
            add(gridLayout);
        }
    }

    public void configureGridColumns() {
        grid.setColumnReorderingAllowed(true);
        for (Field field : Utils.getAllFields(domainType, AbstractEntity.class, false)) {
            if (Utils.isVisible(field, READ)) {
                CrudFieldConfig config = Utils.getDefaultCrudConfig(field);
                if (config.getRenderer() != null) {
                    grid.addColumn(config.getRenderer())
                            .setHeader(Utils.toHumanFriendly(field.getName()))
                            .setKey(field.getName())
                            .setAutoWidth(true)
                            .setSortable(true)
                            .setResizable(true);
                } else {
                    grid.addColumn(field.getName())
                            .setHeader(Utils.toHumanFriendly(field.getName()))
                            .setResizable(true)
                            .setAutoWidth(true)
                            .setSortable(true);
                }
            }
        }
    }

    public void configureColumnToggleMenu() {
        columnToggleContextMenu.removeAll();
        grid.getColumns().forEach(col -> {
            var menuItem = columnToggleContextMenu.addItem(Utils.toHumanFriendly(col.getKey()),
                    evt -> col.setVisible(evt.getSource().isChecked()));
            menuItem.setCheckable(true);
            menuItem.setChecked(col.isVisible());
        });
    }

    public void configureFilters() {
        if (!withFilterRow) return;
        var headerRow = grid.appendHeaderRow();
        grid.getColumns().forEach(column -> {
            TextField filter = new TextField();
            filter.addThemeVariants(TextFieldVariant.LUMO_SMALL);
            filter.setClearButtonVisible(true);
            filter.setSizeFull();
            filter.setPlaceholder("Filter");
            filter.setValueChangeMode(ValueChangeMode.EAGER);
            filter.addValueChangeListener((event) -> {
                grid.getListDataView().addFilter((entity) -> {
                    String fieldValue = "";
                    try {
                        fieldValue = String.valueOf(BeanUtils.getSimpleProperty(entity, column.getKey()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.showErrorNotification("Failed to apply the filter on " + Utils.toHumanFriendly(column.getKey()));
                    }
                    return StringUtils.containsIgnoreCase(fieldValue, filter.getValue());
                });
            });
            headerRow.getCell(column).setComponent(filter);
        });
    }

    public void onGridSelectionChanged() {
        if (form != null && form.isAttached()) form.close();
        selected = grid.asSingleSelect().getValue();
        updateButtons();
        if (selected != null && withSplitView)
            showForm(READ, selected);
    }

    public void refreshGrid() {
        grid.asSingleSelect().clear();
        try {
            var items = findAll();
            grid.setItems(items);
        } catch (Exception e) {
            e.printStackTrace();
            Utils.showErrorNotification(READ.getErrorMassageFormat(), domainTypeName, e.getMessage());
        }
    }

    public Collection<T> findAll() {
        return repo.findAll();
    }

    public void onCrudFormSubmit(CrudFormEvent<T> evt) {
        if (evt.getItem() != null) {
            try {
                switch (evt.getOperation()) {
                    case CREATE, UPDATE -> repo.save(evt.getItem());
                    case DELETE -> repo.delete(evt.getItem());
                }
                Utils.showSuccessNotification(evt.getOperation().getSuccessMassageFormat(), domainTypeName);
            } catch (Exception e) {
                e.printStackTrace();
                Utils.showErrorNotification(evt.getOperation().getErrorMassageFormat(), domainTypeName, e.getMessage());
            }
        }
        fireEvent(new CrudEvent<>(this, false, evt.getItem(), evt.getOperation()));
        if (gridRefreshOnFormSubmit)
            refreshGrid();
    }

    public void configureCreatedForm() {
        if (User.class.isAssignableFrom(domainType) && (form.operation == READ || form.operation == DELETE)){
            var photo = new Image(Utils.getPhotoAsStreamResource((User)form.getBean()),"");
            photo.setWidth("100px");
            var layout = new HorizontalLayout(photo);
            layout.setJustifyContentMode(JustifyContentMode.CENTER);
            layout.setWidthFull();
            form.getContent().addComponentAsFirst(layout);
        }
    }

    protected void refreshButtonClickHandler() {
        refreshGrid();
    }

    protected void addButtonClickHandler() {
        try {
            T entiy = domainType.getConstructor().newInstance();
            if (Student.class.isAssignableFrom(domainType))
                ((Student)entiy).setMainRole(Role.STUDENT);
            if (Personnel.class.isAssignableFrom(domainType))
                ((Personnel)entiy).setMainRole(Role.TEACHER);
            showForm(CREATE, entiy);
        } catch (Exception e) {
            e.printStackTrace();
            Utils.showErrorNotification("Failed to create a new %s. Cause: %s", domainTypeName, e.getMessage());
        }
    }

    protected void updateButtonClickHandler() {
        if (selected != null)
            showForm(UPDATE, selected);
    }

    protected void deleteButtonClickHandler() {
        if (selected != null)
            showForm(DELETE, selected);
    }

    protected void showForm(CrudOperation operation, T entity) {
        if (form != null && form.isAttached())
            form.close();
        form = new CrudForm<>(domainType, operation);
        form.addCrudFormSubmitListener(this::onCrudFormSubmit);
        form.setBean(entity);
        configureCreatedForm();
        if (withSplitView)
            form.show(formLayout);
        else form.show();
    }

    protected void updateButtons() {
        updateButton.setEnabled(selected != null);
        deleteButton.setEnabled(selected != null);
    }

    protected void setGridVisibleColumns(List<String> visibleColumns) {
        grid.getColumns().forEach(col -> {
            if (!visibleColumns.contains(col.getKey()))
                grid.removeColumn(col);
        });
    }

    public void refreshSelected(){
        selected = repo.findById(selected.getId()).get();
        grid.getDataProvider().refreshItem(selected);
    }

    protected void configureContextMenu() {
        var dataMenu = contextMenu.addItem("Data");
        dataMenu.getSubMenu().addItem("Load this grid data from file", evt -> {
            var dialog = new DataImportWizard<>(domainType);
            dialog.addDetachListener(e -> refreshGrid());
            dialog.open();
        });

        dataMenu.getSubMenu().addItem("Export this grid data to file", evt -> {

        });

        dataMenu.getSubMenu().addItem("Import Database from file", evt -> {
//            var command = "";
//            try {
//                var proc = Runtime.getRuntime().exec(command);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//                Utils.showErrorNotification("Failed to execute command");
//            }
        });

        dataMenu.getSubMenu().addItem("Export Database to file", evt -> {

        });

        if (User.class.isAssignableFrom(domainType)) {
            contextMenu.add(new Hr());
            var userMenu = contextMenu.addItem("User");
            userMenu.getSubMenu().addItem("Update photo", evt -> {
                if (selected != null) {
                    refreshSelected();
                    var dialog = new UserPhotoUpdater((User) selected);
                    dialog.addFormSubmitListener(e -> refreshGrid());
                    dialog.show();
                }
            });
        }
        if (Student.class.isAssignableFrom(domainType)) {
            contextMenu.add(new Hr());
            var studentMenu = contextMenu.addItem("Student");
            studentMenu.getSubMenu().addItem("Update classroom", evt -> {
                if (selected != null) {
                    refreshSelected();
                    var dialog = new StudentClassroomUpdater((Student) selected);
                    dialog.addFormSubmitListener(e -> refreshGrid());
                    dialog.show();
                }
            });
            studentMenu.getSubMenu().addItem("School certificate", evt -> {
                if (selected != null)
                    Utils.openLinkOnNewTab(STUDENT_SCHOOL_CERTIFICATE_URL_PATTERN, selected.getId());
            });
            studentMenu.getSubMenu().addItem("Payments summary", evt -> {
                if (selected != null) {
                    refreshSelected();
                    new StudentReceiptPrintDialog((Student) selected).show();
                }
            });
            studentMenu.getSubMenu().add(new Hr());
            studentMenu.getSubMenu().addItem("Students' List", evt -> {
                new StudentListPrintDialog().show();
            });
            studentMenu.getSubMenu().addItem("Students' badges", evt -> {
                Utils.openLinkOnNewTab(STUDENTS_BADGES_REPORT_URL_PATTERN);
            });
        }
        if (Personnel.class.isAssignableFrom(domainType)) {
            contextMenu.add(new Hr());
            var personnelMenu = contextMenu.addItem("Personnel");
            personnelMenu.getSubMenu().addItem("Personnels badges", evt -> {
                Utils.openLinkOnNewTab(PERSONNELS_BADGES_REPORT_URL_PATTERN);
            });
        }
    }

    public Registration addCrudOperationListener(ComponentEventListener<CrudEvent<T>> listener) {
        return addListener(CrudEvent.class, (ComponentEventListener) listener);
    }

    @Getter
    public static class CrudEvent<T extends AbstractEntity> extends ComponentEvent<Crud<T>> {
        private T item;
        private CrudOperation operation;

        public CrudEvent(Crud<T> source, boolean fromClient, T item, CrudOperation operation) {
            super(source, fromClient);
            this.item = item;
            this.operation = operation;
        }
    }
}
