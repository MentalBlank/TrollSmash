package Sockets;

import javax.swing.UIManager.LookAndFeelInfo;
import java.awt.event.*;
import java.sql.ResultSet;
import java.util.*;
import javax.swing.DefaultListModel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import Json.*;
import Preload.Item;

/**
 * Root class, handles all user interaction.
 * @version 0.8 r21
 */
public class Interface extends javax.swing.JFrame {

    public static int version = 80;
    public static String rev = "r21";
    public static ServerBase _server;
    public static SqlConnection _sql;
    public static String serverIp;
    public static StringBuffer sb;
    private StringBuilder _sb = new StringBuilder();
    private static Boolean _scheduled = false;
    public static void main(String[] args) {
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
        }

        new Interface().setVisible(true);

        _sql = new SqlConnection();

        writeLog("[Root] Connecting to mySQL Database...", 2);

        if(_sql.connect()) {
            try {
                writeLog("[Root] Connected to Database! \"" + _sql.con.getMetaData().getDatabaseProductName() +
                        " v" + _sql.con.getMetaData().getDatabaseProductVersion() + "\"", 2);
            
                writeLog("[Root] Checking Database Version...", 2);
                ResultSet rs = _sql.query("SELECT version FROM hs_settings");
                if(rs.next()) {
                    if(rs.getString("version").equals(SqlConnection.dbVersion))
                        writeLog("[Root] Database check complete. EliteDB " + SqlConnection.dbVersion, 2);
                    else {
                        writeLog("[Root] Error! Incorrect database version! Unable to continue.", 2);
                    }
                }
                writeLog("[Root] Initialized", 2);
                btnUnlock.setEnabled(true);
            } catch (Exception x) {}
        } else {
            writeLog("[Root] Errors Occured During Boot-up", 2);
        }
    }
    

    /** Creates new form Interface */
    public Interface() {
        initComponents();
        sb = new StringBuffer();
        sb.append("Elite v").append((double) version / 100).append("b ").append(rev);
        sVersion.setText(sb.toString());
        
        listLoggedIn.setModel(new DefaultListModel());
        listExp.setModel(new DefaultListModel());

        addWindowListener(new WindowAdapter() {
            @Override
            @SuppressWarnings("static-access")
             public void windowClosing(WindowEvent e)
             {
                if(btnUnlock.isEnabled() && _server != null)
                    _Shutdown();

                System.exit(0);
             }
          });

        /** Initialize Menu Items **/
        final JMenuItem _kick = new JMenuItem("Kick");
        _kick.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int selected[] = listLoggedIn.getSelectedIndices();
                for (int i=0; i < selected.length; i++) {
                    _server.lobby.kickPlayer(listLoggedIn.getModel().getElementAt(selected[i]).toString());
                }
            }
        });

        final JMenuItem _upg = new JMenuItem("Make VIP");
        _upg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int selected[] = listLoggedIn.getSelectedIndices();
                for (int i=0; i < selected.length; i++) {
                    _server.lobby.updatePlayer(listLoggedIn.getModel().getElementAt(selected[i]).toString(), "vip", 1);
                }
            }
        });
        final JMenuItem _unupg = new JMenuItem("Un-VIP");
        _unupg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int selected[] = listLoggedIn.getSelectedIndices();
                for (int i=0; i < selected.length; i++) {
                    _server.lobby.updatePlayer(listLoggedIn.getModel().getElementAt(selected[i]).toString(), "vip", 0);
                }
            }
        });

        final JMenuItem _founder = new JMenuItem("Make Founder");
        _founder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int selected[] = listLoggedIn.getSelectedIndices();
                for (int i=0; i < selected.length; i++) {
                    _server.lobby.updatePlayer(listLoggedIn.getModel().getElementAt(selected[i]).toString(), "founder", 1);
                }
            }
        });
        final JMenuItem _unfounder = new JMenuItem("Un-Founder");
        _unfounder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int selected[] = listLoggedIn.getSelectedIndices();
                for (int i=0; i < selected.length; i++) {
                    _server.lobby.updatePlayer(listLoggedIn.getModel().getElementAt(selected[i]).toString(), "founder", 0);
                }
            }
        });
        final JMenuItem _mod = new JMenuItem("Make Moderator");
        _mod.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int selected[] = listLoggedIn.getSelectedIndices();
                for (int i=0; i < selected.length; i++) {
                    _server.lobby.updatePlayer(listLoggedIn.getModel().getElementAt(selected[i]).toString(), "mod", 1);
                }
            }
        });
        final JMenuItem _demote = new JMenuItem("Demote");
        _demote.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int selected[] = listLoggedIn.getSelectedIndices();
                for (int i=0; i < selected.length; i++) {
                    _server.lobby.updatePlayer(listLoggedIn.getModel().getElementAt(selected[i]).toString(), "admin", 0);
                }
            }
        });
        final JMenuItem _admin = new JMenuItem("Make Admin");
        _admin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int selected[] = listLoggedIn.getSelectedIndices();
                for (int i=0; i < selected.length; i++) {
                    _server.lobby.updatePlayer(listLoggedIn.getModel().getElementAt(selected[i]).toString(), "admin", 1);
                }
            }
        });
        
        final JMenuItem _ban = new JMenuItem("Ban");
        _ban.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int selected[] = listLoggedIn.getSelectedIndices();
                for (int i=0; i < selected.length; i++) {
                    _server.lobby.updatePlayer(listLoggedIn.getModel().getElementAt(selected[i]).toString(), "banned", 1);
                }
            }
        });

        final JMenuItem _trace = new JMenuItem("Trace");
        _trace.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int selected[] = listLoggedIn.getSelectedIndices();
                for (int i=0; i < selected.length; i++) {
                    _server.lobby.setTrace(listLoggedIn.getModel().getElementAt(selected[i]).toString(), true);
                }
            }
        });

        final JMenuItem _untrace = new JMenuItem("Untrace");
        _untrace.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int selected[] = listLoggedIn.getSelectedIndices();
                for (int i=0; i < selected.length; i++) {
                    _server.lobby.setTrace(listLoggedIn.getModel().getElementAt(selected[i]).toString(), false);
                }
            }
        });

        final JMenuItem _delete = new JMenuItem("Delete");
        _delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int selected[] = listLoggedIn.getSelectedIndices();
                for (int i=0; i < selected.length; i++) {
                    _server.lobby.updatePlayer(listLoggedIn.getModel().getElementAt(selected[i]).toString(), "delete", 1);
                }
            }
        });

        /** Initialize Menus **/
        jPopupMenu1.add(_kick);
        jPopupMenu1.add(_upg);
        jPopupMenu1.add(_unupg);
        jPopupMenu1.add(_founder);
        jPopupMenu1.add(_unfounder);
        jPopupMenu1.add(_ban);
        jPopupMenu1.add(new JPopupMenu.Separator());
        jPopupMenu1.add(_mod);
        jPopupMenu1.add(_admin);
        jPopupMenu1.add(_demote);
        jPopupMenu1.add(new JPopupMenu.Separator());
        jPopupMenu1.add(_trace);
        jPopupMenu1.add(_untrace);
        jPopupMenu1.add(new JPopupMenu.Separator());
        jPopupMenu1.add(_delete);

        listLoggedIn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(final MouseEvent e) {
                this.maybeShowPopup(e);
            }

            @Override
            public void mousePressed(final MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    final int index = listLoggedIn.locationToIndex(e.getPoint());
                    listLoggedIn.setSelectedIndex(index);
                    if(index != -1) {
                        _kick.setEnabled(true);
                        _upg.setEnabled(true);
                        _unupg.setEnabled(true);
                        _founder.setEnabled(true);
                        _unfounder.setEnabled(true);
                        _ban.setEnabled(true);
                        _delete.setEnabled(true);
                        final ConnectionHandler uho = _server.lobby.getHandler(listLoggedIn.getModel().getElementAt(index).toString());
                        if(uho._trace) {
                            _trace.setEnabled(false);
                            _untrace.setEnabled(true);
                        } else {
                            _trace.setEnabled(true);
                            _untrace.setEnabled(false);
                        }
                    } else {
                        _kick.setEnabled(false);
                        _upg.setEnabled(false);
                        _unupg.setEnabled(false);
                        _founder.setEnabled(false);
                        _unfounder.setEnabled(false);
                        _ban.setEnabled(false);
                        _delete.setEnabled(false);
                        _trace.setEnabled(false);
                        _untrace.setEnabled(false);
                    }
                }
                this.maybeShowPopup(e);
            }

            private void maybeShowPopup(final MouseEvent e) {
                if (e.isPopupTrigger()) {
                    jPopupMenu1.show(listLoggedIn, e.getX(), e.getY());
                }
            }
        });

        txtSMsg.addKeyListener
        (new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    int key = e.getKeyCode();
                    if (key == KeyEvent.VK_ENTER) {
                        btnSend.doClick();
                    }
                }
            }
        );
        jBar.setVisible(false);
        writeLog("[Interface] Initialized", 2);
    }

    public static void writeLog(String msg, int txtarea) {
        try {
            sb.setLength(0);
            sb.append(msg).append("\n");
            if(txtarea == 1){
                //sLoginLog.getDocument().insertString(0, msg + "\n", null);
                //System.out.println(msg);
            } else if(txtarea == 2){
                sActLog.getDocument().insertString(0, sb.toString(), null);
                System.out.println(msg);
            } else if (txtarea == 3) {
                sChatLog.getDocument().insertString(0, sb.toString(), null);
                //System.out.println(msg);
            } else if (txtarea == 4) {
                sPacketLog.getDocument().insertString(0, sb.toString(), null);
                //sPacketLog.insert(msg + "\n", 0);
                //System.out.println(msg);
            }
        }
        catch (Exception e) {
        }
    }

    @SuppressWarnings("static-access")
    public static void _Boot ()
    {
        btnRestart.setEnabled(false);
        btnShutdown.setEnabled(false);
        btnStart.setEnabled(false);
        mUntrace.setEnabled(false);
        mTrace.setEnabled(false);
        writeLog("[Root] Booting up...", 2);
        new Thread(_server = new ServerBase(serverIp)).start();
        _scheduled = false;
    }
    
    public static void _beginShutdown(Boolean restart)
    {
        if(_scheduled) {
            writeLog("[Root] Server is currently scheduled for shutdown/restart", 2);
            return;
        } else {
            if(restart)
                writeLog("[Root] Server is now scheduled for restart in 15 seconds...", 2);
             else
                writeLog("[Root] Server is now scheduled for shutdown in 15 seconds...", 2);

            _scheduled = true;
        }
        if(restart) {
            _server.lobby.sendData("%xt%warning%-1%Server restart in 15 seconds! Please logout now to avoid data lost.%");
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                         _Shutdown();
                        _Boot();
                    } catch (Exception e) {
                    }
                }
            }, 15000);
        } else {
            _server.lobby.sendData("%xt%warning%-1%Server shutdown in 15 seconds! Please logout now to avoid data lost.%");
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                         _Shutdown();
                    } catch (Exception e) {
                    }
                }
            }, 15000);
        }
    }

    @SuppressWarnings("static-access")
    public static void _Shutdown ()
    {
        writeLog("[Root] Shutting down...", 2);
        if(_server != null) {
            if(_server.lobby != null) {
                _server.lobby.finalize();
            }
            _server.close();
            _server = null;
            _sql.update("UPDATE wqw_servers SET online=0,count=0 WHERE ip='" + Interface.serverIp + "'");
            writeLog("[Root] Shutdown completed. Auto-boot up Enabled", 2);
            btnStart.setEnabled(true);
            btnRestart.setEnabled(false);
            btnShutdown.setEnabled(false);
            mTrace.setEnabled(false);
            mUntrace.setEnabled(false);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        if(_server == null) {
                            writeLog("[Root] Server is idle, auto-restarting...", 2);
                             _Boot();
                        }
                    } catch (Exception e) {
                    }
                }
            }, 600000);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        sTotal = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jLabel1 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        sActLog = new javax.swing.JTextArea();
        jScrollPane5 = new javax.swing.JScrollPane();
        listLoggedIn = new javax.swing.JList();
        jBar = new javax.swing.JProgressBar();
        jPanel2 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtToken = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        tokenButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        spnResTime = new javax.swing.JSpinner();
        resTimeButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        spnRate = new javax.swing.JSpinner();
        goldButton = new javax.swing.JButton();
        xpButton = new javax.swing.JButton();
        txtToken1 = new javax.swing.JTextField();
        tokenButton1 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listExp = new javax.swing.JList();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        sPacketLog = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        sChatLog = new javax.swing.JTextArea();
        btnSend = new javax.swing.JButton();
        txtSMsg = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        txtJson = new javax.swing.JTextArea();
        btnItemAdd = new javax.swing.JButton();
        sVersion = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        btnUnlock = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        btnStart = new javax.swing.JMenuItem();
        btnRestart = new javax.swing.JMenuItem();
        btnShutdown = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        btnExit = new javax.swing.JMenuItem();
        mSettings = new javax.swing.JMenu();
        mTrace = new javax.swing.JMenuItem();
        mUntrace = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Elite HS Emulator");
        setBackground(new java.awt.Color(51, 51, 51));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setForeground(new java.awt.Color(51, 51, 51));
        setLocationByPlatform(true);
        setName("InterfaceMain"); // NOI18N
        setResizable(false);

        sTotal.setText("Total Online: 0");

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setEnabled(false);
        jToolBar1.add(jSeparator2);

        jLabel1.setText("Elite HS Emulator - A very epic emulator used by elites! xD");
        jToolBar1.add(jLabel1);

        jTabbedPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        sActLog.setBackground(new java.awt.Color(0, 0, 0));
        sActLog.setColumns(20);
        sActLog.setEditable(false);
        sActLog.setFont(new java.awt.Font("Arial", 0, 13));
        sActLog.setForeground(new java.awt.Color(255, 0, 204));
        sActLog.setLineWrap(true);
        sActLog.setRows(5);
        jScrollPane2.setViewportView(sActLog);

        listLoggedIn.setBackground(new java.awt.Color(0, 0, 0));
        listLoggedIn.setFont(new java.awt.Font("Arial", 0, 13));
        listLoggedIn.setForeground(new java.awt.Color(255, 0, 204));
        jScrollPane5.setViewportView(listLoggedIn);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jBar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 586, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Actions Log & Users List", jPanel1);

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel5.setText("Root Settings");

        txtToken.setEnabled(false);

        jLabel6.setText("Client Token:");

        tokenButton.setText("Change");
        tokenButton.setEnabled(false);
        tokenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tokenButtonActionPerformed(evt);
            }
        });

        jLabel4.setText("Monster Respawn Time:");

        spnResTime.setAutoscrolls(true);
        spnResTime.setValue(8);

        resTimeButton.setText("Change");
        resTimeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resTimeButtonActionPerformed(evt);
            }
        });

        jLabel2.setText("Rates:");

        spnRate.setAutoscrolls(true);
        spnRate.setValue(1);

        goldButton.setText("Change Gold Rate");
        goldButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goldButtonActionPerformed(evt);
            }
        });

        xpButton.setText("Change Exp Rate");
        xpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xpButtonActionPerformed(evt);
            }
        });

        txtToken1.setEnabled(false);

        tokenButton1.setText("Change");
        tokenButton1.setEnabled(false);
        tokenButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tokenButton1ActionPerformed(evt);
            }
        });

        jLabel8.setText("Message of the Day:");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel5)
                    .addComponent(jLabel2)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtToken)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(spnResTime, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE)
                            .addComponent(spnRate, javax.swing.GroupLayout.Alignment.LEADING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tokenButton)
                            .addComponent(resTimeButton)
                            .addComponent(tokenButton1, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addComponent(jLabel8)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txtToken1)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                            .addComponent(xpButton, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(goldButton))))
                .addContainerGap(38, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addGap(15, 15, 15)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtToken, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tokenButton))
                .addGap(14, 14, 14)
                .addComponent(jLabel4)
                .addGap(4, 4, 4)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spnResTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(resTimeButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spnRate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(xpButton)
                    .addComponent(goldButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtToken1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tokenButton1))
                .addContainerGap(96, Short.MAX_VALUE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        listExp.setBackground(new java.awt.Color(0, 0, 0));
        listExp.setForeground(new java.awt.Color(255, 0, 204));
        listExp.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Initialize the Lobby first." };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(listExp);

        jLabel7.setText("Exp To Level Information:");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addContainerGap(213, Short.MAX_VALUE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Server Commands", jPanel2);

        sPacketLog.setBackground(new java.awt.Color(0, 0, 0));
        sPacketLog.setColumns(20);
        sPacketLog.setEditable(false);
        sPacketLog.setFont(new java.awt.Font("Arial", 0, 13));
        sPacketLog.setForeground(new java.awt.Color(255, 0, 204));
        sPacketLog.setLineWrap(true);
        sPacketLog.setRows(5);
        jScrollPane4.setViewportView(sPacketLog);

        jTabbedPane1.addTab("Packet Log", jScrollPane4);

        sChatLog.setBackground(new java.awt.Color(0, 0, 0));
        sChatLog.setColumns(20);
        sChatLog.setEditable(false);
        sChatLog.setFont(new java.awt.Font("Arial", 0, 13));
        sChatLog.setForeground(new java.awt.Color(255, 0, 204));
        sChatLog.setLineWrap(true);
        sChatLog.setRows(5);
        jScrollPane3.setViewportView(sChatLog);

        btnSend.setText("Send");
        btnSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtSMsg, javax.swing.GroupLayout.DEFAULT_SIZE, 677, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSend)
                .addContainerGap())
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 760, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSMsg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSend))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Chat Log", jPanel3);

        jPanel4.setBackground(new java.awt.Color(0, 0, 0));

        jLabel3.setText("<html>  <head>  <style> <!-- body { background-color: #000; color: #ffffff; } p { color: #ffffff;  } a, a:visited { color: #00ffff;} --> </style> </head>  <body>  <b>About/Credits:</b>  <p>The Elite AQW Emulator was created by the Xyo and Lolwut</p>  <ul><b>Development Team:</b> <li><span style=\"color: #ff00ff; font-weight: bold;\">Lolwut</span> - Core/Root Programmer</li>  <li><span style=\"color: #00ffff; font-weight: bold;\">Xyo</span> - Co Programmer/Formula Calculator</li>  </ul> <ul><b>Features:</b> <li>Supports Quests</li> <li>Suppors PvP Battles and etc...</li> <li>Supports Parties</li> <li>Supports names for a private room e.g(roomname-yey)</li> <li>Real AQW Admin/Moderator Commands</li> <li>Really friendly GUI Interface</li> <li>And much more...!</li> </ul> </ul> <br /> <p><b>Important Note:</b> This emulator was specifically developed to be used and only for<a href=\"http://eqw.zapto.org/\" target=\"_new\"> EQW (Elite Quest Worlds)</a>, If you are in the possession<br />  of the emu,  immediately delete it or Artix Entertainment will rape you out of it. <br /><br /> <span style=\"font-weight: bold; font-size: 16pt;\">It shall only be used for personally and especially for EQW!</span></p>  <br/> <p><b>&copy; 2010 Elite AQW Emulator</b></p>  </body>  </html>");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(82, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(47, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("About/Credits", jPanel4);

        txtJson.setBackground(new java.awt.Color(0, 0, 0));
        txtJson.setColumns(20);
        txtJson.setForeground(new java.awt.Color(255, 0, 204));
        txtJson.setLineWrap(true);
        txtJson.setRows(5);
        jScrollPane6.setViewportView(txtJson);

        btnItemAdd.setText("Add Item!");
        btnItemAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnItemAddActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 750, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(btnItemAdd)
                        .addContainerGap(669, Short.MAX_VALUE))))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnItemAdd)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Item Adder (JSON)", jPanel7);

        sVersion.setText("Elite v0.8b r1");

        txtPassword.setText("MysticXyo");

        btnUnlock.setText("Unlock Emulator");
        btnUnlock.setEnabled(false);
        btnUnlock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUnlockActionPerformed(evt);
            }
        });

        jMenu1.setText("File");

        btnStart.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.ALT_MASK));
        btnStart.setText("Boot");
        btnStart.setEnabled(false);
        btnStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartActionPerformed(evt);
            }
        });
        jMenu1.add(btnStart);

        btnRestart.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.ALT_MASK));
        btnRestart.setText("Restart");
        btnRestart.setEnabled(false);
        btnRestart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRestartActionPerformed(evt);
            }
        });
        jMenu1.add(btnRestart);

        btnShutdown.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_MASK));
        btnShutdown.setText("Shutdown");
        btnShutdown.setEnabled(false);
        btnShutdown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShutdownActionPerformed(evt);
            }
        });
        jMenu1.add(btnShutdown);
        jMenu1.add(jSeparator1);

        btnExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        btnExit.setText("Exit");
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });
        jMenu1.add(btnExit);

        jMenuBar1.add(jMenu1);

        mSettings.setText("Settings");

        mTrace.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        mTrace.setText("Trace All Players");
        mTrace.setEnabled(false);
        mTrace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mTraceActionPerformed(evt);
            }
        });
        mSettings.add(mTrace);

        mUntrace.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        mUntrace.setText("Untrace All Players");
        mUntrace.setEnabled(false);
        mUntrace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mUntraceActionPerformed(evt);
            }
        });
        mSettings.add(mUntrace);

        jMenuBar1.add(mSettings);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 789, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(sTotal)
                        .addGap(130, 130, 130)
                        .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUnlock)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 239, Short.MAX_VALUE)
                        .addComponent(sVersion))
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 769, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sVersion, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUnlock))
                .addGap(21, 21, 21))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendActionPerformed
        _server.lobby.sendData("%xt%moderator%-1%Server: " + _server.lobby.cleanStr(txtSMsg.getText()) + "%");
        writeLog("[Global] Server: " + txtSMsg.getText(), 3);
        txtSMsg.setText("");
    }//GEN-LAST:event_btnSendActionPerformed

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        if(btnUnlock.isEnabled() && _server != null)
            _beginShutdown(false);
        System.exit(0);
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnRestartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRestartActionPerformed
        _beginShutdown(true);
    }//GEN-LAST:event_btnRestartActionPerformed

    private void btnShutdownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShutdownActionPerformed
        _beginShutdown(false);
    }//GEN-LAST:event_btnShutdownActionPerformed

    private void btnStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartActionPerformed
        _Boot();
    }//GEN-LAST:event_btnStartActionPerformed

    @SuppressWarnings("static-access")
    private void mTraceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mTraceActionPerformed
        for(ConnectionHandler u:_server.lobby._users) {
            u._trace = true;
        }
        writeLog("[Lobby] Tracing all player actions. (Not applied to new logged in users)", 2);
    }//GEN-LAST:event_mTraceActionPerformed

    @SuppressWarnings("static-access")
    private void mUntraceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mUntraceActionPerformed
        for(ConnectionHandler u:_server.lobby._users) {
            u._trace = false;
        }
        writeLog("[Lobby] Untracing all player actions.", 2);
}//GEN-LAST:event_mUntraceActionPerformed

    private void xpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xpButtonActionPerformed
        String msg = "Attention! Exp Rate is now "+spnRate.getValue()+"x!";
        _server.lobby.sendData("{\"t\":\"xt\",\"b\":{\"r\":-1,\"o\":{\"cmd\":\"umsg\",\"s\":\""+msg+"\"}}}");
        _sql.update("UPDATE wqw_settings SET xprate="+spnRate.getValue());
        _server.lobby.xpRate = Integer.parseInt(spnRate.getValue().toString());
    }//GEN-LAST:event_xpButtonActionPerformed

    private void tokenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tokenButtonActionPerformed
        _sql.update("UPDATE wqw_settings SET loginkey='"+txtToken.getText()+"'");
        _server.lobby.sToken = txtToken.getText();
    }//GEN-LAST:event_tokenButtonActionPerformed

    private void resTimeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resTimeButtonActionPerformed
        int x = Integer.parseInt(spnResTime.getValue().toString());
        String msg = "Attention! Monster Respawn Time is now "+x+" second/s!";
         _server.lobby.sendData("{\"t\":\"xt\",\"b\":{\"r\":-1,\"o\":{\"cmd\":\"umsg\",\"s\":\""+msg+"\"}}}");
        _server.lobby.respawnTime = x * 1000;
    }//GEN-LAST:event_resTimeButtonActionPerformed

    private void goldButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goldButtonActionPerformed
        String msg = "Attention! Gold Rate is now "+spnRate.getValue()+"x!";
        _server.lobby.sendData("{\"t\":\"xt\",\"b\":{\"r\":-1,\"o\":{\"cmd\":\"umsg\",\"s\":\""+msg+"\"}}}");
        _sql.update("UPDATE wqw_settings SET goldrate="+spnRate.getValue());
        _server.lobby.goldRate = Integer.parseInt(spnRate.getValue().toString());
    }//GEN-LAST:event_goldButtonActionPerformed

    private void btnUnlockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUnlockActionPerformed
        char[] x = "MysticXyo".toCharArray();
        if(Arrays.equals(txtPassword.getPassword(), x)) {
            _Boot();
            txtPassword.setVisible(false);
            btnUnlock.setVisible(false);
            writeLog("[Root] Password Accepted, Starting Booting Procedures...", 2);
        } else {
            writeLog("[Root] Wrong Password!", 2);
        }
    }//GEN-LAST:event_btnUnlockActionPerformed
    public String buildString(String ... _s) {
        _sb.setLength(0);
        for(int i = 0; i < _s.length; i++) {
            _sb.append(_s[i]);
        }
        return _sb.toString();
    }

    private String cleanupWord(String origWord)
     {
        String newWord;

        newWord = origWord;

        // replace backslash with double backslash
        newWord = replaceChar(newWord, "\\", "\\\\");

        // replace apostrophe with "\'"
        newWord = replaceChar(newWord, "'", "\\'");

        // replace question mark with "\?"
        newWord = replaceChar(newWord, "?", "\\?");

        // replace percent sign with "\%"
        newWord = replaceChar(newWord, "%", "\\%");

        // replace quotation-mark with "\""
        newWord = replaceChar(newWord, "\"", "\\\"");

        // replace underscore with "\_"
        newWord = replaceChar(newWord, "_", "\\_");

        return newWord;
     }

    private String replaceChar(String word, String pattern, String replacement)
     {
        StringBuffer temp;
        StringTokenizer st;
        boolean firstToken;

        temp = new StringBuffer("");    // initialize to zero-length

        // if the word begins with the "pattern" String,
        // start the StringBuffer out with the "replacement"
        // String

        if(word.startsWith(pattern) == true)
        {
            temp = temp.append(replacement);
        }

        // split the word into its tokens
        st = new StringTokenizer(word, pattern);

        // loop through the tokens, appending the "replacement"
        // characters ONLY IF the token is not blank, which prevents
        // the programmer from making multiple subtitutions that
        // would in fact corrupt the data
        firstToken = true;

        while(st.hasMoreTokens() == true)
        {
            String tempString = st.nextToken();

            if(tempString.length() > 0)
            {
                // if this is not the first token in the series,
                // append the replacement character to the
                // StringBuffer before tacking on the token
                if(firstToken == false)
                {
                    temp = temp.append(replacement);
                }

                temp = temp.append(tempString);

                firstToken = false; // set the flag to "false"
            }
        }

        return temp.toString();
     }
    private void btnItemAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnItemAddActionPerformed
        //{"ItemID":48,"sIcon":"iiclass","bTemp":0,"sLink":"Darkness","sElmt":"None",
        //"bStaff":0,"CharItemID":7,"iRng":10,"iDPS":0,"bCoins":0,"sES":"ar","iHrs":50,"sFile":"Darkness.swf",
        //"sType":"Armor","sDesc":"I cast darkness into your armor! This look is popular with super vigilantes.",
        //"iStk":1,"bBank":"0","iCost":7500,"bEquip":1,"bUpg":0,"iRty":10,"sName":"Darkness Armor","iQty":0}
        try {
            //JSONObject x = new JSONObject(txtJson.getText());
            JSONArray test = new JSONArray(txtJson.getText());
            String shop = "";
            for(int i = 0; i < test.length(); i++) {
                JSONObject x = test.getJSONObject(i);
                shop += ("," + x.getInt("ItemID"));
                if(!_server.lobby._items.containsKey(x.getInt("ItemID"))) {
                    writeLog("[mySQL] Adding ItemID: " + x.getInt("ItemID"), 2);
                    
                    _sql.update(buildString("insert  into `hs_items`(`itemID`,`sLink`,`sElmt`,`bStaff`,`iRng`,",
                         "`iDPS`,`bCoins`,`sES`,`sType`,`iCost`,`iRty`,`iLvl`,`sIcon`,`iQty`,`iHrs`,",
                         "`sFile`,`iStk`,`sDesc`,`bUpg`,`sName`,`bTemp`,`sFaction`,`iClass`,`FactionID`,",
                         "`iReqRep`,`iReqCP`,`classID`,`EnhID`,`sReqQuests`,`isFounder`) ",
                         "values ",
                         "(" , Integer.toString(x.getInt("ItemID")) , ",'" , x.getString("sLink") , "','" , x.getString("sElmt") , "'",
                         "," , Integer.toString(x.getInt("bStaff")) , "," , Integer.toString(x.getInt("iRng")) , "," , "0",
                         "," , Integer.toString(x.getInt("bCoins")) , ",'" , x.getString("sES") , "','" , x.getString("sType"),
                         "'," , Integer.toString(x.getInt("iCost")) , "," , Integer.toString(x.getInt("iRty")) , "," , Integer.toString(x.getInt("iLvl")),
                         ",'" , x.getString("sIcon") , "'," , Integer.toString(x.getInt("iQty")) , "," , "50",
                         ",'" , x.getString("sFile") , "'," , Integer.toString(x.getInt("iStk")) , ",'" , cleanupWord(x.getString("sDesc")),
                         "'," , Integer.toString(x.getInt("bUpg")) , ",'" , cleanupWord(x.getString("sName")) , "',",Integer.toString(x.getInt("bTemp")),",'',0,0,0,0,0,1,'"+x.getString("sReqQuests")+"',0)"));
                    _server.lobby._items.put(x.getInt("ItemID"), new Item(x.getInt("ItemID"), _sql));
                } else {
                    writeLog("[Json] Item Exists!", 2);
                }
            }
            writeLog("[Json] Shop String: " + shop, 2);
            
        } catch (JSONException e) {
            writeLog("[Json] " + e.getMessage(), 2);
        }
    }//GEN-LAST:event_btnItemAddActionPerformed

    private void tokenButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tokenButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tokenButton1ActionPerformed



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem btnExit;
    private javax.swing.JButton btnItemAdd;
    public static javax.swing.JMenuItem btnRestart;
    private javax.swing.JButton btnSend;
    public static javax.swing.JMenuItem btnShutdown;
    public static javax.swing.JMenuItem btnStart;
    private static javax.swing.JButton btnUnlock;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton goldButton;
    public static javax.swing.JProgressBar jBar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar1;
    public static javax.swing.JList listExp;
    public static javax.swing.JList listLoggedIn;
    private static javax.swing.JMenu mSettings;
    public static javax.swing.JMenuItem mTrace;
    public static javax.swing.JMenuItem mUntrace;
    private javax.swing.JButton resTimeButton;
    public static javax.swing.JTextArea sActLog;
    private static javax.swing.JTextArea sChatLog;
    private static javax.swing.JTextArea sPacketLog;
    public static javax.swing.JLabel sTotal;
    public static javax.swing.JLabel sVersion;
    private javax.swing.JSpinner spnRate;
    private javax.swing.JSpinner spnResTime;
    public static javax.swing.JButton tokenButton;
    public static javax.swing.JButton tokenButton1;
    private javax.swing.JTextArea txtJson;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtSMsg;
    public static javax.swing.JTextField txtToken;
    public static javax.swing.JTextField txtToken1;
    private javax.swing.JButton xpButton;
    // End of variables declaration//GEN-END:variables

}
