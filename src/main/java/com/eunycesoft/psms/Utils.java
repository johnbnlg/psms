package com.eunycesoft.psms;

import com.eunycesoft.psms.data.AbstractEntity;
import com.eunycesoft.psms.data.ExtendedJpaRepository;
import com.eunycesoft.psms.data.entity.User;
import com.eunycesoft.psms.views.components.CollectionEditor;
import com.eunycesoft.psms.views.components.gridcrud.CrudFieldConfig;
import com.eunycesoft.psms.views.components.gridcrud.CrudOperation;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.converter.StringToFloatConverter;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.util.SharedUtil;
import org.apache.commons.beanutils.BeanUtils;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;

import static com.eunycesoft.psms.Application.repositories;
import static com.eunycesoft.psms.Constants.*;
import static com.eunycesoft.psms.views.components.gridcrud.CrudOperation.*;

public class Utils {
    public static void showErrorNotification(String format, Object... args) {
        Notification notification = new Notification(String.format(format, args), 5000, Notification.Position.BOTTOM_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.open();
    }

    public static void showSuccessNotification(String format, Object... args) {
        Notification notification = new Notification(String.format(format, args), 5000, Notification.Position.BOTTOM_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.open();
    }

    public static void openLinkOnNewTab(String linkPattern, Object... params) {
        UI.getCurrent().getPage().open(String.format(linkPattern, params));
    }

    public static List<Field> getAllFields(Class<?> type, Class<?> topLimit, boolean includeTopLimit) {
        List<Field> fieldsList = new ArrayList<>();
        for (Class<?> cls = type; !cls.equals(topLimit); cls = cls.getSuperclass()) {
            fieldsList.addAll(0, Arrays.asList(cls.getDeclaredFields()));
        }
        if (includeTopLimit) fieldsList.addAll(0, Arrays.asList(topLimit.getDeclaredFields()));
        return fieldsList;
    }

    public static boolean isNullable(Field field) {
        if (field.isAnnotationPresent(NotNull.class))
            return false;
        if (field.isAnnotationPresent(ManyToOne.class))
            return field.getAnnotation(ManyToOne.class).optional();
        return true;
    }

    public static String rankFormat(Integer rank, Locale locale) {
        if (rank == null) return "";
        var suffix = "";
        if (locale.getLanguage().equals("en")) {
            if (rank % 10 == 1 && rank % 100 != 11) suffix = "st";
            else if (rank % 10 == 2 && rank % 100 != 12) suffix = "nd";
            else if (rank % 10 == 3 && rank % 100 != 13) suffix = "rd";
            else suffix = "th";
        } else {
            if (rank % 10 == 1 && rank % 100 != 11) suffix = "er";
            else suffix = "ème";
        }
        return String.format("%d<sup>%s</sup>", rank, suffix);
    }

    public static String formatDoubleNumber(Double average) {
        if (average == null) return "";
        return String.format(markFormat, average);
    }

    public static String toHumanFriendly(String camelCaseString) {
        String[] parts = SharedUtil.splitCamelCase(camelCaseString);
        for (int i = 0; i < parts.length; i++) {
            parts[i] = (i == 0) ? SharedUtil.capitalize(parts[i]) : parts[i].toLowerCase();
        }
        return SharedUtil.join(parts, " ");
    }

    public static String databaseColumnToCamelCase(String dashSeparated) {
        if (dashSeparated == null) {
            return null;
        }
        String[] parts = dashSeparated.split("_");
        for (int i = 1; i < parts.length; i++) {
            parts[i] = SharedUtil.capitalize(parts[i]);
        }
        return SharedUtil.join(parts, "");
    }

    public static Date localDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static Double ifNull(Double number, Double defaultValue) {
        return number == null ? defaultValue : number;
    }

    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return localDateToDate(localDateTime.toLocalDate());
    }

    public static String booleanToYesNo(boolean bool, Locale locale) {
        return bool ? locale.getLanguage().equals("fr") ? "Oui" : "Yes"
                : locale.getLanguage().equals("fr") ? "Non" : "No";
    }

    public static String birthDateString(User user, Locale locale) {
        var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        var at = locale.getLanguage().equals("fr") ? "à" : "at";
        var place = user.getPlaceOfBirth() == null ? "" : user.getPlaceOfBirth();
        return String.format("%s %s %s", user.getDateOfBirth().format(formatter), at, place);
    }

    public static StreamResource getPhotoAsStreamResource(User user) {
        StreamResource streamResource = new StreamResource(user.getRegistrationNumber(), () -> getPhotoAsInputStream(user));
        streamResource.setContentType("image/jpeg");
        return streamResource;
    }

    public static InputStream getPhotoAsInputStream(User user) {
        String photoPath = System.getProperty("user.dir") + File.separator + "photos" + File.separator;
        InputStream inputStream = null;
        try {
            inputStream = Files.newInputStream(new File(photoPath + user.getRegistrationNumber() + ".jpg").toPath());
        } catch (IOException e) {
        }
        if (inputStream == null) {
            var fileName = user.getGender().name().equals("F") ? "female.png" : "male.png";
            inputStream = Application.class.getResourceAsStream("/images/" + fileName);
        }
        return inputStream;
    }

    public static Image makeColorTransparent(BufferedImage im, final Color color, float threshold) {
        ImageFilter filter = new RGBImageFilter() {
            public float markerAlpha = color.getRGB() | 0xFF000000;

            public final int filterRGB(int x, int y, int rgb) {
                int currentAlpha = rgb | 0xFF000000;           // just to make it clear, stored the value in new variable
                float diff = Math.abs((currentAlpha - markerAlpha) / markerAlpha);  // Now get the difference
                if (diff <= threshold) {                      // Then compare that threshold value
                    return 0x00FFFFFF & rgb;
                } else {
                    return rgb;
                }
            }
        };
        ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
        return Toolkit.getDefaultToolkit().createImage(ip);
    }

    public static BufferedImage imageToBufferedImage(Image image) {
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_4BYTE_ABGR_PRE);
        Graphics2D g2 = bufferedImage.createGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
        return bufferedImage;
    }

    public static boolean isVisible(Field field, CrudOperation operation) {
        if (field.isAnnotationPresent(OneToOne.class)
            || field.isAnnotationPresent(OneToMany.class)
            || field.getName().equalsIgnoreCase("password")) {
            return false;
        }
        var allowedOperations = new HashSet<CrudOperation>();
        allowedOperations.add(READ);
        allowedOperations.add(DELETE);
        if (field.isAnnotationPresent(Column.class)) {
            var anno = field.getAnnotation(Column.class);
            if (anno.insertable())
                allowedOperations.add(CREATE);
            if (anno.updatable())
                allowedOperations.add(UPDATE);
        } else if (field.isAnnotationPresent(ManyToOne.class)) {
            var anno = field.getAnnotation(ManyToOne.class);
            allowedOperations.add(CREATE);
            if (anno.optional())
                allowedOperations.add(UPDATE);
        } else if (field.isAnnotationPresent(OneToOne.class)) {
            var anno = field.getAnnotation(OneToOne.class);
            allowedOperations.add(CREATE);
            if (anno.optional())
                allowedOperations.add(UPDATE);
        } else if (!Modifier.isTransient(field.getModifiers())) {
            allowedOperations.add(CREATE);
            allowedOperations.add(UPDATE);
        }
        return allowedOperations.contains(operation);
    }

    public static <T> CrudFieldConfig getDefaultCrudConfig(Field field) {
        var fieldType = field.getType();
        var label = Utils.toHumanFriendly(field.getName());
        if (Integer.class.isAssignableFrom(fieldType) || int.class.isAssignableFrom(fieldType)) {
            var renderer = new NumberRenderer<T>(entity -> {
                try {
                    var value = BeanUtils.getSimpleProperty(entity, field.getName());
                    return (value == null) ? null : Integer.valueOf(value);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, NumberFormat.getIntegerInstance());
            return new CrudFieldConfig(field, new IntegerField(label), renderer);
        }
        if (Float.class.isAssignableFrom(fieldType) || float.class.isAssignableFrom(fieldType)) {
            var renderer = new NumberRenderer<T>(entity -> {
                try {
                    var value = BeanUtils.getSimpleProperty(entity, field.getName());
                    return (value == null) ? null : Float.valueOf(value);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, DecimalFormat.getNumberInstance());
            var converter = new StringToFloatConverter("Please enter a valid float");
            return new CrudFieldConfig(field, new TextField(label), converter, renderer, null);
        }

        if (Double.class.isAssignableFrom(fieldType) || double.class.isAssignableFrom(fieldType)) {
            var renderer = new NumberRenderer<T>(entity -> {
                try {
                    var value = BeanUtils.getSimpleProperty(entity, field.getName());
                    return (value == null) ? null : Double.valueOf(value);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, NumberFormat.getNumberInstance());
            return new CrudFieldConfig(field, new NumberField(label), renderer);
        }

        if (Boolean.class.isAssignableFrom(fieldType) || boolean.class.isAssignableFrom(fieldType)) {
            return new CrudFieldConfig(field, new Checkbox(label));
        }
        if (LocalDate.class.isAssignableFrom(fieldType) || LocalDateTime.class.isAssignableFrom(fieldType)) {
            var formField = new DatePicker(label, LocalDate.now());
            formField.setLocale(Locale.FRENCH);
            var renderer = new LocalDateRenderer<T>(entity -> {
                try {
                    var value = BeanUtils.getSimpleProperty(entity, field.getName());
                    return (value == null) ? null : LocalDate.parse(value);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, dateFormatter);
            return new CrudFieldConfig(field, formField, renderer);
        }

        if (LocalDateTime.class.isAssignableFrom(fieldType)) {
            var formField = new DateTimePicker(label, LocalDateTime.now());
            formField.setLocale(Locale.FRENCH);
            var renderer = new LocalDateTimeRenderer<T>(entity -> {
                try {
                    var value = BeanUtils.getSimpleProperty(entity, field.getName());
                    return (value == null) ? null : LocalDateTime.parse(value);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, dateTimeFormatter);
            return new CrudFieldConfig(field, formField, renderer);
        }

        if (Enum.class.isAssignableFrom(fieldType)) {
            return new CrudFieldConfig(field, new ComboBox<>(label, fieldType.getEnumConstants()));
        }

        if (AbstractEntity.class.isAssignableFrom(fieldType)) {
            var repo = (ExtendedJpaRepository) repositories.getRepositoryFor(fieldType).get();
            return new CrudFieldConfig(field, new ComboBox<>(label, repo.findAll()));
        }
        if (Collection.class.isAssignableFrom(fieldType)) {
            var parameterizedType = (ParameterizedType) field.getGenericType();
            Class clazz = (Class) (parameterizedType.getActualTypeArguments()[0]);
            if (AbstractEntity.class.isAssignableFrom(clazz)) {
                var repo = (ExtendedJpaRepository) repositories.getRepositoryFor(clazz).get();
                var tcsField = new CollectionEditor(label, repo.findAll());
                return new CrudFieldConfig(field, tcsField);
            }
            if (clazz.isEnum()) {
                var tcsField = new CollectionEditor(label, Arrays.asList(clazz.getEnumConstants()));
                return new CrudFieldConfig(field, tcsField);
            }
        }

        if (field.isAnnotationPresent(Email.class)) {
            var config = new CrudFieldConfig(field, new EmailField(label));
            config.setNullRepresentation("");
            return config;
        }
        var config = new CrudFieldConfig(field, new TextField(label));
        config.setNullRepresentation("");
        return config;
    }

    public static boolean isMobileDevice() {
        var browser = VaadinSession.getCurrent().getBrowser();
        return (browser.isAndroid() || browser.isIPhone() || browser.isWindowsPhone());
    }

    public static Double doubleSum(Double... numbers) {
        Double result = null;
        for (Double nbr : numbers) {
            if (nbr != null) result += nbr;
        }
        return result;
    }

    public static Integer integerSum(Integer... numbers) {
        Integer result = null;
        for (Integer nbr : numbers) {
            if (nbr != null) result += nbr;
        }
        return result;
    }

    public static String getSessionLanguage() {
        var session = VaadinSession.getCurrent();
        return (session != null) ? session.getLocale().getLanguage() : "fr";
    }
}



