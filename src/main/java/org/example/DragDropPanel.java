package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class DragDropPanel extends JPanel {

    public interface ImageDropListener {
        void onImageDropped(BufferedImage image);
    }

    private final ImageDropListener dropListener;

    public DragDropPanel(ImageDropListener dropListener) {
        this.dropListener = dropListener;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createDashedBorder(
                Color.GRAY, 2.0f, 5.0f, 5.0f, true
        ));

        JLabel label = new JLabel("Перетащите изображение сюда", SwingConstants.CENTER);
        label.setFont(label.getFont().deriveFont(Font.PLAIN, 16f));
        add(label, BorderLayout.CENTER);

        new DropTarget(this, DnDConstants.ACTION_COPY, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent dtde) {
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        @SuppressWarnings("unchecked")
                        List<File> droppedFiles = (List<File>) dtde.getTransferable()
                                .getTransferData(DataFlavor.javaFileListFlavor);
                        if (!droppedFiles.isEmpty()) {
                            File file = droppedFiles.get(0);
                            BufferedImage img = ImageIO.read(file);
                            if (img != null && dropListener != null) {
                                dropListener.onImageDropped(img);
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, true);
    }
}