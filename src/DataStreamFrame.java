import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.System.in;
import static java.nio.file.StandardOpenOption.CREATE;

public class DataStreamFrame extends JFrame {
    JPanel main;
    JPanel display;
    JPanel button;
    JPanel search;

    JTextArea left;
    JTextArea right;

    JScrollPane leftScroll;
    JScrollPane rightScroll;

    JButton load;
    JButton filter;
    JButton quit;

    JLabel label;

    JTextField searchString;

    private File selectedFile;
    private Path filePath;

    private Set set = new HashSet();

    public DataStreamFrame() {
        main =  new JPanel();
        main.setLayout(new BorderLayout());

        createDisplay();
        createButton();
        createSearch();

        main.add(search, BorderLayout.NORTH);
        main.add(display, BorderLayout.CENTER);
        main.add(button, BorderLayout.SOUTH);

        add(main);

        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screen = kit.getScreenSize();

        int screenWidth = screen.width;
        int screenHeight = screen.height;

        setSize(screenWidth / 2, screenHeight / 2);
        setLocation(screenWidth / 4, screenHeight / 4);

        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void createSearch() {
        search = new JPanel();
        search.setLayout(new GridLayout(1, 2));

        searchString = new JTextField();
        searchString.setToolTipText("Enter a search string here");
        searchString.setBackground(new Color(203, 232, 202));

        label = new JLabel("Search: ");
        label.setFont(new Font("Arial", Font.PLAIN, 24));
        label.setHorizontalAlignment(JLabel.RIGHT);

        search.add(label);
        search.add(searchString);
    }

    public void createDisplay() {
        display = new JPanel();
        display.setLayout(new GridLayout(1, 2));
        display.setBorder(new TitledBorder(new EtchedBorder(), ""));

        left = new JTextArea();
        right = new JTextArea();

        left.setEditable(false);
        right.setEditable(false);

        left.setBackground(new Color(235, 234, 230));
        right.setBackground(new Color(235, 234, 230));

        left.setFont(new Font("Arial", Font.PLAIN, 18));
        right.setFont(new Font("Arial", Font.PLAIN, 18));

        leftScroll = new JScrollPane(left, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        rightScroll = new JScrollPane(right, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        leftScroll.setToolTipText("Original file");
        rightScroll.setToolTipText("Filtered file");

        display.add(leftScroll);
        display.add(rightScroll);
    }

    public void createButton() {
        button = new JPanel();
        button.setLayout(new GridLayout(1, 3));
        button.setBorder(new TitledBorder(new EtchedBorder(), ""));

        load = new JButton("Load");
        filter = new JButton("Filter");
        quit = new JButton("Quit");

        filter.setEnabled(false);
        filter.setBackground(new Color(235, 205, 202));

        load.setFont(new Font("Arial", Font.BOLD, 24));
        filter.setFont(new Font("Arial", Font.BOLD, 24));
        quit.setFont(new Font("Arial", Font.BOLD, 24));

        load.addActionListener((ActionEvent e) -> {loadFile();});
        filter.addActionListener((ActionEvent e) -> {filterFile();});
        quit.addActionListener((ActionEvent e) -> {System.exit(0);});

        button.add(load);
        button.add(filter);
        button.add(quit);
    }

    public void loadFile() {
        JFileChooser chooser = new JFileChooser();
        File workingDirectory = new File(System.getProperty("user.dir"));
        chooser.setCurrentDirectory(workingDirectory);
        if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            filePath = selectedFile.toPath();
        }

        filter.setEnabled(true);
        filter.setBackground(null);
        JOptionPane.showMessageDialog(main, "File Loaded", "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public void filterFile() {
        left.setText("");
        right.setText("");

        String wordFilter = searchString.getText();
        String rec = "";

        try(Stream<String> lines = Files.lines(Paths.get(selectedFile.getPath()))) {
            Set<String> set = lines.filter(w -> w.contains(wordFilter)).collect(Collectors.toSet());
            set.forEach(w -> right.append(w + "\n"));
        } catch (FileNotFoundException e) {
            System.out.println("File not found!!!");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            InputStream in = new BufferedInputStream(Files.newInputStream(filePath, CREATE));
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            int line = 0;
            while(reader.ready()) {
                rec = reader.readLine();
                left.append(rec + "\n");
                line++;
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found!!!");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
