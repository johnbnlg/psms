package com.eunycesoft.psms.views.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.customfield.CustomFieldVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.*;

public class CollectionEditor<T> extends CustomField<Collection<T>> {
    private MultiSelectListBox<T> source = new MultiSelectListBox<>();
    private MultiSelectListBox<T> target = new MultiSelectListBox<>();

    private Collection<T> sourceItems, targetItems;
    private Button toTarget = new Button(VaadinIcon.ANGLE_RIGHT.create(), e -> toTarget());
    private Button allToTarget = new Button(VaadinIcon.ANGLE_DOUBLE_RIGHT.create(), e -> allToTarget());
    private Button toSource = new Button(VaadinIcon.ANGLE_LEFT.create(), e -> toSource());
    private Button allToSource = new Button(VaadinIcon.ANGLE_DOUBLE_LEFT.create(), e -> allToSource());

    public CollectionEditor() {
        super();
        addThemeVariants(CustomFieldVariant.LUMO_SMALL);
        var btnLayout = new VerticalLayout();
        btnLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        btnLayout.setSpacing(false);
        btnLayout.setWidth("50px");
        List.of(toTarget, allToTarget, toSource, allToSource).forEach(btn -> {
            btn.addThemeVariants(ButtonVariant.LUMO_SMALL);
            btnLayout.add(btn);
        });
        List.of(source, target).forEach(list -> {
            list.setWidthFull();
            list.setHeight("180px");
            list.getStyle().set("border", "1px var(--lumo-primary-color) solid");
            list.getStyle().set("border-radius", "var(--lumo-border-radius)");
        });
        var mainLayout = new HorizontalLayout(source, btnLayout, target);
        mainLayout.setSpacing(false);
        mainLayout.setSizeFull();
        add(mainLayout);
    }

    public CollectionEditor(Collection<T> initialSourceItems) {
        this();
        setItems(initialSourceItems);
    }

    public CollectionEditor(String label, Collection<T> initialSourceItems) {
        this();
        setItems(initialSourceItems);
        setLabel(label);
    }

    @Override
    protected Collection<T> generateModelValue() {
        return targetItems;
    }

    @Override
    protected void setPresentationValue(Collection<T> newPresentationValue) {
        targetItems.addAll(newPresentationValue);
        sourceItems.removeAll(newPresentationValue);
        refresh();
    }

    public void toTarget() {
        targetItems.addAll(source.getSelectedItems());
        sourceItems.removeAll(source.getSelectedItems());
        refresh();
    }

    public void allToTarget() {
        targetItems.addAll(sourceItems);
        sourceItems.clear();
        refresh();
    }

    public void toSource() {
        sourceItems.addAll(target.getSelectedItems());
        targetItems.removeAll(target.getSelectedItems());
        refresh();
    }

    public void allToSource() {
        sourceItems.addAll(targetItems);
        targetItems.clear();
        refresh();
    }

    public void setItems(Collection<T> items) {
        if (items instanceof List<T>){
            sourceItems = new ArrayList<>();
            targetItems = new ArrayList<>();
        }else {
            sourceItems = new HashSet<>();
            targetItems = new HashSet<>();
        }
        sourceItems.addAll(items);
        refresh();
    }

    public void refresh() {
        source.setItems(sourceItems);
        target.setItems(targetItems);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        List.of(source, target, toTarget, allToTarget, toSource, allToSource).forEach(btn -> btn.setEnabled(!readOnly));
    }

    @Override
    public Collection<T> getValue() {
        return targetItems;
    }
}
