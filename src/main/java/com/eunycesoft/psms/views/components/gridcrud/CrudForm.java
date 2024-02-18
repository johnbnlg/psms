package com.eunycesoft.psms.views.components.gridcrud;

import com.eunycesoft.psms.Utils;
import com.eunycesoft.psms.data.AbstractEntity;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.eunycesoft.psms.views.components.gridcrud.CrudOperation.DELETE;
import static com.eunycesoft.psms.views.components.gridcrud.CrudOperation.READ;

@Getter
public class CrudForm<T extends AbstractEntity> extends Form {

    protected BeanValidationBinder<T> binder;
    protected Class<T> domainType;

    protected T bean;
    protected String domainTypeName;
    protected CrudOperation operation;

    // Mapping bean fields to form fields
    protected Map<String, AbstractField> fieldsMapping = new HashMap<>();

    public CrudForm(Class<T> domainType, CrudOperation operation) {
        super();
        this.domainType = domainType;
        this.operation = operation;
        this.binder = new BeanValidationBinder<>(domainType);
        this.domainTypeName = Utils.toHumanFriendly(domainType.getSimpleName());
        caption.setText(String.format(operation.getCaptionFormat(), domainTypeName.toLowerCase()));
        submitButton.setText(operation.getSubmitButtonText());
        submitButton.setIcon(operation.getIcon().create());
        for (Field field : Utils.getAllFields(domainType, AbstractEntity.class, false)) {
            CrudFieldConfig config;
            if (Utils.isVisible(field, operation)) {
                config = Utils.getDefaultCrudConfig(field);
                fieldsMapping.put(field.getName(), config.getFormField());
                getContent().add(config.getFormField());
                if (operation.equals(READ) || operation.equals(DELETE))
                    config.getFormField().setReadOnly(true);
                if (config.getConverter() != null)
                    binder.forField(config.getFormField())
                            .withNullRepresentation(config.getNullRepresentation())
                            .withConverter(config.getConverter())
                            .bind(field.getName());
                else binder.forField(config.getFormField())
                        .withNullRepresentation(config.getNullRepresentation())
                        .bind(field.getName());
            }
        }
        binder.readBean(bean);
    }

    public void setBean(T bean) {
        this.bean = Objects.requireNonNull(bean);
        binder.readBean(bean);
    }


    @Override
    public void onSubmit() {
        Objects.requireNonNull(bean);
        switch (operation) {
            case READ -> close();
            case DELETE -> {
                fireEvent(new CrudFormEvent<>(this, false, bean, operation));
                close();
            }
            case CREATE, UPDATE -> {
                if (binder.writeBeanIfValid(bean)) {
                    fireEvent(new CrudFormEvent<>(this, false, bean, operation));
                    close();
                }
            }
        }
    }

    public Registration addCrudFormSubmitListener(ComponentEventListener<CrudFormEvent<T>> listener) {
        return addListener(CrudFormEvent.class, (ComponentEventListener) listener);
    }

    @Getter
    public static class CrudFormEvent<T extends AbstractEntity> extends ComponentEvent<CrudForm<T>> {
        private T item;
        private CrudOperation operation;

        public CrudFormEvent(CrudForm<T> source, boolean fromClient, T item, CrudOperation operation) {
            super(source, fromClient);
            this.item = item;
            this.operation = operation;
        }
    }
}