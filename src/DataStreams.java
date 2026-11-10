import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.Collectors;

public class DataStreams extends JFrame
{
    private final JTextArea originalTextArea;
    private final JTextArea filteredTextArea;
    private final JTextField searchField;
    private final JButton searchButton;

    private Path filePath;

    public DataStreams()
    {
        setTitle("Java Data Streams");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));


        originalTextArea = new JTextArea();
        filteredTextArea = new JTextArea();

        originalTextArea.setEditable(false);
        filteredTextArea.setEditable(false);


        JScrollPane originalScroll = new JScrollPane(originalTextArea);
        JScrollPane filteredScroll = new JScrollPane(filteredTextArea);


        JPanel textPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        JPanel originalPanel = new JPanel(new BorderLayout());
        originalPanel.add(new JLabel("Original File"), BorderLayout.NORTH);
        originalPanel.add(originalScroll, BorderLayout.CENTER);

        JPanel filteredPanel = new JPanel(new BorderLayout());
        filteredPanel.add(new JLabel("Filtered Results"), BorderLayout.NORTH);
        filteredPanel.add(filteredScroll, BorderLayout.CENTER);

        textPanel.add(originalPanel);
        textPanel.add(filteredPanel);

        add(textPanel, BorderLayout.CENTER);


        JPanel searchPanel = new JPanel(new FlowLayout());

        searchField = new JTextField(20);

        searchPanel.add(new JLabel("Search String:"));
        searchPanel.add(searchField);

        add(searchPanel, BorderLayout.NORTH);


        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton loadButton = new JButton("Load File");
        searchButton = new JButton("Search File");
        JButton quitButton = new JButton("Quit");

        searchButton.setEnabled(false);

        buttonPanel.add(loadButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(quitButton);

        add(buttonPanel, BorderLayout.SOUTH);


        loadButton.addActionListener(e -> loadFile());
        searchButton.addActionListener(e -> searchFile());
        quitButton.addActionListener(e -> System.exit(0));

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadFile()
    {
        JFileChooser fileChooser = new JFileChooser();

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION)
        {
            Path selectedPath = fileChooser.getSelectedFile().toPath();

            try
            {
                List<String> lines = Files.readAllLines(selectedPath);

                filePath = selectedPath;

                originalTextArea.setText(String.join(System.lineSeparator(), lines));

                filteredTextArea.setText("");

                searchField.setText("");

                searchButton.setEnabled(true);
            }
            catch (IOException e)
            {
                JOptionPane.showMessageDialog(
                        this,
                        "Error loading file: " + e.getMessage(),
                        "File Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void searchFile()
    {
        if (filePath == null)
        {
            return;
        }

        String searchString = searchField.getText();

        if (searchString.isEmpty())
        {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a search string.",
                    "Search Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        try (Stream<String> lines = Files.lines(Paths.get(filePath.toString())))
        {
            List<String> results = lines
                    .filter(line -> line.contains(searchString))
                    .collect(Collectors.toList());

            filteredTextArea.setText(String.join(System.lineSeparator(), results));

            if (results.isEmpty())
            {
                filteredTextArea.setText("No matching lines found.");
            }
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(
                    this,
                    "Error searching file: " + e.getMessage(),
                    "Search Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    static void main(String[] args)
    {
        new DataStreams();
    }
}