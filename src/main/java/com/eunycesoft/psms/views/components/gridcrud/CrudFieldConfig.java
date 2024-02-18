package com.eunycesoft.psms.views.components.gridcrud;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.renderer.Renderer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Field;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CrudFieldConfig<T> {
    private Field field;
    private AbstractField<?, T> formField;
    private Converter<?, ?> converter;
    private Renderer<T> renderer;

    private Object nullRepresentation;

    public CrudFieldConfig(Field field, AbstractField<?, T> formField) {
        this.field = field;
        this.formField = formField;
    }

    public CrudFieldConfig(Field field, AbstractField<?, T> formField, Renderer<T> renderer) {
        this.field = field;
        this.renderer = renderer;
        this.formField = formField;
    }
}
