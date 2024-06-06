import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class livres extends JFrame {

    private JTable booksTable;
    private JTextField searchField;
    private JButton searchButton;
    private JButton returnButton;
    private JButton viewCartButton;
    private JButton categoryButton; // Bouton Catégorie
    private List<Emprunt> panier = new ArrayList<>();
    private String[] categories = {"Science-fiction", "Roman", "Aventure", "Policier","Magie"};
    private int userId; // ID de l'utilisateur connecté

    public livres(int userId) { // Modification du constructeur pour prendre userId
        this.userId = userId;

        setTitle("Page de Présentation des Livres");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel titleLabel = new JLabel("Bienvenue dans la Bibliothèque!");
        returnButton = new JButton("Retour");
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new biblio().setVisible(true);
            }
        });

        searchField = new JTextField(20);
        searchButton = new JButton("Rechercher");

        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Rechercher: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Ajout du bouton Catégorie
        categoryButton = new JButton("Catégorie");
        categoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCategoryDialog();
            }
        });
        searchPanel.add(categoryButton);

        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Seul le bouton "Emprunter" est éditable
            }
        };
        tableModel.addColumn("ID");
        tableModel.addColumn("Titre");
        tableModel.addColumn("Auteur");
        tableModel.addColumn("Catégorie");
        tableModel.addColumn("Emprunter");

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:8889/MaBibliotheque", "root", "root")) {
            String query = "SELECT * FROM livre";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    int idLivre = resultSet.getInt("id_livre");
                    String titre = resultSet.getString("titre");
                    String auteur = resultSet.getString("auteur");
                    String categorie = resultSet.getString("categorie");
                    Object[] rowData = new Object[]{idLivre, titre, auteur, categorie, "Emprunter"};
                    tableModel.addRow(rowData);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur SQL : " + ex.getMessage());
        }

        booksTable = new JTable(tableModel);
        booksTable.getColumn("Emprunter").setCellRenderer(new ButtonRenderer());
        booksTable.getColumn("Emprunter").setCellEditor(new ButtonEditor(new JCheckBox()));
        setLayout(new BorderLayout());
        add(titleLabel, BorderLayout.NORTH);
        add(returnButton, BorderLayout.WEST);
        add(searchPanel, BorderLayout.NORTH);
        add(new JScrollPane(booksTable), BorderLayout.CENTER);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchBooks();
            }
        });

        viewCartButton = new JButton("Voir le Panier");
        viewCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCart();
            }
        });
        add(viewCartButton, BorderLayout.SOUTH);

        loadEmpruntsFromDatabase();
    }

    private void searchBooks() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>((DefaultTableModel) booksTable.getModel());
        booksTable.setRowSorter(sorter);
        if (searchTerm.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchTerm));
        }
    }

    private void showCategoryDialog() {
        String selectedCategory = (String) JOptionPane.showInputDialog(
                this,
                "Choisir une catégorie:",
                "Catégories",
                JOptionPane.PLAIN_MESSAGE,
                null,
                categories,
                categories[0]);

        if (selectedCategory != null) {
            filterBooksByCategory(selectedCategory);
        }
    }

    private void filterBooksByCategory(String category) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>((DefaultTableModel) booksTable.getModel());
        booksTable.setRowSorter(sorter);
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + category, 3)); // 3 est l'index de la colonne de catégorie
    }

    private void loadEmpruntsFromDatabase() {
        panier.clear();
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:8889/MaBibliotheque", "root", "root")) {
            String query = "SELECT * FROM emprunts WHERE id_utilisateur = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, userId); // Utilisation de l'identifiant de l'utilisateur
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    String bookId = resultSet.getString("id_livre");
                    LocalDate dueDate = resultSet.getDate("date_retour_prevue").toLocalDate();
                    panier.add(new Emprunt(bookId, "", "", dueDate));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur SQL : " + ex.getMessage());
        }
    }

    private void showCart() {
        StringBuilder cartContent = new StringBuilder("Panier de l'utilisateur " + userId + ":\n");
        for (Emprunt emprunt : panier) {
            cartContent.append("Livre ID: ").append(emprunt.getId())
                       .append(", Date de retour: ").append(emprunt.getDueDate())
                       .append("\n");
        }
        JOptionPane.showMessageDialog(this, cartContent.toString());
    }

    private void addBookToCart(int bookId) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:8889/MaBibliotheque", "root", "root")) {
            String query = "INSERT INTO emprunts (id_utilisateur, id_livre, date_emprunt, date_retour_prevue) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, userId); // Utilisation de l'identifiant de l'utilisateur
                preparedStatement.setInt(2, bookId);
                preparedStatement.setDate(3, Date.valueOf(LocalDate.now()));
                preparedStatement.setDate(4, Date.valueOf(LocalDate.now().plusWeeks(2)));
                preparedStatement.executeUpdate();
                JOptionPane.showMessageDialog(this, "Livre emprunté avec succès!");
                loadEmpruntsFromDatabase(); // Recharger les emprunts pour mettre à jour le panier
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur SQL : " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        int userId = 1; // Par défaut, utilisez l'ID utilisateur 1 pour les tests
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new livres(userId).setVisible(true);
            }
        });
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private String label;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            JButton button = new JButton(label);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int bookId = (int) table.getValueAt(row, 0);
                    addBookToCart(bookId);
                    fireEditingStopped();
                }
            });
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return label;
        }
    }
}

class Emprunt {
    private String id;
    private String title;
    private String author;
    private LocalDate dueDate;

    public Emprunt(String id, String title, String author, LocalDate dueDate) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.dueDate = dueDate;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }
}
