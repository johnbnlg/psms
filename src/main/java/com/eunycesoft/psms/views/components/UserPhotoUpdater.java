package com.eunycesoft.psms.views.components;

import com.eunycesoft.psms.Utils;
import com.eunycesoft.psms.data.entity.User;
import com.eunycesoft.psms.views.components.gridcrud.Form;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.server.StreamResource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class UserPhotoUpdater extends Form {
    private final User user;
    String photoPath = System.getProperty("user.dir") + File.separator + "photos" + File.separator;
    private MemoryBuffer buffer = new MemoryBuffer();

    public UserPhotoUpdater(User user) {
        this.user = user;
        setTitle("Updating user's photo");
        var upload = new Upload(buffer);
        var image = new Image(Utils.getPhotoAsStreamResource(user), "User image");
        var imageLayout = new HorizontalLayout(image);
        image.setWidth("150px");
        imageLayout.setWidthFull();
        imageLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        getContent().add(new Span("User: " + user.getFullName()), imageLayout, upload);
        if (Utils.isMobileDevice()) {
            getContent().add(new Checkbox("From capture device", true, evt -> {
                if (evt.getValue()) {
                    upload.getElement().setAttribute("capture", "environment");
                } else {
                    upload.getElement().removeAttribute("capture");
                }
            }));
        }
        upload.setAcceptedFileTypes("image/*");
        upload.addSucceededListener(evt -> {
            upload.clearFileList();
            image.setSrc(new StreamResource(
                    evt.getFileName(), () -> buffer.getInputStream()));
        });
    }

    @Override
    public void onSubmit() {
        try {
            var os = Files.newOutputStream(new File(photoPath + user.getRegistrationNumber() + ".jpg").toPath(),
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            var image = ImageIO.read(buffer.getInputStream());
            var jpgImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
            jpgImage.createGraphics().drawImage(image, 0, 0, Color.white, null);
            ImageIO.write(jpgImage, "jpg", os);
            os.close();
            Utils.showSuccessNotification("User photo updated successfully");
            super.onSubmit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
