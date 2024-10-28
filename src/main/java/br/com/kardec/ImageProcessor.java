package br.com.kardec;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import javax.imageio.ImageIO;

public class ImageProcessor extends JFrame {
    private JLabel imageLabel;
    private BufferedImage originalImage;
    private BufferedImage processedImage;

    public ImageProcessor() {
        setTitle("Processador de Imagem");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Painel para mostrar a imagem
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        add(imageLabel, BorderLayout.CENTER);

        // Painel de botões (parâmetros)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(7, 1));

        JButton openButton = new JButton("Abrir Imagem");
        JButton grayscaleButton = new JButton("Converter para Tons de Cinza");
        JButton binaryButton = new JButton("Converter para Binária");
        JButton rgbButton = new JButton("Converter para RGB");
        JButton meanFilterButton = new JButton("Filtro Média");
        JButton medianFilterButton = new JButton("Filtro Mediana");
        JButton gaussianFilterButton = new JButton("Filtro Gaussiano");
        JButton denoiseButton = new JButton("Remover Ruído");

        // Ações dos botões
        openButton.addActionListener(e -> openImage());
        grayscaleButton.addActionListener(e -> convertToGrayscale());
        binaryButton.addActionListener(e -> convertToBinary());
        rgbButton.addActionListener(e -> revertToRGB());
        meanFilterButton.addActionListener(e -> applyMeanFilter());
        medianFilterButton.addActionListener(e -> applyMedianFilter());
        gaussianFilterButton.addActionListener(e -> applyGaussianFilter());
        denoiseButton.addActionListener(e -> applyDenoiseFilter());

        buttonPanel.add(openButton);
        buttonPanel.add(grayscaleButton);
        buttonPanel.add(binaryButton);
        buttonPanel.add(rgbButton);
        buttonPanel.add(meanFilterButton);
        buttonPanel.add(medianFilterButton);
        buttonPanel.add(gaussianFilterButton);
        buttonPanel.add(denoiseButton);

        add(buttonPanel, BorderLayout.EAST);
        pack();
        setLocationRelativeTo(null); // Centraliza a janela
        setVisible(true);
    }

    // Abrir imagem
    private void openImage() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                originalImage = ImageIO.read(file);
                processedImage = originalImage;
                imageLabel.setIcon(new ImageIcon(processedImage));
                pack();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao carregar imagem");
            }
        }
    }

    // Converter para tons de cinza
    private void convertToGrayscale() {
        if (processedImage != null) {
            BufferedImage grayscaleImage = new BufferedImage(
                    processedImage.getWidth(), processedImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
            Graphics g = grayscaleImage.getGraphics();
            g.drawImage(processedImage, 0, 0, null);
            g.dispose();
            processedImage = grayscaleImage;
            imageLabel.setIcon(new ImageIcon(processedImage));
        }
    }

    // Converter para imagem binária
    private void convertToBinary() {
        if (processedImage != null) {
            BufferedImage binaryImage = new BufferedImage(
                    processedImage.getWidth(), processedImage.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
            Graphics g = binaryImage.getGraphics();
            g.drawImage(processedImage, 0, 0, null);
            g.dispose();
            processedImage = binaryImage;
            imageLabel.setIcon(new ImageIcon(processedImage));
        }
    }

    // Reverter para RGB
    private void revertToRGB() {
        if (originalImage != null) {
            processedImage = originalImage;
            imageLabel.setIcon(new ImageIcon(processedImage));
        }
    }

    // Aplicar filtro de média
    private void applyMeanFilter() {
        processedImage = applyKernelFilter(processedImage, new float[][] {
                {1/9f, 1/9f, 1/9f},
                {1/9f, 1/9f, 1/9f},
                {1/9f, 1/9f, 1/9f}
        });
        imageLabel.setIcon(new ImageIcon(processedImage));
    }

    // Aplicar filtro de mediana
    private void applyMedianFilter() {
        if (processedImage != null) {
            BufferedImage result = new BufferedImage(processedImage.getWidth(), processedImage.getHeight(), processedImage.getType());
            int[] window = new int[9];
            for (int y = 1; y < processedImage.getHeight() - 1; y++) {
                for (int x = 1; x < processedImage.getWidth() - 1; x++) {
                    int k = 0;
                    for (int j = -1; j <= 1; j++) {
                        for (int i = -1; i <= 1; i++) {
                            window[k++] = new Color(processedImage.getRGB(x + i, y + j)).getRed();
                        }
                    }
                    Arrays.sort(window);
                    int median = window[window.length / 2];
                    Color newColor = new Color(median, median, median);
                    result.setRGB(x, y, newColor.getRGB());
                }
            }
            processedImage = result;
            imageLabel.setIcon(new ImageIcon(processedImage));
        }
    }

    // Aplicar filtro Gaussiano
    private void applyGaussianFilter() {
        processedImage = applyKernelFilter(processedImage, new float[][] {
                {1/16f, 2/16f, 1/16f},
                {2/16f, 4/16f, 2/16f},
                {1/16f, 2/16f, 1/16f}
        });
        imageLabel.setIcon(new ImageIcon(processedImage));
    }

    // Função para aplicar filtros com kernel
    private BufferedImage applyKernelFilter(BufferedImage img, float[][] kernel) {
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage filteredImage = new BufferedImage(width, height, img.getType());

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                float newValue = 0.0f;
                for (int j = -1; j <= 1; j++) {
                    for (int i = -1; i <= 1; i++) {
                        newValue += new Color(img.getRGB(x + i, y + j)).getRed() * kernel[j + 1][i + 1];
                    }
                }
                int grayValue = Math.min(Math.max((int) newValue, 0), 255);
                Color newColor = new Color(grayValue, grayValue, grayValue);
                filteredImage.setRGB(x, y, newColor.getRGB());
            }
        }
        return filteredImage;
    }

    // Aplicar filtro de remoção de ruído combinado
    private void applyDenoiseFilter() {
        if (processedImage != null) {
            BufferedImage meanFiltered = applyKernelFilter(processedImage, new float[][] {
                    {1/9f, 1/9f, 1/9f},
                    {1/9f, 1/9f, 1/9f},
                    {1/9f, 1/9f, 1/9f}
            });

            BufferedImage medianFiltered = applyMedianFilterAndReturn(processedImage);
            BufferedImage gaussianFiltered = applyKernelFilter(processedImage, new float[][] {
                    {1/16f, 2/16f, 1/16f},
                    {2/16f, 4/16f, 2/16f},
                    {1/16f, 2/16f, 1/16f}
            });

            BufferedImage denoisedImage = new BufferedImage(processedImage.getWidth(), processedImage.getHeight(), processedImage.getType());

            for (int y = 0; y < processedImage.getHeight(); y++) {
                for (int x = 0; x < processedImage.getWidth(); x++) {
                    int meanGray = new Color(meanFiltered.getRGB(x, y)).getRed();
                    int medianGray = new Color(medianFiltered.getRGB(x, y)).getRed();
                    int gaussianGray = new Color(gaussianFiltered.getRGB(x, y)).getRed();

                    int combinedGray = (int) (0.25 * meanGray + 0.25 * medianGray + 0.5 * gaussianGray);
                    Color newColor = new Color(combinedGray, combinedGray, combinedGray);
                    denoisedImage.setRGB(x, y, newColor.getRGB());
                }
            }

            processedImage = denoisedImage;
            imageLabel.setIcon(new ImageIcon(processedImage));
        }
    }

    // Método auxiliar para aplicar o filtro mediana e retornar o resultado
    private BufferedImage applyMedianFilterAndReturn(BufferedImage img) {
        BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        int[] window = new int[9];
        for (int y = 1; y < img.getHeight() - 1; y++) {
            for (int x = 1; x < img.getWidth() - 1; x++) {
                int k = 0;
                for (int j = -1; j <= 1; j++) {
                    for (int i = -1; i <= 1; i++) {
                        window[k++] = new Color(img.getRGB(x + i, y + j)).getRed();
                    }
                }
                Arrays.sort(window);
                int median = window[window.length / 2];
                Color newColor = new Color(median, median, median);
                result.setRGB(x, y, newColor.getRGB());
            }
        }
        return result;
    }

    public static void main(String[] args) {
        new ImageProcessor();
    }
}
