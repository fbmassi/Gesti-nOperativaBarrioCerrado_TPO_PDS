package com.barrio.presentacion.vistas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.barrio.dominio.personas.Persona;

/**
 * Ventana de login (Swing). En caso de éxito abre el DashboardView del rol.
 */
public class LoginView extends JFrame {

    private static final Color PRIMARIO = new Color(0, 134, 190);

    private final transient Contexto contexto;
    private final JTextField emailTxt = new JTextField(18);
    private final JPasswordField passTxt = new JPasswordField(18);
    private final JLabel mensaje = new JLabel(" ", SwingConstants.CENTER);

    public LoginView(Contexto contexto) {
        this.contexto = contexto;
        setTitle("Barrio Cerrado - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(420, 380);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(crearEncabezado(), BorderLayout.NORTH);
        add(crearFormulario(), BorderLayout.CENTER);
    }

    private JPanel crearEncabezado() {
        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(PRIMARIO);
        header.setPreferredSize(new Dimension(420, 90));
        JLabel titulo = new JLabel("SISTEMA BARRIO CERRADO");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        header.add(titulo);
        return header;
    }

    private JPanel crearFormulario() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;

        JLabel sub = new JLabel("Iniciar sesión", SwingConstants.CENTER);
        sub.setFont(new Font("SansSerif", Font.BOLD, 16));
        form.add(sub, c);

        c.gridwidth = 1;
        c.gridy = 1;
        c.gridx = 0;
        form.add(new JLabel("Email:"), c);
        c.gridx = 1;
        form.add(emailTxt, c);

        c.gridy = 2;
        c.gridx = 0;
        form.add(new JLabel("Contraseña:"), c);
        c.gridx = 1;
        form.add(passTxt, c);

        JButton loginBtn = new JButton("Ingresar");
        loginBtn.setBackground(PRIMARIO);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.addActionListener(e -> intentarLogin());
        c.gridy = 3;
        c.gridx = 0;
        c.gridwidth = 2;
        form.add(loginBtn, c);

        mensaje.setForeground(Color.RED);
        c.gridy = 4;
        form.add(mensaje, c);

        getRootPane().setDefaultButton(loginBtn);
        return form;
    }

    private void intentarLogin() {
        String email = emailTxt.getText().trim();
        String password = new String(passTxt.getPassword()).trim();
        try {
            Persona usuario = contexto.autenticacion.login(email, password);
            if (usuario == null) {
                mensaje.setText("Email o contraseña incorrectos");
                return;
            }
            dispose();
            new DashboardView(contexto, usuario).setVisible(true);
        } catch (Exception ex) {
            // Muestra el error real (p. ej. base de datos en uso por otra instancia).
            mensaje.setText("Error: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Error al iniciar sesión", JOptionPane.ERROR_MESSAGE);
        }
    }
}
