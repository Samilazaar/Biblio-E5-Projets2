import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class biblio extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;

    public biblio() {
        // Configuration de la fenêtre
        setTitle("Bibliothèque Application");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Démarrez en mode plein écran
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Création des composants
        JLabel titleLabel = new JLabel("Bienvenue à la Bibliothèque");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel usernameLabel = new JLabel("Nom d'utilisateur:");
        JLabel passwordLabel = new JLabel("Mot de passe:");
        JLabel emailLabel = new JLabel("Adresse e-mail:");
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        emailField = new JTextField(20);

        JButton loginButton = new JButton("Connexion");
        JButton signupButton = new JButton("Inscription");

        // Mise en page avec le gestionnaire de mise en page BorderLayout
        setLayout(new BorderLayout());
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(titleLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        formPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(loginButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(signupButton, gbc);

        add(formPanel, BorderLayout.CENTER);


        // Ajout des écouteurs d'événements aux boutons
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSignup();
            }
        });

    
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // Code de connexion à la base de données
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:8889/MaBibliotheque", "root", "root")) {
            String query = "SELECT * FROM utilisateurs WHERE username = ? AND mdp = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                if (preparedStatement.executeQuery().next()) {
                    // Connexion réussie
                    JOptionPane.showMessageDialog(null, "Connexion réussie pour l'utilisateur : " + username);
                    if (username.equals("sami") && password.equals("sami")) {
                        admin adminPage = new admin();
                        adminPage.setVisible(true);
                    } else {
                        livres livresPage = new livres();
                        livresPage.setVisible(true);
                    }

                    // Redirection vers la page de présentation des livres
                    dispose(); // Ferme la fenêtre actuelle
                } else {
                    // Identifiants incorrects
                    JOptionPane.showMessageDialog(null, "Identifiants incorrects. Veuillez réessayer.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur SQL lors de la connexion : " + ex.getMessage());
        }
    }

    private void handleSignup() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String email = emailField.getText();

        // Code pour insérer les informations dans la base de données
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:8889/MaBibliotheque", "root", "root")) {
            String query = "INSERT INTO utilisateurs (username, mdp, email) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.setString(3, email);
                preparedStatement.executeUpdate();
            }
            JOptionPane.showMessageDialog(null, "Inscription réussie pour l'utilisateur : " + username);
        
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur SQL lors de l'inscription : " + ex.getMessage());
        }
        
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new biblio().setVisible(true);
            }
        });
    }
}